package org.kangmo.tradeapi;


public class JTradeChannel {
	private API.Channel channel;
	public JTradeChannel(API.Channel channel) {
		this.channel = channel;
	}

	public Volumes volumes( CurrencyPair currencyPair) {
		return API.sync(continuation -> {
			return channel.getOrder().volumes(currencyPair, continuation);
		} );
	}

	public java.util.List<Transfer> transfers( CurrencyPair currencyPair, TransferType transferType) {
		return transfers(currencyPair, transferType, null);
	}

	public java.util.List<Transfer> transfers( CurrencyPair currencyPair) {
		return transfers(currencyPair, null, null);
	}

	public java.util.List<Transfer> transfers( CurrencyPair currencyPair, TransferType transferType, PageDesc pageDesc) {
		return API.sync(continuation -> {
			return channel.getOrder().transfers(currencyPair, transferType, pageDesc, continuation);
		} );
	}

	public java.util.List<Order> orders(CurrencyPair currencyPair, java.util.List<FillStatus> statuses, java.util.List<OrderId> orderIds) throws APIException {
		return orders(currencyPair, statuses, orderIds, null);
	}

	public java.util.List<Order> orders(CurrencyPair currencyPair, java.util.List<FillStatus> statuses, java.util.List<OrderId> orderIds, PageDesc pageDesc) throws APIException {
		return API.sync(continuation -> {
			return channel.getOrder().orders(currencyPair, statuses, orderIds, pageDesc, continuation);
		} );
	}

	public java.util.List<OpenOrder> openOrders(CurrencyPair currencyPair, PageDesc pageDesc) throws APIException {
		return API.sync(continuation -> {
			return channel.getOrder().openOrders(currencyPair, pageDesc, continuation);
		} );
	}

	public OrderId placeLimitOrder(CurrencyPair currencyPair, OrderSide type, long krw, String currency, java.math.BigDecimal coinAmount) throws APIException {
		return API.sync(continuation -> {
			return channel.getOrder().placeLimitOrder(
				currencyPair,
				type,
				new Price(new java.math.BigDecimal(krw)),
				new Amount(currency, coinAmount),
				continuation
			);
		} );
	}

	public OrderId placeMarketOrder(CurrencyPair currencyPair, OrderSide type, long krw) throws APIException {
		if (type != OrderSide.BuyOrder)
			throw new APIException("invalid_request");

		return API.sync(continuation -> {
			return channel.getOrder().placeMarketOrder(
				currencyPair,
				OrderSide.BuyOrder,
				new Amount("unused", new java.math.BigDecimal(krw) ),
				continuation
			);
		} );
	}

	public OrderId placeMarketOrder(CurrencyPair currencyPair, OrderSide type, java.math.BigDecimal coinAmount) throws APIException {
		if (type != OrderSide.SellOrder)
			throw new APIException("invalid_request");

		return API.sync(continuation -> {
			return channel.getOrder().placeMarketOrder(
				currencyPair,
				OrderSide.SellOrder,
				new Amount("unused", coinAmount),
				continuation
			);
		} );
	}

	public java.util.List<CancelOrderResult> cancelOrder(CurrencyPair currencyPair, java.util.List<OrderId> orderIds) throws APIException {
		return API.sync(continuation -> {
			return channel.getOrder().cancelOrder(currencyPair, orderIds, continuation);
		} );
	}
}
