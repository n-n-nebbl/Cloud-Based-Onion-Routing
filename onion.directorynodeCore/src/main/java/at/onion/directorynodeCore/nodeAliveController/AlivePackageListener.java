package at.onion.directorynodeCore.nodeAliveController;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import at.onion.commons.AliveMessage;
import at.onion.directorynodeCore.nodeManagementService.NodeManagementService;

public class AlivePackageListener implements Runnable{
	
	private NodeManagementService nodeManagementService;
	
	private DatagramSocket serverSocket;
	
	public AlivePackageListener(int port, NodeManagementService nodeManagementService) 
			throws SocketException{		
		this.nodeManagementService = nodeManagementService;
		serverSocket = new DatagramSocket(port);		
	}

	@Override
	public void run() {
		while(serverSocket != null){	
			byte[] inBytes = new byte[100];
            DatagramPacket receivePacket = new DatagramPacket(inBytes, inBytes.length);
            try {
				serverSocket.receive(receivePacket);
				byte[] recivedBytes = receivePacket.getData();
				AliveMessage aliveMessage = AliveMessage.createFromByteArray(recivedBytes);
				nodeManagementService.updateNodeTimestampForUuid(aliveMessage.getUuid());
			} catch (IOException e) {
				//TODO: log
			}
            
		}
	}
	
	public void shutdown(){
		if(serverSocket == null)return;
		if(!serverSocket.isClosed()){
			serverSocket.close();
		}
		serverSocket = null;
	}

}
