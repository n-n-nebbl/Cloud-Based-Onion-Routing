package at.onion.commons;

import static at.onion.commons.CryptoUtils.generateSecretAESKey;
import static at.onion.commons.CryptoUtils.getKeyPair;
import static at.onion.commons.CryptoUtils.unwrapSecretKey;
import static at.onion.commons.CryptoUtils.wrapSecretKey;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import at.onion.commons.ServerUtils.ConnectionHandler;

public class KeyExchangeConnectionHandler implements ConnectionHandler {

	private Socket		socket;
	private KeyPair		keyPair;
	private Key			otherPublicKey;
	private SecretKey	sessionKey;
	private ConnectionHandler		command;

	/**
	 * Used by clients
	 */
	public KeyExchangeConnectionHandler() {
	}

	/**
	 * Used by servers
	 */
	public KeyExchangeConnectionHandler(ConnectionHandler command) {
		this.command = command;
	}

	@Override
	public void execute(Socket socket) {
		if (!hasCommand()) {
			return;
		}
		try {
			checkKeyPair();
			
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

			oos.writeObject(keyPair.getPublic());
			otherPublicKey = (Key) ois.readObject();

			sessionKey = generateSecretAESKey(256);
			byte[] encryptedSessionKey = wrapSecretKey(otherPublicKey, sessionKey);
			oos.writeObject(encryptedSessionKey);

			this.command.execute(socket);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	public void connect(String hostname, int port) throws UnknownHostException, IOException, NoSuchAlgorithmException,
			NoSuchProviderException, ClassNotFoundException, InvalidKeyException, NoSuchPaddingException {
		checkKeyPair();
		
		socket = new Socket(hostname, port);

		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

		otherPublicKey = (Key) ois.readObject();
		oos.writeObject(keyPair.getPublic());

		byte[] encryptedSessionKey = (byte[]) ois.readObject();
		sessionKey = unwrapSecretKey(keyPair.getPrivate(), encryptedSessionKey);
	}

	private void checkKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
		if(this.keyPair == null) {
			this.keyPair = getKeyPair();
		}
	}

	public Key getOtherPublicKey() {
		return otherPublicKey;
	}

	public SecretKey getSessionKey() {
		return sessionKey;
	}

	public Socket getSocket() {
		return socket;
	}

	public boolean hasCommand() {
		return (this.command != null) ? true : false;
	}
}
