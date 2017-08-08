package org.kangmo.tradeapi
import java.math.BigDecimal


data class Preference (
	val notifyTrades: Boolean,
	val notifyDepositWithdrawal: Boolean,
	val verifyMfaOnLogin : Boolean,
	val coinOutMfaThreshold : BigDecimal
)

data class User (
	val email : String,
	val nameCheckedAt : java.sql.Timestamp?,
	val name  : String?,
	val phone : String?,
	// 1976-04-03
	val birthday : String?,
	// m : male
	// f : female
	val gender : String?,
	val prefs : Preference?,
  val maxCoinOutPerDay: BigDecimal,
	val maxFiatInPerDay: BigDecimal,
	val maxFiatOutPerDay: BigDecimal,
	val bannedAt: java.sql.Timestamp?,
	val userLevel: Int,
	val coinOutWithin24h : BigDecimal
)

data class Wallet (
	val `in` : List<Address>,
	val `out` : List<Address>,
	val balance : List<Amount>,
	val pendingOut : List<Amount>,
	val pendingNonmemberOut: List<Amount>,
	val pendingOrders : List<Amount>,
	val available : List<Amount>,
	val fee : BigDecimal
)

class UserChannel(context : Context): AbstractUserChannel(context) {
	suspend fun info() = getUserFuture<User>("user/info", User::class.java)
	suspend fun wallet() = getUserFuture<Wallet>("user/wallet", Wallet::class.java)
}
