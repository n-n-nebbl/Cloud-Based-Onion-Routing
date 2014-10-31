package at.onion.commons;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Test;

public class SSLTest {

	@Test
	public void testSSLSockets() throws Exception {
		new Thread() {
			public void run() {
				try {
					ServerSocket ss = CryptoUtils.createEncryptedServerSocket(9999);
					Socket s = ss.accept();
					new ObjectOutputStream(s.getOutputStream()).writeObject("test");
					s.close();
					ss.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			};
		}.start();

		Thread.sleep(100);
		
		try {
			Socket s = CryptoUtils.createEncryptedSocket("localhost", 9999);
			System.out.println(new ObjectInputStream(s.getInputStream()).readObject().toString());
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 

	}
	
}
