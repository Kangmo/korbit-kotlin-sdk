package org.kangmo.tradeapi

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import org.kangmo.helper.JsonUtil
import org.kangmo.http.HTTPActor

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

abstract class AbstractChannel() {
	suspend fun<T> getPublicFuture(resource : String, clazz: Class<T>) : T {
		val future = CompletableFuture<T>()
		HTTPActor.dispatcher.send(GetPublicResource(resource) { jsonResponse ->
			val obj : T = JsonUtil.get().fromJson(jsonResponse, clazz)

			future.complete( obj )
		})
		return future.get()
	}
}

abstract class AbstractUserChannel(val ctx : Context) {
	suspend fun <T> getUserFuture(resource: String, clazz: Class<T>): T {
		val future = CompletableFuture<T>()

		HTTPActor.dispatcher.send(GetUserResource(ctx, resource) { jsonResponse ->
			val obj: T = JsonUtil.get().fromJson(jsonResponse, clazz)
			future.complete(obj)
		})

		return future.get()
	}

	suspend fun <T> postUserFuture(resource : String, postData : String, clazz: Class<T>): T {
		val future = CompletableFuture<T>()

		HTTPActor.dispatcher.send(PostUserResource(ctx, resource, postData) { jsonResponse ->
			val obj: T = JsonUtil.get().fromJson(jsonResponse, clazz)
			future.complete(obj)
		})

		return future.get()
	}
}
