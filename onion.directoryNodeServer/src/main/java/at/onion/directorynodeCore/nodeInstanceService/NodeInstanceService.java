package at.onion.directorynodeCore.nodeInstanceService;

import at.onion.directorynodeCore.domain.Node;

public interface NodeInstanceService {

	void startNewNodeInstance() throws CloudConnectionException;

	public void shutdownNodeInstaceOwnerForNode(Node node) throws CloudConnectionException;
}
