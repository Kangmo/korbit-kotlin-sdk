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

enum class FeePriority(val priority: String) {
	Normal("normal"),
	Save("saver")
}

class CoinChannel(val context: Context): AbstractUserChannel(context) {

	// Only BTC is supported.
	suspend fun assignBtcInAddress(): CoinAddress {
		val future = CompletableFuture<CoinAddress>()

		val postData = "currency=btc"

		HTTPActor.dispatcher.send(PostUserResource(context, "user/coins/address/assign", postData ) { jsonResponse ->
			val response = JsonUtil.get().fromJson(jsonResponse, AssignCoinAddressResponse::class.java)
			if (response.status == "success") future.complete( CoinAddress( response.address ) )
			else future.obtrudeException( APIException(response.status) )
		})

		return future.get()
	}

	// Only BTC is supported.
	suspend fun requestBtcOut(amount: Amount, address: CoinAddress, priority: FeePriority = FeePriority.Normal): CoinOutRequest {
		val future = CompletableFuture<CoinOutRequest>()

		val postData = "currency=btc&amount=${amount.value}&address=${address.address}&fee_priority=${priority.priority}"
		HTTPActor.dispatcher.send( PostUserResource(context, "user/coins/out", postData ) { jsonResponse ->
			val response = JsonUtil.get().fromJson(jsonResponse, CoinOutStatus::class.java)
			if (response.status == "success") future.complete( CoinOutRequest( "btc", response.transferId ) )
			else future.obtrudeException( APIException(response.status) )
		})

		return future.get()
	}


	// Only BTC is supported.
	suspend fun queryBtcOut(request : CoinOutRequest? = null) : List<CoinStatus> {
		val params = "currency=btc" + ( if (request == null) "" else "&id=${request.id}" )

		return getUserFuture<List<CoinStatus>>("user/coins/status?$params", List::class.java as Class<List<CoinStatus>>)
	}

	// Only BTC is supported.
	suspend fun cancelBtcOut(request : CoinOutRequest): CoinOutRequest {

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
