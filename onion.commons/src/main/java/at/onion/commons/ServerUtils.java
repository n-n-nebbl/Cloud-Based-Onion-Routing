package at.onion.commons;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class ServerUtils {

	private Map<Integer, ListeningThread>	threads	= new HashMap<>();

	/**
	 * Starts a thread which will listen on the given port. For every accepted
	 * connection on that port the method "doSomething" will be called of the
	 * commandThread parameter.
	 * 
	 * @param port
	 *            accepting connections on this port
	 * @param commandThread
	 *            executing this command for every connection accepted
	 * @throws IOException
	 */
	public void startAcceptingConnections(int port, ConnectionHandler commandThread) throws IOException {
		ListeningThread listener = new ListeningThread(port, commandThread);
		new Thread(listener).start();
		threads.put(port, listener);
	}

	/**
	 * stop listening thread on the following port
	 * 
	 * @param port
	 */
	public void stopAcceptionConnections(int port) {
		threads.get(port).stopThread();
	}

	private class ListeningThread implements Runnable {

		private ConnectionHandler			command;
		private ServerSocket	ss;

		public ListeningThread(int port, ConnectionHandler command) throws IOException {
			this.command = command;
			ss = new ServerSocket(port);
		}

		@Override
		public void run() {
			try {
				while (true) {
					Socket socket = ss.accept();
					Thread t = new Thread(new ConnectionThread(command, socket));
					t.start();
				}
			} catch (SocketException ex) {
				// occures if stopThread is called
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void stopThread() {
			if (ss == null) return;
			if (ss.isClosed()) return;
			try {
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private class ConnectionThread implements Runnable {

		private ConnectionHandler	command;
		private Socket	socket;

		public ConnectionThread(ConnectionHandler command, Socket socket) {
			this.command = command;
			this.socket = socket;
		}

		@Override
		public void run() {
			command.execute(socket);
		}
	}

	public interface ConnectionHandler {

		public void execute(Socket socket);
	}
}
