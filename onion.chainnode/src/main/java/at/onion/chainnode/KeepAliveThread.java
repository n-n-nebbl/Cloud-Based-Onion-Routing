package at.onion.chainnode;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.UUID;

import at.onion.commons.AliveMessage;

public class KeepAliveThread implements Runnable {

	private DatagramSocket clientSocket;
	private AliveMessage aliveMessage;
	private InetAddress host;
	private int port;
	private int sendIntervallInMS;
	private boolean isRunning = true;
	
	public KeepAliveThread(InetAddress host, int port, String clientId, int sendIntervallInMS) 
			throws UnknownHostException{
		this.sendIntervallInMS = sendIntervallInMS;
		this.port = port;
		this.aliveMessage = new AliveMessage(UUID.fromString(clientId));
		this.host = host;
	}
	
	@Override
	public void run() {
		while(isRunning()){
			try {
				clientSocket = new DatagramSocket();
				byte[] msg = aliveMessage.getByteArray();			
				DatagramPacket datagramPacket = new DatagramPacket(msg, msg.length, host, port);
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
	
	public boolean isRunning() {
		return isRunning;
	}
	
	public void setStopped() {
		this.isRunning = false;
	}
}
