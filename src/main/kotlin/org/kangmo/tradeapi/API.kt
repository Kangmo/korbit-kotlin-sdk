package org.kangmo.tradeapi

import kotlinx.coroutines.experimental.runBlocking
import org.kangmo.helper.JsonUtil
import org.kangmo.http.HTTP
import java.math.BigDecimal
import java.util.concurrent.Future

data class Version (
	val major : Int,
	val minor : Int,
	val revision : Int
)

data class Constants (
	val krwWithdrawalFee:	BigDecimal, // Fixed fee for KRW withdrawals. ( 1,000 KRW )
	val maxKrwWithdrawal:	BigDecimal,	// Maximum daily amount for KRW withdrawals. ( 10,000,000 KRW )
	val minKrwWithdrawal:	BigDecimal,	// Minimum amount for a KRW withdrawal. ( 2,000 KRW )
	val btcTickSize:			BigDecimal,	// BTC order unit size.( 500 KRW )
	val btcWithdrawalFee:	BigDecimal,	// Fixed fee for BTC withdrawals. ( 0.0005 BTC )
	val maxBtcOrder:			BigDecimal,	// Maximum BTC amount for placing an order. ( 100 BTC )
	val maxBtcPrice:			BigDecimal,	// Maximum price of 1 BTC for an order. ( 100,000,000 KRW )
	val minBtcOrder:			BigDecimal,	// Minimum BTC amount for placing an order. ( 0.01 BTC )
	val minBtcPrice:			BigDecimal,	// Minimum price of 1 BTC for an order. ( 1,000 KRW )
	val maxBtcWithdrawal:	BigDecimal,	// Maximum amount for BTC withdrawals. ( 5 BTC )
	val minBtcWithdrawal:	BigDecimal,	// Minimum amount for BTC withdrawals. ( 0.0001 BTC )
	val etcTickSize:			BigDecimal,	// Ethereum Classic order unit size( 10 KRW )
	val maxEtcOrder:			BigDecimal,	// Maximum Ethereum Classic amount for placing an order.( 5,000ETC )
	val maxEtcPrice:			BigDecimal,	// Maximum price of 1 ETC for an order. ( 100,000,000 KRW )
	val minEtcOrder:			BigDecimal,	// Minimum ETC amount for placing an order. ( 0.1 ETC )
	val minEtcPrice:			BigDecimal,	// Minimum price of 1 ETC for an order. ( 100 KRW )
	val ethTickSize:			BigDecimal,	// Ethereum order unit size( 50 KRW )
	val maxEthOrder:			BigDecimal,	// Maximum Ethereum amount for placing an order.( 20,000ETH )
	val maxEthPrice:			BigDecimal,	// Maximum price of 1 ETH for an order. ( 100,000,000 KRW )
	val minEthOrder:			BigDecimal,	// Minimum ETH amount for placing an order. ( 0.5 ETH )
	val minEthPrice:			BigDecimal,	// Minimum price of 1 ETH for an order. ( 1,000 KRW )
	val minTradableLevel:	BigDecimal	// 2nd Tier
)

data class OAuthResponse(val token_type: String, val access_token: String, val expires_in: Long, val refresh_token: String)

object API {
	@JvmStatic
	val market = MarketChannel()

	@JvmStatic
	fun<T> sync( callback : suspend ()->T) : T {
		return runBlocking<T> {
			callback()
		}
	}

	@JvmStatic
	fun version() : Version {
		val jsonResponse = HTTP.get(URLPrefix.prefix + "version")
		val versonObject = JsonUtil.get().fromJson(jsonResponse, Version::class.java)
		return versonObject
	}

	@JvmStatic
	fun constants() : Constants {
		val jsonResponse = HTTP.get(URLPrefix.prefix + "constants")
		val constantsObject = JsonUtil.get().fromJson(jsonResponse, Constants::class.java)
		return constantsObject
	}

	class Channel(val context: Context) {
		val order = TradeChannel(context)
		val coin = CoinChannel(context)
		val user = UserChannel(context)
	}

	@JvmStatic
	fun createChannel(apiKey:String, apiSecret: String, email:String, password:String) : Channel {

		val postData = "client_id=${apiKey}&client_secret=${apiSecret}&username=${email}&password=${password}&grant_type=password"
		val jsonResponse = HTTP.post(URLPrefix.prefix + "oauth2/access_token", postData)
		val r = JsonUtil.get().fromJson(jsonResponse, OAuthResponse::class.java)

		return Channel( Context(r.token_type, r.access_token, r.expires_in, r.refresh_token) )
	}

	@JvmStatic
  fun createChannel( context : Context) : Channel {
    return Channel( context )
  }
}