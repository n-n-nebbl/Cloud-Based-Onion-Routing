package at.onion.directorynodeCore.nodeManagementService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.onion.commons.NodeInfo;
import at.onion.directorynodeCore.domain.Node;

public class SimpleNodeManagementService implements NodeManagementService {
	
	private Logger logger;
	private HashMap<String, Node> nodeMap;
	
	public SimpleNodeManagementService(){
		logger = LoggerFactory.getLogger(this.getClass());
		nodeMap = new HashMap<String, Node>();
	}
	
	@Override
	public synchronized UUID addNodeAsNodeInfoAndGenerateUuid(NodeInfo nodeInfo) 
			throws UnknownHostException {
		Node node = getNodeForNodeInfo(nodeInfo);
		UUID uuid = node.getUuid();
		logNewNode(node);
		nodeMap.put(uuid.toString(), node);
		return uuid;
	}

	@Override
	public synchronized List<Node> getNodeList() {
		return new ArrayList<Node>(nodeMap.values());
	}

	@Override
	public synchronized void removeNode(Node node) {		
		String key = node.getUuid().toString();
		nodeMap.remove(key);
	}

	@Override
	public synchronized Node getNodeByUuid(UUID uuid) {
		return nodeMap.get(uuid.toString());
	}

	@Override
	public synchronized void updateNodeTimestampForUuid(UUID uuid) {
		Node node = getNodeByUuid(uuid);		
		if(node != null) node.setLastAliveTimestamp(new Date());
	}
	
	private void logNewNode(Node node){
		logger.debug("New node added: [" + node.getIpAddress() + ":" + node.getPort() + "]");
	}
	
	private Node getNodeForNodeInfo(NodeInfo nodeInfo) 
			throws UnknownHostException{
		Node node = new Node();
		InetAddress inetAddress = InetAddress.getByName(nodeInfo.getHostname());
		node.setIpAddress(inetAddress);
		node.setPort(nodeInfo.getPort());
		node.setPublicKey(nodeInfo.getPublicKey());
		node.setUuid(UUID.randomUUID());
		node.setLastAliveTimestamp(new Date());
		return node;
	}
	
}
