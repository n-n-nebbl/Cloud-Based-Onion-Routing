package at.onion.proxy;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import at.onion.proxy.proxyconnection.FilterTestProxyConnection;
import at.onion.proxy.proxyconnection.ProxyConnection;
import at.onion.proxy.proxyconnection.TestProxyConnection;
import at.onion.proxy.socks5.Socks5TCPConnection;

public class ProxyFactory {

	public synchronized static TCPConnection getConnection(Class<? extends TCPConnection> objectClass,
			Class<? extends ProxyConnection> proxyConnectionClass, List<TCPConnection> connectionList, Socket s)
			throws IOException {

		if (objectClass.equals(Socks5TCPConnection.class)) {
			return new Socks5TCPConnection(proxyConnectionClass, connectionList, s);
		}

		return null;
	}

	public synchronized static ProxyConnection getProxyConnection(Class<? extends ProxyConnection> objectClass,
			String destinationHost, int destinationPort, TCPConnection clientConnection) throws IOException {

		if (objectClass.equals(TestProxyConnection.class)) {
			return new TestProxyConnection(destinationHost, destinationPort, clientConnection);
		}
		if (objectClass.equals(FilterTestProxyConnection.class)) {
			return new FilterTestProxyConnection(destinationHost, destinationPort, clientConnection);
		}

		return null;
	}
}
