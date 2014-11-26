package at.onion.directorynodeCore.chainGernatorService;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import at.onion.commons.CryptoUtils;
import at.onion.commons.NodeChainInfo;
import at.onion.commons.NodeInfo;
import at.onion.directorynodeCore.domain.Node;
import at.onion.directorynodeCore.nodeManagementService.NodeManagementService;

public class SimpleChainGeneratorService implements ChainGenerationService {
	
	private NodeManagementService nodeManagementService;
	private int nodeChainElementCount;
    private Random randomGenerator;
	
	public void setNodeManagementService(NodeManagementService nodeManagementService) {
		this.nodeManagementService = nodeManagementService;
	}
	
	public void setNodeChainElementCount(int nodeChainElementCount){
		this.nodeChainElementCount = nodeChainElementCount;
	}
	
	public SimpleChainGeneratorService(){
		randomGenerator = new Random();
	}

	@Override
	public NodeChainInfo getNodeChain() 
			throws NotEnoughNodesException{
		NodeChainInfo nodeChain = new NodeChainInfo();
		List<Node> nodeList = getRandomSubsetFromNodeList(getCompleteNodeList());	
		List<NodeInfo> nodeInfoList = getNodeInfoListForNodeList(nodeList);
		NodeInfo[] nodeArray = getNodeArrayForList(nodeInfoList);
		nodeChain.setNodes(nodeArray);
		return nodeChain;
	}
	
	public List<Node> getCompleteNodeList() 
			throws NotEnoughNodesException{
		List<Node> comleteNodeList = nodeManagementService.getNodeList();
		if(comleteNodeList.size() < nodeChainElementCount) throw new NotEnoughNodesException(); 
		return comleteNodeList;
	}
	
	public NodeInfo[] getNodeArrayForList(List<NodeInfo> nodeList){
		NodeInfo[] retArray = new NodeInfo[nodeList.size()];
		for(int i = 0; i < nodeList.size(); i++){
			retArray[i] = nodeList.get(i);
		}
		return retArray;
	}
	
	public List<NodeInfo> getNodeInfoListForNodeList(List<Node> nodeList){
		List<NodeInfo> nodeInfoList = new ArrayList<NodeInfo>();
		for(int i = 0; i < nodeInfoList.size(); i++){
			nodeInfoList.add(getNodeFromNodeInfo(nodeList.get(i)));
		}
		return nodeInfoList;
	}
	
	public NodeInfo getNodeFromNodeInfo(Node node){
		NodeInfo nodeInfo = new NodeInfo();
		nodeInfo.setHostname(node.getIpAddress().toString());
		nodeInfo.setPort(node.getPort());
		nodeInfo.setPublicKey(node.getPublicKey());
		return nodeInfo;
	}
	
	private List<Node> getRandomSubsetFromNodeList(List<Node> nodeList){
		ArrayList<Node> retList = new ArrayList<Node>();
		for(int i = 0; i < nodeChainElementCount; i++){
			int elementId = randomGenerator.nextInt(nodeList.size());
			Node element = nodeList.remove(elementId);
			retList.add(element);
		}
		return retList;		
	}

}
