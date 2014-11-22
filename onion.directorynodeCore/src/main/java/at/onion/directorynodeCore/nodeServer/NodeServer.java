package at.onion.directorynodeCore.nodeServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NodeServer implements Runnable{
	
	private ExecutorService threadPool;
	private ServerSocket serverSocket;
	
	public NodeServer() throws IOException{
		int port = 8001;
		serverSocket = new ServerSocket(port);
		threadPool = Executors.newCachedThreadPool();
	}
	
	public void shutdown(){
		cleanUpThreadPool();
	}

	@Override
	public void run() {
		try{
			while(true){
				Socket clientConnectionSocket = serverSocket.accept();
				threadPool.execute(new RequestHandler(clientConnectionSocket));
			}
		}catch(IOException ex){
			cleanUpThreadPool();
		}
	}
	
	private void cleanUp(){
		cleanUpThreadPool();
		cleanUpServerSocket();
	}
	
	private void cleanUpThreadPool(){
		if(threadPool == null) return;				
		try {
			threadPool.awaitTermination(4L, TimeUnit.SECONDS);
		} catch (InterruptedException e) {}		
	}
	
	private void cleanUpServerSocket(){
		if(serverSocket == null) return;
		if(serverSocket.isClosed()) return;
		try {
			serverSocket.close();
		} catch (IOException e) {}
	}

}
