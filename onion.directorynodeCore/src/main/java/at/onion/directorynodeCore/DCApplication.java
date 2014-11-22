package at.onion.directorynodeCore;

import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import at.onion.commons.CryptoUtils;
import at.onion.commons.NodeChainInfo;
import at.onion.commons.NodeInfo;
import at.onion.commons.directoryNode.Request;
import at.onion.directorynodeCore.nodeServer.NodeServer;

public class DCApplication{
	
	public static ExecutorService threadPool; 

	public static void main(String [] args) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, ClassNotFoundException{
		System.out.println("Starting server");
		final NodeServer nodeServer = new NodeServer();
		new Thread(nodeServer).start();
		
	    Runtime.getRuntime().addShutdownHook(
	    	new Thread() {
	    		public void run() {
	    			System.out.println("Server shutdown");
	    			nodeServer.shutdown();
	    		}
	    	}
	    );
	}	
}
