package at.onion.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import at.onion.proxy.Socks5AuthentificationResponse.SOCKS5AUTHENTIFICATIONRESPONSE;
import at.onion.proxy.Socks5Response.SOCKS5RESPONSE;

public class TCPConnection extends Thread
{
	private int					socketTimeout	= 5000;

	private Socket				socket			= null;

	private AtomicBoolean		running			= new AtomicBoolean(false);

	private BufferedReader		in				= null;

	private PrintWriter			out				= null;

	private Thread				thread			= null;

	private List<TCPConnection>	connectionList	= null;

	private class DestinationAddress
	{
		private String	host;

		private int		port;

		public DestinationAddress(String target, int port)
		{
			this.host = target;
			this.port = port;
		}

		public String getHost()
		{
			return this.host;
		}

		public int getPort()
		{
			return this.port;
		}
	}

	public synchronized static TCPConnection getInstance(
			List<TCPConnection> connectionList, Socket s) throws IOException
	{
		TCPConnection instance = new TCPConnection(connectionList, s);
		instance.startThread();
		return instance;
	}

	public Thread startThread()
	{
		if (thread == null)
		{
			thread = new Thread(this);
			thread.start();
		}

		return thread;
	}

	private TCPConnection(List<TCPConnection> connectionList, Socket socket)
			throws IOException
	{
		System.out.println(String.format("Connection from %s:%d started.",
				socket.getLocalAddress(), socket.getLocalPort()));
		this.socket = socket;
		this.connectionList = connectionList;
		this.socket.setSoTimeout(this.socketTimeout);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(this.socket.getOutputStream(), true);
		this.running.set(true);
	}

	public synchronized void setStopped()
	{
		this.running.set(false);

		if (this.socket != null)
			try
			{
				this.socket.close();
			} catch (IOException e)
			{
			}

		connectionList.remove(this);

		System.out.println(String.format("Connection from %s:%d terminated.",
				socket.getLocalAddress(), socket.getLocalPort()));
	}

	// Todo: reply not right...
	private boolean doInitAuthentification() throws IOException
	{
		int ver = in.read();

		if (ver != 0x05)
		{
			System.out.println(String.format(
					"Connection from %s:%d wants version %d not supported.",
					socket.getLocalAddress(), socket.getLocalPort(), ver));
			return false;
		}

		int nmethods = in.read();

		byte[] methods = new byte[nmethods];

		for (int i = 0; i < nmethods; i++)
		{
			methods[i] = (byte) in.read();

			if (methods[i] == Socks5AuthentificationResponse
					.getResponseByte(SOCKS5AUTHENTIFICATIONRESPONSE.SOCKS5_AUTH_NOAUTH))
			{
				out.write(0x05); // Version 1
				out.write(Socks5AuthentificationResponse
						.getResponseByte(SOCKS5AUTHENTIFICATIONRESPONSE.SOCKS5_AUTH_NOAUTH));
				out.flush();
				return true;
			}
		}

		// "No auth" not found :(
		System.out.println(String.format(
				"Connection from %s:%d wants auth not supported.",
				socket.getLocalAddress(), socket.getLocalPort()));

		out.write(0x05); // Version 1
		out.write(Socks5AuthentificationResponse
				.getResponseByte(SOCKS5AUTHENTIFICATIONRESPONSE.SOCKS5_AUTH_REJECT));
		out.flush();
		return false;
	}

	private DestinationAddress doInitConnectionRequest() throws IOException,
			SocketException
	{
		// See http://tools.ietf.org/html/rfc1928
		int ver = in.read();

		// CONNECT X'01'
		// BIND X'02'
		// UDP ASSOCIATE X'03'
		int cmd = in.read();

		// Reserved
		int rsv = in.read();

		// Atype
		int atype = in.read();

		String hostName = "";
		int port = 0;

		byte[] host = new byte[0];

		// Ipv4 address
		if (atype == 0x01)
		{
			host = new byte[]
			{ (byte) in.read(), (byte) in.read(), (byte) in.read(),
					(byte) in.read() };

			hostName = getIpAddress(host, 4);
		} else if (atype == 0x04)
		{
			host = new byte[]
			{ (byte) in.read(), (byte) in.read(), (byte) in.read(),
					(byte) in.read(), (byte) in.read(), (byte) in.read(),
					(byte) in.read(), (byte) in.read(), (byte) in.read(),
					(byte) in.read(), (byte) in.read(), (byte) in.read(),
					(byte) in.read(), (byte) in.read(), (byte) in.read(),
					(byte) in.read() };

			hostName = "";
		} else
		// Address
		if (atype == 0x03)
		{
			host = new byte[in.read()];

			for (int i = 0; i < host.length; i++)
			{
				host[i] = (byte) in.read();
				hostName += (char) host[i];
			}
		}

		byte[] portHex = new byte[]
		{ (byte) in.read(), (byte) in.read() };

		out.write(0x05);

		boolean success = false;

		if (ver != 0x05)
		{
			System.out
					.println(String
							.format("Connection from %s:%d wants connection version not supported.",
									socket.getLocalAddress(),
									socket.getLocalPort()));

			out.write(Socks5Response
					.getResponseByte(SOCKS5RESPONSE.SOCKS5_REP_NALLOWED));
		} else if (cmd != 0x01)
		{
			System.out
					.println(String
							.format("Connection from %s:%d wants connection command not supported.",
									socket.getLocalAddress(),
									socket.getLocalPort()));

			out.write(Socks5Response
					.getResponseByte(SOCKS5RESPONSE.SOCKS5_REP_CNOTSUP));
		}

		else if (rsv != 0x00)
		{
			System.out.println(String.format(
					"Connection from %s:%d wants connection reserverd not 0.",
					socket.getLocalAddress(), socket.getLocalPort()));

			out.write(Socks5Response
					.getResponseByte(SOCKS5RESPONSE.SOCKS5_REP_NALLOWED));
		}

		else if (atype == 0x04)
		{
			System.out.println(String.format(
					"Connection from %s:%d wants connection to ipv6.",
					socket.getLocalAddress(), socket.getLocalPort()));

			out.write(Socks5Response
					.getResponseByte(SOCKS5RESPONSE.SOCKS5_REP_NALLOWED));
		} else
		{
			// Todo: test connection

			if (startConnection(hostName, port))
			{
				out.write(Socks5Response
						.getResponseByte(SOCKS5RESPONSE.SOCKS5_REP_SUCCEEDED));
				success = true;
			}

		}

		// Reserved
		out.write(0x00);

		// Send address type
		out.write(atype);

		// Send address
		for (int i = 0; i < host.length; i++)
			out.write(host[i]);

		out.write(portHex[0]);
		out.write(portHex[1]);
		out.flush();

		if (success)
			return new DestinationAddress(hostName, port);

		return null;
	}

	public static String getIpAddress(byte[] rawBytes, int nr)
	{
		int i = nr;
		String ipAddress = "";
		for (byte raw : rawBytes)
		{
			ipAddress += (raw & 0xFF);
			if (--i > 0)
			{
				ipAddress += ".";
			}
		}

		return ipAddress;
	}

	private boolean startConnection(String host, int port)
			throws SocketException
	{
		System.out.println(String.format(
				"Connection from %s:%d: Starting connection to %s:%d.",
				socket.getLocalAddress(), socket.getLocalPort(), host, port));

		return true;
	}

	public void run()
	{
		String line = "";

		try
		{
			// http://tools.ietf.org/html/rfc1928 3.
			// The client connects to the server, and sends a version
			// identifier/method selection message:
			if (!doInitAuthentification())
			{
				this.setStopped();
				return;
			}

			DestinationAddress address = doInitConnectionRequest();

			if (address == null)
			{
				System.out.println(String.format(
						"Connection from %s:%d: Failed.",
						socket.getLocalAddress(), socket.getLocalPort()));

				this.setStopped();
				return;
			}

			while (running.get() && (line = in.readLine()) != null)
			{
				System.out.println(String.format("Got line: %s", line));
			}
		} catch (IOException | SocketException e)
		{
			this.setStopped();
		}
	}
}
