package org.kangmo.tradeapi;

import scala.collection.convert.WrapAsJava$;
import scala.collection.convert.WrapAsScala$;

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
		scala.collection.Seq<TransactionCategory> seq = WrapAsScala$.MODULE$.asScalaBuffer( categories ).toList();
		scala.Option<OrderId> orderIdOption = scala.Option.empty();
		scala.Option<PageDesc> pageOption = scala.Option.empty();
		if (orderId != null) orderIdOption = new scala.Some<OrderId>(orderId);
		if (pageDesc != null) pageOption = new scala.Some<PageDesc>(pageDesc);

		java.util.List<UserTransaction> txs = WrapAsJava$.MODULE$.seqAsJavaList( JHelper.wait( channel.order().transactions(seq, orderIdOption, pageOption) ) );	
		return txs;
	}

	public java.util.List<OpenOrder> openOrders() throws APIException {
		return WrapAsJava$.MODULE$.seqAsJavaList( JHelper.wait(channel.order().openOrders() ) );
	}

	public OrderId placeLimitOrder(JORDER_TYPE type, long krw, double btc) throws APIException {
		OrderId id = JHelper.wait( channel.order().placeLimitOrder(new BuyOrder(), 
		                                    					  new Price("krw", new java.math.BigDecimal(krw)), 
		                                    					  new Amount("btc", new java.math.BigDecimal(btc))) );
		return id;
	}

	public OrderId placeMarketOrder(JORDER_TYPE type, long krw) throws APIException {
		if (type != JORDER_TYPE.BUY)
			throw new APIException("invalid_request");
		OrderId id = JHelper.wait( channel.order().placeMarketOrder(new BuyOrder(), 
		                                                		   new Amount("krw", new java.math.BigDecimal(krw))) );
		return id;		
	}

	public OrderId placeMarketOrder(JORDER_TYPE type, double btc) throws APIException {
		if (type != JORDER_TYPE.SELL)
			throw new APIException("invalid_request");
		OrderId id = JHelper.wait( channel.order().placeMarketOrder(new SellOrder(), 
		                                                		   new Amount("btc", new java.math.BigDecimal(btc))) );			
		return id;		
	}

	public java.util.List<CancelOrderResult> cancelOrder(java.util.List<OrderId> orderIds) throws APIException {
		scala.collection.Seq<OrderId> orderIdSeq = WrapAsScala$.MODULE$.asScalaBuffer( orderIds ).toList();

		java.util.List<CancelOrderResult> result = WrapAsJava$.MODULE$.seqAsJavaList( JHelper.wait( channel.order().cancelOrder(orderIdSeq) ) );			

		return result;
	}
}
