package at.onion.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.onion.proxy.proxyconnection.ProxyConnection;
import at.onion.proxy.socks4.Socks4Metadata;
import at.onion.proxy.socks4.Socks4TCPConnection;
import at.onion.proxy.socks5.Socks5Metadata;
import at.onion.proxy.socks5.Socks5TCPConnection;

public class TCPProxyServer extends Thread {
	private Logger									logger					= LoggerFactory.getLogger(getClass());

	private ServerSocket							serverSocket			= null;

	private AtomicBoolean							running					= new AtomicBoolean(false);

	private static Thread							thread					= null;

	private List<TCPConnection>						connections				= Collections
																					.synchronizedList(new ArrayList<TCPConnection>());

	private Class<? extends ProxyConnection>		connectionProxyClass	= null;
	private List<Class<? extends TCPConnection>>	allowedProxyConnections	= new ArrayList<Class<? extends TCPConnection>>();

	public TCPProxyServer(Class<? extends TCPConnection>[] allowedProxyConnections,
			Class<? extends ProxyConnection> connectionProxyClass, int localPort) throws SocksException {
		logger.info(String.format("Starting " + this.getClass().getName() + " server, TCP_PORT=%d ...", localPort));

		try {
			for (int i = 0; i < allowedProxyConnections.length; i++)
				this.allowedProxyConnections.add(allowedProxyConnections[i]);

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

	public synchronized boolean isStopped() {
		return !this.running.get();
	}

	public void run() {
		while (running.get()) {
			TCPConnection c = null;

			try {
				try {
					Socket s = serverSocket.accept();

					int version = s.getInputStream().read();

					if (version == Socks5Metadata.SOCKS5_Version
							&& this.allowedProxyConnections.contains(Socks5TCPConnection.class)) {

						c = ProxyFactory.getConnection(Socks5TCPConnection.class, connectionProxyClass,
								this.connections, s);

						if (c == null)
							logger.error(String.format("%s not in %s.", Socks5TCPConnection.class.getName(),
									ProxyFactory.class.getName()));
					} else if (version == Socks4Metadata.SOCKS4_Version
							&& this.allowedProxyConnections.contains(Socks4TCPConnection.class)) {

						c = ProxyFactory.getConnection(Socks4TCPConnection.class, connectionProxyClass,
								this.connections, s);

						if (c == null)
							logger.error(String.format("%s not in %s.", Socks4TCPConnection.class.getName(),
									ProxyFactory.class.getName()));
					} else {
						logger.error("Unknown version from %s.", s.getInetAddress());
						s.getOutputStream().write("UNKNOWN_VERSION".getBytes());
						s.getOutputStream().flush();
						s.close();
					}
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
