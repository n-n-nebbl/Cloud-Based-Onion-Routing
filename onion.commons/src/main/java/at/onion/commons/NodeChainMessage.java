package at.onion.commons;

import java.io.Serializable;

public class NodeChainMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	private byte[] payload;
	
	private NodeInfo header;

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
}
