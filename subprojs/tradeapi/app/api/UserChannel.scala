package org.kangmo.tradeapi
import java.math.BigDecimal

import org.kangmo.http._
import org.kangmo.helper._


case class Preference (
	notifyTrades : Boolean,
	notifyDepositWithdrawal : Boolean
)

case class User (
	email : String,
	nameCheckedAt : Option[java.sql.Timestamp],
	name  : Option[String],
	phone : Option[String],
	// 1976-04-03
	birthday : Option[String], 
	// m : male
	// f : female
	gender : Option[String],
	prefs : Option[Preference]
)

case class Wallet (
	in : Seq[Address],
	out : Seq[Address],
	balance : Seq[Amount],
	pendingOut : Seq[Amount],
	pendingOrders : Seq[Amount],
	available : Seq[Amount],
	fee : BigDecimal
)

class UserChannel(context : Context) extends AbstractUserChannel(context) {
	def info() = getUserFuture[User]("user/info")
	def wallet() = getUserFuture[Wallet]("user/wallet") 
}
