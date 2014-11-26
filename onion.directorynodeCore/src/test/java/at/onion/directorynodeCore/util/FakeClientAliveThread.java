package at.onion.directorynodeCore.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.UUID;

import at.onion.commons.AliveMessage;

public class FakeClientAliveThread implements Runnable{
	private DatagramSocket clientSocket;
	private UUID clientId;
	private AliveMessage aliveMessage;
	private InetAddress inetAddress;
	private int port;
	private int sendIntervallInMS;
	
	public FakeClientAliveThread(int port, UUID clientId, int sendIntervallInMS) 
			throws UnknownHostException{
		this.sendIntervallInMS = sendIntervallInMS;
		this.port = port;
		this.clientId = clientId;
		this.aliveMessage = new AliveMessage(clientId);
		this.inetAddress = InetAddress.getByName("localhost");
	}
	
	@Override
	public void run() {
		while(true){
			try {
				clientSocket = new DatagramSocket();
				byte[] msg = aliveMessage.getByteArray();			
				DatagramPacket datagramPacket = new DatagramPacket(msg, msg.length, inetAddress, port);
				clientSocket.send(datagramPacket);
				clientSocket.close();
			} catch (SocketException e1) {
			} catch (IOException e) {
			}
			
			try {
			    Thread.sleep(sendIntervallInMS);
			} catch (InterruptedException e) {}
		}
	}
}
