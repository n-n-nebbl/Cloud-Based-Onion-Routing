package at.onion.directorynodeCore.nodeManagementService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.onion.commons.NodeInfo;
import at.onion.directorynodeCore.domain.Node;

public class SimpleNodeManagementService implements NodeManagementService {
	
	private Logger logger;
	
	public SimpleNodeManagementService(){
		logger = LoggerFactory.getLogger(this.getClass());
	}
	
	@Override
	public String addNodeAndReturnId(NodeInfo nodeInfo) 
			throws UnknownHostException {
		Node node = new Node();
		UUID uuid = UUID.randomUUID();
		logger.error("Hallo" + nodeInfo.getHostname());
		InetAddress inetAddress = InetAddress.getByName(nodeInfo.getHostname());
		node.setIpAddress(inetAddress);
		node.setPort(nodeInfo.getPort());
		node.setPublicKey(nodeInfo.getPublicKey());
		node.setUuid(uuid);
		//TODO: Add node
		return uuid.toString();
	}

	@Override
	public List<NodeInfo> getNodeList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeNode(Node node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Node getNodeByUuid(UUID uuid) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
