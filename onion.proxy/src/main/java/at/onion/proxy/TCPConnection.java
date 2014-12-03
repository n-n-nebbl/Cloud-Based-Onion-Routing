package at.onion.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TCPConnection implements Runnable {

	public static final String			lineDeterminiter	= "\r\n";

	protected Logger					logger				= LoggerFactory.getLogger(getClass());

	protected Socket					socket				= null;

	private AtomicBoolean				running				= new AtomicBoolean(false);

	private InputStream					in					= null;

	protected OutputStream				out					= null;

	private Thread						thread				= null;

	private List<TCPConnection>			connectionList		= null;

	private TCPConnectionProxyProperty	connectionProperty	= null;

	public TCPConnection(List<TCPConnection> connectionList, Socket socket, int socketTimeout, boolean startThread,
			TCPConnectionProxyProperty connectionProperty) throws IOException {
		logger.info(String.format("Connection from %s:%d (ProxyProperty, directory: %s:%d) started.",
				socket.getLocalAddress(), socket.getLocalPort(), connectionProperty.getDirectoryNodeHostName(),
				connectionProperty.getDirectoryNodePort()));
		this.socket = socket;
		this.connectionProperty = connectionProperty;

		if (connectionList != null) {
			this.connectionList = connectionList;
			this.connectionList.add(this);
		}

		this.socket.setSoTimeout(socketTimeout);
		in = socket.getInputStream();
		out = socket.getOutputStream();
		this.running.set(true);

		if (startThread) {
			thread = new Thread(this);
			thread.start();
		}
	}

	public synchronized void setStopped() {

		if (!this.running.get()) return;

		this.running.set(false);

		if (socket != null)
			logger.info(String.format("Connection to %s terminated.", socket.getRemoteSocketAddress()));

		if (this.in != null) try {
			this.socket.getInputStream().close();
			this.in.close();
			this.in = null;
		} catch (IOException e1) {
			this.in = null;
		}

		if (this.out != null) {

			try {
				this.socket.getOutputStream().close();
			} catch (IOException e) {
			}
			this.out = null;
		}

		if (this.socket != null) try {
			this.socket.close();
			this.socket = null;
		} catch (IOException e) {
			this.socket = null;
		}

		if (connectionList != null) connectionList.remove(this);

	}

	public TCPConnectionProxyProperty getTCPConnectionProxyProperty() {
		return connectionProperty;
	}

	public byte[] readData() throws IOException {
		byte[] buffer = new byte[this.socket.getReceiveBufferSize()];

		if (this.isStopped()) return null;

		if (this.in != null) {
			int len = in.read(buffer);

			if (len < 0) return null;

			return Arrays.copyOfRange(buffer, 0, len);
		}

		return null;
	}

	public byte readByte() throws IOException {

		if (this.in != null) {
			return (byte) in.read();
		}

		return -1;
	}

	public OutputStream getOutputStream() {
		return this.out;
	}

	public boolean isStopped() {
		return !this.running.get();
	}

	public void send(byte[] data) {

		if (this.out != null) {
			try {
				this.out.write(data);
			} catch (IOException e) {
				this.setStopped();
			}
		}
	}

	@Override
	public void run() {

	}
}
