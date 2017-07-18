package org.kangmo.tradeapi;

import scala.collection.convert.WrapAsJava$;
import scala.collection.convert.WrapAsScala$;

public class JFiatChannel {
	private API.Channel channel;
	public JFiatChannel(API.Channel channel) {
		this.channel = channel;
	}

	public FiatAddress assignInAddress() throws APIException {
		return JHelper.wait( channel.fiat().assignInAddress() );
	}

	public FiatAddress registerOutAddress(String bankName, String accountNumber) throws APIException {
		scala.Option<String> none = scala.Option.empty();
		FiatAddress outAddress = JHelper.wait( channel.fiat().registerOutAddress(new FiatAddress(bankName, accountNumber, none)) );
		return outAddress;
	}

	public FiatOutRequest requestFiatOut(long krw) throws APIException {
		FiatOutRequest req = JHelper.wait( channel.fiat().requestFiatOut(new Amount("krw", new java.math.BigDecimal(krw))) );
		return req;
	}

	public java.util.List<FiatStatus> queryFiatOut() throws APIException {
		return queryFiatOut(null);
	}

	public java.util.List<FiatStatus> queryFiatOut(FiatOutRequest req) throws APIException {
		scala.Option<FiatOutRequest> reqOption = scala.Option.empty();
		if (req != null) 
			reqOption = new scala.Some<FiatOutRequest>(req);
		java.util.List<FiatStatus> statuses = WrapAsJava$.MODULE$.seqAsJavaList( JHelper.wait( channel.fiat().queryFiatOut(reqOption) ) );
		return statuses;
	}
	public void cancelFiatOut(FiatOutRequest req) throws APIException {
		JHelper.wait( channel.fiat().cancelFiatOut(req) );
	}
}