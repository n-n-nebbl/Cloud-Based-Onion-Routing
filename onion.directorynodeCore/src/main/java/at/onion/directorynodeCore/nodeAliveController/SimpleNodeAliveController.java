package at.onion.directorynodeCore.nodeAliveController;

import java.net.SocketException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;

import at.onion.directorynodeCore.domain.Node;
import at.onion.directorynodeCore.nodeInstanceService.NodeInstanceService;
import at.onion.directorynodeCore.nodeManagementService.NodeManagementService;

public class SimpleNodeAliveController{
	
	private NodeManagementService nodeManagementService;
	private NodeInstanceService nodeInstanceService;
	private int nodeOfflineThresholdInMS;
	private int alivePackagePort;
	private int minimumNodeInstances;
	
	private long nowTime;
	
	private AlivePackageListener alivePackageListener;
	
	public void setNodeManagementService(NodeManagementService nodeManagementService) {
		this.nodeManagementService = nodeManagementService;
	}

	public void setNodeInstanceService(NodeInstanceService nodeInstanceService) {
		this.nodeInstanceService = nodeInstanceService;
	}
	
	public void setNodeOfflineThresholdInMS(int nodeOfflineThresholdInMS) {
		this.nodeOfflineThresholdInMS = nodeOfflineThresholdInMS;
	}

	public void setAlivePackagePort(int alivePackagePort) {
		this.alivePackagePort = alivePackagePort;
	}
	
	public void setMinimumNodeInstances(int minimumNodeInstances) {
		this.minimumNodeInstances = minimumNodeInstances;
	}

	public void startAlivePackageServer() throws SocketException{
		if(alivePackageListener != null)return;
		alivePackageListener = new AlivePackageListener(alivePackagePort, nodeManagementService);
		new Thread(alivePackageListener).start();
	}
	
	public void stopAlivePackageServer(){
		if(alivePackageListener == null)return;
		alivePackageListener.shutdown();
		alivePackageListener = null;
	}
	
	public void execute(){
		checkNodesForTimeout();
		checkForMinimumNodeInstances();
	}
	
	private void checkNodesForTimeout(){
		setNowTime();
		Iterator<Node> nodeIterator = nodeManagementService.getNodeList().iterator();
		while(nodeIterator.hasNext()){
			Node node = nodeIterator.next();
			checkSingleNodeForTimeout(node);
		}		
	}
	
	private void checkSingleNodeForTimeout(Node node){
		if(nodeIsOverOfflineThreshold(node)){
			nodeManagementService.removeNode(node);
			nodeInstanceService.shutdownNodeInstaceOwnerForNode(node);
		}			
	}
	
	private Boolean nodeIsOverOfflineThreshold(Node node){
		Date nodeDate = node.getLastAliveTimestamp();
		long timeDiffInMS = nowTime - nodeDate.getTime();
		return (timeDiffInMS >= nodeOfflineThresholdInMS);		
	}
	
	private void setNowTime(){
		Date nowDate = new Date();
		nowTime = nowDate.getTime();
	}
	
	private void checkForMinimumNodeInstances(){
		List<Node> nodeList = nodeManagementService.getNodeList();
		int missingInstanceCount = minimumNodeInstances - nodeList.size();
		while(missingInstanceCount > 0){
			nodeInstanceService.startNewNodeInstance();
			missingInstanceCount--;
		}
	}
}
