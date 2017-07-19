package org.kangmo.tradeapi;

public class JTradeChannel {
	private API.Channel channel;
	public JTradeChannel(API.Channel channel) {
		this.channel = channel;
	}

	public java.util.List<UserTransaction> transactions() throws APIException {
		return transactions_2args(null, null);
	}
	public java.util.List<UserTransaction> transactions(OrderId orderId) throws APIException {
		return transactions_2args(orderId, null);
	}
	public java.util.List<UserTransaction> transactions(PageDesc pageDesc) throws APIException {
		return transactions_2args(null, pageDesc);
	}
	public java.util.List<UserTransaction> transactions(java.util.List<TransactionCategory> categories) throws APIException {
		return transactions_3args(categories, null, null);
	}
	public java.util.List<UserTransaction> transactions(java.util.List<TransactionCategory> categories, OrderId orderId) throws APIException {
		return transactions_3args(categories, orderId, null);
	}
	public java.util.List<UserTransaction> transactions(java.util.List<TransactionCategory> categories, PageDesc pageDesc) throws APIException {
		return transactions_3args(categories, null, pageDesc);
	}
	private java.util.List<UserTransaction> transactions_2args(OrderId orderId, PageDesc pageDesc) throws APIException {
		return transactions_3args(new java.util.ArrayList<TransactionCategory>(), orderId, pageDesc);
	}
	private java.util.List<UserTransaction> transactions_3args(java.util.List<TransactionCategory> categories, OrderId orderId, PageDesc pageDesc) throws APIException {
		java.util.List<UserTransaction> txs = API.sync(continuation -> {
			return channel.getOrder().transactions(categories, orderId, pageDesc, continuation);
		} );
		return txs;
	}

	public java.util.List<OpenOrder> openOrders() throws APIException {
		return API.sync(continuation -> {
			return channel.getOrder().openOrders(continuation);
		} );
	}

	public OrderId placeLimitOrder(OrderSide type, long krw, double btc) throws APIException {
		return API.sync(continuation -> {
			return channel.getOrder().placeLimitOrder(
				type,
				new Price("krw", new java.math.BigDecimal(krw)),
				new Amount("btc", new java.math.BigDecimal(btc)),
				continuation
			);
		} );
	}

	public OrderId placeMarketOrder(OrderSide type, long krw) throws APIException {
		if (type != OrderSide.BuyOrder)
			throw new APIException("invalid_request");

		return API.sync(continuation -> {
			return channel.getOrder().placeMarketOrder(
				OrderSide.BuyOrder,
				new Amount("krw", new java.math.BigDecimal(krw)),
				continuation
			);
		} );
	}

	public OrderId placeMarketOrder(OrderSide type, double btc) throws APIException {
		if (type != OrderSide.SellOrder)
			throw new APIException("invalid_request");

		return API.sync(continuation -> {
			return channel.getOrder().placeMarketOrder(
				OrderSide.SellOrder,
				new Amount("btc", new java.math.BigDecimal(btc)),
				continuation
			);
		} );
	}

	public java.util.List<CancelOrderResult> cancelOrder(java.util.List<OrderId> orderIds) throws APIException {
		return API.sync(continuation -> {
			return channel.getOrder().cancelOrder(orderIds, continuation);
		} );
	}
}
