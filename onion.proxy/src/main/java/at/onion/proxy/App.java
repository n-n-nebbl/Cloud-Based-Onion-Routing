package at.onion.proxy;

/**
 * Hello world!
 *
 */
public class App
{
	public static void main(String[] args)
	{
		System.out.println("Hello World!");

		TCPServer.getInstance(9000);
	}
}
