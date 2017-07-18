package org.kangmo.tradeapi

import org.kangmo.http._
import org.kangmo.helper._

import java.math.BigDecimal
import scala.concurrent.{Future,Await}
import scala.concurrent.duration._
case class Version (
	major : Int,
	minor : Int,
	revision : Int
)

case class Constants (
	transactionFee : BigDecimal,
	minKrwWithdrawal : BigDecimal,
	maxKrwWithdrawal : BigDecimal,
	krwWithdrawalFee : BigDecimal,
	btcWithdrawalFee : BigDecimal,
	minBtcWithdrawal : BigDecimal,
	maxBtcWithdrawal : BigDecimal,
	minBtcOrder : BigDecimal,
	maxBtcOrder : BigDecimal,
	minBtcPrice : BigDecimal,
	maxBtcPrice : BigDecimal
)

case class OAuthResponse(token_type: String, access_token: String, expires_in: Long, refresh_token: String)

object API {
	val market = new MarketChannel()

	// You can Wrap an API with this function to synchronously wait for the API call.
	def sync[T](f : Future[T]) : T = {
		Await.result(f, 60 seconds /*timeout*/ )
	}

	def version() : Version = {
		val jsonResponse = HTTP.get(URLPrefix.prefix + s"version")
		val versonObject = Json.deserialize[Version](jsonResponse)
		versonObject
	}
	def constants() : Constants = {
		val jsonResponse = HTTP.get(URLPrefix.prefix + s"constants")
		val constantsObject = Json.deserialize[Constants](jsonResponse)
		constantsObject
	}

	class Channel(val context: Context) {
		val order = new TradeChannel(context)
		val coin = new CoinChannel(context)
		val fiat = new FiatChannel(context)
		val user = new UserChannel(context)
	}

  def setHost(host : String): Unit = {
    if (host.contains("localhost") || host.contains("127.0.0.1")) {
      URLPrefix.prefix = s"http://$host/v1/"
    } else {
      URLPrefix.prefix = s"https://$host/v1/"
    }
  }

  def getHost() = {
    URLPrefix.prefix
  }

	def createChannel(apiKey:String, apiSecret: String, email:String, password:String) : Channel = {

		val postData = s"client_id=${apiKey}&client_secret=${apiSecret}&username=${email}&password=${password}&grant_type=password"
		val jsonResponse = HTTP.post(URLPrefix.prefix + s"oauth2/access_token", postData)
		val r = Json.deserialize[OAuthResponse](jsonResponse)

		new Channel( Context(r.token_type, r.access_token, r.expires_in, r.refresh_token) )
	}

  def createChannel( context : Context) : Channel = {
    new Channel( context )
  }
}