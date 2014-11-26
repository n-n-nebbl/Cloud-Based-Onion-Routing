package at.onion.directorynodeCore.nodeManagementService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
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
		Node node = new Node();
		UUID uuid = UUID.randomUUID();
		logger.error("Hallo" + nodeInfo.getHostname());
		InetAddress inetAddress = InetAddress.getByName(nodeInfo.getHostname());
		node.setIpAddress(inetAddress);
		node.setPort(nodeInfo.getPort());
		node.setPublicKey(nodeInfo.getPublicKey());
		node.setUuid(uuid);
		System.out.println("ADD:" + uuid.toString());
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
	
}
