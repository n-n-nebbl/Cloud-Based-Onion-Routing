package at.onion.directorynodeCore.chainGernatorService;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import at.onion.commons.CryptoUtils;
import at.onion.commons.NodeChainInfo;
import at.onion.commons.NodeInfo;
import at.onion.directorynodeCore.domain.Node;
import at.onion.directorynodeCore.nodeManagementService.NodeManagementService;

public class SimpleChainGeneratorService implements ChainGenerationService {
	
	private NodeManagementService nodeManagementService;
	
	@Value("${nodeChain.elementCount}")
	private int nodeChainElementCount;

    @Value("${nodeChain.testModeSendCorrupedNodeInEverySecondRequest}")
    private boolean testModeForDirtyChains;

    private int requestCount = 0;

    private Random randomGenerator;
    private Logger logger;
	
	public void setNodeManagementService(NodeManagementService nodeManagementService) {
		this.nodeManagementService = nodeManagementService;
	}
	
	public void setNodeChainElementCount(int nodeChainElementCount){
		this.nodeChainElementCount = nodeChainElementCount;
	}
	
	public SimpleChainGeneratorService(){
		randomGenerator = new Random();
		logger = LoggerFactory.getLogger(this.getClass());
	}

	@Override
	public NodeChainInfo getNodeChain() 
			throws NotEnoughNodesException {
        requestCount++;
		List<Node> nodeList = getRandomSubsetFromNodeList(getCompleteNodeList());		
		List<NodeInfo> nodeInfoList = getNodeInfoListForNodeList(nodeList);
        if (testModeForDirtyChains) {
            nodeInfoList = curruptEverySecondChainForTesting(nodeInfoList);
        }
        logNodeChain(nodeInfoList);
        return getNodeChainForNodeList(nodeInfoList);
	}

    private NodeChainInfo getNodeChainForNodeList(List<NodeInfo> nodeInfoList) {
        NodeChainInfo nodeChain = new NodeChainInfo();
        NodeInfo[] nodeArray = getNodeArrayForList(nodeInfoList);
        nodeChain.setNodes(nodeArray);
        return nodeChain;
    }
	
	private List<Node> getCompleteNodeList()
			throws NotEnoughNodesException {
		List<Node> comleteNodeList = nodeManagementService.getNodeList();
		if(comleteNodeList.size() < nodeChainElementCount) throw new NotEnoughNodesException(); 
		return comleteNodeList;
	}
	
	private NodeInfo[] getNodeArrayForList(List<NodeInfo> nodeList) {
		NodeInfo[] retArray = new NodeInfo[nodeList.size()];
		for (int i = 0; i < nodeList.size(); i++) {
			retArray[i] = nodeList.get(i);
		}
		return retArray;
	}
	
	private List<NodeInfo> getNodeInfoListForNodeList(List<Node> nodeList) {
		List<NodeInfo> nodeInfoList = new ArrayList<NodeInfo>();
		for (int i = 0; i < nodeList.size(); i++) {
			nodeInfoList.add(getNodeFromNodeInfo(nodeList.get(i)));
		}
		return nodeInfoList;
	}
	
	private NodeInfo getNodeFromNodeInfo(Node node) {
		NodeInfo nodeInfo = new NodeInfo();
		nodeInfo.setHostname(node.getIpAddress().getHostAddress());
		nodeInfo.setPort(node.getPort());
		nodeInfo.setPublicKey(node.getPublicKey());
		return nodeInfo;
	}
	
	private List<Node> getRandomSubsetFromNodeList(List<Node> nodeList) {
		ArrayList<Node> retList = new ArrayList<Node>();
		for(int i = 0; i < nodeChainElementCount; i++){
			int elementId = randomGenerator.nextInt(nodeList.size());
			Node element = nodeList.remove(elementId);
			retList.add(element);
		}
		return retList;		
	}
	
	private void logNodeChain(List<NodeInfo> nodeInfoList) {
		if(!logger.isDebugEnabled())return;
		String logString = "Create NodeChain with nodes: ";
		for(int i = 0; i < nodeInfoList.size(); i++){
			NodeInfo nodeInfo = nodeInfoList.get(i);
			logString += "[" + nodeInfo.getHostname() + ":" + nodeInfo.getPort() + "]";
		}
		logger.debug(logString);
	}

    private List<NodeInfo> curruptEverySecondChainForTesting(List<NodeInfo> nodeInfoList) {
        if (requestIsOdd()) {
            int lastNodeIndex = nodeInfoList.size();
            int lastNodePos = lastNodeIndex - 1;
            logger.debug("Corruping node {} in nodechaing", lastNodeIndex);
            nodeInfoList.get(lastNodePos).setPort(22);
        }
        return nodeInfoList;
    }

    private Boolean requestIsOdd() {
        return ( (requestCount % 2) == 1);
    }

}
