package at.onion.directorynodeCore;

import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;

import at.onion.commons.CryptoUtils;
import at.onion.commons.NodeInfo;

public class NodeListContainer {
	public List<NodeInfo> getNodeList(){
		ArrayList<NodeInfo> nodeList = new ArrayList<NodeInfo>();
    	
    	try{			
			NodeInfo test1 = new NodeInfo("182.172.19.5", 4231, CryptoUtils.generateDefaultRSAKeyPair().getPublic());
			nodeList.add(test1);
			NodeInfo test2 = new NodeInfo("bla.blubb.at", 1234, CryptoUtils.generateDefaultRSAKeyPair().getPublic());
			nodeList.add(test2);
			NodeInfo test3 = new NodeInfo("182.172.19.10", 3512, CryptoUtils.generateDefaultRSAKeyPair().getPublic());
			nodeList.add(test3);
			NodeInfo test4 = new NodeInfo("182.172.19.11", 3512, CryptoUtils.generateDefaultRSAKeyPair().getPublic());
			nodeList.add(test4);
			NodeInfo test5 = new NodeInfo("182.172.19.12", 3512, CryptoUtils.generateDefaultRSAKeyPair().getPublic());
			nodeList.add(test5);
		}catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return nodeList;
	}
	
	public void addNode(NodeInfo nodeInfo){
		
	}
	
	public void removeNode(NodeInfo nodeInfo){
		
	}
	
	public NodeInfo getNodeByInetAddress(InetAddress inetAddress){
		return null;
	}	
}
