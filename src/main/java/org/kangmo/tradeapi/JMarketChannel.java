package org.kangmo.tradeapi;

import scala.collection.convert.WrapAsJava$;
import scala.collection.convert.WrapAsScala$;

public class JMarketChannel {
	public JMarketChannel() {}
	public Ticker ticker() throws APIException {
		return JHelper.wait( API$.MODULE$.market().ticker() );
	}
	public FullTicker fullTicker() throws APIException {
		return JHelper.wait( API$.MODULE$.market().fullTicker() );
	}
	public OrderBook orderbook() throws APIException {
		return JHelper.wait( API$.MODULE$.market().orderbook() );
	}
	public java.util.List<Transaction> transactions(Long sinceTxId) throws APIException {
		TransactionId since = new TransactionId(sinceTxId);
		return WrapAsJava$.MODULE$.seqAsJavaList( JHelper.wait( API$.MODULE$.market().transactions(since) ) );
	}
}