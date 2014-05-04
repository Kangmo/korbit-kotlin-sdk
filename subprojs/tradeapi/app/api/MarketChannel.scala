package org.kangmo.tradeapi

import org.kangmo.http._
import org.kangmo.helper._

import java.math.BigDecimal

case class Ticker (	
	timestamp: java.sql.Timestamp,
	last: BigDecimal
)

case class FullTicker (	
	timestamp: java.sql.Timestamp,
	last: BigDecimal,
	bid: Option[Long],
	ask: Option[Long],
	low: Option[BigDecimal],
	high: Option[BigDecimal],
	volume: Option[BigDecimal]
)

case class OrderBook (
	timestamp: java.sql.Timestamp,
	// The bid price represents the maximum price that a buyer or buyers are willing to pay
	bids : Seq[Seq[BigDecimal]],
	// The ask price represents the minimum price that a seller or sellers are willing to receive
	asks : Seq[Seq[BigDecimal]]
)

case class Transaction (
	timestamp: Option[java.sql.Timestamp],
	tid: Long,
	price: BigDecimal,
	amount: BigDecimal
)

case class TransactionId(id: Long)

class MarketChannel {
	def ticker()( callback : Either[Error, Ticker] => Unit ) {
		HTTPActor.dispatcher ! GetPublicResource("ticker" ) { jsonResponse => 
			val ticker = Json.deserialize[Ticker](jsonResponse)
			callback(Right(ticker))
		}
	}
	def fullTicker()( callback : Either[Error, FullTicker] => Unit ) {
		HTTPActor.dispatcher ! GetPublicResource("ticker/detailed" ) { jsonResponse => 
			val fullTicker = Json.deserialize[FullTicker](jsonResponse)
			callback(Right(fullTicker))
		}		
	}
	def orderbook()( callback : Either[Error, OrderBook] => Unit ) {
		HTTPActor.dispatcher ! GetPublicResource("orderbook?group=true" ) { jsonResponse => 
			val orderbook = Json.deserialize[OrderBook](jsonResponse)
			callback(Right(orderbook))
		}
	}
	def transactions(sinceTransactionId : TransactionId)(callback : Either[Error, Seq[Transaction]] => Unit ) {
		HTTPActor.dispatcher ! GetPublicResource(s"transactions?since=${sinceTransactionId.id}" ) { jsonResponse => 
			val txs = Json.deserialize[Seq[Transaction]](jsonResponse)
			callback(Right(txs))
		}
	}
}