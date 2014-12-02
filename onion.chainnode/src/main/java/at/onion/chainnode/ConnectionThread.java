package at.onion.chainnode;

import java.io.InputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.onion.commons.CryptoUtils;
import at.onion.commons.NodeChainMessage;
import at.onion.commons.NodeInfo;
import static at.onion.chainnode.ForwardingMode.*;

/**
 * Handles one connection between nodes or node and target
 * 
 * @author NEBEL
 */
public class ConnectionThread implements Runnable {

	private Logger			logger	= LoggerFactory.getLogger(getClass());
	private NetworkService	networkService;
	private Socket			incomingSocket;
	private Socket			outgoingSocket;

	public ConnectionThread(Socket s) {
		this.networkService = new NetworkService();
		this.incomingSocket = s;
	}

	@Override
	public void run() {
		try {
			// get first message
			InputStream in = incomingSocket.getInputStream();
			NodeChainMessage msg = networkService.readMessage(in);

			// build socket to next node/target
			NodeInfo header = msg.getHeader();

			logger.debug("connect to " + header.getHostname() + ":" + header.getPort());

			boolean target = header.getPublicKey() == null;
			if (!target) {
				outgoingSocket = CryptoUtils.createEncryptedSocket(header.getHostname(), header.getPort());
			} else {
				outgoingSocket = new Socket(header.getHostname(), header.getPort());
			}

			// now just forward messages in both directions

			int mode1 = (target) ? FORWARDING_NODE_TO_TARGET : FORWARDING_NODE_TO_NODE;
			int mode2 = (target) ? FORWARDING_TARGET_TO_NODE : FORWARDING_NODE_TO_NODE;

			ForwardingMode fMode1 = new ForwardingMode(mode1, ROUTE_TO_SERVER);
			ForwardingMode fMode2 = new ForwardingMode(mode2, ROUTE_TO_CLIENT, msg.getClientPublicKey());

			// forward messages from client to server
			new Thread(new ForwardingThread(incomingSocket, outgoingSocket, fMode1)).start();

			// forward messages from server to client
			logger.debug(header.getPort() + ": to_client_thread");
			new Thread(new ForwardingThread(outgoingSocket, incomingSocket, fMode2)).start();

			// send message
			networkService.sendMessage(outgoingSocket.getOutputStream(), msg);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}