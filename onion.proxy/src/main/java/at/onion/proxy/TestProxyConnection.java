package at.onion.proxy;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

import java.io.IOException;
import java.net.Socket;

public class TestProxyConnection extends TCPConnection {

	public static final int		proxySocketTimeout	= (int) MILLISECONDS.convert(1, MINUTES);

	private Socks5TCPConnection	socksConnection		= null;

	public TestProxyConnection(Socket s, Socks5TCPConnection socksConnection) throws IOException {
		super(null, s, proxySocketTimeout);

		this.socksConnection = socksConnection;
	}

	@Override
	public void send(byte[] message) {

		logger.info(String.format("Send to final connection: %s", new String(message)));
		super.send(message);
	}

	@Override
	public void setStopped() {
		if (isStopped()) return;

		super.setStopped();
		socksConnection.setStopped();
	}

	public void run() {
		byte[] data;

		try {
			while (!this.isStopped() && (data = this.readData()) != null) {

				logger.info(String.format("Got from final connection: %s", new String(data)));
				socksConnection.send(data);
			}
		} catch (IOException e) {
		}

		this.setStopped();
	}
}
