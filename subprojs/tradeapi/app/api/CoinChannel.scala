package org.kangmo.tradeapi

import org.kangmo.http._
import org.kangmo.helper._

import java.math.BigDecimal

case class CoinStatus(
	timestamp : java.sql.Timestamp,
	id : Long,
	`type` : String, // "coin-in", "coin-out"
	amount : Amount,
	in : Option[CoinAddress],
	out : Option[CoinAddress],
	reference : Option[String],
	completedAt : Option[java.sql.Timestamp]
)

case class CoinOutRequest(currency: String, id : Long)

private case class CoinOutStatus(status: String, transferId: Long)
private case class AssignCoinAddressResponse(status : String, address : String)

class CoinChannel(context : Context)  {

	def assignInAddress()(callback : Either[Error, CoinAddress] => Unit ) {
		val postData = "currency=btc"
		HTTPActor.dispatcher ! PostUserResource(context, "user/coins/address/assign", postData ) { jsonResponse => 
			val response = Json.deserialize[AssignCoinAddressResponse](jsonResponse)
			val result = if (response.status == "success") Right( CoinAddress( response.address ) ) else Left( Error(response.status)) 
			callback(result)
		}
	}

	def requestCoinOut(amount : Amount, address : CoinAddress)(callback : Either[Error, CoinOutRequest] => Unit ) {
		val postData = s"currency=${amount.currency}&amount=${amount.value}&address=${address.address}"
		HTTPActor.dispatcher ! PostUserResource(context, "user/coins/out", postData ) { jsonResponse => 
			val response = Json.deserialize[CoinOutStatus](jsonResponse)
			val result = if (response.status == "success") Right( CoinOutRequest( amount.currency, response.transferId ) ) else Left( Error(response.status)) 
			callback(result)
		}
	}

	def queryCoinOut(request : Option[CoinOutRequest] = None)(callback : Either[Error, Seq[CoinStatus]] => Unit ) {
		val params = "currency=btc" + ( if (request == None) "" else s"&id=${request.get.id}" )
		HTTPActor.dispatcher ! GetUserResource(context, s"user/coins/status?$params" ) { jsonResponse => 
			val response = Json.deserialize[Seq[CoinStatus]](jsonResponse)
			callback( Right( response ) )
		}
	}

	def cancelCoinOut(request : CoinOutRequest)(callback : Option[Error] => Unit ) {
		val postData = s"currency=${request.currency}&id=${request.id}"
		HTTPActor.dispatcher ! PostUserResource(context, "user/coins/out/cancel", postData ) { jsonResponse => 
			val response = Json.deserialize[CoinOutStatus](jsonResponse)
			val result = if (response.status == "success") None else Some( Error(response.status)) 
			callback(result)
		}
	}
}
