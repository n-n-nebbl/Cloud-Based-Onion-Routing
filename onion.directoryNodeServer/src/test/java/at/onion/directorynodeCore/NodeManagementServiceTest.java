package at.onion.directorynodeCore;

import static org.junit.Assert.*;

import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import at.onion.commons.CryptoUtils;
import at.onion.commons.NodeInfo;
import at.onion.directorynodeCore.domain.Node;
import at.onion.directorynodeCore.nodeManagementService.NodeManagementService;
import at.onion.directorynodeCore.nodeManagementService.SimpleNodeManagementService;

public class NodeManagementServiceTest {
	/**
	
	private NodeManagementService nodeManagementService;
	private NodeInfo nodeInfo1;
	private NodeInfo nodeInfo2;
	private NodeInfo nodeInfo3;
	private NodeInfo nodeInfo4;
	private NodeInfo nodeInfo5;
	
	@Before
	public void setUp() 
			throws NoSuchAlgorithmException, NoSuchProviderException{
		nodeManagementService = new SimpleNodeManagementService();		
		setUpNodeInfos();
	}
	
	@Test
	public void addNodeAndGenerateUuid_shouldGenerateUuid() 
			throws UnknownHostException{
		UUID id = nodeManagementService.addNodeAsNodeInfoAndGenerateUuid(nodeInfo1);
		assertTrue(id != null);
		List<Node> nodeList = nodeManagementService.getNodeList();
		Node ni = nodeList.get(0);	
		assertEquals(ni.getUuid().toString(), id.toString());
	}
	
	@Test
	public void addNodeAndGenerateUuid_shouldConvertNode() 
			throws UnknownHostException{
		UUID id = nodeManagementService.addNodeAsNodeInfoAndGenerateUuid(nodeInfo1);
		List<Node> nodeList = nodeManagementService.getNodeList();
		Node ni = nodeList.get(0);
		assertEquals(ni.getPort(), nodeInfo1.getPort());
		//TODO: Check ip addess
		assertEquals(ni.getPublicKey(), nodeInfo1.getPublicKey());
	}
	
	@Test
	public void addNodeAndGenerateUuid_shouldCreateIntialTimestamp() 
			throws UnknownHostException{
		nodeManagementService.addNodeAsNodeInfoAndGenerateUuid(nodeInfo1);
		List<Node> nodeList = nodeManagementService.getNodeList();
		Date nodeDate = nodeList.get(0).getLastAliveTimestamp();
		Date nowDate = new Date();
		assertTrue(nowDate.compareTo(nodeDate) >= 0);
	}
	
	@Test
	public void addNodesAndGetNodeList_shouldReturnAddedNodes() 
			throws UnknownHostException{
		nodeManagementService.addNodeAsNodeInfoAndGenerateUuid(nodeInfo1);
		nodeManagementService.addNodeAsNodeInfoAndGenerateUuid(nodeInfo2);
		nodeManagementService.addNodeAsNodeInfoAndGenerateUuid(nodeInfo3);
		nodeManagementService.addNodeAsNodeInfoAndGenerateUuid(nodeInfo4);
		List<Node> nodeList = nodeManagementService.getNodeList();
		assertEquals(nodeList.size(), 4);
	}
	
	@Test
	public void removeNode_shouldRemoveNodeFromPool() 
			throws UnknownHostException{
		UUID id = nodeManagementService.addNodeAsNodeInfoAndGenerateUuid(nodeInfo1);
		UUID id2 = nodeManagementService.addNodeAsNodeInfoAndGenerateUuid(nodeInfo2);
		List<Node> nodeList = nodeManagementService.getNodeList();
		nodeManagementService.removeNode(nodeList.get(0));
		nodeList = nodeManagementService.getNodeList();
		assertEquals(nodeList.size(), 1);		
		nodeManagementService.removeNode(nodeList.get(0));
		nodeList = nodeManagementService.getNodeList();
		assertEquals(nodeList.size(), 0);
	}
	
	@Test
	public void getNodeByUuid_shouldReturnCorrectNode() 
			throws UnknownHostException{
		UUID id = nodeManagementService.addNodeAsNodeInfoAndGenerateUuid(nodeInfo1);
		nodeManagementService.addNodeAsNodeInfoAndGenerateUuid(nodeInfo2);
		Node n = nodeManagementService.getNodeByUuid(id);
		assertEquals(n.getUuid(), id);
		assertEquals(n.getPort(), nodeInfo1.getPort());
	}
	
	@Test
	public void updateNodeTimestampForUuid() 
			throws UnknownHostException{
		UUID id = nodeManagementService.addNodeAsNodeInfoAndGenerateUuid(nodeInfo1);
		Date ts1 = new Date();
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {}
		nodeManagementService.updateNodeTimestampForUuid(id);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {}
		Date ts2 = new Date();
		Date nodeDate = nodeManagementService.getNodeByUuid(id).getLastAliveTimestamp();
		assertTrue(ts1.before(nodeDate));
		assertTrue(ts2.after(nodeDate));
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
