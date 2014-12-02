package at.onion.commons;

import java.io.Serializable;

/**
 * This class is a simple DTO containing all information about the node-chain.
 * 
 * @author NEBEL
 *
 */
public class NodeChainInfo implements Serializable{
	
	private static final long	serialVersionUID	= 7235266733975334574L;
	private NodeInfo[] nodes;
	
	public NodeChainInfo() {
		this(3);
	}
	
	public NodeChainInfo(int nodecount) {
		this.nodes = new NodeInfo[nodecount];
	}
	
	public NodeInfo[] getNodes() {
		return nodes;
	}

	public void setNodes(NodeInfo[] nodes) {
		this.nodes = nodes;
	}
}
