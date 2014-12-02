package at.onion.commons;

import java.util.UUID;
import static org.junit.Assert.*;

import org.junit.Test;



public class AliveMessageTest {
	
	@Test
	public void getByteArrayAndCreateFromByteArray_shouldResoreMessage(){
		UUID uuid = UUID.randomUUID();
		AliveMessage msg = new AliveMessage(uuid);
		
		byte[] msgBytes = msg.getByteArray();
		AliveMessage msgNew = AliveMessage.createFromByteArray(msgBytes);
		
		assertEquals(msgNew.getUuid().toString(), msg.getUuid().toString());		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createFromByteArrayWithInvalidPrefix_shouldThrowIllegalArgumentException(){
		String bla = "asdf";
		UUID uuid = UUID.randomUUID();
		String msg = bla + uuid.toString();
		byte[] bytes = msg.getBytes();
		AliveMessage msgNew = AliveMessage.createFromByteArray(bytes);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createFromByteArrayWithInvalidUuid_shouldThrowIllegalArgumentException(){
		String msg = AliveMessage.messagePrefix + "asdfasdfasdfasdfasdf";
		byte[] bytes = msg.getBytes();
		AliveMessage msgNew = AliveMessage.createFromByteArray(bytes);
	}
}
