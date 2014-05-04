package org.kangmo.tradeapi

import org.kangmo.http._
import org.kangmo.helper._

import java.math.BigDecimal

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

class TradeChannel(context : Context)  {
	def transactions(categories : Seq[TransactionCategory] = Seq(), 
		             orderId : Option[OrderId] = None, 
		             pageDesc : Option[PageDesc] = None)
		            (callback : Either[Error, Seq[UserTransaction]] => Unit ) {
		val categoryParam : Seq[String] = categories.map{ c => s"category=${c.category}"}
		val orderIdParam : Option[String] = orderId.map{ orderId => s"order_id=${orderId.id}"}
		val pageDescParam : Option[String] = pageDesc.map{ pageDesc => s"offset=${pageDesc.offset}&limit=${pageDesc.limit}"}

		val params = List(categoryParam, 
						  if (orderIdParam == None) Seq() else Seq(orderIdParam.get), 
						  if (pageDescParam == None) Seq() else Seq(pageDescParam.get) ).flatMap(x => x).mkString("&")
		
		HTTPActor.dispatcher ! GetUserResource(context, s"user/transactions?$params" ) { jsonResponse => 
			val txs = Json.deserialize[Seq[UserTransaction]](jsonResponse)
			callback(Right(txs))
		}
	}
	
	def openOrders()(callback : Either[Error, Seq[OpenOrder]] => Unit ) {
		HTTPActor.dispatcher ! GetUserResource(context, "user/orders/open" ) { jsonResponse => 
			val openOrders = Json.deserialize[Seq[OpenOrder]](jsonResponse)
			callback(Right(openOrders))
		}
	}
	
	private def placeOrder(orderSide:OrderSide, postData: String, callback : Either[Error, OrderId] => Unit) {
		HTTPActor.dispatcher ! PostUserResource(context, s"user/orders/${orderSide.side}", postData ) { jsonResponse => 
			val placeOrderResult = Json.deserialize[PlaceOrderResult](jsonResponse)
			val result = 
				if (placeOrderResult.status == "success") Right( OrderId(placeOrderResult.orderId) ) 
				else Left( Error(placeOrderResult.status) )

			callback(result)
		}
	}

	def placeLimitOrder(orderSide:OrderSide, price:Price, amount:Amount)(callback : Either[Error, OrderId] => Unit) {
		// BUGBUG : Need to use price.currency instead of krw
		val postData = s"type=limit&currency=krw&price=${price.value}&coin_amount=${amount.value}"
		placeOrder(orderSide, postData, callback)
	}

	def placeMarketOrder(orderSide:OrderSide, amount:Amount)(callback : Either[Error, OrderId] => Unit ) {
		// BUGBUG : Need to add an input parameter instead of hard-coding krw
		val postData = 
			s"type=market&currency=krw" + 
			(if (orderSide.side == "buy") s"&fiat_amount=${amount.value}" else s"&coin_amount=${amount.value}")
		
		placeOrder(orderSide, postData, callback)
	}

	def cancelOrder(orderIds:Seq[OrderId])(callback : Seq[CancelOrderResult] => Unit) {

		val postData = orderIds.map{ orderId => s"id=${orderId.id}"}.mkString("&")
		
		HTTPActor.dispatcher ! PostUserResource(context, "user/orders/cancel", postData ) { jsonResponse => 
			val result = Json.deserialize[Seq[CancelOrderResult]](jsonResponse)
			callback(result)
		}
	}
}

