package org.kangmo.tradeapi;

public class JCoinChannel {
	private API.Channel channel;
	public JCoinChannel(API.Channel channel) {
		this.channel = channel;
	}

	public CoinAddress assignInAddress() throws APIException {
		return API.sync(continuation -> {
			return channel.getCoin().assignInAddress(continuation);
		} );
	}

	public CoinOutRequest requestCoinOut(String destAddress, java.math.BigDecimal btc) throws APIException {
		return API.sync(continuation -> {
			return channel.getCoin().requestCoinOut(new Amount("btc", btc), new CoinAddress(destAddress), continuation );
		} );
	}

	public java.util.List<CoinStatus> queryCoinOut(CoinOutRequest req) throws APIException {
		return API.sync(continuation -> {
			return channel.getCoin().queryCoinOut(req, continuation );
		} );

	}
	public void cancelCoinOut(CoinOutRequest req) throws APIException {
		API.sync(continuation -> {
			return channel.getCoin().cancelCoinOut(req, continuation );
		} );
	}
}
