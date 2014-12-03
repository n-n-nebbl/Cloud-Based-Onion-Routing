package at.onion.directorynodeCore.nodeInstanceService;

import at.onion.directorynodeCore.domain.Node;

public interface NodeInstanceService {
	
	public void startNewNodeInstance();
	
	public void shutdownNodeInstaceOwnerForNode(Node node);
}
