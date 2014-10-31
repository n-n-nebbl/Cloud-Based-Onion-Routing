package at.onion.proxy;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import at.onion.proxy.Socks5AuthentificationResponse.SOCKS5AUTHENTIFICATIONRESPONSE;
import at.onion.proxy.Socks5Response.SOCKS5RESPONSE;

public class Socks5TCPConnection extends TCPConnection {

	private TestProxyConnection	proxyConnection	= null;

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

	public Socks5TCPConnection(List<TCPConnection> connectionList, Socket s) throws IOException {
		super(connectionList, s, TestProxyConnection.proxySocketTimeout);
	}

	// Todo: reply not right...
	private boolean doInitAuthentification() throws IOException {
		int ver = this.readByte();

		if (ver != 0x05) {
			logger.error(String.format("Connection from %s:%d wants version %d not supported.",
					socket.getLocalAddress(), socket.getLocalPort(), ver));
			return false;
		}

		int nmethods = this.readByte();

		byte[] methods = new byte[nmethods];

		for (int i = 0; i < nmethods; i++) {
			methods[i] = this.readByte();

			if (methods[i] == Socks5AuthentificationResponse
					.getResponseByte(SOCKS5AUTHENTIFICATIONRESPONSE.SOCKS5_AUTH_NOAUTH)) {
				out.write(0x05); // Version 1
				out.write(Socks5AuthentificationResponse
						.getResponseByte(SOCKS5AUTHENTIFICATIONRESPONSE.SOCKS5_AUTH_NOAUTH));
				out.flush();
				return true;
			}
		}

		// "No auth" not found :(
		logger.error(String.format("Connection from %s:%d wants auth not supported.", socket.getLocalAddress(),
				socket.getLocalPort()));

		out.write(0x05); // Version 1
		out.write(Socks5AuthentificationResponse.getResponseByte(SOCKS5AUTHENTIFICATIONRESPONSE.SOCKS5_AUTH_REJECT));
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
		if (atype == 0x01) {
			host = new byte[] { this.readByte(), this.readByte(), this.readByte(), this.readByte() };

			hostName = getIpAddress(host, 4);
		} else if (atype == 0x04) {
			host = new byte[] { this.readByte(), this.readByte(), this.readByte(), this.readByte(), this.readByte(),
					this.readByte(), this.readByte(), this.readByte(), this.readByte(), this.readByte(),
					this.readByte(), this.readByte(), this.readByte(), this.readByte(), this.readByte(),
					this.readByte() };

			hostName = "";
		} else
		// Address
		if (atype == 0x03) {
			host = new byte[this.readByte()];

			for (int i = 0; i < host.length; i++) {
				host[i] = (byte) this.readByte();
				hostName += (char) host[i];
			}
		}

		byte[] portHex = new byte[] { this.readByte(), this.readByte() };

		port = portHex[0] * 256 + portHex[1];

		out.write(0x05);

		boolean success = false;

		if (ver != 0x05) {
			logger.error(String.format("Connection from %s:%d wants connection version not supported.",
					socket.getLocalAddress(), socket.getLocalPort()));

			out.write(Socks5Response.getResponseByte(SOCKS5RESPONSE.SOCKS5_REP_NALLOWED));
		} else if (cmd != 0x01) {
			logger.error(String.format("Connection from %s:%d wants connection command not supported.",
					socket.getLocalAddress(), socket.getLocalPort()));

			out.write(Socks5Response.getResponseByte(SOCKS5RESPONSE.SOCKS5_REP_CNOTSUP));
		}

		else if (rsv != 0x00) {
			logger.error(String.format("Connection from %s:%d wants connection reserverd not 0.",
					socket.getLocalAddress(), socket.getLocalPort()));

			out.write(Socks5Response.getResponseByte(SOCKS5RESPONSE.SOCKS5_REP_NALLOWED));
		}

		else if (atype == 0x04) {
			logger.error(String.format("Connection from %s:%d wants connection to ipv6.", socket.getLocalAddress(),
					socket.getLocalPort()));

			out.write(Socks5Response.getResponseByte(SOCKS5RESPONSE.SOCKS5_REP_NALLOWED));
		} else {
			// Todo: test connection

			try {
				if (startConnection(hostName, port)) {
					out.write(Socks5Response.getResponseByte(SOCKS5RESPONSE.SOCKS5_REP_SUCCEEDED));
					success = true;
				}
			} catch (UnknownHostException e) {
				out.write(Socks5Response.getResponseByte(SOCKS5RESPONSE.SOCKS5_REP_HUNREACH));
				logger.error(String.format("Connection from %s:%d unknown host.", socket.getLocalAddress(),
						socket.getLocalPort()));

			} catch (IOException e) {
				out.write(Socks5Response.getResponseByte(SOCKS5RESPONSE.SOCKS5_REP_REFUSED));
				logger.error(String.format("Connection from %s:%d connection refused.", socket.getLocalAddress(),
						socket.getLocalPort()));
			}

		}

		// Reserved
		out.write(0x00);

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

		proxyConnection = new TestProxyConnection(new Socket(host, port), this);

		return true;
	}

	@Override
	public void setStopped() {
		if (proxyConnection != null) proxyConnection.setStopped();
		super.setStopped();
	}

	@Override
	public void send(byte[] data) {

		logger.info(String.format("Send to client connection: %s", new String(data)));
		super.send(data);
	}

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
				logger.info(String.format("Got data from client: %s", new String(data)));

				if (proxyConnection != null) proxyConnection.send(data);
			}
		} catch (IOException | SocksException e) {
		}
		this.setStopped();
	}
}
