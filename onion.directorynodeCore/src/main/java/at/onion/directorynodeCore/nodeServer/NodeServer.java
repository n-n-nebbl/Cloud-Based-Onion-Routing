package at.onion.directorynodeCore.nodeServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;

import at.onion.directorynodeCore.chainGernatorService.ChainGenerationService;
import at.onion.directorynodeCore.nodeManagementService.NodeManagementService;

public class NodeServer implements Runnable{
	
	private TaskExecutor threadPool;	
	private ChainGenerationService chainGeneratorService;
	private NodeManagementService nodeManagementService;	
	private Logger logger;
	private ServerSocket serverSocket;
	
    @Value("${requestServer.port}") 
	private int port;
	
	public NodeServer() throws IOException{
		logger = LoggerFactory.getLogger(this.getClass());
		serverSocket = new ServerSocket(port);
	}
	
	public void setChainGeneratorService(ChainGenerationService chainGeneratorService) {
		this.chainGeneratorService = chainGeneratorService;
	}
	
	public void setNodeManagementService(NodeManagementService nodeService) {
		this.nodeManagementService = nodeService;
	}
	
	public void setThreadPool(TaskExecutor threadPool){
		this.threadPool = threadPool;
	}
	
	public void setPort(int port){
		this.port = port;
	}
	
	public void shutdown(){
		cleanUp();
	}

	@Override
	public void run() {
		logger.debug("Start request server on port:" + port);
		try{
			while(true){
				Socket clientConnectionSocket = serverSocket.accept();
				threadPool.execute(new RequestHandler(clientConnectionSocket, chainGeneratorService, nodeManagementService));
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
