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
  date: Option[Long],
	tid: Long,
	price: BigDecimal,
	amount: BigDecimal
)

case class TransactionId(id: Long)

class MarketChannel extends AbstractChannel {
	def ticker() = getPublicFuture[Ticker]("ticker")

	def fullTicker() = getPublicFuture[FullTicker]("ticker/detailed")

	def orderbook() = getPublicFuture[OrderBook]("orderbook?group=true")

	def transactions(sinceTransactionId : TransactionId) = {
		getPublicFuture[Seq[Transaction]](s"transactions?since=${sinceTransactionId.id}")
	}
}