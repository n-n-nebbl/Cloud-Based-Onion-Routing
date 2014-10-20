package at.onion.directorynode;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import at.onion.commons.NodeChainInfo;
import at.onion.commons.NodeInfo;

@Controller
public class DirectoryController {

	@RequestMapping(produces="application/json")
	@ResponseBody
	public String provideNodeChainInformation() {
		NodeChainInfo info = new NodeChainInfo();
		
		//TODO: generate real node-info

		NodeInfo test1 = new NodeInfo("182.172.19.5", 4231);
		NodeInfo test2 = new NodeInfo("bla.blubb.at", 1234);
		NodeInfo test3 = new NodeInfo("182.172.19.10", 3512);
		
		NodeInfo[] testInfos = new NodeInfo[]{test1, test2, test3};
		
		info.setNodes(testInfos);
		
		JSONObject result = new JSONObject().append("nodes", info.getNodes());

		return result.toString();
	}
}
