package at.onion.directorynodeCore.domain;

import java.net.InetAddress;
import java.security.Key;
import java.util.UUID;

public class Node {
	private UUID uuid;
	private InetAddress ipAddress;
	private int port;
	private Key publicKey;
	private int lastAliveSignalInMS;	

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	
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

	public Key getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(Key publicKey) {
		this.publicKey = publicKey;
	}
	
	public int getLastAliveSignalInMS() {
		return lastAliveSignalInMS;
	}

	public void setLastAliveSignalInMS(int lastAliveSignalInMS) {
		this.lastAliveSignalInMS = lastAliveSignalInMS;
	}
	
}
