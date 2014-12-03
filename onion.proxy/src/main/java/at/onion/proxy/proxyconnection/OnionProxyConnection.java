package at.onion.proxy.proxyconnection;

import static at.onion.commons.CryptoUtils.createEncryptedSocket;
import static at.onion.commons.CryptoUtils.encrypt;
import static at.onion.commons.CryptoUtils.getKeyPair;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.onion.commons.CryptoUtils;
import at.onion.commons.NodeChainInfo;
import at.onion.commons.NodeChainMessage;
import at.onion.commons.NodeInfo;
import at.onion.directoryNodeClient.InternalServerErrorException;
import at.onion.directoryNodeClient.InvalidResultException;
import at.onion.directoryNodeClient.SimpleCoreClient;
import at.onion.proxy.TCPConnection;
import at.onion.proxy.TCPConnectionProxyProperty;
import at.onion.proxy.socks5.Socks5Metadata;

public class OnionProxyConnection implements ProxyConnection, Runnable {
	protected Logger					logger						= LoggerFactory.getLogger(getClass());

	private TCPConnection				clientConnection			= null;
	private Thread						thread						= null;

	private Socket						destinationSocket			= null;
	private TCPConnection				destinationConnection		= null;
	private NodeChainInfo				nodeChain					= null;
	private TCPConnectionProxyProperty	tcpConnectionProxyProperty	= null;
	private String						host						= "";
	private int							port						= -1;

	public OnionProxyConnection(String host, int port, TCPConnection socksConnection) throws UnknownHostException,
			IOException {
		logger.info("OnionProxyConnection started");
		this.host = host;
		this.port = port;
		this.clientConnection = socksConnection;
		this.tcpConnectionProxyProperty = socksConnection.getTCPConnectionProxyProperty();

		SimpleCoreClient directoryNode = new SimpleCoreClient(InetAddress.getByName(tcpConnectionProxyProperty
				.getDirectoryNodeHostName()), tcpConnectionProxyProperty.getDirectoryNodePort());

		try {
			nodeChain = directoryNode.getNodeChain();
		} catch (IOException e) {
			throw new IOException(String.format("Error getting the node chain: %s.", e));

		} catch (InvalidResultException e) {
			throw new IOException(String.format("Error getting the node chain: Invalid result: %s.", e));

		} catch (InternalServerErrorException e) {
			throw new IOException(String.format("Error getting the node chain: InternalServerError %s.", e));

		} catch (at.onion.directoryNodeClient.NotEnoughNodesException e) {
			throw new IOException(String.format("Error getting the node chain, not enough nodes: %s.", e));
		}

		NodeInfo first = nodeChain.getNodes()[0];

		logger.info(String.format("Try to connect to first socket: %s.", first));

		if (first.getHostname() == null || first.getHostname().length() < 0)
			throw new IOException("Error connecting to first node: connection data invalid.");
		try {
			this.destinationSocket = createEncryptedSocket(first.getHostname(), first.getPort());
		} catch (KeyManagementException e) {
			throw new IOException(String.format("Error connection to first node, key management exception: %s.", e));
		} catch (UnrecoverableKeyException e) {
			throw new IOException(String.format("Error connection to first node, unrecoverable key exception: %s.", e));
		} catch (NoSuchAlgorithmException e) {
			throw new IOException(String.format("Error connection to first node, no such algorithm exception: %s.", e));
		} catch (KeyStoreException e) {
			throw new IOException(String.format("Error connection to first node, key store exception: %s.", e));
		} catch (NoSuchProviderException e) {
			throw new IOException(String.format("Error connection to first node, no such provider exception: %s.", e));
		}

	}

	@Override
	public void startConnectionAndThread() throws IOException {
		this.destinationConnection = new TCPConnection(null, destinationSocket, Socks5Metadata.proxySocketTimeout,
				false, this.tcpConnectionProxyProperty);

		thread = new Thread(this);
		thread.start();
	}

	public byte[] encryptNode(PublicKey pubKey, byte[] payLoad, NodeInfo nodeInfo) throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, IOException {
		NodeChainMessage msg = new NodeChainMessage();
		msg.setPayload(payLoad);
		msg.setHeader(nodeInfo);
		msg.setClientPublicKey(getKeyPair().getPublic());

		return encrypt(pubKey, msg);
	}

	public byte[] decryptNode(byte[] payLoad, NodeInfo nodeInfo) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException, ClassNotFoundException, IllegalBlockSizeException,
			BadPaddingException, IOException {
		return CryptoUtils.decrypt(payLoad);
	}

	public void sendToDestination(byte[] message) throws IOException {

		logger.info(new String(message));

		List<NodeInfo> nodeChain = Arrays.asList(this.nodeChain.getNodes());
		Collections.reverse(nodeChain);

		try {
			message = encryptNode((PublicKey) nodeChain.get(0).getPublicKey(), message, new NodeInfo(this.host,
					this.port, null));
			for (int i = 1; i < nodeChain.size(); i++) {
				message = encryptNode((PublicKey) nodeChain.get(i).getPublicKey(), message, nodeChain.get(i - 1));
			}

		} catch (Exception e) {
			throw new IOException(String.format("Error sending to first node: %s.", e));
		}

		ObjectOutputStream stream = new ObjectOutputStream(this.destinationConnection.getOutputStream());
		stream.writeObject(message);

		// /this.destinationConnection.send(message);
	}

	public void sendToClient(byte[] message) throws IOException {

		List<NodeInfo> nodeChain = Arrays.asList(this.nodeChain.getNodes());

		try {
			for (NodeInfo n : nodeChain) {
				message = decryptNode(message, n);
			}
		} catch (Exception e) {
			throw new IOException(String.format("Error sending to first node: %s.", e));
		}

		logger.info("got:" + new String(message));

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
