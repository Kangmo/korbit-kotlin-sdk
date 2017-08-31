package org.kangmo.tradeapi;

public class JMarketChannel {
	public JMarketChannel() {}
	public Ticker ticker(CurrencyPair currencyPair) throws APIException {
		return API.sync(continuation -> {
			return API.getMarket().ticker(currencyPair, continuation);
		} );
	}
	public FullTicker fullTicker(CurrencyPair currencyPair) throws APIException {
		return API.sync(continuation -> {
			return API.getMarket().fullTicker(currencyPair, continuation);
		} );
	}
	public OrderBook orderbook(CurrencyPair currencyPair) throws APIException {
		return API.sync(continuation -> {
			return API.getMarket().orderbook(currencyPair, continuation);
		} );
	}
	public java.util.List<Transaction> transactions(CurrencyPair currencyPair, TimeInterval interval) throws APIException {
		return API.sync(continuation -> {
			return API.getMarket().transactions(currencyPair, interval, continuation);
		} );
	}
}