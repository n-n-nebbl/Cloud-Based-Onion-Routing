package at.onion.directorynode.coreClient;

import java.io.IOException;
import java.net.UnknownHostException;

import at.onion.commons.NodeChainInfo;
import at.onion.commons.NodeInfo;

public interface CoreClient {
	
	public NodeChainInfo getNodeChain()
				throws UnknownHostException, IOException, InvalidResultException;
	
	public String addNode(NodeInfo node)
			throws UnknownHostException, IOException, InvalidResultException;
	
}
