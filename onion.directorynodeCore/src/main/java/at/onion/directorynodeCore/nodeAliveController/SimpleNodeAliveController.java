package at.onion.directorynodeCore.nodeAliveController;

import at.onion.directorynodeCore.nodeInstanceService.NodeInstanceService;
import at.onion.directorynodeCore.nodeManagementService.NodeManagementService;

public class SimpleNodeAliveController{
	
	private NodeManagementService nodeManagementService;
	private NodeInstanceService nodeInstanceService;
	private int nodeOfflineThresholdInMS;
	private int nodeOfflineScanIntervallInMS;
	private int alivePackagePort;
	
	public void setNodeManagementService(NodeManagementService nodeManagementService) {
		this.nodeManagementService = nodeManagementService;
	}

	public void setNodeInstanceService(NodeInstanceService nodeInstanceService) {
		this.nodeInstanceService = nodeInstanceService;
	}
	
	public void setNodeOfflineThresholdInMS(int nodeOfflineThresholdInMS) {
		this.nodeOfflineThresholdInMS = nodeOfflineThresholdInMS;
	}

	public void setNodeOfflineScanIntervallInMS(int nodeOfflineScanIntervallInMS) {
		this.nodeOfflineScanIntervallInMS = nodeOfflineScanIntervallInMS;
	}

	public void setAlivePackagePort(int alivePackagePort) {
		this.alivePackagePort = alivePackagePort;
	}

	public void startAlivePackageServer(){
		
	}
	
	public void startAliveWatcher(){
		
	}
}
