package at.onion.chainnode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.onion.commons.CryptoUtils;
import at.onion.commons.NodeInfo;
import at.onion.directoryNodeClient.CoreClient;
import at.onion.directoryNodeClient.SimpleCoreClient;

public class ChainNode {

	private static final int	DEFAULT_PORT			= 9001;
	private static final int	DEFAULT_DIRNODE_PORT	= 8001;

	private static Logger		logger					= LoggerFactory.getLogger(ChainNode.class);

	public static void main(String[] args) {
		ConnectionService connServ = new ConnectionService();

		int port = DEFAULT_PORT;
		int dirNodePort = DEFAULT_DIRNODE_PORT;
		try {
			port = Integer.parseInt(System.getProperty("chainNode.port"));
		} catch (Exception e) {
		}

		try {
			dirNodePort = Integer.parseInt(System.getProperty("dirNode.port"));
		} catch (Exception e) {
		}

		String dirNodeHostname = System.getProperty("dirNode.hostname");
		logger.debug("===============>" + dirNodeHostname + ":" + dirNodePort);

		if (dirNodeHostname == null) {
			logger.warn("No directoryNode Hostname found --> no connection to dirNode possible");
		} else {
			try {
				String localAdress = getLocalIPAdress();
				CoreClient dirNodeClient = new SimpleCoreClient(InetAddress.getByName(dirNodeHostname), dirNodePort);
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

		URL whatismyip;
		try {
			whatismyip = new URL("http://agentgatech.appspot.com/");
			BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));

			String ip = in.readLine().trim();
			logger.debug("IP per external found {}.", ip);
			return ip;
		} catch (MalformedURLException e1) {
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
		while (e.hasMoreElements()) {
			NetworkInterface n = e.nextElement();
			Enumeration<InetAddress> ee = n.getInetAddresses();
			while (ee.hasMoreElements()) {
				InetAddress i = ee.nextElement();
				logger.debug("IP found {}.", i.getHostAddress());
				if (i instanceof Inet6Address) {
					continue;
				}

				// filter wrong adresses
				int first = Integer.parseInt(i.getHostAddress().split("\\.")[0]);
				int second = Integer.parseInt(i.getHostAddress().split("\\.")[1]);
				if (first != 127) {
					if (!(first == 169 && second == 254)) {
						logger.debug("Let's take IP: {}.", i.getHostAddress());
						return i.getHostAddress();
					}
				}
			}
		}
		return null;
	}
}
