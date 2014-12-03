package at.onion.proxy.proxyconnection;

import static at.onion.commons.CryptoUtils.createEncryptedSocket;
import static at.onion.commons.CryptoUtils.encrypt;
import static at.onion.commons.CryptoUtils.getKeyPair;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
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
import at.onion.proxy.socks5.Socks5Metadata;

public class OnionProxyConnection implements ProxyConnection, Runnable {
	protected Logger		logger					= LoggerFactory.getLogger(getClass());

	private TCPConnection	clientConnection		= null;
	private Thread			thread					= null;

	private Socket			destinationSocket		= null;
	private TCPConnection	destinationConnection	= null;
	private NodeChainInfo	nodeChain				= null;

	public OnionProxyConnection(String host, int port, TCPConnection socksConnection) throws UnknownHostException,
			IOException {

		this.clientConnection = socksConnection;

		SimpleCoreClient directoryNode = new SimpleCoreClient(InetAddress.getByName(socksConnection
				.getTCPConnectionProxyProperty().getDirectoryNodeHostName()), socksConnection
				.getTCPConnectionProxyProperty().getDirectoryNodePort());

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
				false, null);

		thread = new Thread(this);
		thread.start();
	}

	public byte[] encryptNode(byte[] payLoad, NodeInfo nodeInfo) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException,
			IOException {
		NodeChainMessage msg = new NodeChainMessage();
		msg.setPayload(payLoad);
		msg.setHeader(nodeInfo);
		msg.setClientPublicKey(getKeyPair().getPublic());

		return encrypt(nodeInfo.getPublicKey(), msg);
	}

	public byte[] decryptNode(byte[] payLoad, NodeInfo nodeInfo) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException, ClassNotFoundException, IllegalBlockSizeException,
			BadPaddingException, IOException {
		return CryptoUtils.decrypt(payLoad);
	}

	public void sendToDestination(byte[] message) throws IOException {

		List<NodeInfo> nodeChain = Arrays.asList(this.nodeChain.getNodes());
		Collections.reverse(nodeChain);

		try {
			for (NodeInfo n : nodeChain) {
				message = encryptNode(message, n);
			}
		} catch (Exception e) {
			throw new IOException(String.format("Error sending to first node: %s.", e));
		}

		this.destinationConnection.send(message);
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
