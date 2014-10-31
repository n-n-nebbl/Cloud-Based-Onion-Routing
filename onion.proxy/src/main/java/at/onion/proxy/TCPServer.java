package at.onion.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TCPServer extends Thread
{
	private ServerSocket		serverSocket	= null;

	private AtomicBoolean		running			= new AtomicBoolean(false);

	private static Thread		thread			= null;

	private static TCPServer	instance		= null;

	private List<TCPConnection>	connections		= Collections
														.synchronizedList(new ArrayList<TCPConnection>());

	public synchronized static TCPServer getInstance(int localPort)
	{
		if (instance == null)
		{
			try
			{
				System.out.println(String.format(
						"Starting SOCKS server, TCP_PORT=%d ...", localPort));
				instance = new TCPServer(localPort);
			} catch (IOException e)
			{
				System.out
						.println("Error binding port: " + localPort + " " + e);
				return null;
			}

			thread = new Thread(instance);
			thread.start();
		}

		return instance;
	}

	private TCPServer(int localPort) throws IOException
	{
		serverSocket = new ServerSocket(localPort);
		serverSocket.setSoTimeout(5000);
		this.running.set(true);
	}

	public synchronized void setStopped()
	{
		this.running.set(false);

		if (this.serverSocket != null)
			try
			{
				this.serverSocket.close();
			} catch (IOException e)
			{

			}

		this.serverSocket = null;
	}

	public synchronized void run()
	{

		while (running.get())
		{
			TCPConnection c = null;

			try
			{
				try
				{
					c = TCPConnection.getInstance(this.connections,
							serverSocket.accept());
					connections.add(c);

				} catch (SocketTimeoutException ex)
				{
					// System.out.println("STO" + ex);
				}

			} catch (IOException e)
			{
				System.out.println(String.format("Socket I/O Exception 1: %s",
						e));

				if (c != null)
					c.setStopped();
			}

		}
	}
}
