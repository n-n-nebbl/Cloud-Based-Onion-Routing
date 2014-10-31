package at.onion.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	private static Logger	logger	= LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws IOException, SocksException {
		TCPServer server = new TCPServer(Socks5TCPConnection.class, 9000);

		logger.info("Enter to exit.");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		String line = "";

		while ((line = in.readLine()) != null) {
			logger.info("Exit server.");
			server.setStopped();
			return;
		}
	}
}
