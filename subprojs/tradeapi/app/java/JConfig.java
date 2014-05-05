package org.kangmo.tradeapi;

public class JConfig {
	public static String getUrlPrefix() {
		return URLPrefix$.MODULE$.prefix();
	}
	public static void setUrlPrefix(String prefix) {		
		URLPrefix$.MODULE$.prefix_$eq(prefix);
	}
}