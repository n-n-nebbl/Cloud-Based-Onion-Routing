package at.onion.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.onion.proxy.proxyconnection.ProxyConnection;

public class TCPProxyServer extends Thread {
	private Logger								logger					= LoggerFactory.getLogger(getClass());

	private ServerSocket						serverSocket			= null;

	private AtomicBoolean						running					= new AtomicBoolean(false);

	private static Thread						thread					= null;

	private List<TCPConnection>					connections				= Collections
																				.synchronizedList(new ArrayList<TCPConnection>());

	private Class<? extends TCPConnection>		connectionClass			= null;
	private Class<? extends ProxyConnection>	connectionProxyClass	= null;

	public TCPProxyServer(Class<? extends TCPConnection> connectionClass,
			Class<? extends ProxyConnection> connectionProxyClass, int localPort) throws SocksException {
		logger.info(String.format("Starting " + this.getClass().getName() + " server, TCP_PORT=%d ...", localPort));

		try {
			this.connectionClass = connectionClass;
			this.connectionProxyClass = connectionProxyClass;
			serverSocket = new ServerSocket(localPort);
			serverSocket.setSoTimeout(5000);
			this.running.set(true);

			thread = new Thread(this);
			thread.start();
		} catch (IOException e) {
			throw new SocksException(String.format("Error binding port: %d: %s", localPort, e));
		}
	}

	public synchronized void setStopped() {
		this.running.set(false);

		if (this.serverSocket != null) try {
			this.serverSocket.close();
		} catch (IOException e) {

		}

		this.serverSocket = null;
	}

	public void run() {
		while (running.get()) {
			TCPConnection c = null;

			try {
				try {
					c = ProxyFactory.getConnection(connectionClass, connectionProxyClass, this.connections,
							serverSocket.accept());

					if (c == null)
						logger.error(String.format("%s not in %s.", connectionClass.getName(),
								ProxyFactory.class.getName()));

				} catch (SocketTimeoutException ex) {
					// System.out.println("STO" + ex);
				}

			} catch (IOException e) {
				logger.error(String.format("Socket I/O Exception 1: %s", e));

				if (c != null) c.setStopped();
			}

		}
	}
}
