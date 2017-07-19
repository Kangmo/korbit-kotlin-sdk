package org.kangmo.tradeapi

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.kangmo.helper.JsonUtil
import org.kangmo.http.HTTPActor

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

abstract class AbstractChannel() {
	inline suspend fun<reified T : Any> getPublicFuture(resource : String) : T {
		val future = CompletableFuture<T>()
		HTTPActor.dispatcher.send(GetPublicResource(resource) { jsonResponse ->
			val obj : T = JsonUtil.get().fromJson(jsonResponse, T::class.java)

			future.complete( obj )
		})
		return future.get()
	}
}

abstract class AbstractUserChannel(val ctx : Context) {
	inline suspend fun <reified T : Any> getUserFuture(resource: String): T {
		val future = CompletableFuture<T>()

		HTTPActor.dispatcher.send(GetUserResource(ctx, resource) { jsonResponse ->
			val obj: T = JsonUtil.get().fromJson(jsonResponse, T::class.java)
			future.complete(obj)
		})

		return future.get()
	}

	inline suspend fun <reified T : Any> postUserFuture(resource : String, postData : String): T {
		val future = CompletableFuture<T>()

		HTTPActor.dispatcher.send(PostUserResource(ctx, resource, postData) { jsonResponse ->
			val obj: T = JsonUtil.get().fromJson(jsonResponse, T::class.java)
			future.complete(obj)
		})

		return future.get()
	}
}
