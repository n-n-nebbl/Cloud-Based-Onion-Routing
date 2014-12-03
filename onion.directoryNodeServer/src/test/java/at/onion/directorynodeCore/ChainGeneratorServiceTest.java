package at.onion.directorynodeCore;

import static org.junit.Assert.*;

import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import at.onion.commons.CryptoUtils;
import at.onion.commons.NodeChainInfo;
import at.onion.commons.NodeInfo;
import at.onion.directorynodeCore.chainGernatorService.ChainGenerationService;
import at.onion.directorynodeCore.chainGernatorService.NotEnoughNodesException;
import at.onion.directorynodeCore.chainGernatorService.SimpleChainGeneratorService;
import at.onion.directorynodeCore.domain.Node;
import at.onion.directorynodeCore.nodeManagementService.NodeManagementService;
import at.onion.directorynodeCore.nodeManagementService.SimpleNodeManagementService;

public class ChainGeneratorServiceTest {
	
	/*
	private NodeManagementService nodeManagementService;
	private ChainGenerationService chainGenerationService1;
	private ChainGenerationService chainGenerationService2;
	
	private NodeInfo nodeInfo1;
	private NodeInfo nodeInfo2;
	private NodeInfo nodeInfo3;
	private NodeInfo nodeInfo4;
	private NodeInfo nodeInfo5;
	
	@Before
	public void setUp() 
			throws NoSuchAlgorithmException, NoSuchProviderException, UnknownHostException{
		nodeManagementService = new SimpleNodeManagementService();
		
		SimpleChainGeneratorService chainGenerationService1 = new SimpleChainGeneratorService();
		chainGenerationService1.setNodeManagementService(nodeManagementService);
		chainGenerationService1.setNodeChainElementCount(3);
		this.chainGenerationService1 = chainGenerationService1;
		
		SimpleChainGeneratorService chainGenerationService2 = new SimpleChainGeneratorService();
		chainGenerationService2.setNodeManagementService(nodeManagementService);
		chainGenerationService2.setNodeChainElementCount(5);
		this.chainGenerationService2 = chainGenerationService2;		
		
		setUpNodeInfos();
	}
	
	@Test
	public void getNodeChain_shouldReturnCorrectItemCount() 
			throws NotEnoughNodesException, UnknownHostException{
		nodeManagementService.addNodeAsNodeInfoAndGenerateUuid(nodeInfo1);
		nodeManagementService.addNodeAsNodeInfoAndGenerateUuid(nodeInfo2);
		nodeManagementService.addNodeAsNodeInfoAndGenerateUuid(nodeInfo3);
		nodeManagementService.addNodeAsNodeInfoAndGenerateUuid(nodeInfo4);
		nodeManagementService.addNodeAsNodeInfoAndGenerateUuid(nodeInfo5);
		NodeChainInfo nodeChain1 = chainGenerationService1.getNodeChain();
		assertEquals(3, nodeChain1.getNodes().length);
		NodeChainInfo nodeChain2 = chainGenerationService2.getNodeChain();
		assertEquals(5, nodeChain2.getNodes().length);		
	}
	
	@Test(expected = NotEnoughNodesException.class)
	public void getNodeChainWithZeroNodes_shouldThrowNotEnoughNodesException() 
			throws NotEnoughNodesException{
		NodeChainInfo nodeChain1 = chainGenerationService1.getNodeChain();
	}
	
	@Test(expected = NotEnoughNodesException.class)
	public void getNodeChainWithInsufficientNodes_shouldThrowNotEnoughNodesException() 
			throws NotEnoughNodesException, UnknownHostException{
		nodeManagementService.addNodeAsNodeInfoAndGenerateUuid(nodeInfo1);
		nodeManagementService.addNodeAsNodeInfoAndGenerateUuid(nodeInfo2);
		NodeChainInfo nodeChain1 = chainGenerationService1.getNodeChain();
	}
	
	public void setUpNodeInfos()
			throws NoSuchAlgorithmException, NoSuchProviderException{
		nodeInfo1 = new NodeInfo("182.172.19.1", 4231, CryptoUtils.generateDefaultRSAKeyPair().getPublic());		
		nodeInfo2 = new NodeInfo("182.172.19.2", 4231, CryptoUtils.generateDefaultRSAKeyPair().getPublic());
		nodeInfo3 = new NodeInfo("182.172.19.3", 4231, CryptoUtils.generateDefaultRSAKeyPair().getPublic());
		nodeInfo4 = new NodeInfo("182.172.19.4", 4231, CryptoUtils.generateDefaultRSAKeyPair().getPublic());
		nodeInfo5 = new NodeInfo("182.172.19.5", 4231, CryptoUtils.generateDefaultRSAKeyPair().getPublic());
	}
	*/

}
