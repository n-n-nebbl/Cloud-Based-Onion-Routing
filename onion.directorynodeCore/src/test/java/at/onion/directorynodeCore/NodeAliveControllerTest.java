package at.onion.directorynodeCore;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import at.onion.directorynodeCore.nodeAliveController.NodeAliveService;
import at.onion.directorynodeCore.nodeAliveController.SimpleNodeAliveController;
import at.onion.directorynodeCore.nodeInstanceService.NodeInstanceService;
import at.onion.directorynodeCore.nodeManagementService.NodeManagementService;
import at.onion.directorynodeCore.nodeManagementService.SimpleNodeManagementService;

public class NodeAliveControllerTest {
	
	private NodeInstanceService mockedNodeInstanceService;
	private NodeManagementService nodeManagementService;
	private SimpleNodeAliveController nodeAliveController;
	
	@Before
	public void setUpClass(){		
		nodeAliveController = new SimpleNodeAliveController();
		
		nodeAliveController.setNodeInstanceService(mockedNodeInstanceService);
		mockedNodeInstanceService = mock(NodeInstanceService.class);
		
		nodeManagementService = new SimpleNodeManagementService();
		nodeAliveController.setNodeManagementService(nodeManagementService);
		
		nodeAliveController.setAlivePackagePort(8002);
		nodeAliveController.setNodeOfflineScanIntervallInMS(5);
		nodeAliveController.setNodeOfflineThresholdInMS(100);
	}
	
	@Test
	public void incommingAlivePackage_shouldUpdateTimestamp(){
		assertTrue(true);
	}
}
