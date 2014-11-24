package org.kangmo.http
import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import scala.concurrent.ExecutionContext.Implicits.global

import org.kangmo.helper._
import org.kangmo.helper.Logger._

abstract class AbstractRequest(val options : Seq[Symbol]) {
	def addNonceToHeader(httpHeaders : Map[String,String], nonce : Option[Long]) = {
		val nonceHeader = nonce.map{("Nonce"-> _.toString)} 
		if (nonceHeader == None ) httpHeaders else httpHeaders + nonceHeader.get
	}

	def execute(nonce : Option[Long]) : Unit
}

abstract class PostRequest(
	urlStr:String, 
	override val options : Seq[Symbol],
	postData:String = "", 
	httpHeaders : Map[String,String] = collection.immutable.HashMap() ) (callback : String => Unit) extends AbstractRequest(options) {

	def execute(nonce : Option[Long]) = {
		val httpResponse = HTTP.post(urlStr, postData, addNonceToHeader(httpHeaders, nonce) )
		callback( httpResponse )
	}
}

abstract class GetRequest(
	urlStr:String, 
	override val options : Seq[Symbol],
	httpHeaders : Map[String,String] = collection.immutable.HashMap() ) (callback : String => Unit) extends AbstractRequest(options) {

	def execute(nonce : Option[Long]) = {
		val httpResponse = HTTP.get(urlStr, addNonceToHeader(httpHeaders, nonce) ) 
		callback( httpResponse )
	}
}

class HTTPSerialWorker extends Actor {
	def receive = {
		case r : AbstractRequest => {
			assert(r.options.contains('AddNonceOption))
			// If nonce is required, execute the request in serial with nonce set.
			try {
				r.execute( Some(System.currentTimeMillis) )
			} catch {
				case e:Exception =>
				log.error("Internal Error. " + e.getMessage )
				log.error(Excp.getStackTrace(e))
			}
		}
	}
}

class HTTPDispatcher extends Actor {
	def receive = {
		case r : AbstractRequest => {
			if (r.options.contains('AddNonceOption)) {
				// If nonce is required, execute the request in serial with nonce set.
				HTTPActor.serialWorker ! r
			} else {
				// If nonce is not required, execute the request concurrently.
				scala.concurrent.Future {
					r.execute( None )
				}
			} 
		}
	}
}

object HTTPActor {
	val system = ActorSystem("HttpSystem")
	val serialWorker = system.actorOf(Props[HTTPSerialWorker], name = "http-serial-worker")
	val dispatcher = system.actorOf(Props[HTTPDispatcher], name = "http-dispatcher")
}


