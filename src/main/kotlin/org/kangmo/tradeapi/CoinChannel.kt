package org.kangmo.tradeapi

import org.kangmo.helper.JsonUtil
import org.kangmo.http.HTTPActor
import java.math.BigDecimal
import java.util.concurrent.CompletableFuture

data class CoinStatus(
	val timestamp : java.sql.Timestamp,
	val id : Long,
	val `type` : String, // "coin-in", "coin-out"
	val amount : Amount,
	val `in` : CoinAddress?,
	val `out` : CoinAddress?,
	val reference : String?,
	val completedAt : java.sql.Timestamp?
)

data class CoinOutRequest(val currency: String, val id: Long)

private data class CoinOutCancelResponse(val status: String, val transferId: Long)
private data class AssignCoinAddressResponse(val status: String, val address: String)

class CoinChannel(val context: Context): AbstractUserChannel(context) {

	suspend fun assignInAddress(): CoinAddress {
		val future = CompletableFuture<CoinAddress>()

		val postData = "currency=btc"

		HTTPActor.dispatcher.send(PostUserResource(context, "user/coins/address/assign", postData ) { jsonResponse ->
			val response = JsonUtil.get().fromJson(jsonResponse, AssignCoinAddressResponse::class.java)
			if (response.status == "success") future.complete( CoinAddress( response.address ) )
			else future.obtrudeException( APIException(response.status) )
		})

		return future.get()
	}

	suspend fun requestCoinOut(amount: Amount, address: CoinAddress): CoinOutRequest {
		val future = CompletableFuture<CoinOutRequest>()

		val postData = "currency=${amount.currency}&amount=${amount.value}&address=${address.address}"
		HTTPActor.dispatcher.send( PostUserResource(context, "user/coins/out", postData ) { jsonResponse ->
			val response = JsonUtil.get().fromJson(jsonResponse, CoinOutStatus::class.java)
			if (response.status == "success") future.complete( CoinOutRequest( amount.currency, response.transferId ) )
			else future.obtrudeException( APIException(response.status) )
		})

		return future.get()
	}

	suspend fun queryCoinOut(request : CoinOutRequest? = null) : List<CoinStatus> {
		val params = "currency=btc" + ( if (request == null) "" else "&id=${request.id}" )

		return getUserFuture<List<CoinStatus>>("user/coins/status?$params")
	}

	suspend fun cancelCoinOut(request : CoinOutRequest): CoinOutRequest {

		val future = CompletableFuture<CoinOutRequest>()

		val postData = "currency=${request.currency}&id=${request.id}"
		
		HTTPActor.dispatcher.send( PostUserResource(context, "user/coins/out/cancel", postData ) { jsonResponse ->
			val response = JsonUtil.get().fromJson(jsonResponse, CoinOutCancelResponse::class.java)
			if (response.status == "success") future.complete(request)
			else future.obtrudeException( APIException(response.status) )
		})

		return future.get()
	}
}
