package at.onion.directorynodeCore.nodeServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import at.onion.commons.CryptoUtils;
import at.onion.commons.NodeChainInfo;
import at.onion.commons.NodeInfo;
import at.onion.commons.directoryNode.Request;
import at.onion.commons.directoryNode.RequestType;
import at.onion.commons.directoryNode.Response;
import at.onion.commons.directoryNode.ResponseStatus;

public class RequestHandler implements Runnable{
	
	private Socket socket;
	
	public RequestHandler(Socket socket){
		this.socket = socket;
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
				//TDODO: Log
			}
			sendResponse(response);
			socket.close();
		} catch (IOException e) {
			//TODO: Log
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	private Response executeRequestAndCreateResponse(Request request){
		Response response;
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
    	//TODO: Implement service access
    	
    	NodeChainInfo info = new NodeChainInfo();
    	try{			
			NodeInfo test1 = new NodeInfo("182.172.19.5", 4231, CryptoUtils.generateDefaultRSAKeyPair().getPublic());
			NodeInfo test2 = new NodeInfo("bla.blubb.at", 1234, CryptoUtils.generateDefaultRSAKeyPair().getPublic());
			NodeInfo test3 = new NodeInfo("182.172.19.10", 3512, CryptoUtils.generateDefaultRSAKeyPair().getPublic());
			NodeInfo[] testInfos = new NodeInfo[]{test1, test2, test3};
			info.setNodes(testInfos);
		}catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Response response = new Response();
		response.setNodeChain(info);
		response.setResponseStatus(ResponseStatus.OK);
		
		return response;
    }
    
    private Response addNodeAndGetResponse(NodeInfo node){
    	//TODO: Implement service access
		Response response = new Response();
		response.setResponseStatus(ResponseStatus.OK);
		
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
		OutputStream outputStream = socket.getOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
		objectOutputStream.writeObject(response);	    	
    }
}
