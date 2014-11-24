package org.kangmo.tradeapi

import org.kangmo.http._
import org.kangmo.helper._

import java.math.BigDecimal
import scala.concurrent._

abstract class OrderSide(val side:String)
case class BuyOrder() extends OrderSide("buy")
case class SellOrder() extends OrderSide("sell")

case class OrderId(id:Long)

case class OpenOrder (
	timestamp : java.sql.Timestamp,
	id : Long,
	`type` : String, // "bid" or "ask"
	price : Price,
	total : Amount,
	open : Amount
)

abstract class TransactionDetail {}

case class FillsDetail(
	price : Price,
	amount : Amount,
	orderId : Int
) extends TransactionDetail

case class FiatsDetail(
	amount : Amount,
	depositName: Option[String],
	in : Option[FiatAddress],
	out : Option[FiatAddress]
) extends TransactionDetail

case class CoinsDetail(
	amount : Amount,
	transactionId : Option[String],
	in  : Option[CoinAddress],
	out : Option[CoinAddress]
) extends TransactionDetail


// parameter category to get UserTransactions can be either fills,krw, or btc.
case class UserTransaction (
	timestamp : java.sql.Timestamp,
	completedAt : Option[java.sql.Timestamp],
	id : Long,
	// List of types for different categories.
	// "buy" : [fills] market trade - buy
	// "sell" : [fills] market trade - sell
	// "fiat-in" : [krw] KRW deposit
	// "fiat-out" : [krw] KRW withdrawal
	// "coin-in" : [btc] BTC receival from BTC address
	// "coin-out" : [btc] BTC transfer to BTC address
	`type` : String, 
	fee : Option[Amount],
	// KRW, BTC balance of the user wallet after the transaction happened.
	balances : Seq[Amount],
	// Detailed information for each type of transaction.
	fillsDetail : Option[FillsDetail],
 	fiatsDetail : Option[FiatsDetail],
	coinsDetail : Option[CoinsDetail]
)

abstract class TransactionCategory(val category : String)
case class FillsCategory() extends TransactionCategory("fills")
case class CoinsCategory() extends TransactionCategory("coins")
case class FiatsCategory() extends TransactionCategory("fiats")

case class CancelOrderResult(orderId : Long, status : String)

private case class PlaceOrderResult(orderId : Long, status : String)

class TradeChannel(context : Context) extends AbstractUserChannel(context) {
	def transactions(categories : Seq[TransactionCategory] = Seq(), 
		             orderId : Option[OrderId] = None, 
		             pageDesc : Option[PageDesc] = None) = {
		val categoryParam : Seq[String] = categories.map{ c => s"category=${c.category}"}
		val orderIdParam : Option[String] = orderId.map{ orderId => s"order_id=${orderId.id}"}
		val pageDescParam : Option[String] = pageDesc.map{ pageDesc => s"offset=${pageDesc.offset}&limit=${pageDesc.limit}"}

		val params = List(categoryParam, 
						  if (orderIdParam == None) Seq() else Seq(orderIdParam.get), 
						  if (pageDescParam == None) Seq() else Seq(pageDescParam.get) ).flatMap(x => x).mkString("&")
		
		getUserFuture[Seq[UserTransaction]](s"user/transactions?$params")
	}
	
	def openOrders() = getUserFuture[Seq[OpenOrder]]("user/orders/open")
	
	private def placeOrder(orderSide:OrderSide, postData: String) = {
		val p = Promise[OrderId]
		
		HTTPActor.dispatcher ! PostUserResource(context, s"user/orders/${orderSide.side}", postData ) { jsonResponse => 
			val placeOrderResult = Json.deserialize[PlaceOrderResult](jsonResponse)

			if (placeOrderResult.status == "success") p success OrderId(placeOrderResult.orderId) 
			else p failure new APIException( placeOrderResult.status ) 
		}

		p.future
	}

	def placeLimitOrder(orderSide:OrderSide, price:Price, amount:Amount) = {
		// BUGBUG : Need to use price.currency instead of krw
		val postData = s"type=limit&currency=krw&price=${price.value}&coin_amount=${amount.value}"
		placeOrder(orderSide, postData)
	}

	def placeMarketOrder(orderSide:OrderSide, amount:Amount) = {
		// BUGBUG : Need to add an input parameter instead of hard-coding krw
		val postData = 
			s"type=market&currency=krw" + 
			(if (orderSide.side == "buy") s"&fiat_amount=${amount.value}" else s"&coin_amount=${amount.value}")
		
		placeOrder(orderSide, postData)
	}

	def cancelOrder(orderIds:Seq[OrderId]) = {

		val postData = orderIds.map{ orderId => s"id=${orderId.id}"}.mkString("&")
		
		postUserFuture[Seq[CancelOrderResult]]("user/orders/cancel", postData)
	}
}

