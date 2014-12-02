package at.onion.chainnode;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Waits for new connections and starts a ConnectionThread for each connection
 * 
 * @author NEBEL
 */
public class ListeningThread implements Runnable {

	private Logger			logger	= LoggerFactory.getLogger(getClass());
	private ServerSocket	ss;
	private List<Socket>	sockets;

	public ListeningThread(ServerSocket ss) {
		this.ss = ss;
		this.sockets = new ArrayList<>();
	}

	/**
	 * stop listening
	 */
	public void stopListening() {
		try {
			if (ss != null && ss.isClosed()) {
				ss.close();
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				// wait for new connection
				Socket s = ss.accept();
				// remember connection
				sockets.add(s);
				// start new ConnectionThread for this connection
				new Thread(new ConnectionThread(s)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// close all connections
		for (Socket s : sockets) {
			try {
				if (s.isConnected() || !s.isClosed()) {
					s.close();
				}
			} catch (Exception e) {
			}
		}
	}
}
