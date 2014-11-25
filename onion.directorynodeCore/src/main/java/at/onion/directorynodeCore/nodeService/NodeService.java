package at.onion.directorynodeCore.nodeService;

import at.onion.commons.NodeInfo;

public interface NodeService {
	
	public String addNodeAndReturnId(NodeInfo node);
	
	public void setAliveForNode();
	
	public void removeDeadNodes();
}
