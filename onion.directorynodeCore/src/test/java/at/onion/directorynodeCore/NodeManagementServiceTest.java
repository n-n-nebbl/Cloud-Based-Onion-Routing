package at.onion.directorynodeCore;

import org.junit.Before;

import at.onion.directorynodeCore.nodeManagementService.NodeManagementService;
import at.onion.directorynodeCore.nodeManagementService.SimpleNodeManagementService;

public class NodeManagementServiceTest {
	
	private NodeManagementService nodeManagementService;
	
	@Before
	public void setUp(){
		nodeManagementService = new SimpleNodeManagementService();
	}
	
	
}
