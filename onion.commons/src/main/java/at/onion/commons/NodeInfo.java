package at.onion.commons;

import java.io.Serializable;
import java.security.Key;

public class NodeInfo implements Serializable {

	private static final long	serialVersionUID	= 1L;
	private String				hostname			= "";
	private int					port				= 0;
	private Key					publicKey;

	public NodeInfo(String hostname, int port, Key publicKey) {
		this.hostname = hostname;
		this.port = port;
		this.publicKey = publicKey;
	}

	public NodeInfo() {
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
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

	public boolean isTarget() {
		return publicKey == null;
	}

	@Override
	public String toString() {
		return String.format("NodeInfo{%s:%d, key size: %d, key algo: %s}", this.hostname, this.port,
				this.publicKey.getEncoded().length, this.publicKey.getAlgorithm().toString());
	}
}
