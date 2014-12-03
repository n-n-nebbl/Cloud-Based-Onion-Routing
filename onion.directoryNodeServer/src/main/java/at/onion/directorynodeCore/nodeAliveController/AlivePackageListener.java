package at.onion.directorynodeCore.nodeAliveController;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.onion.commons.AliveMessage;
import at.onion.directorynodeCore.nodeManagementService.NodeManagementService;

public class AlivePackageListener implements Runnable{
	
	private NodeManagementService nodeManagementService;
	private Logger logger;
	private DatagramSocket serverSocket;
	private int port;
	
	public AlivePackageListener(int port, NodeManagementService nodeManagementService) 
			throws SocketException{		
		this.port = port;
		this.nodeManagementService = nodeManagementService;
		serverSocket = new DatagramSocket(port);
		logger = LoggerFactory.getLogger(this.getClass());
	}

	@Override
	public void run() {
		logger.debug("Listen for alive messages on port: " + port);
		while(serverSocket != null){	
			byte[] inBytes = new byte[100];
            DatagramPacket receivePacket = new DatagramPacket(inBytes, inBytes.length);
            try {
				serverSocket.receive(receivePacket);
				byte[] recivedBytes = receivePacket.getData();
				AliveMessage aliveMessage = AliveMessage.createFromByteArray(recivedBytes);
				nodeManagementService.updateNodeTimestampForUuid(aliveMessage.getUuid());
			} catch (IOException e) {
				logger.warn("Reading UDP alive message failed", e);
			}            
		}
		logger.debug("Stop listening for alive messages");
	}
	
	public void shutdown(){
		logger.debug("Stop listening for alive messages");
		if(serverSocket == null)return;
		if(!serverSocket.isClosed()){
			serverSocket.close();
		}
		serverSocket = null;
	}

}
