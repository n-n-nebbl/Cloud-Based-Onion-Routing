package at.onion.testservice.simpleclient;

import static at.onion.commons.CryptoUtils.createEncryptedSocket;
import static at.onion.commons.CryptoUtils.decrypt;
import static at.onion.commons.CryptoUtils.encrypt;
import static at.onion.commons.CryptoUtils.getKeyPair;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.sql.NClob;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import at.onion.commons.CryptoUtils;
import at.onion.commons.NodeChainInfo;
import at.onion.commons.NodeChainMessage;
import at.onion.commons.NodeInfo;
import at.onion.directoryNodeClient.SimpleCoreClient;

@Controller
public class SimpleController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public ModelAndView processGET() {
		return new ModelAndView("index");
	}

	@RequestMapping(value = "/index.html", method = RequestMethod.POST)
	public ModelAndView processPOST(@RequestParam String directoryServer,
			@RequestParam int dirPort, @RequestParam String requestServer,
			@RequestParam int requestPort, @RequestParam String request) {
		String result = callWebservice(directoryServer, dirPort, requestServer,
				requestPort, request);
		return new ModelAndView("index", "output", result);
	}

	private String callWebservice(String directoryServer, int dirPort,
			String requestServer, int requestPort, String request) {
		StringBuilder log = new StringBuilder("Call testService started<br />");
		request = request.replace("\\n", "\n");
		request += "\n\n";

		SimpleCoreClient directoryNode = null;
		NodeChainInfo nodeChainInfo = null;
		try {
			directoryNode = new SimpleCoreClient(
					InetAddress.getByName(directoryServer), dirPort);

			nodeChainInfo = directoryNode.getNodeChain();
			log.append("received nodechain<br />");

		} catch (UnknownHostException e) {
			log.append("Could not get directoryServer address: "
					+ e.getMessage());
			return log.toString();
		} catch (Exception e) {
			log.append("Could not retrieve nodes: " + e.getMessage());
			return log.toString();
		}

		List<NodeInfo> nodeChain = Arrays.asList(nodeChainInfo.getNodes());
		Collections.reverse(nodeChain);

		Key publicKey;
		try {
			publicKey = getKeyPair().getPublic();
		} catch (Exception e) {
			log.append("Could create KeyPair: " + e.getMessage());
			return log.toString();
		}

		// Original message, that will be sent to the target
		NodeChainMessage msg = new NodeChainMessage();
		msg.setPayload(request.getBytes());
		NodeInfo header = new NodeInfo(requestServer, requestPort, null);
		msg.setHeader(header);
		msg.setClientPublicKey(publicKey);

		NodeInfo currentNode = nodeChain.get(2);

		try {
			for (int i = 0; i < nodeChain.size(); i++) {
				currentNode = nodeChain.get(i);
				log.append("node" + (nodeChain.size() - i) + "added: " + currentNode.getHostname() + ":" + currentNode.getPort() + "<br />");
				NodeChainMessage outerMsg = new NodeChainMessage();
				outerMsg.setPayload(encrypt(currentNode.getPublicKey(), msg));
				header = new NodeInfo(currentNode.getHostname(),
						currentNode.getPort(), currentNode.getPublicKey());
				outerMsg.setHeader(header);
				outerMsg.setClientPublicKey(publicKey);

				msg = outerMsg;
			}
		} catch (Exception e) {
			log.append("Could not encrypt messages: " + e.getMessage());
			return log.toString();
		}

		byte[] encrypted = msg.getPayload();

		NodeInfo first = currentNode;
		
		logger.info("same node? --> " + first.equals(currentNode));

		logger.info(String.format("Try to connect to first socket: %s.", first));

		Socket destinationSocket = null;

		if (first.getHostname() == null || first.getHostname().length() < 0) {
			log.append("Error connecting to first node: connection data invalid.");
			return log.toString();
		}
		try {
			destinationSocket = createEncryptedSocket(first.getHostname(),
					first.getPort());
		} catch (Exception e) {
			log.append("Could not connect to first node: " + e.getMessage());
			return log.toString();
		}

		ObjectOutputStream stream;
		ObjectInputStream oin;
		try {
			stream = new ObjectOutputStream(destinationSocket.getOutputStream());
			stream.writeObject(encrypted);
			log.append("Request sent...<br />");
		} catch (IOException e) {
			log.append("Could send message to first node: " + e.getMessage());
			return log.toString();
		}
		try {
			oin = new ObjectInputStream(destinationSocket.getInputStream());

			while (true) {
				byte[] result = (byte[]) oin.readObject();
				for(int i=0; i<nodeChain.size(); i++) {
					result = decrypt(result);
				}
				log.append(new String(result));
			}
		} catch (EOFException e) {
		} catch (Exception e) {
			log.append("exception occured while reading response: " + e.getMessage());
			e.printStackTrace();
		}

		return log.toString().replace("\n", "<br />");
	}

	public byte[] encryptNode(PublicKey pubKey, byte[] payLoad,
			NodeInfo nodeInfo) throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchProviderException,
			NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, IOException {
		NodeChainMessage msg = new NodeChainMessage();
		msg.setPayload(payLoad);
		msg.setHeader(nodeInfo);
		msg.setClientPublicKey(getKeyPair().getPublic());

		return encrypt(pubKey, msg);
	}

	public byte[] decryptNode(byte[] payLoad, NodeInfo nodeInfo)
			throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException,
			ClassNotFoundException, IllegalBlockSizeException,
			BadPaddingException, IOException {
		return CryptoUtils.decrypt(payLoad);
	}
}
