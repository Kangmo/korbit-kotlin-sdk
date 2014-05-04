package org.kangmo.tradeapi

import org.kangmo.http._
import org.kangmo.helper._

import java.math.BigDecimal

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


class FiatChannel(context : Context)  {
	
	def assignInAddress()(callback : Either[Error, FiatAddress] => Unit ) {		
		val postData = "currency=krw"

		HTTPActor.dispatcher ! PostUserResource(context, "user/fiats/address/assign", postData ) { jsonResponse => 
			val response = Json.deserialize[AssignFiatInAddressResponse](jsonResponse)
			val result = if (response.status == "success") Right( FiatAddress( response.bank, response.account, Some(response.owner) ) ) 
			             else Left( Error(response.status)) 
			callback(result)
		}
	}

	def registerOutAddress(address : FiatAddress)(callback : Either[Error, FiatAddress] => Unit ) {

		val postData = s"currency=krw&bank=${address.bank}&account=${address.account}"
		HTTPActor.dispatcher ! PostUserResource(context, "user/fiats/address/register", postData ) { jsonResponse => 
			val response = Json.deserialize[RegisterFiatOutAddressResponse](jsonResponse)
			val result = if (response.status == "success") Right( FiatAddress( address.bank, address.account, address.owner ) ) 
			             else Left( Error(response.status)) 
			callback(result)
		}
	}

	def requestFiatOut(amount : Amount)(callback : Either[Error, FiatOutRequest] => Unit ) {
		val postData = s"currency=${amount.currency}&amount=${amount.value}"
		HTTPActor.dispatcher ! PostUserResource(context, "user/fiats/out", postData ) { jsonResponse => 
			val response = Json.deserialize[FiatOutStatus](jsonResponse)
			val result = if (response.status == "success") Right( FiatOutRequest( amount.currency, response.transferId ) ) else Left( Error(response.status)) 
			callback(result)
		}
	}
	def queryFiatOut(request : Option[FiatOutRequest] = None )(callback : Either[Error, Seq[FiatStatus]] => Unit ) {
		val params = "currency=krw" + ( if (request == None) "" else s"&id=${request.get.id}" )
		HTTPActor.dispatcher ! GetUserResource(context, s"user/fiats/status?$params" ) { jsonResponse => 
			val response = Json.deserialize[Seq[FiatStatus]](jsonResponse)
			callback( Right( response ) )
		}
	}
	def cancelFiatOut(request : FiatOutRequest)(callback : Option[Error] => Unit ) {
		val postData = s"currency=${request.currency}&id=${request.id}"
		HTTPActor.dispatcher ! PostUserResource(context, "user/fiats/out/cancel", postData ) { jsonResponse => 
			val response = Json.deserialize[FiatOutStatus](jsonResponse)
			val result = if (response.status == "success") None else Some( Error(response.status)) 
			callback(result)
		}
	}
}
