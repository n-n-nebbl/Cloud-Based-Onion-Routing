package at.onion.proxy;

public class Socks4Response
{
	public enum SOCKS4RESPONSE
	{
		SOCKS4_REP_SUCCEEDED, SOCKS4_REP_REJECTED, SOCKS4_REP_IDENT_FAIL, SOCKS4_REP_USERID

	}

	public static int getResponseInteger(SOCKS4RESPONSE response)
			throws SocksException
	{
		switch (response)
		{
		case SOCKS4_REP_SUCCEEDED:
			return 90;
		case SOCKS4_REP_REJECTED:
			return 91;
		case SOCKS4_REP_IDENT_FAIL:
			return 92;
		case SOCKS4_REP_USERID:
			return 93;
		default:
			throw new SocksException("Response " + response
					+ " not implemented.");
		}
	}

	public static String getResponseName(SOCKS4RESPONSE response)
			throws SocksException
	{
		switch (response)
		{
		case SOCKS4_REP_SUCCEEDED:
			return "request granted (succeeded)";
		case SOCKS4_REP_REJECTED:
			return "request rejected or failed";
		case SOCKS4_REP_IDENT_FAIL:
			return "cannot connect identd";
		case SOCKS4_REP_USERID:
			return "user id not matched";
		default:
			throw new SocksException("Response " + response
					+ " not implemented.");
		}
	}
}