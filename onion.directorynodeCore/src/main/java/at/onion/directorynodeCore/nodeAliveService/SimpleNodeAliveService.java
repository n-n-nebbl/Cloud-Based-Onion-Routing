package at.onion.directorynodeCore.nodeAliveService;

import at.onion.directorynodeCore.nodeInstanceService.NodeInstanceService;
import at.onion.directorynodeCore.nodeManagementService.NodeManagementService;

public class SimpleNodeAliveService implements NodeAliveService{
	
	private NodeManagementService nodeManagementService;
	private NodeInstanceService nodeInstanceService;
	private int nodeOfflineThresholdInMS;
	
	public void setNodeManagementService(NodeManagementService nodeManagementService) {
		this.nodeManagementService = nodeManagementService;
	}

	public void setNodeInstanceService(NodeInstanceService nodeInstanceService) {
		this.nodeInstanceService = nodeInstanceService;
	}
	
	public void setNodeOfflineThresholdInMS(int nodeOfflineThresholdInMS) {
		this.nodeOfflineThresholdInMS = nodeOfflineThresholdInMS;
	}

}
