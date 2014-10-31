package at.onion.proxy;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TCPConnection extends Thread {

	public static final String		lineDeterminiter	= "\r\n";

	protected Logger				logger				= LoggerFactory.getLogger(getClass());

	protected Socket				socket				= null;

	private AtomicBoolean			running				= new AtomicBoolean(false);

	private DataInputStream			in					= null;

	protected BufferedOutputStream	out					= null;

	private Thread					thread				= null;

	private List<TCPConnection>		connectionList		= null;

	public TCPConnection(List<TCPConnection> connectionList, Socket socket, int socketTimeout) throws IOException {
		logger.info(String.format("Connection from %s:%d started.", socket.getLocalAddress(), socket.getLocalPort()));
		this.socket = socket;

		if (connectionList != null) {
			this.connectionList = connectionList;
			this.connectionList.add(this);
		}

		this.socket.setSoTimeout(socketTimeout);
		in = new DataInputStream(socket.getInputStream());
		out = new BufferedOutputStream(socket.getOutputStream());
		this.running.set(true);

		thread = new Thread(this);
		thread.start();
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

	public byte[] readData() throws IOException {
		byte[] buffer = new byte[5000];

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

	protected boolean isStopped() {
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

	public static String getIpAddress(byte[] rawBytes, int nr) {
		int i = nr;
		String ipAddress = "";
		for (byte raw : rawBytes) {
			ipAddress += (raw & 0xFF);
			if (--i > 0) {
				ipAddress += ".";
			}
		}

		return ipAddress;
	}
}
