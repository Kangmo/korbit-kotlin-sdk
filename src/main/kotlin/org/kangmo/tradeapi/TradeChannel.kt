package org.kangmo.tradeapi

import org.kangmo.helper.JsonUtil
import org.kangmo.http.HTTPActor

import java.math.BigDecimal
import java.util.concurrent.CompletableFuture

enum class OrderSide(val side:String) {
	BuyOrder("buy"),
	SellOrder("sell")
}

data class OrderId(val id:Long)

data class OpenOrder (
	val timestamp : java.sql.Timestamp,
	val id : Long,
	val `type` : String, // "bid" or "ask"
	val price : Price,
	val total : Amount,
	val open : Amount
)

abstract class TransactionDetail {}

data class FillsDetail(
	val price : Price,
	val amount : Amount,
	val orderId : Int
): TransactionDetail()

data class FiatsDetail(
	val amount : Amount,
	val depositName: String?,
	val `in` : FiatAddress?,
	val `out` : FiatAddress?
): TransactionDetail()

data class CoinsDetail(
	val amount : Amount,
	val transactionId : String?,
	val `in`  : CoinAddress?,
	val `out` : CoinAddress?
): TransactionDetail()


// parameter category to get UserTransactions can be either fills,krw, or btc.
data class UserTransaction (
	val timestamp : java.sql.Timestamp,
	val completedAt : java.sql.Timestamp?,
	val id : Long,
	// List of types for different categories.
	// "buy" : [fills] market trade - buy
	// "sell" : [fills] market trade - sell
	// "fiat-in" : [krw] KRW deposit
	// "fiat-out" : [krw] KRW withdrawal
	// "coin-in" : [btc] BTC receival from BTC address
	// "coin-out" : [btc] BTC transfer to BTC address
	val `type` : String,
	val fee : Amount?,
	// KRW, BTC balance of the user wallet after the transaction happened.
	val balances : Amount?,
	// Detailed information for each type of transaction.
	val fillsDetail : FillsDetail?,
	val fiatsDetail : FiatsDetail?,
	val coinsDetail : CoinsDetail?
)

enum class TransactionCategory(val category : String) {
	FillsCategory("fills"),
	CoinsCategory("coins"),
	FiatsCategory("fiats")
}

data class CancelOrderResult(val orderId : Long, val status : String)

private data class PlaceOrderResult(val orderId : Long, val status : String)

class TradeChannel(val context : Context): AbstractUserChannel(context) {
	suspend fun transactions(categories : List<TransactionCategory> = listOf(),
		             orderId : OrderId? = null,
		             pageDesc : PageDesc? = null): List<UserTransaction> {
		val categoryParam : List<String> = categories.map{ c -> "category=${c.category}"}
		val orderIdParam : String? = if (orderId == null) null else "order_id=${orderId.id}"
		val pageDescParam : String? = if (pageDesc == null) null else "offset=${pageDesc.offset}&limit=${pageDesc.limit}"

		val params = listOf(categoryParam,
						  if (orderIdParam == null) listOf() else listOf(orderIdParam),
						  if (pageDescParam == null) listOf() else listOf(pageDescParam) ).flatMap{x -> x}.joinToString("&")
		
		return getUserFuture<List<UserTransaction>>("user/transactions?$params")
	}
	
	suspend fun openOrders() = getUserFuture<List<OpenOrder>>("user/orders/open")
	
	private suspend fun placeOrder(orderSide:OrderSide, postData: String): OrderId {
		val future = CompletableFuture<OrderId>()
		
		HTTPActor.dispatcher.send( PostUserResource(context, "user/orders/${orderSide.side}", postData ) { jsonResponse ->
			val placeOrderResult = JsonUtil.get().fromJson(jsonResponse, PlaceOrderResult::class.java)

			if (placeOrderResult.status == "success") future.complete( OrderId(placeOrderResult.orderId) )
			else future.obtrudeException( APIException( placeOrderResult.status ) )
		} )

		return future.get()
	}

	suspend fun placeLimitOrder(orderSide:OrderSide, price:Price, amount:Amount): OrderId {
		// BUGBUG : Need to send amount.currency
		val postData = "type=limit&currency=${price.currency}&price=${price.value}&coin_amount=${amount.value}"
		return placeOrder(orderSide, postData)
	}

	suspend fun placeMarketOrder(orderSide:OrderSide, amount:Amount): OrderId {
		// BUGBUG : Need to send amount.currency
		val postData =
			"type=market&currency=krw" +
			(if (orderSide.side == "buy") "&fiat_amount=${amount.value}" else "&coin_amount=${amount.value}")
		
		return placeOrder(orderSide, postData)
	}

	suspend fun  cancelOrder(orderIds:List<OrderId>): List<CancelOrderResult> {

		val postData = orderIds.map{ orderId -> "id=${orderId.id}"}.joinToString("&")
		
		return postUserFuture<List<CancelOrderResult>>("user/orders/cancel", postData)
	}
}

