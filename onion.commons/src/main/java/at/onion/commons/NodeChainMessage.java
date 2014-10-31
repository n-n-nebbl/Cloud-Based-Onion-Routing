package at.onion.commons;

import java.io.Serializable;
import java.security.Key;

public class NodeChainMessage implements Serializable {

	private static final long	serialVersionUID	= 1L;
	private byte[]				payload;
	private NodeInfo			header;
	private Key					clientPublicKey;

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}

	public NodeInfo getHeader() {
		return header;
	}

	public void setHeader(NodeInfo header) {
		this.header = header;
	}

	public Key getClientPublicKey() {
		return clientPublicKey;
	}

	public void setClientPublicKey(Key clientPublicKey) {
		this.clientPublicKey = clientPublicKey;
	}
}
