package at.onion.commons;

import java.util.UUID;

public class AliveMessage {
	public static final String messagePrefix = "NODE_ALIVE";
	
	private UUID uuid;
	
	public AliveMessage(UUID uuid){
		this.uuid = uuid;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	
	public byte[] getByteArray(){
		String messageString = messagePrefix + uuid.toString();
		return messageString.getBytes();
	}
	
	public static AliveMessage createFromByteArray(byte[] bytes){
		String messageString = new String(bytes);
		int prefixLength = messagePrefix.length();
		
		String prefixSubstring = messageString.substring(0, prefixLength); 
		if(!prefixSubstring.equals(messagePrefix)){
			throw new IllegalArgumentException("No AliveMessage prefix found");
		}
		
		String uuidSubstring = messageString.substring(prefixLength, messageString.length()).trim();
		UUID uuid = UUID.fromString(uuidSubstring);
		return new AliveMessage(uuid);		
	}
}
