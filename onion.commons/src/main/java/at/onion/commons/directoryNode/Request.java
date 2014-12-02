package at.onion.commons.directoryNode;

import java.io.Serializable;

import at.onion.commons.NodeInfo;

public class Request implements Serializable{
	
	private static final long	serialVersionUID	= 7849858821547978124L;
	private RequestType requestType;
	private NodeInfo newNode;
	
	public RequestType getRequestType() {
		return requestType;
	}
	
	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}
	
	public NodeInfo getNewNode() {
		return newNode;
	}
	
	public void setNewNode(NodeInfo newNode) {
		this.newNode = newNode;
	}
	

}
