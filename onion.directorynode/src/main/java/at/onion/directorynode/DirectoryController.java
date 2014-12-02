package at.onion.directorynode;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.json.JSONObject;
import org.codehaus.jackson.*;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import at.onion.commons.CryptoUtils;
import at.onion.commons.NodeChainInfo;
import at.onion.commons.NodeInfo;
import at.onion.directorynode.coreClient.CoreClient;
import at.onion.directorynode.coreClient.InvalidResultException;
import at.onion.directorynode.coreClient.SimpleCoreClient;

@Controller
public class DirectoryController {

	/*
	@Autowired
	private CoreClient coreClient;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	

	@RequestMapping(value="/", produces="application/json")
	@ResponseBody
	public String provideNodeChainInformation() 
			throws IOException, InvalidResultException {
		NodeChainInfo info = coreClient.getNodeChain();		
		JSONObject result = new JSONObject().append("nodes", info.getNodes());
		return result.toString();
	}
	
	@RequestMapping(value="/nodes", method = RequestMethod.POST, produces="application/json")
	@ResponseBody
	public String addNode(@RequestBody String nodeInfoString) 
			throws UnknownHostException, IOException, InvalidResultException{
		ObjectMapper mapper = new ObjectMapper();
		NodeInfo nodeInfo = null;
		logger.debug(nodeInfoString);
		try{
			nodeInfo = mapper.readValue(nodeInfoString, NodeInfo.class);
		}catch(JsonMappingException ex){
			logger.error("Mapping error", ex);
		}
		logger.warn("TEST");
		String id = coreClient.addNode(nodeInfo);
		JSONObject result = new JSONObject().put("id", id);
		return result.toString();
	}
	*/
}
