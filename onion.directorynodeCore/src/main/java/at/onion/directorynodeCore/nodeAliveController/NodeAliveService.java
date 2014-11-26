package at.onion.directorynodeCore.nodeAliveController;

import java.util.UUID;

public interface NodeAliveService {
	
	public void setAliveForUuid(UUID uuid);
	
	public void startOnPort(int port); 
}
