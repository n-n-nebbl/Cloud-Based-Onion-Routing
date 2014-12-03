package at.onion.proxy;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import at.onion.proxy.proxyconnection.FilterTestProxyConnection;
import at.onion.proxy.proxyconnection.OnionProxyConnection;
import at.onion.proxy.proxyconnection.ProxyConnection;
import at.onion.proxy.proxyconnection.TestProxyConnection;
import at.onion.proxy.socks4.Socks4TCPConnection;
import at.onion.proxy.socks5.Socks5TCPConnection;

public class ProxyFactory {

	public static List<Class<? extends ProxyConnection>> getProxyTypes() {
		List<Class<? extends ProxyConnection>> list = new ArrayList<Class<? extends ProxyConnection>>();

		list.add(OnionProxyConnection.class);
		list.add(TestProxyConnection.class);
		list.add(FilterTestProxyConnection.class);

		return list;
	}

	public synchronized static TCPConnection getConnection(Class<? extends TCPConnection> objectClass,
			Class<? extends ProxyConnection> proxyConnectionClass, List<TCPConnection> connectionList, Socket s,
			TCPConnectionProxyProperty proxyProperty) throws IOException {

		if (objectClass.equals(Socks5TCPConnection.class)) {
			return new Socks5TCPConnection(proxyConnectionClass, connectionList, s, proxyProperty);
		}
		if (objectClass.equals(Socks4TCPConnection.class)) {
			return new Socks4TCPConnection(proxyConnectionClass, connectionList, s, proxyProperty);
		}

		return null;
	}

	public synchronized static ProxyConnection getProxyConnection(Class<? extends ProxyConnection> objectClass,
			String destinationHost, int destinationPort, TCPConnection clientConnection) throws IOException {

		if (objectClass.equals(TestProxyConnection.class)) {
			return new TestProxyConnection(destinationHost, destinationPort, clientConnection);
		}
		if (objectClass.equals(OnionProxyConnection.class)) {
			return new OnionProxyConnection(destinationHost, destinationPort, clientConnection);
		}
		if (objectClass.equals(FilterTestProxyConnection.class)) {
			return new FilterTestProxyConnection(destinationHost, destinationPort, clientConnection);
		}

		return null;
	}
}
