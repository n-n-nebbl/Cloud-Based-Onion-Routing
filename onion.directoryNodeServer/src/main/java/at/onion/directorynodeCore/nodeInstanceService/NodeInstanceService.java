package at.onion.directorynodeCore.nodeInstanceService;

import at.onion.directorynodeCore.domain.Node;

public interface NodeInstanceService {
	
	public void startNewNodeInstance()
            throws ClaudConnectionException;
	
	public void shutdownNodeInstaceOwnerForNode(Node node)
            throws ClaudConnectionException;
}
