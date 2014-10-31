package at.onion.chainnode;

import java.io.IOException;
import java.net.ServerSocket;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.util.HashMap;
import java.util.Map;

import at.onion.commons.CryptoUtils;

public class ConnectionService {

	private Map<Integer, ListeningThread>	listener;

	public ConnectionService() {
		this.listener = new HashMap<>();
	}

	public void startListening(int port) throws UnrecoverableKeyException, KeyManagementException, KeyStoreException,
			NoSuchAlgorithmException, NoSuchProviderException, IOException {
		//get new encrypted serversocket
		ServerSocket ss = CryptoUtils.createEncryptedServerSocket(port);
		//prepare and start listening thread
		ListeningThread listeningThread = new ListeningThread(ss);
		listener.put(port, listeningThread);
		new Thread(listeningThread).start();
		System.out.println("node starting listening on port " + port);
	}

	public void stopListening(int port) {
		ListeningThread listeningThread = listener.remove(port);
		if (listeningThread != null) {
			listeningThread.stopListening();
		}
		System.out.println("node stop listening on port " + port);
	}

}
