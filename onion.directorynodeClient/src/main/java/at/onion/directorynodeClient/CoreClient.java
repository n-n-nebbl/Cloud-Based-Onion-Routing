package at.onion.directorynodeClient;

import java.io.IOException;
import java.net.UnknownHostException;

import at.onion.commons.NodeChainInfo;
import at.onion.commons.NodeInfo;

public interface CoreClient {
	
	public NodeChainInfo getNodeChain()
				throws IOException, InvalidResultException, InternalServerErrorException, NotEnoughNodesException;
	
	public String addNode(NodeInfo node)
			throws IOException, InvalidResultException, InternalServerErrorException;
	
	public void closeConnection();
	
}
