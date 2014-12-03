package at.onion.proxy.proxyconnection;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.onion.proxy.TCPConnection;
import at.onion.proxy.TCPConnectionProxyProperty;
import at.onion.proxy.socks5.Socks5Metadata;

public class TestProxyConnection implements ProxyConnection, Runnable {
	protected Logger		logger					= LoggerFactory.getLogger(getClass());

	private TCPConnection	clientConnection		= null;
	private Thread			thread					= null;

	private TCPConnection	destinationConnection	= null;
	private Socket			destinationSocket		= null;

	public TestProxyConnection(String host, int port, TCPConnection socksConnection) throws UnknownHostException,
			IOException {

		this.clientConnection = socksConnection;
		this.destinationSocket = new Socket(host, port);
	}

	@Override
	public void startConnectionAndThread() throws IOException {
		this.destinationConnection = new TCPConnection(null, destinationSocket, Socks5Metadata.proxySocketTimeout,
				false, new TCPConnectionProxyProperty());

		thread = new Thread(this);
		thread.start();
	}

	public void sendToDestination(byte[] message) {

		// logger.info(String.format("Send to final connection: %s", new
		// String(message)));
		this.destinationConnection.send(message);
	}

	public void sendToClient(byte[] message) {

		clientConnection.send(message);
	}

	public void run() {
		byte[] data;

		try {
			while (!this.isStopped() && (data = destinationConnection.readData()) != null) {

				// logger.info(String.format("Got from final connection: %s",
				// new String(data)));
				sendToClient(data);
			}
		} catch (IOException e) {
		}

		this.setStopped();
	}

	public void setStopped() {
		if (isStopped()) return;

		this.destinationConnection.setStopped();
		this.clientConnection.setStopped();
	}

	@Override
	public boolean isStopped() {
		return destinationConnection.isStopped();
	}
}
