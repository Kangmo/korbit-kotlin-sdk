package org.kangmo.tradeapi;

public class JMarketChannel {
	public JMarketChannel() {}
	public Ticker ticker() throws APIException {
		return API.sync(continuation -> {
			return API.getMarket().ticker(continuation);
		} );
	}
	public FullTicker fullTicker() throws APIException {
		return API.sync(continuation -> {
			return API.getMarket().fullTicker(continuation);
		} );
	}
	public OrderBook orderbook() throws APIException {
		return API.sync(continuation -> {
			return API.getMarket().orderbook(continuation);
		} );
	}
	public java.util.List<Transaction> transactions(Long sinceTxId) throws APIException {
		TransactionId since = new TransactionId(sinceTxId);

		return API.sync(continuation -> {
			return API.getMarket().transactions(since, continuation);
		} );
	}
}