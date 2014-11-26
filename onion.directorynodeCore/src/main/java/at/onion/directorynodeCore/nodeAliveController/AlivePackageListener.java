package at.onion.directorynodeCore.nodeAliveController;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class AlivePackageListener implements Runnable{
	
	private DatagramSocket datagrammPacket;
	
	public AlivePackageListener(int port) 
			throws SocketException{
		datagrammPacket = new DatagramSocket(port);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
