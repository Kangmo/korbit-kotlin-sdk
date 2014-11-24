package org.kangmo.tradeapi

import org.kangmo.http._
import org.kangmo.helper._

import java.math.BigDecimal
import scala.concurrent._

case class FiatStatus(
	timestamp : java.sql.Timestamp,
	id : Long,
	`type` : String, // "fiat-in", "fiat-out"
	amount : Amount,
	in : Option[FiatAddress],
	out : Option[FiatAddress],
	completedAt : Option[java.sql.Timestamp]
)

case class FiatOutRequest(currency: String, id : Long)

private case class AssignFiatInAddressResponse(status : String, bank : String, account : String, owner : String)
private case class FiatOutStatus(status: String, transferId: Long)
private case class RegisterFiatOutAddressResponse(status : String)


class FiatChannel(context : Context) extends AbstractUserChannel(context) {
	
	def assignInAddress() = {		
		val p = Promise[FiatAddress]

		val postData = "currency=krw"

		HTTPActor.dispatcher ! PostUserResource(context, "user/fiats/address/assign", postData ) { jsonResponse => 
			val response = Json.deserialize[AssignFiatInAddressResponse](jsonResponse)
			if (response.status == "success") p success FiatAddress( response.bank, response.account, Some(response.owner) ) 
			else p failure new APIException(response.status)
		}

		p.future
	}

	def registerOutAddress(address : FiatAddress) = {
		val p = Promise[FiatAddress]

		val postData = s"currency=krw&bank=${address.bank}&account=${address.account}"

		HTTPActor.dispatcher ! PostUserResource(context, "user/fiats/address/register", postData ) { jsonResponse => 
			val response = Json.deserialize[RegisterFiatOutAddressResponse](jsonResponse)
			if (response.status == "success") p success FiatAddress( address.bank, address.account, None ) 
			else p failure new APIException(response.status)
		}

		p.future
	}

	def requestFiatOut(amount : Amount) = {
		val p = Promise[FiatOutRequest]

		val postData = s"currency=${amount.currency}&amount=${amount.value}"
		
		HTTPActor.dispatcher ! PostUserResource(context, "user/fiats/out", postData ) { jsonResponse => 
			val response = Json.deserialize[FiatOutStatus](jsonResponse)
			if (response.status == "success") p success FiatOutRequest( amount.currency, response.transferId ) 
			else p failure new APIException(response.status)
		}

		p.future
	}

	def queryFiatOut(request : Option[FiatOutRequest] = None ) = {
		val params = "currency=krw" + ( if (request == None) "" else s"&id=${request.get.id}" )
		getUserFuture[Seq[FiatStatus]](s"user/fiats/status?$params")
	}

	def cancelFiatOut(request : FiatOutRequest) = {
		val p = Promise[FiatOutRequest]

		val postData = s"currency=${request.currency}&id=${request.id}"

		HTTPActor.dispatcher ! PostUserResource(context, "user/fiats/out/cancel", postData ) { jsonResponse => 
			val response = Json.deserialize[FiatOutStatus](jsonResponse)
			if (response.status == "success") p success request 
			else p failure new APIException(response.status)
		}

		p.future
	}
}
