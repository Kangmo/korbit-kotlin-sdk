package org.kangmo.tradeapi

import org.kangmo.helper.JsonUtil
import org.kangmo.http.HTTPActor

import java.math.BigDecimal
import java.util.concurrent.CompletableFuture

enum class OrderSide(val side:String) {
	BuyOrder("buy"),
	SellOrder("sell")
}

data class OrderId( val id:Long )

data class OpenOrder (
	val timestamp : java.sql.Timestamp,
	val id : Long,
	val `type` : String, // "bid" or "ask"
	val price : Price,
	val total : Amount,
	val open : Amount
)

data class Order (
	val id: 						Long,								//	The unique identifier of the order.
	val currency_pair:	String,							//	The type of currency that the order was processed in.
	val side:						String,							//	The type of the order. 'bid’ for bid orders, 'ask’ for ask orders.
	val avg_price:			BigDecimal,					//	The weighted average of the price the currency was traded at.
	val price:					BigDecimal,					//	The price that the user has set the limit to. 0 if the order was a market order.
	val order_amount:		BigDecimal,					//	The amount of the trading currency you ordered. Both in bid(i.e. KRW => BTC) and ask(i.e. BTC => KRW) orders, the amount is displayed in the non-fiat currency(i.e. BTC).
	val filled_amount:	BigDecimal,					//	The amount of the trading currency that has been partially filled. When filledAmount is equal to orderAmount, the order is complete. Shows only if limit trade.
	val created_at:			java.sql.Timestamp,	//	Unix timestamp in milliseconds by the time the order was placed.
	val last_filled_at:	java.sql.Timestamp?,//	Unix timestamp in milliseconds when the order was last filled, either partially or fully. Does not show if not filled at all.
	val status:					String,							//	Current status of the order. Can be one of the following: 'unfilled’, 'partially_filled’, or 'filled’.
	val fee:						BigDecimal					//	The amount of the fee charged. The fee is charged at the quote currency. When the order is a bid order(i.e. KRW => BTC), the fee is charged in BTC. When the order is an ask order(i.e. BTC => KRW), the fee is charged in KRW. Doesn’t show up if the order is unfilled at all.
)

enum class FillStatus(val status: String) {
	Unfilled("unfilled"),
	PartiallyFilled("partially_filled"),
	FullyFilled("filled"),
}

data class CancelOrderResult(val orderId : Long, val status : String)

enum class TransferType(val type: String) {
	Deposit("deposit"),
	Withdrawal("withdrawal")
}

abstract class TransferDetails

// SUB-FIELDS (FOR COIN DEPOSIT/WITHDRAWAL)
data class CoinTransferDetails(
	val transaction_id: 	String, //	Transaction ID of the coin deposit/withdrawal.
	val address: 					String, //	Destination address of the coin.
	val destination_tag:	String? //	Destination tag that shows up only when the transfer is requested in XRP.
) : TransferDetails()

// SUB-FIELDS (FOR FIAT DEPOSIT/WITHDRAWAL)
class FiatTransferDetails (
	val bank:						String, //	Name of the bank used for this transaction.
	val account_number:	String, // 	Account number of the bank
	val owner:					String 	//	Recipient of the bank transfer
) : TransferDetails()

data class Transfer(
	val id: 					Long, 								// The unique identifier of the order.
	val type: 				String, 							//	The type of the transfer, which is either deposit or withdrawal.
	val currency: 		String, 							//	The currency that the transfer was made in.
	val amount:				BigDecimal, 					//	The amount of the deposit/withdrawal.
	val completed_at: java.sql.Timestamp?, 	// 	Unix timestamp in milliseconds when the transfer order is complete. Shows only when the order is complete.
	val updated_at : 	java.sql.Timestamp,		//	Unix timestamp in milliseconds when the transfer order was last updated. The transfers are ordered by this field, most recently updated transfer showing at the top.
	val created_at: 	java.sql.Timestamp,		//	Unix timestamp in milliseconds by the time the transfer order was initiated.
	val status: 			String, 							//	Current status of the order.
	val fee: 					BigDecimal, 					// 	The amount of the fee that was charged for withdrawal. Shows only for filled withdrawals that a fee was incurred. The currency is equivalent to the currency of the withdrawal.
	val details: 			TransferDetails
)

private data class PlaceOrderResult(val orderId : Long, val status : String, val currency_pair: String)

data class VolumeDetails (
	val volume: 		BigDecimal, //	User’s trading volume in the corresponding exchange during the last 30 days in KRW.
	val maker_fee: 	BigDecimal, //	The rate of trading fee for maker in basis points(BPS).
	val taker_fee: 	BigDecimal //	The rate of trading fee for taker in basis points(BPS).
)

data class Volumes(
	val btc_krw: 			VolumeDetails,
	val eth_krw: 			VolumeDetails,
	val etc_krw: 			VolumeDetails,
	val total_volume:	BigDecimal, // 	User’s total trading volume of all exchanges in KRW.
	val timestamp:		java.sql.Timestamp  //	Unix timestamp in milliseconds of the last time when trading volume and fees are calculated on an hourly basis.
)

class TradeChannel(val context : Context): AbstractUserChannel(context) {
	suspend fun volumes(currencyPair: CurrencyPair? = null) : Volumes {
		val url = "user/volume" +
			if (currencyPair == null) "?currency_pair=all" else "?currency_pair=${currencyPair.pair}"

		return getUserFuture<Volumes>(url, Volumes::class.java)
	}

	suspend fun transfers(currencyPair: CurrencyPair, transferType: TransferType? = null, pageDesc: PageDesc? = null) : List<Transfer> {
		val url = "user/transfers?currency_pair=${currencyPair.pair}" +
							if (transferType == null) "&type=all" else "&type=${transferType.type}" +
							if (pageDesc == null) "" else "&offset=${pageDesc.offset}&limit=${pageDesc.limit}"

		return getUserFuture<List<Transfer>>(url, List::class.java as Class<List<Transfer>>)
	}

	suspend fun orders(currencyPair: CurrencyPair, statuses: List<FillStatus>, orderIds:List<OrderId>, pageDesc: PageDesc? = null) : List<Order> {
		val statusList = statuses.map{ status -> "status=${status.status}"}.joinToString("&")
		val orderIdList = orderIds.map{ orderId -> "id=${orderId.id}"}.joinToString("&")

		val url = "user/orders?currency_pair=${currencyPair.pair}" +
			if (statusList == "") "" else "&${statusList}" +
			if (orderIdList == "") "" else "&${orderIdList}" +
			if (pageDesc == null) "" else "&offset=${pageDesc.offset}&limit=${pageDesc.limit}"

		return getUserFuture<List<Order>>(url, List::class.java as Class<List<Order>>)
	}

	suspend fun openOrders(currencyPair: CurrencyPair, pageDesc: PageDesc?) : List<OpenOrder> {
		val url = "user/orders/open?currency_pair=${currencyPair.pair}" +
			         if (pageDesc == null) "" else "&offset=${pageDesc.offset}&limit=${pageDesc.limit}"

		return getUserFuture<List<OpenOrder>>(url, List::class.java as Class<List<OpenOrder>>)
	}
	private suspend fun placeOrder(orderSide:OrderSide, postData: String): OrderId {
		val future = CompletableFuture<OrderId>()
		
		HTTPActor.dispatcher.send( PostUserResource(context, "user/orders/${orderSide.side}", postData ) { jsonResponse ->
			val placeOrderResult = JsonUtil.get().fromJson(jsonResponse, PlaceOrderResult::class.java)

			if (placeOrderResult.status == "success") future.complete( OrderId(placeOrderResult.orderId) )
			else future.obtrudeException( APIException( placeOrderResult.status ) )
		} )

		return future.get()
	}

	suspend fun placeLimitOrder(currencyPair: CurrencyPair, orderSide:OrderSide, price:Price, amount:Amount): OrderId {
		// BUGBUG : Need to send amount.currency
		val postData = "currency_pair=${currencyPair.pair}&type=limit&price=${price.value}&coin_amount=${amount.value}"
		return placeOrder(orderSide, postData)
	}

	suspend fun placeMarketOrder(currencyPair: CurrencyPair, orderSide:OrderSide, amount:Amount): OrderId {
		// BUGBUG : Need to send amount.currency
		val postData =
			"currency_pair=${currencyPair.pair}&type=market&" +
			(if (orderSide.side == "buy") "fiat_amount=${amount.value}" else "coin_amount=${amount.value}")
		
		return placeOrder(orderSide, postData)
	}

	suspend fun cancelOrder(currencyPair: CurrencyPair, orderIds:List<OrderId>): List<CancelOrderResult> {

		val postData = "currency_pair=${currencyPair.pair}&"+orderIds.map{ orderId -> "id=${orderId.id}"}.joinToString("&")
		
		return postUserFuture<List<CancelOrderResult>>("user/orders/cancel", postData, List::class.java as Class<List<CancelOrderResult>>)
	}
}

