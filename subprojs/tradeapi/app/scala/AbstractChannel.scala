package org.kangmo.tradeapi

import org.kangmo.http._
import org.kangmo.helper._

import java.math.BigDecimal
import scala.concurrent._

abstract class AbstractChannel() {
	def getPublicFuture[T : Manifest](resource : String) : Future[T] = {
		val p = Promise[T]

		HTTPActor.dispatcher ! GetPublicResource(resource) { jsonResponse =>
			val obj : T = Json.deserialize[T](jsonResponse)
			p success obj
		}

		p.future
	}
}

abstract class AbstractUserChannel(context : Context) {
	def getUserFuture[T : Manifest](resource : String) : Future[T] = {
		val p = Promise[T]

		HTTPActor.dispatcher ! GetUserResource(context, resource) { jsonResponse =>
			val obj : T = Json.deserialize[T](jsonResponse)
			p success obj
		}

		p.future
	}

	def postUserFuture[T : Manifest](resource : String, postData : String) : Future[T] = {
		val p = Promise[T]

		HTTPActor.dispatcher ! PostUserResource(context, resource, postData) { jsonResponse =>
			val obj : T = Json.deserialize[T](jsonResponse)
			p success obj
		}

		p.future
	}
}
