package at.onion.proxy;

public class Socks5Response
{
	public enum SOCKS5RESPONSE
	{
		SOCKS5_REP_SUCCEEDED, SOCKS5_REP_FAIL, SOCKS5_REP_NALLOWED, SOCKS5_REP_NUNREACH, SOCKS5_REP_HUNREACH, SOCKS5_REP_REFUSED, SOCKS5_REP_EXPIRED, SOCKS5_REP_CNOTSUP, SOCKS5_REP_ANOTSUP, SOCKS5_REP_INVADDR
	}

	public static byte getResponseByte(SOCKS5RESPONSE response)
			throws SocksException
	{
		switch (response)
		{
		case SOCKS5_REP_SUCCEEDED:
			return (byte) 0x00;
		case SOCKS5_REP_FAIL:
			return (byte) 0x01;
		case SOCKS5_REP_NALLOWED:
			return (byte) 0x02;
		case SOCKS5_REP_NUNREACH:
			return (byte) 0x03;
		case SOCKS5_REP_HUNREACH:
			return (byte) 0x04;
		case SOCKS5_REP_REFUSED:
			return (byte) 0x05;
		case SOCKS5_REP_EXPIRED:
			return (byte) 0x06;
		case SOCKS5_REP_CNOTSUP:
			return (byte) 0x07;
		case SOCKS5_REP_ANOTSUP:
			return (byte) 0x08;
		case SOCKS5_REP_INVADDR:
			return (byte) 0x09;
		default:
			throw new SocksException("Response " + response
					+ " not implemented.");
		}
	}

	public static String getResponseName(SOCKS5RESPONSE response)
			throws SocksException
	{
		switch (response)
		{
		case SOCKS5_REP_SUCCEEDED:
			return "succeeded";
		case SOCKS5_REP_FAIL:
			return "general SOCKS server failure";
		case SOCKS5_REP_NALLOWED:
			return "connection not allowed by ruleset";
		case SOCKS5_REP_NUNREACH:
			return "Network unreachable";
		case SOCKS5_REP_HUNREACH:
			return "Host unreachable";
		case SOCKS5_REP_REFUSED:
			return "connection refused";
		case SOCKS5_REP_EXPIRED:
			return "TTL expired";
		case SOCKS5_REP_CNOTSUP:
			return "Command not supported";
		case SOCKS5_REP_ANOTSUP:
			return "Address not supported";
		case SOCKS5_REP_INVADDR:
			return "Invalid address";
		default:
			throw new SocksException("Response " + response
					+ " not implemented.");
		}
	}
}