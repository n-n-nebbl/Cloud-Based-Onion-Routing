package at.onion.proxy.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.onion.proxy.SocksException;
import at.onion.proxy.TCPProxyServer;
import at.onion.proxy.proxyconnection.ProxyConnection;

public class ProxyManager {

	private Logger			logger	= LoggerFactory.getLogger(this.getClass());
	private TCPProxyServer	server	= null;
	public MainWindow		window	= null;

	public ProxyManager(MainWindow window) {
		this.window = window;
	}

	public boolean startServer(Class<? extends ProxyConnection>[] allowedProxyConnections,
			Class<? extends ProxyConnection> connectionProxyClass) {

		// Restart
		if (server != null) {
			logger.info("Server running, stopping...");
			server.setStopped();

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				logger.error("Sleep failed");
				window.exitProgramm();
			}
		}

		try {
			server = new TCPProxyServer(allowedProxyConnections, connectionProxyClass, 9000);
		} catch (SocksException e) {

			logger.error(String.format("Error starting server: %s", e));
			return false;
		}

		return true;
	}

	public void stopServer() {

		if (server != null) {
			server.setStopped();
		} else
			logger.info("Server not running.");
	}

}
