package at.onion.directorynodeCore.domain;

import java.net.InetAddress;

public class Node {
	private InetAddress ipAddress;
	private int port;
	private int lastAliveSignalInMS;
	
	public InetAddress getIpAddress() {
		return ipAddress;
	}
	
	public void setIpAddress(InetAddress ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getLastAliveSignalInMS() {
		return lastAliveSignalInMS;
	}

	public void setLastAliveSignalInMS(int lastAliveSignalInMS) {
		this.lastAliveSignalInMS = lastAliveSignalInMS;
	}
	
}
