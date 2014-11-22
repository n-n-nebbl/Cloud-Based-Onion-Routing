package at.onion.commons.directoryNode;

import java.io.Serializable;

import at.onion.commons.NodeChainInfo;

public class Response implements Serializable{
	
	private static final long	serialVersionUID	= -7661975815818048782L;
	private ResponseStatus responseStatus;
	private NodeChainInfo nodeChain;
	
	public ResponseStatus getResponseStatus() {
		return responseStatus;
	}
	
	public void setResponseStatus(ResponseStatus responseStatus) {
		this.responseStatus = responseStatus;
	}
	
	public NodeChainInfo getNodeChain() {
		return nodeChain;
	}
	
	public void setNodeChain(NodeChainInfo nodeChain) {
		this.nodeChain = nodeChain;
	}

}
