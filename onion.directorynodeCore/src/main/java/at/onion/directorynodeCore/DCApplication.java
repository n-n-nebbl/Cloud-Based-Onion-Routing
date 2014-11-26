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
import at.onion.directorynodeCore.nodeAliveController.SimpleNodeAliveController;
import at.onion.directorynodeCore.nodeServer.NodeServer;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DCApplication{
	
	public static ExecutorService threadPool; 

	public static void main(String [] args) 
			throws IOException, NoSuchAlgorithmException, NoSuchProviderException, ClassNotFoundException{
		System.out.println("Starting server");
		
		ApplicationContext context = 
				new ClassPathXmlApplicationContext(new String[] {"SpringBeans.xml"});
		
		final NodeServer nodeServer = (NodeServer) context.getBean("nodeServer");
		new Thread(nodeServer).start();
		
		final SimpleNodeAliveController nodeAliveController = (SimpleNodeAliveController) context.getBean("nodeAliveController");
		nodeAliveController.startAlivePackageServer();
		
	    Runtime.getRuntime().addShutdownHook(
	    	new Thread() {
	    		public void run() {
	    			System.out.println("Server shutdown");
	    			nodeServer.shutdown();
	    			nodeAliveController.stopAlivePackageServer();
	    		}
	    	}
	    );
	}	
}
