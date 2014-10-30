package at.onion.proxy;

public class Socks5AuthentificationResponse
{
	public enum SOCKS5AUTHENTIFICATIONRESPONSE
	{
		SOCKS5_AUTH_NOAUTH, SOCKS5_AUTH_GSSAPI, SOCKS5_AUTH_USERPASS, SOCKS5_AUTH_CHAP, SOCKS5_AUTH_EAP, SOCKS5_AUTH_MAF, SOCKS5_AUTH_REJECT
	}

	public static byte getResponseByte(SOCKS5AUTHENTIFICATIONRESPONSE response)
	{
		switch (response)
		{
		case SOCKS5_AUTH_NOAUTH:
			return (byte) 0x00;
		case SOCKS5_AUTH_GSSAPI:
			return (byte) 0x01;
		case SOCKS5_AUTH_USERPASS:
			return (byte) 0x02;
		case SOCKS5_AUTH_CHAP:
			return (byte) 0x03;
		case SOCKS5_AUTH_EAP:
			return (byte) 0x05;
		case SOCKS5_AUTH_MAF:
			return (byte) 0x08;
		case SOCKS5_AUTH_REJECT:
			return (byte) 0xFF;
		default:
			return (byte) 0xFF;
		}
	}
}