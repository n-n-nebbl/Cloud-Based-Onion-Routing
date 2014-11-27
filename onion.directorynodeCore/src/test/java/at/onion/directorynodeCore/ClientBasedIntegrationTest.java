package at.onion.directorynodeCore;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import at.onion.commons.CryptoUtils;
import at.onion.commons.NodeChainInfo;
import at.onion.commons.NodeInfo;
import at.onion.directorynode.coreClient.CoreClient;
import at.onion.directorynode.coreClient.InternalServerErrorException;
import at.onion.directorynode.coreClient.InvalidResultException;
import at.onion.directorynode.coreClient.NotEnoughNodesException;
import at.onion.directorynode.coreClient.SimpleCoreClient;
import at.onion.directorynodeCore.util.FakeClientAliveThread;

public class ClientBasedIntegrationTest {
	
	private NodeInfo nodeInfo1;
	private NodeInfo nodeInfo2;
	private NodeInfo nodeInfo3;
	private NodeInfo nodeInfo4;
	private NodeInfo nodeInfo5;
	
	@Before
	public void setUp() 
			throws NoSuchAlgorithmException, NoSuchProviderException{
		setUpNodeInfos();
	}
	
	@Test
	public void requestNodeChainOnServerWithEnoughNodes_shouldReturnNodeChain() 
			throws NoSuchAlgorithmException, NoSuchProviderException, ClassNotFoundException, 
			IOException, InvalidResultException, InternalServerErrorException, NotEnoughNodesException{
		DCApplication.main(null);
		InetAddress serverAddr = InetAddress.getByName("127.0.0.1");
		int serverPort = 8001;
		CoreClient client = SimpleCoreClient.getInstanceForInetAddressAndPort(serverAddr, serverPort);
		
		String id1 = client.addNode(nodeInfo1);
		String id2 = client.addNode(nodeInfo2);
		String id3 = client.addNode(nodeInfo3);
		
		FakeClientAliveThread fakeClient1 = new FakeClientAliveThread(8002, UUID.fromString(id1), 10);
		new Thread(fakeClient1).start();
		FakeClientAliveThread fakeClient2 = new FakeClientAliveThread(8002, UUID.fromString(id2), 10);
		new Thread(fakeClient2).start();
		FakeClientAliveThread fakeClient3 = new FakeClientAliveThread(8002, UUID.fromString(id3), 10);
		new Thread(fakeClient3).start();
		
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Hostname could be converted to ip -> identify over public key
		ArrayList<String> keyStringList = new ArrayList<String>();
		keyStringList.add(new String(nodeInfo1.getPublicKey().getEncoded()));
		keyStringList.add(new String(nodeInfo2.getPublicKey().getEncoded()));
		keyStringList.add(new String(nodeInfo3.getPublicKey().getEncoded()));
		
		NodeChainInfo nodeChain = client.getNodeChain();
		NodeInfo[] nodes = nodeChain.getNodes();
		for(int i = 0; i < nodes.length; i++){
			NodeInfo node = nodes[i];
			String nKey = new String(node.getPublicKey().getEncoded());
			assertTrue(keyStringList.contains(nKey));
		}
	}
	
	
	public void setUpNodeInfos()
			throws NoSuchAlgorithmException, NoSuchProviderException{
		nodeInfo1 = new NodeInfo("127.0.0.1", 4231, CryptoUtils.generateDefaultRSAKeyPair().getPublic());		
		nodeInfo2 = new NodeInfo("182.172.19.2", 4231, CryptoUtils.generateDefaultRSAKeyPair().getPublic());
		nodeInfo3 = new NodeInfo("182.172.19.3", 4231, CryptoUtils.generateDefaultRSAKeyPair().getPublic());
		nodeInfo4 = new NodeInfo("182.172.19.4", 4231, CryptoUtils.generateDefaultRSAKeyPair().getPublic());
		nodeInfo5 = new NodeInfo("182.172.19.5", 4231, CryptoUtils.generateDefaultRSAKeyPair().getPublic());
	}
}
