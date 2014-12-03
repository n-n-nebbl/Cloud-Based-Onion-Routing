package at.onion.proxy.socks5;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import at.onion.proxy.ProxyFactory;
import at.onion.proxy.SocksException;
import at.onion.proxy.TCPConnection;
import at.onion.proxy.TCPConnectionProxyProperty;
import at.onion.proxy.proxyconnection.ProxyConnection;

public class Socks5TCPConnection extends TCPConnection {

	private ProxyConnection						proxyConnection			= null;
	private Class<? extends ProxyConnection>	connectionProxyClass	= null;

	private class DestinationAddress {
		private String	host;

		private int		port;

		public DestinationAddress(String target, int port) {
			this.host = target;
			this.port = port;
		}

		public String getHost() {
			return this.host;
		}

		public int getPort() {
			return this.port;
		}
	}

	public Socks5TCPConnection(Class<? extends ProxyConnection> connectionProxyClass,
			List<TCPConnection> connectionList, Socket s, TCPConnectionProxyProperty proxyConnectionProperty)
			throws IOException {
		super(connectionList, s, Socks5Metadata.proxySocketTimeout, true, proxyConnectionProperty);
		this.connectionProxyClass = connectionProxyClass;
	}

	// Todo: reply not right...
	private boolean doInitAuthentification() throws IOException {
		// int ver = this.readByte(); <- already read

		int nmethods = this.readByte();

		byte[] methods = new byte[nmethods];

		for (int i = 0; i < nmethods; i++) {
			methods[i] = this.readByte();

			if (methods[i] == Socks5Metadata.SOCKS5_AUTH_NOAUTH) {
				out.write(Socks5Metadata.SOCKS5_Version); // Version 1
				out.write(Socks5Metadata.SOCKS5_AUTH_NOAUTH);
				out.flush();
				return true;
			}
		}

		// "No auth" not found :(
		logger.error(String.format("Connection from %s:%d wants auth not supported.", socket.getLocalAddress(),
				socket.getLocalPort()));

		out.write(Socks5Metadata.SOCKS5_Version); // Version 1
		out.write(Socks5Metadata.SOCKS5_AUTH_REJECT);
		out.flush();
		return false;
	}

	private DestinationAddress doInitConnectionRequest() throws IOException, SocksException {
		// See http://tools.ietf.org/html/rfc1928
		int ver = this.readByte();

		// CONNECT X'01'
		// BIND X'02'
		// UDP ASSOCIATE X'03'
		int cmd = this.readByte();

		// Reserved
		int rsv = this.readByte();

		// Atype
		int atype = this.readByte();

		String hostName = "";
		int port = 0;

		byte[] host = new byte[0];

		// Ipv4 address
		if (atype == Socks5Metadata.SOCKS5_HOSTNAMETYPE_IPV4) {
			host = new byte[] { this.readByte(), this.readByte(), this.readByte(), this.readByte() };

			hostName = Socks5Metadata.convertIPAddress(host, 4);
		} else if (atype == Socks5Metadata.SOCKS5_HOSTNAMETYPE_IPV6) {
			host = new byte[] { this.readByte(), this.readByte(), this.readByte(), this.readByte(), this.readByte(),
					this.readByte(), this.readByte(), this.readByte(), this.readByte(), this.readByte(),
					this.readByte(), this.readByte(), this.readByte(), this.readByte(), this.readByte(),
					this.readByte() };

			hostName = "";
		} else
		// Address
		if (atype == Socks5Metadata.SOCKS5_HOSTNAMETYPE_NAME) {
			host = new byte[this.readByte()];

			for (int i = 0; i < host.length; i++) {
				host[i] = (byte) this.readByte();
				hostName += (char) host[i];
			}
		}

		byte[] portHex = new byte[] { this.readByte(), this.readByte() };

		port = Socks5Metadata.convertTwoBytesToPort(portHex[0], portHex[1]);

		//
		// Sends the answer
		//

		out.write(Socks5Metadata.SOCKS5_Version);

		boolean success = false;

		if (ver != Socks5Metadata.SOCKS5_Version) {
			logger.error(String.format("Connection from %s:%d wants connection version not supported.",
					socket.getLocalAddress(), socket.getLocalPort()));

			out.write(Socks5Metadata.SOCKS5_REP_NALLOWED);
		} else if (cmd != 0x01) {
			logger.error(String.format("Connection from %s:%d wants connection command not supported.",
					socket.getLocalAddress(), socket.getLocalPort()));

			out.write(Socks5Metadata.SOCKS5_REP_CNOTSUP);
		}

		else if (rsv != Socks5Metadata.SOCKS5_Reserved) {
			logger.error(String.format("Connection from %s:%d wants connection reserverd not 0.",
					socket.getLocalAddress(), socket.getLocalPort()));

			out.write(Socks5Metadata.SOCKS5_REP_NALLOWED);
		}

		else if (atype == Socks5Metadata.SOCKS5_HOSTNAMETYPE_IPV6) {
			logger.error(String.format("Connection from %s:%d wants connection to ipv6.", socket.getLocalAddress(),
					socket.getLocalPort()));

			out.write(Socks5Metadata.SOCKS5_REP_NALLOWED);
		} else {
			try {
				if (startConnection(hostName, port)) {
					out.write(Socks5Metadata.SOCKS5_REP_SUCCEEDED);
					success = true;
				}
			} catch (UnknownHostException e) {
				out.write(Socks5Metadata.SOCKS5_REP_HUNREACH);
				logger.error(String.format("Connection from %s:%d unknown host.", socket.getLocalAddress(),
						socket.getLocalPort()));

			} catch (IOException e) {
				out.write(Socks5Metadata.SOCKS5_REP_REFUSED);
				logger.error(String.format("Connection from %s:%d connection refused.", socket.getLocalAddress(),
						socket.getLocalPort()));
			}

		}

		// Reserved
		out.write(Socks5Metadata.SOCKS5_Reserved);

		// Send address type
		out.write(atype);

		// Sends the length
		out.write(host.length);

		// Send address
		for (int i = 0; i < host.length; i++)
			out.write(host[i]);

		out.write(portHex[0]);
		out.write(portHex[1]);
		out.flush();

		if (success) return new DestinationAddress(hostName, port);

		return null;
	}

	private boolean startConnection(String host, int port) throws UnknownHostException, IOException {
		logger.info(String.format("Connection from %s:%d: Starting connection to %s:%d.", socket.getLocalAddress(),
				socket.getLocalPort(), host, port));

		proxyConnection = ProxyFactory.getProxyConnection(connectionProxyClass, host, port, this);
		if (proxyConnection == null) {
			logger.error(String.format("%s not in %s.", connectionProxyClass.getName(), ProxyFactory.class.getName()));

			return false;
		}

		proxyConnection.startConnectionAndThread();

		return true;
	}

	@Override
	public void setStopped() {
		if (proxyConnection != null) proxyConnection.setStopped();
		super.setStopped();
	}

	@Override
	public void send(byte[] data) {

		// logger.info(String.format("Send to client connection: %s", new
		// String(data)));
		super.send(data);
	}

	@Override
	public void run() {

		try {
			// http://tools.ietf.org/html/rfc1928 3.
			// The client connects to the server, and sends a version
			// identifier/method selection message:
			if (!doInitAuthentification()) {
				this.setStopped();
				return;
			}

			DestinationAddress address = doInitConnectionRequest();

			if (address == null) {
				logger.error(String.format("Connection from %s:%d: Failed.", socket.getLocalAddress(),
						socket.getLocalPort()));

				this.setStopped();
				return;
			}

			byte[] data;

			while (!this.isStopped() && !proxyConnection.isStopped() && (data = this.readData()) != null) {
				// logger.info(String.format("Got data from client: %s", new
				// String(data)));

				if (proxyConnection != null) proxyConnection.sendToDestination(data);
			}
		} catch (IOException | SocksException e) {
		}
		this.setStopped();
	}
}
