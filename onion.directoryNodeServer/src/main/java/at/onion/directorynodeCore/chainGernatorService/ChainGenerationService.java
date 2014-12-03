package at.onion.directorynodeCore.chainGernatorService;

import at.onion.commons.NodeChainInfo;

public interface ChainGenerationService {
	
	public NodeChainInfo getNodeChain() 
			throws NotEnoughNodesException;
	
}
