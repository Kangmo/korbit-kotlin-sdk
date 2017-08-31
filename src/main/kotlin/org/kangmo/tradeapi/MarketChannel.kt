package org.kangmo.tradeapi

import java.math.BigDecimal

data class Ticker (
	val timestamp: java.sql.Timestamp,
	val last: BigDecimal
)

data class FullTicker (
	val timestamp: java.sql.Timestamp,
	val last: BigDecimal,
	val bid: Long?,
	val ask: Long?,
	val low: BigDecimal?,
	val high: BigDecimal?,
	val volume: BigDecimal?
)

data class OrderBook (
	val timestamp: java.sql.Timestamp,
	// The bid price represents the maximum price that a buyer or buyers are willing to pay
	val bids : List<List<BigDecimal>>,
	// The ask price represents the minimum price that a seller or sellers are willing to receive
	val asks : List<List<BigDecimal>>
)

data class Transaction (
	val timestamp: java.sql.Timestamp?,
	val tid: Long,
	val price: BigDecimal,
	val amount: BigDecimal
)

data class TransactionId(val id: Long)


class MarketChannel: AbstractChannel() {
	suspend fun ticker(currencyPair : CurrencyPair = CurrencyPair.BtcKrw) = getPublicFuture<Ticker>("ticker?currency_pair=${currencyPair.pair}", Ticker::class.java)

	suspend fun fullTicker(currencyPair : CurrencyPair = CurrencyPair.BtcKrw) = getPublicFuture<FullTicker>("ticker/detailed?currency_pair=${currencyPair.pair}", FullTicker::class.java)

	suspend fun orderbook(currencyPair : CurrencyPair = CurrencyPair.BtcKrw) = getPublicFuture<OrderBook>("orderbook?currency_pair=${currencyPair.pair}", OrderBook::class.java)

	suspend fun transactions(currencyPair : CurrencyPair = CurrencyPair.BtcKrw, interval : TimeInterval) : List<Transaction> =
		getPublicFuture<List<Transaction>>("transactions?currency_pair=${currencyPair.pair}&time=${interval.unit}", List::class.java as Class<List<Transaction>>)
}