package at.onion.directorynodeCore.chainGernatorService;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import at.onion.commons.CryptoUtils;
import at.onion.commons.NodeChainInfo;
import at.onion.commons.NodeInfo;
import at.onion.directorynodeCore.NodeListContainer;

public class SimpleChainGeneratorService implements ChainGenerationService {
	
	private NodeListContainer nodeListContainer;
	private int nodeChainElementCount;
    private Random randomGenerator;
	
	public void setNodeListContainer(NodeListContainer nodeListContainer) {
		this.nodeListContainer = nodeListContainer;
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
		List<NodeInfo> nodeList = getRandomSubsetFromNodeList(getCompleteNodeList());		
		NodeInfo[] nodeArray = getNodeArrayForList(nodeList);
		nodeChain.setNodes(nodeArray);
		return nodeChain;
	}
	
	public List<NodeInfo> getCompleteNodeList() 
			throws NotEnoughNodesException{
		List<NodeInfo> comleteNodeList = nodeListContainer.getNodeList();
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
	
	private List<NodeInfo> getRandomSubsetFromNodeList(List<NodeInfo> nodeList){
		ArrayList<NodeInfo> retList = new ArrayList<NodeInfo>();
		for(int i = 0; i < nodeChainElementCount; i++){
			int elementId = randomGenerator.nextInt(nodeList.size());
			NodeInfo element = nodeList.remove(elementId);
			retList.add(element);
		}
		return retList;		
	}

}
