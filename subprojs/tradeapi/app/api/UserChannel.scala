package org.kangmo.tradeapi

import org.kangmo.http._
import org.kangmo.helper._

import java.math.BigDecimal
import scala.concurrent._

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

class UserChannel(context : Context) {
	/*
	def info2() : Future[User] = {
		val p = promise[User]

		val request = GetUserResource(context, "user/info" ) { jsonResponse => 
			val user = Json.deserialize[User](jsonResponse)
			p success user
		}
//		HTTPActor.dispatcher ! request
		p.future
	}
*/
	def info()(callback : Either[Error, User] => Unit) {
		val request = GetUserResource(context, "user/info" ) { jsonResponse => 
			val user = Json.deserialize[User](jsonResponse)
			callback(Right(user))
		}
		HTTPActor.dispatcher ! request
	}
	def wallet()(callback : Either[Error, Wallet] => Unit) {
		HTTPActor.dispatcher ! GetUserResource(context, "user/wallet" ) { jsonResponse => 

			val wallet = Json.deserialize[Wallet](jsonResponse)
			callback(Right(wallet))
		}
	}
}
