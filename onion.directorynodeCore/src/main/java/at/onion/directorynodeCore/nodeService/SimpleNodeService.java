package at.onion.directorynodeCore.nodeService;

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.onion.commons.NodeInfo;
import at.onion.directorynodeCore.NodeListContainer;
import at.onion.directorynodeCore.domain.Node;

public class SimpleNodeService implements NodeService {
	
	private NodeListContainer nodeListContainer;
	private Logger logger;
	
	public void setNodeListContainer(NodeListContainer nodeListContainer) {
		this.nodeListContainer = nodeListContainer;
		logger = LoggerFactory.getLogger(this.getClass());
	}

	@Override
	public String addNodeAndReturnId(NodeInfo nodeInfo) {
		// TODO: Implement real function
		Node node = new Node();
		//InetAddress inetAddress = InetAddress.getByName(nodeInfo.getHostname());
		logger.debug("Add node");
		return "1234";
	}

	@Override
	public void setAliveForNode() {
		// TODO: Implement real function
		// TODO Auto-generated method stub

	}

	@Override
	public void removeDeadNodes() {
		// TODO: Implement real function
		// TODO Auto-generated method stub
	}
	
}
