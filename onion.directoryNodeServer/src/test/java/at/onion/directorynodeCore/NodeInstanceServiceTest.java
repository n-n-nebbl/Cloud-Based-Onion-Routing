package at.onion.directorynodeCore;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import org.junit.Test;

public class NodeInstanceServiceTest {
	
	@Test
	public void test(){
        try {
            InetAddress inetAddress = Inet4Address.getByName("54.69.215.57");
            System.out.println(inetAddress.getHostAddress());

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
