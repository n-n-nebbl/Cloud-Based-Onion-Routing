package at.onion.chainnode;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyManagementException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static at.onion.commons.CryptoUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import at.onion.commons.NodeChainMessage;
import at.onion.commons.NodeInfo;

public class Tests {

	private Logger	logger	= LoggerFactory.getLogger(getClass());

	@Test
	public void testBlockEncryption() throws IOException, InvalidKeyException, NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException, ClassNotFoundException, IllegalBlockSizeException,
			BadPaddingException {
		NodeChainMessage msg = new NodeChainMessage();
		NodeInfo header = new NodeInfo("test", 1234, null);
		msg.setHeader(header);
		String start = "blockEncryptionTestMessage";
		msg.setPayload(serialize(start));

		KeyPair pair = getKeyPair();
		msg.setClientPublicKey(pair.getPublic());

		NodeChainMessage msg2 = decryptMessage(encrypt(pair.getPublic(), msg));

		String end = (String) deserialize(msg2.getPayload());

		logger.info("Original Message: " + start);
		logger.info("After encryption/decryption: " + end);

		assertEquals(start, end);
	}

	private void print(byte[] b) {
		StringBuffer temp = new StringBuffer("");
		for (int i = 0; i < b.length; i++) {
			temp.append(b[i] + " ");
		}
		logger.info(temp.toString());
	}

	@Test
	public void simpleSendingTest() throws IOException, NoSuchAlgorithmException, NoSuchProviderException,
			UnrecoverableKeyException, KeyManagementException, KeyStoreException, InterruptedException,
			InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		ConnectionService connServ = new ConnectionService();
		Key publicKey = getKeyPair().getPublic();

		NodeChainMessage msg = new NodeChainMessage();
		final String sampleCall = "GET /sampleCall.asx HTTP/1.1";
		msg.setPayload(sampleCall.getBytes());
		NodeInfo header = new NodeInfo("localhost", 9999, null);
		msg.setHeader(header);
		msg.setClientPublicKey(publicKey);

		byte[] encrypted = encrypt(publicKey, msg);

		// simple target server that checks if sent message is arriving and
		// readable
		new Thread() {
			public void run() {
				try {
					ServerSocket ss = new ServerSocket(9999);
					Socket s = ss.accept();
					byte[] buffer = new byte[100];
					int read;
					String line = "";
					while ((read = s.getInputStream().read(buffer)) != -1) {
						line = new String(buffer, 0, read);
						logger.info("server: " + line);
					}

					s.close();
					ss.close();

					assertEquals(sampleCall, line);
				} catch (IOException e) {
					e.printStackTrace();
					assertTrue(false);
				}

			};
		}.start();

		connServ.startListening(9000);

		Thread.sleep(1000);

		try {
			Socket s = createEncryptedSocket("localhost", 9000);
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			oos.writeObject(encrypted);
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}

		Thread.sleep(2000);

		connServ.stopListening(9000);
	}

	@Test
	public void sendingTest() throws NoSuchAlgorithmException, NoSuchProviderException, IOException,
			InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException,
			UnrecoverableKeyException, KeyManagementException, KeyStoreException, InterruptedException {
		ConnectionService connServ = new ConnectionService();
		Key publicKey = getKeyPair().getPublic();

		// Original message, that will be sent to the target
		NodeChainMessage msg = new NodeChainMessage();
		msg.setPayload("GET /onion.testservice/ HTTP/1.1\nHost: localhost:8080\n\n".getBytes());
		NodeInfo header = new NodeInfo("localhost", 8080, null);
		msg.setHeader(header);
		msg.setClientPublicKey(publicKey);

		NodeChainMessage outerMsg = new NodeChainMessage();
		outerMsg.setPayload(encrypt(publicKey, msg));
		header = new NodeInfo("localhost", 9003, publicKey);
		outerMsg.setHeader(header);
		outerMsg.setClientPublicKey(publicKey);

		byte[] encrypted = encrypt(publicKey, outerMsg);

		// target socket, that reads one message and prints it
		new Thread() {
			public void run() {
				try {
					ServerSocket ss = createEncryptedServerSocket(9998);
					Socket s = ss.accept();
					logger.info("try to get message");
					logger.info(deserialize((byte[]) new ObjectInputStream(s.getInputStream()).readObject())
							.toString());
					s.close();
					ss.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			};
		}.start();

		connServ.startListening(9002);
		connServ.startListening(9003);

		Thread.sleep(1000);

		Socket s = null;
		try {
			s = createEncryptedSocket("localhost", 9002);
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			logger.info("sending");
			oos.writeObject(encrypted);
			logger.info("message sent");
			ObjectInputStream oin = new ObjectInputStream(s.getInputStream());

			while (true) {
				byte[] result = (byte[]) oin.readObject();
				byte[] temp = decrypt(result);
				temp = decrypt(temp);
				logger.info("client: " + new String(temp));
			}
		} catch (EOFException e) {
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				s.close();
			} catch (Exception e) {
			}
		}

		Thread.sleep(1000);

		connServ.stopListening(9002);
		connServ.stopListening(9003);
	}

}
