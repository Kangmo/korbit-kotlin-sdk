package org.kangmo.tradeapi
import java.math.BigDecimal


data class Preference (
	val notifyDepositWithdrawal: Boolean,
	val notifyTrades: Boolean
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
	val userLevel: Int
)

data class	Balances (
	val available:		BigDecimal, 		//	The amount of funds you can use.
	val trade_in_use: BigDecimal, 		//	The amount of funds that are being used in trade.
	val withdrawal_in_use: BigDecimal	//	The amount of funds that are being processed for withdrawal.
)

data class CoinAccountDetails(
	val address:					String,	//	The address of your wallet.
	val destination_tag:	String?	//	Destination tag used in XRP transactions. Only shows for XRP account.
)

data class FiatAccountDetails(
	val bank_name:			String, //	The name of the bank. Shows only for KRW.
	val account_number:	String, //	The account number of the bank. Shows only for KRW.
	val account_name:		String 	//	The name of the owner of the registered bank. Shows only for KRW.
)

data class DepositAccounts(
	val btc :	CoinAccountDetails,
	val etc :	CoinAccountDetails,
	val eth :	CoinAccountDetails,
	val xrp :	CoinAccountDetails,
	val krw : FiatAccountDetails
)

data class Accounts (
	val deposit: DepositAccounts
)

class UserChannel(context : Context): AbstractUserChannel(context) {
	suspend fun info() = getUserFuture<User>("user/info", User::class.java)
	suspend fun balances() = getUserFuture<Balances>("user/balances", Balances::class.java)
	suspend fun accounts() = getUserFuture<Accounts>("user/accounts", Accounts::class.java)
}
