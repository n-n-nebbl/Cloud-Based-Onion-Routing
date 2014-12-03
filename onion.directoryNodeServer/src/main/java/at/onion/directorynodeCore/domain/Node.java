package at.onion.directorynodeCore.domain;

import java.net.InetAddress;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

public class Node {
	private UUID uuid;
	private InetAddress ipAddress;
	private int port;
	private Key publicKey;
	private Date lastAliveTimestamp;	

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

	public Date getLastAliveTimestamp() {
		return lastAliveTimestamp;
	}

	public void setLastAliveTimestamp(Date lastAliveTimestamp) {
		this.lastAliveTimestamp = lastAliveTimestamp;
	}
	
}
