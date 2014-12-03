package at.onion.directorynodeCore.nodeServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.UUID;

import at.onion.commons.CryptoUtils;
import at.onion.commons.NodeChainInfo;
import at.onion.commons.NodeInfo;
import at.onion.commons.directoryNode.Request;
import at.onion.commons.directoryNode.RequestType;
import at.onion.commons.directoryNode.Response;
import at.onion.commons.directoryNode.ResponseStatus;
import at.onion.directorynodeCore.chainGernatorService.ChainGenerationService;
import at.onion.directorynodeCore.chainGernatorService.NotEnoughNodesException;
import at.onion.directorynodeCore.nodeManagementService.NodeManagementService;

public class RequestHandler implements Runnable{
	
	private ChainGenerationService chainGeneratorService;
	private NodeManagementService nodeService;
		
	private Socket socket;
	private Logger logger;		
	
	public RequestHandler(Socket socket, ChainGenerationService chainGeneratorService, NodeManagementService nodeService){
		this.chainGeneratorService = chainGeneratorService;
		this.nodeService = nodeService;
		this.socket = socket;
		logger = LoggerFactory.getLogger(this.getClass());
	}
	
	public void setChainGeneratorService(ChainGenerationService chainGeneratorService) {
		this.chainGeneratorService = chainGeneratorService;
	}

	@Override
	public void run() {
		try {
			Response response;
			try{
				Request request = readRequest();
				response = executeRequestAndCreateResponse(request);
			}catch(ClassNotFoundException ex){
				response = getInvalidRequestResponse();
				logger.error("Cannot process request - ClassNotFound", ex);
				logger.debug("Return error response");
			}
			sendResponse(response);
			socket.close();
			logger.debug("Response sent");
		} catch (IOException ex) {
			logger.error("Communication error", ex);
		} 
	}
	
	private Response executeRequestAndCreateResponse(Request request){
		Response response;
		logger.debug("Process response for requestType: " + request.getRequestType());
		switch(request.getRequestType()){
			case GET_NODECHAIN:				
				response = getNodeChainResponse();
				break;
			case ADD_NODE:
				response = addNodeAndGetResponse(request.getNewNode());
				break;
			default:
				response = getInvalidRequestResponse();
		}
		return response;
	}
    
    private Response getNodeChainResponse(){
    	Response response = new Response();
    	
		try {
			NodeChainInfo nodeChain = chainGeneratorService.getNodeChain();
			response.setNodeChain(nodeChain);
			response.setResponseStatus(ResponseStatus.OK);
		} catch (NotEnoughNodesException e) {
			logger.warn("Not enough nodes for required chain lenght - send error resonse", e);
			response.setResponseStatus(ResponseStatus.ERR_NOT_ENOUGH_NODES);
		}
		
		return response;
    }
    
    private Response addNodeAndGetResponse(NodeInfo node){
		Response response = new Response();
		try {
			UUID id = nodeService.addNodeAsNodeInfoAndGenerateUuid(node);
			response.setResponseStatus(ResponseStatus.OK);
			response.setId(id.toString());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			// TODO Error handling
			e.printStackTrace();
		}		
		return response;    	
    }
    
    private Response getInvalidRequestResponse(){
		Response response = new Response();
		response.setResponseStatus(ResponseStatus.ERR_INVALID_REQUEST);
		return response;
    }
    
    private Request readRequest() 
    		throws IOException, ClassNotFoundException {
    	InputStream inputStream = socket.getInputStream();
    	ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);    	
    	Request request = (Request)objectInputStream.readObject();
    	return request;
    }
    
    private void sendResponse(Response response) 
    		throws IOException{
    	logger.debug("Send response");
		OutputStream outputStream = socket.getOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
		objectOutputStream.writeObject(response);	    	
    }
}
