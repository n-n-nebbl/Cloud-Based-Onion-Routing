package at.onion.commons;

/**
 * This class is a simple DTO containing all information about the node-chain.
 * 
 * @author NEBEL
 *
 */
public class NodeChainInfo {
	
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
