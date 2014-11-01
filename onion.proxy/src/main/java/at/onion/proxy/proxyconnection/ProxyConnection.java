package at.onion.proxy.proxyconnection;

import java.io.IOException;

public interface ProxyConnection {

	// Starts the connection, should be implemented as Runnable and read
	public void startConnectionAndThread() throws IOException;

	public void sendToClient(byte[] message);

	public void sendToDestination(byte[] message);

	public void setStopped();

	public boolean isStopped();
}
