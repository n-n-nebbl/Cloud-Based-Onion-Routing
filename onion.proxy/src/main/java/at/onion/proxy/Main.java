package at.onion.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.onion.proxy.proxyconnection.FilterTestProxyConnection;
import at.onion.proxy.socks5.Socks5TCPConnection;

public class Main {
	private static Logger	logger	= LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws IOException, SocksException {
		TCPProxyServer server = new TCPProxyServer(Socks5TCPConnection.class, FilterTestProxyConnection.class, 9000);

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
