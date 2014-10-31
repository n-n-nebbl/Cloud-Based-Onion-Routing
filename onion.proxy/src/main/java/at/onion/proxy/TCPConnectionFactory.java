package at.onion.proxy;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class TCPConnectionFactory {

	public synchronized static TCPConnection getInstance(Class<? extends TCPConnection> objectClass,
			List<TCPConnection> connectionList, Socket s) throws IOException {

		if (objectClass.equals(Socks5TCPConnection.class)) {
			return new Socks5TCPConnection(connectionList, s);
		}

		return null;
	}
}
