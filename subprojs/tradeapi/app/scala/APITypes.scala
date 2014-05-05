package org.kangmo.tradeapi

import java.math.BigDecimal

class APIException(status:String) extends Exception(status)

case class Price(currency: String, value: BigDecimal)
case class Amount(currency: String, value: BigDecimal)

case class PageDesc(offset: Long, limit : Long)

abstract class MoneyAddress {
}

case class FiatAddress(
	bank : String,
	account : String,
	owner : Option[String]
) extends MoneyAddress

case class CoinAddress(
	address : String 
) extends MoneyAddress

case class UnifiedMoneyAddress(
	bank : Option[String],
	account : Option[String],
	owner : Option[String],
	address : Option[String]
)

case class Address(
	alias : String,
	currency : String,
	address : UnifiedMoneyAddress
)
