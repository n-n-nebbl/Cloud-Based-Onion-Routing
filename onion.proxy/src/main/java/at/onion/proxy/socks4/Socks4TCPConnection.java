package at.onion.proxy.socks4;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import at.onion.proxy.ProxyFactory;
import at.onion.proxy.SocksException;
import at.onion.proxy.TCPConnection;
import at.onion.proxy.TCPConnectionProxyProperty;
import at.onion.proxy.proxyconnection.ProxyConnection;
import at.onion.proxy.socks5.Socks5Metadata;

public class Socks4TCPConnection extends TCPConnection {

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

	public Socks4TCPConnection(Class<? extends ProxyConnection> connectionProxyClass,
			List<TCPConnection> connectionList, Socket s, TCPConnectionProxyProperty proxyConnectionProperty)
			throws IOException {
		super(connectionList, s, Socks5Metadata.proxySocketTimeout, true, proxyConnectionProperty);
		this.connectionProxyClass = connectionProxyClass;
	}

	private DestinationAddress doInitConnectionRequest() throws IOException, SocksException {

		// http://en.wikipedia.org/wiki/SOCKS
		// field 1: SOCKS version number, 1 byte, must be 0x04 for this version
		// int ver = this.readByte(); <- already read

		// field 2: command code, 1 byte:
		// 0x01 = establish a TCP/IP stream connection
		// 0x02 = establish a TCP/IP port binding
		int cmd = this.readByte();

		// field 3: network byte order port number, 2 bytes
		byte[] portHex = new byte[] { this.readByte(), this.readByte() };
		int port = Socks5Metadata.convertTwoBytesToPort(portHex[0], portHex[1]);

		// field 4: network byte order IP address, 4 bytes
		byte[] host = new byte[] { this.readByte(), this.readByte(), this.readByte(), this.readByte() };
		String hostName = Socks5Metadata.convertIPAddress(host, 4);

		// field 5: the user ID string, variable length, terminated with a null
		// (0x00)
		byte cur;
		while ((cur = this.readByte()) != 0x00) {
		}

		// Socks4a if:
		// - field 4: deliberate invalid IP address, 4 bytes, first three must
		// be 0x00 and the last one must not be 0x00
		// THEN:
		// - field 6: the domain name of the host we want to contact, variable
		// length, terminated with a null (0x00)
		if (host[0] == 0x00 && host[1] == 0x00 && host[2] == 0x00 && host[3] != 0x00) {
			hostName = "";
			while ((cur = this.readByte()) != 0x00) {
				hostName += new String(new byte[] { cur });
			}
		}

		//
		// Sends the answer
		//

		// field 1: null byte
		out.write(Socks4Metadata.SOCKS4_Reserved);

		boolean success = false;

		// field 2: status, 1 byte:
		try {
			if (startConnection(hostName, port)) {
				out.write(Socks4Metadata.SOCKS4_REP_SUCCEEDED);
				success = true;
			}
		} catch (UnknownHostException e) {
			out.write(Socks4Metadata.SOCKS4_REP_REJECTED);
			logger.error(String.format("Connection from %s:%d unknown host: %s.", socket.getLocalAddress(),
					socket.getLocalPort(), e));

		} catch (IOException e) {
			out.write(Socks4Metadata.SOCKS4_REP_REJECTED);
			logger.error(String.format("Connection from %s:%d connection refused: %s.", socket.getLocalAddress(),
					socket.getLocalPort(), e));
		}

		// field 3: 2 arbitrary bytes, that should be ignored
		out.write(Socks4Metadata.SOCKS4_Reserved);
		out.write(Socks4Metadata.SOCKS4_Reserved);

		// field 4: 4 arbitrary bytes, that should be ignored
		out.write(Socks4Metadata.SOCKS4_Reserved);
		out.write(Socks4Metadata.SOCKS4_Reserved);
		out.write(Socks4Metadata.SOCKS4_Reserved);
		out.write(Socks4Metadata.SOCKS4_Reserved);
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
		} catch (Exception e) {
			logger.error(String.format("Error in socks4 connection: %s., closing", e));
		}
		this.setStopped();
	}
}
