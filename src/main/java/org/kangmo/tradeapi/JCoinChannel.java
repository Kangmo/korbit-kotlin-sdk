package org.kangmo.tradeapi;

public class JCoinChannel {
	private API.Channel channel;
	public JCoinChannel(API.Channel channel) {
		this.channel = channel;
	}

	public CoinAddress assignBtcInAddress() throws APIException {
		return API.sync(continuation -> {
			return channel.getCoin().assignBtcInAddress(continuation);
		} );
	}

	public CoinOutRequest requestBtcOut(String destAddress, java.math.BigDecimal btc, FeePriority priority) throws APIException {
		return API.sync(continuation -> {
			return channel.getCoin().requestBtcOut(new Amount("btc", btc), new CoinAddress(destAddress), priority, continuation );
		} );
	}

	public java.util.List<CoinStatus> queryBtcOut(CoinOutRequest req) throws APIException {
		return API.sync(continuation -> {
			return channel.getCoin().queryBtcOut(req, continuation );
		} );

	}
	public void cancelBtcOut(CoinOutRequest req) throws APIException {
		API.sync(continuation -> {
			return channel.getCoin().cancelBtcOut(req, continuation );
		} );
	}
}
