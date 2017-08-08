package org.kangmo.tradeapi

import org.kangmo.helper.JsonUtil
import org.kangmo.http.HTTPActor

import java.math.BigDecimal
import java.util.concurrent.CompletableFuture

data class FiatStatus(
	val timestamp : java.sql.Timestamp,
	val id : Long,
	val `type` : String, // "fiat-in", "fiat-out"
	val amount : Amount,
	val `in` : FiatAddress?,
	val `out` : FiatAddress?,
	val completedAt : java.sql.Timestamp?
)

data class FiatOutRequest(val currency: String, val id : Long)

private data class AssignFiatInAddressResponse(val status : String, val bank : String, val account : String, val owner : String)
private data class FiatOutStatus(val status: String, val transferId: Long)
private data class RegisterFiatOutAddressResponse(val status : String)


class FiatChannel(val context : Context): AbstractUserChannel(context) {
	
	suspend fun assignInAddress() : FiatAddress {
		val future = CompletableFuture<FiatAddress>()

		val postData = "currency=krw"

		HTTPActor.dispatcher.send( PostUserResource(context, "user/fiats/address/assign", postData ) { jsonResponse ->
			val response = JsonUtil.get().fromJson(jsonResponse, AssignFiatInAddressResponse::class.java)
			if (response.status == "success") future.complete( FiatAddress( response.bank, response.account, response.owner ) )
			else future.obtrudeException( APIException(response.status) )
		} )

		return future.get()
	}

	suspend fun registerOutAddress(address : FiatAddress): FiatAddress {
		val future = CompletableFuture<FiatAddress>()

		val postData = "currency=krw&bank=${address.bank}&account=${address.account}"

		HTTPActor.dispatcher.send( PostUserResource(context, "user/fiats/address/register", postData ) { jsonResponse ->
			val response = JsonUtil.get().fromJson(jsonResponse, RegisterFiatOutAddressResponse::class.java)
			if (response.status == "success") future.complete( FiatAddress( address.bank, address.account, null ) )
			else future.obtrudeException( APIException(response.status) )
		} )

		return future.get()
	}

	suspend fun requestFiatOut(amount: Amount): FiatOutRequest {
		val future = CompletableFuture<FiatOutRequest>()

		val postData = "currency=${amount.currency}&amount=${amount.value}"
		
		HTTPActor.dispatcher.send( PostUserResource(context, "user/fiats/out", postData ) { jsonResponse ->
			val response = JsonUtil.get().fromJson(jsonResponse, FiatOutStatus::class.java)
			if (response.status == "success") future.complete( FiatOutRequest( amount.currency, response.transferId ) )
			else future.obtrudeException( APIException(response.status) )
		})

		return future.get()
	}

	suspend fun queryFiatOut(request : FiatOutRequest? = null ): List<FiatStatus> {
		val params = "currency=krw" + ( if (request == null) "" else "&id=${request.id}" )
		return getUserFuture<List<FiatStatus>>("user/fiats/status?$params", List::class.java as Class<List<FiatStatus>>)
	}

	suspend fun cancelFiatOut(request : FiatOutRequest): FiatOutRequest {
		val future = CompletableFuture<FiatOutRequest>()

		val postData = "currency=${request.currency}&id=${request.id}"

		HTTPActor.dispatcher.send( PostUserResource(context, "user/fiats/out/cancel", postData ) { jsonResponse ->
			val response = JsonUtil.get().fromJson(jsonResponse, FiatOutStatus::class.java)
			if (response.status == "success") future.complete(request)
			else future.obtrudeException( APIException(response.status) )
		})

		return future.get()
	}
}
