package org.kangmo.tradeapi;

public class JAPI {
	
	public static Version version() throws APIException {
		return API.version();
	}
	
	public static Constants constants() throws APIException {
		return API.constants();
	}

	public static JMarketChannel market = new JMarketChannel();

	public static void setHost(String host) { API.setHost(host); }

	public static String getHost() { return API.getHost(); }

	public static JChannel createChannel(String apiKey, String apiSecret, String username, String password) {
		return new JChannel( API.createChannel(apiKey, apiSecret, username, password) );
	}
}