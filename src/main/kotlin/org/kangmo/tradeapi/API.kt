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
	val transactionFee : BigDecimal,
	val minKrwWithdrawal : BigDecimal,
	val maxKrwWithdrawal : BigDecimal,
	val krwWithdrawalFee : BigDecimal,
	val btcWithdrawalFee : BigDecimal,
	val minBtcWithdrawal : BigDecimal,
	val maxBtcWithdrawal : BigDecimal,
	val minBtcOrder : BigDecimal,
	val maxBtcOrder : BigDecimal,
	val minBtcPrice : BigDecimal,
	val maxBtcPrice : BigDecimal
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
		val fiat = FiatChannel(context)
		val user = UserChannel(context)
	}

	@JvmStatic
  fun setHost(host : String): Unit {
    if (host.contains("localhost") || host.contains("127.0.0.1")) {
      URLPrefix.prefix = "http://$host/v1/"
    } else {
      URLPrefix.prefix = "https://$host/v1/"
    }
  }

	@JvmStatic
	fun getHost(): String {
    return URLPrefix.prefix
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