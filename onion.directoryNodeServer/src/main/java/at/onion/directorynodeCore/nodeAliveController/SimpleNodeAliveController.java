package at.onion.directorynodeCore.nodeAliveController;

import java.net.SocketException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import at.onion.directorynodeCore.domain.Node;
import at.onion.directorynodeCore.nodeInstanceService.NodeInstanceService;
import at.onion.directorynodeCore.nodeManagementService.NodeManagementService;

public class SimpleNodeAliveController implements NodeAliveController{
	
	private NodeManagementService nodeManagementService;
	private NodeInstanceService nodeInstanceService;
	
	@Value("${nodeAlive.offlineThresholdInMS}")
	private int nodeOfflineThresholdInMS;
	
	@Value("${nodeAlive.port}")
	private int alivePackagePort;
	
	@Value("${nodeAlive.minimumInstances}")
	private int minimumNodeInstances;
	
	private long nowTime;	
	private AlivePackageListener alivePackageListener;
	private Logger logger;
	
	public SimpleNodeAliveController(){
		logger = LoggerFactory.getLogger(this.getClass());
	}
	
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
	
	public void executeAfterStartUp(){
		checkForMinimumNodeInstances();
	}
	
	@Override
	public void execute(){
		checkNodesForTimeout();
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
			logger.debug("Node timed out: [" + node.getIpAddress().toString() + ":" + node.getPort() + "]");
			checkForMinimumNodeInstances();
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
		int missingInstanceCount = getMissingNodeCount();
		while(missingInstanceCount > 0){
			nodeInstanceService.startNewNodeInstance();
			missingInstanceCount--;
		}
	}
	
	private int getMissingNodeCount(){
		List<Node> nodeList = nodeManagementService.getNodeList();
		int missingInstanceCount = minimumNodeInstances - nodeList.size();	
		
		if(missingInstanceCount > 0){
			logger.debug("Node count is " + missingInstanceCount + " node(s) under limit.");
			return missingInstanceCount;
		}else{
			return 0;
		}
	}
}