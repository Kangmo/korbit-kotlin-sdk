package org.kangmo.tradeapi

import java.math.BigDecimal

class APIException(status:String): Exception(status)

data class Price(val currency: String, val value: BigDecimal)
data class Amount(val currency: String, val value: BigDecimal)

data class PageDesc(val offset: Long, val limit : Long)

abstract class MoneyAddress {
}

data class FiatAddress(
	val bank : String,
	val account : String,
	val owner : String?
): MoneyAddress()

data class CoinAddress(
	val address : String
): MoneyAddress()

data class UnifiedMoneyAddress(
	val bank : String?,
	val account : String?,
	val owner : String?,
	val address : String?
)

data class Address(
	val alias : String,
	val currency : String,
	val address : UnifiedMoneyAddress,
	val registeredOwner: String?,
	val status: String?
)

data class CoinOutStatus(
	val status: String,
  val transferType : String,
  val transferId: Long)