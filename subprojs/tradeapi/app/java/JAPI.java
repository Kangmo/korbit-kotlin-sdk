package org.kangmo.tradeapi;

import scala.concurrent.Future;
import scala.concurrent.Promise;

import scala.collection.convert.WrapAsJava$;
import scala.collection.convert.WrapAsScala$;

public class JAPI {
	
	public static Version version() throws APIException {
		return API$.MODULE$.version(); 
	}
	
	public static Constants constants() throws APIException {
		return API$.MODULE$.constants();
	}

	public static JMarketChannel market = new JMarketChannel();

	public static JChannel createChannel(String apiKey, String apiSecret, String username, String password) {
		return new JChannel( API$.MODULE$.createChannel(apiKey, apiSecret, username, password) );
	}
}