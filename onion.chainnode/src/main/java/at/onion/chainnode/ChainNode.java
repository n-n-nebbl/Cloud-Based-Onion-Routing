package at.onion.chainnode;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.onion.commons.CryptoUtils;
import at.onion.commons.NodeInfo;
import at.onion.directoryNodeClient.CoreClient;
import at.onion.directoryNodeClient.SimpleCoreClient;

public class ChainNode {

	private static final int	DEFAULT_PORT	= 9000;

	private static Logger		logger			= LoggerFactory.getLogger(ChainNode.class);

	public static void main(String[] args) {
		ConnectionService connServ = new ConnectionService();

		int port = DEFAULT_PORT;
		try {
			port = Integer.parseInt(System.getProperty("chainNode.port"));
		} catch (Exception e) {
		}

		String dirNodeHostname = System.getProperty("dirNode.hostname");

		if (dirNodeHostname == null) {
			logger.warn("No directoryNode Hostname found --> no connection to dirNode possible");
		} else {
			try {
				String localAdress = getLocalIPAdress();
				CoreClient dirNodeClient = new SimpleCoreClient(InetAddress.getByName(dirNodeHostname), 8001);
				dirNodeClient.addNode(new NodeInfo(localAdress, port, CryptoUtils.getKeyPair().getPublic()));
				logger.info("successfully registered at directoryNode");
			} catch (UnknownHostException e) {
				logger.error("Could not resolve Hostname", e);
			} catch (Exception e) {
				logger.error("Error adding Node to directory Server", e);
			}
		}

		try {
			connServ.startListening(port);
		} catch (Exception e) {
			logger.error("Error occured in ChainNode", e);
			connServ.stopListening(port);
		}
	}

	private static String getLocalIPAdress() throws SocketException {
		Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
		while (e.hasMoreElements()) {
			NetworkInterface n = e.nextElement();
			Enumeration<InetAddress> ee = n.getInetAddresses();
			while (ee.hasMoreElements()) {
				InetAddress i = ee.nextElement();
				logger.debug("ip found {}", i.getHostAddress());
				if (i instanceof Inet6Address) {
					continue;
				}

				// filter wrong adresses
				int first = Integer.parseInt(i.getHostAddress().split("\\.")[0]);
				int second = Integer.parseInt(i.getHostAddress().split("\\.")[1]);
				if (first != 127) {
					if (!(first == 172 && second == 16)) {
						if (!(first == 172 && second == 31)) {
							if (!(first == 169 && second == 254)) {
								return i.getHostAddress();
							}
						}
					}
				}
			}
		}
		return null;
	}
}
