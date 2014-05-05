package org.kangmo.tradeapi;

import scala.collection.convert.WrapAsJava$;
import scala.collection.convert.WrapAsScala$;

public class JCoinChannel {
	private API.Channel channel;
	public JCoinChannel(API.Channel channel) {
		this.channel = channel;
	}

	public CoinAddress assignInAddress() throws APIException {
		CoinAddress inAddress = JHelper.wait( channel.coin().assignInAddress() );
		return inAddress;
	}

	public CoinOutRequest requestCoinOut(String destAddress, double btc) throws APIException {
		CoinOutRequest req = JHelper.wait( channel.coin().requestCoinOut(new Amount("btc", new java.math.BigDecimal(btc)), new CoinAddress(destAddress) ) );
		return req;
	}

	public java.util.List<CoinStatus> queryCoinOut(CoinOutRequest req) throws APIException {
		scala.Option<CoinOutRequest> reqOption = scala.Option.empty();
		if (req != null) 
			reqOption = new scala.Some<CoinOutRequest>(req);

	    java.util.List<CoinStatus> statuses = WrapAsJava$.MODULE$.seqAsJavaList( JHelper.wait( channel.coin().queryCoinOut(reqOption) ) );
	    return statuses; 
	}
	public void cancelCoinOut(CoinOutRequest req) throws APIException {
		JHelper.wait( channel.coin().cancelCoinOut(req) );
	}
}
