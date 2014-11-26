package at.onion.directorynodeCore;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.*;
import at.onion.commons.AliveMessage;
import at.onion.commons.CryptoUtils;
import at.onion.commons.NodeInfo;
import at.onion.directorynodeCore.domain.Node;
import at.onion.directorynodeCore.nodeAliveController.NodeAliveController;
import at.onion.directorynodeCore.nodeAliveController.SimpleNodeAliveController;
import at.onion.directorynodeCore.nodeInstanceService.NodeInstanceService;
import at.onion.directorynodeCore.nodeManagementService.NodeManagementService;
import at.onion.directorynodeCore.nodeManagementService.SimpleNodeManagementService;
import at.onion.directorynodeCore.util.FakeClientAliveThread;

public class NodeAliveControllerTest {
	
	private NodeInstanceService mockedNodeInstanceService;
	private NodeManagementService nodeManagementService;
	private SimpleNodeAliveController nodeAliveController;
	
	private NodeInfo nodeInfo1;
	private NodeInfo nodeInfo2;
	
	@Before
	public void setUpClass() 
			throws NoSuchAlgorithmException, NoSuchProviderException{		
		nodeAliveController = new SimpleNodeAliveController();
		
		mockedNodeInstanceService = mock(NodeInstanceService.class);
		nodeAliveController.setNodeInstanceService(mockedNodeInstanceService);		
		
		nodeManagementService = new SimpleNodeManagementService();
		nodeAliveController.setNodeManagementService(nodeManagementService);
		
		nodeAliveController.setAlivePackagePort(8003);
		nodeAliveController.setNodeOfflineThresholdInMS(50);
		nodeAliveController.setMinimumNodeInstances(2);
		
		setUpNodeInfos();
	}
	
	@After
	public void tearDown(){
		nodeAliveController.stopAlivePackageServer();
	}
	
	@Test
	public void incommingAliveMessage_shouldUpdateTimestamp() 
			throws UnknownHostException, SocketException{
		UUID id = nodeManagementService.addNodeAsNodeInfoAndGenerateUuid(nodeInfo1);				
		FakeClientAliveThread fakeClient = new FakeClientAliveThread(8003, id, 10);
		new Thread(fakeClient).start();
		
		nodeAliveController.startAlivePackageServer();
		
		Date timeStamp1 = nodeManagementService.getNodeByUuid(id).getLastAliveTimestamp();
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {}
		Date timeStamp2 = nodeManagementService.getNodeByUuid(id).getLastAliveTimestamp();
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {}
		Date timeStamp3 = nodeManagementService.getNodeByUuid(id).getLastAliveTimestamp();
		
		assertTrue(timeStamp1.before(timeStamp2));
		assertTrue(timeStamp2.before(timeStamp3));
	}
	
	@Test
	public void incommingAliveMessage_shouldKeepNodesAlive() 
			throws UnknownHostException, SocketException{
		UUID id1 = nodeManagementService.addNodeAsNodeInfoAndGenerateUuid(nodeInfo1);				
		FakeClientAliveThread fakeClient1 = new FakeClientAliveThread(8003, id1, 10);
		new Thread(fakeClient1).start();		
		UUID id2 = nodeManagementService.addNodeAsNodeInfoAndGenerateUuid(nodeInfo1);				
		FakeClientAliveThread fakeClient2 = new FakeClientAliveThread(8003, id2, 10);
		new Thread(fakeClient2).start();
		
		nodeAliveController.startAlivePackageServer();	
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {}
		
		assertEquals(2, nodeManagementService.getNodeList().size());
		nodeAliveController.execute();		
		assertEquals(2, nodeManagementService.getNodeList().size());
	}
	
	@Test
	public void missingAliveMessage_shouldRemoveNode() 
			throws UnknownHostException, SocketException{
		UUID id1 = nodeManagementService.addNodeAsNodeInfoAndGenerateUuid(nodeInfo1);				
		FakeClientAliveThread fakeClient1 = new FakeClientAliveThread(8003, id1, 10);
		new Thread(fakeClient1).start();		
		UUID id2 = nodeManagementService.addNodeAsNodeInfoAndGenerateUuid(nodeInfo1);				
		
		nodeAliveController.startAlivePackageServer();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {}
		
		assertEquals(2, nodeManagementService.getNodeList().size());
		nodeAliveController.execute();		
		assertEquals(1, nodeManagementService.getNodeList().size());
	}
	
	@Test
	public void removingNodeAfterTimeout_shouldTriggerStartAtNodeInstanceService() 
			throws UnknownHostException{
		nodeManagementService.addNodeAsNodeInfoAndGenerateUuid(nodeInfo1);
		nodeManagementService.addNodeAsNodeInfoAndGenerateUuid(nodeInfo2);
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {}
		
		nodeAliveController.execute();
		Mockito.verify(mockedNodeInstanceService, Mockito.times(2)).startNewNodeInstance();
	}
	
	@Test
	public void removingNodeAfterTimeout_shouldTriggerShutdownOfNodeInstance() 
			throws UnknownHostException{
		nodeManagementService.addNodeAsNodeInfoAndGenerateUuid(nodeInfo1);
		Node node1 = nodeManagementService.getNodeList().get(0);
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {}
		
		nodeAliveController.execute();
		Mockito.verify(mockedNodeInstanceService, Mockito.times(1)).shutdownNodeInstaceOwnerForNode(node1);
	}
	
	
	public void setUpNodeInfos()
			throws NoSuchAlgorithmException, NoSuchProviderException{
		nodeInfo1 = new NodeInfo("127.0.0.1", 4231, CryptoUtils.generateDefaultRSAKeyPair().getPublic());		
		nodeInfo2 = new NodeInfo("182.172.19.2", 4231, CryptoUtils.generateDefaultRSAKeyPair().getPublic());
	}
}
