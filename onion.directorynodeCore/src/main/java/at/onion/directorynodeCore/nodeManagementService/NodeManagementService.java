package at.onion.directorynodeCore.nodeManagementService;

import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import at.onion.commons.CryptoUtils;
import at.onion.commons.NodeInfo;
import at.onion.directorynodeCore.domain.Node;

public interface NodeManagementService {
	
	public UUID addNodeAsNodeInfoAndGenerateUuid(NodeInfo node)
			throws UnknownHostException;
	
	public List<Node> getNodeList();
	
	public void removeNode(Node node);
	
	public Node getNodeByUuid(UUID uuid);	
	
	public void updateNodeTimestampForUuid(UUID uuid);
	
}
