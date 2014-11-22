package at.onion.directorynode;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import at.onion.commons.CryptoUtils;
import at.onion.commons.NodeChainInfo;
import at.onion.commons.NodeInfo;
import at.onion.directorynode.coreClient.CoreClient;
import at.onion.directorynode.coreClient.InvalidResultException;
import at.onion.directorynode.coreClient.SimpleCoreClient;

@Controller
public class DirectoryController {

	@RequestMapping(value="/", produces="application/json")
	@ResponseBody
	public String provideNodeChainInformation() 
			throws IOException, InvalidResultException {		
		CoreClient coreClient = new SimpleCoreClient();
		NodeChainInfo info = coreClient.getNodeChain();		
		JSONObject result = new JSONObject().append("nodes", info.getNodes());
		return result.toString();
	}
}
