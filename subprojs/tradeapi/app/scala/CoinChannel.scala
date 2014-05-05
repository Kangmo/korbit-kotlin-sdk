package org.kangmo.tradeapi

import org.kangmo.http._
import org.kangmo.helper._

import java.math.BigDecimal
import scala.concurrent._

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

class CoinChannel(context : Context) extends AbstractUserChannel(context) {

	def assignInAddress() = {
		val p = promise[CoinAddress]

		val postData = "currency=btc"

		HTTPActor.dispatcher ! PostUserResource(context, "user/coins/address/assign", postData ) { jsonResponse => 
			val response = Json.deserialize[AssignCoinAddressResponse](jsonResponse)
			if (response.status == "success") p success CoinAddress( response.address ) 
			else p failure new APIException(response.status)
		}

		p.future
	}

	def requestCoinOut(amount : Amount, address : CoinAddress) = {
		val p = promise[CoinOutRequest]

		val postData = s"currency=${amount.currency}&amount=${amount.value}&address=${address.address}"
		HTTPActor.dispatcher ! PostUserResource(context, "user/coins/out", postData ) { jsonResponse => 
			val response = Json.deserialize[CoinOutStatus](jsonResponse)
			if (response.status == "success") p success CoinOutRequest( amount.currency, response.transferId ) 
			else p failure new APIException(response.status)
		}

		p.future
	}

	def queryCoinOut(request : Option[CoinOutRequest] = None) = {
		val params = "currency=btc" + ( if (request == None) "" else s"&id=${request.get.id}" )

		getUserFuture[Seq[CoinStatus]](s"user/coins/status?$params")
	}

	def cancelCoinOut(request : CoinOutRequest) = {
		val p = promise[CoinOutRequest]

		val postData = s"currency=${request.currency}&id=${request.id}"
		
		HTTPActor.dispatcher ! PostUserResource(context, "user/coins/out/cancel", postData ) { jsonResponse => 
			val response = Json.deserialize[CoinOutStatus](jsonResponse)
			if (response.status == "success") p success request
			else p failure new APIException( response.status )
		}

		p.future
	}
}
