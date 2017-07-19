package org.kangmo.tradeapi;

public class JFiatChannel {
	private API.Channel channel;
	public JFiatChannel(API.Channel channel) {
		this.channel = channel;
	}

	public FiatAddress assignInAddress() throws APIException {
		return API.sync(continuation -> {
			return channel.getFiat().assignInAddress(continuation);
		} );
	}

	public FiatAddress registerOutAddress(String bankName, String accountNumber) throws APIException {
		return API.sync(continuation -> {
			return channel.getFiat().registerOutAddress(new FiatAddress(bankName, accountNumber, null), continuation);
		} );
	}

	public FiatOutRequest requestFiatOut(long krw) throws APIException {
		return API.sync(continuation -> {
			return channel.getFiat().requestFiatOut(new Amount("krw", new java.math.BigDecimal(krw)), continuation);
		} );
	}

	public java.util.List<FiatStatus> queryFiatOut() throws APIException {
		return queryFiatOut(null);
	}

	public java.util.List<FiatStatus> queryFiatOut(FiatOutRequest req) throws APIException {
		return API.sync(continuation -> {
			return channel.getFiat().queryFiatOut(req, continuation);
		} );
	}
	public void cancelFiatOut(FiatOutRequest req) throws APIException {
		API.sync(continuation -> {
			return channel.getFiat().cancelFiatOut(req, continuation);
		} );
	}
}