package at.onion.directorynodeCore.nodeServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.core.task.TaskExecutor;

import at.onion.directorynodeCore.chainGernatorService.ChainGenerationService;
import at.onion.directorynodeCore.nodeService.NodeService;

public class NodeServer implements Runnable{
	
	private TaskExecutor threadPool;
	private ServerSocket serverSocket;
	private ChainGenerationService chainGeneratorService;
	private NodeService nodeService;
	
	public NodeServer() throws IOException{
		int port = 8001;
		serverSocket = new ServerSocket(port);
	}
	
	public void setChainGeneratorService(ChainGenerationService chainGeneratorService) {
		this.chainGeneratorService = chainGeneratorService;
	}
	
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	
	public void setThreadPool(TaskExecutor threadPool){
		this.threadPool = threadPool;
	}
	
	public void shutdown(){
		cleanUp();
	}

	@Override
	public void run() {
		try{
			while(true){
				Socket clientConnectionSocket = serverSocket.accept();
				threadPool.execute(new RequestHandler(clientConnectionSocket, chainGeneratorService, nodeService));
			}
		}catch(IOException ex){
			cleanUp();
		}
	}
	
	private void cleanUp(){
		cleanUpServerSocket();
	}
	
	private void cleanUpServerSocket(){
		if(serverSocket == null) return;
		if(serverSocket.isClosed()) return;
		try {
			serverSocket.close();
		} catch (IOException e) {}
	}

}
