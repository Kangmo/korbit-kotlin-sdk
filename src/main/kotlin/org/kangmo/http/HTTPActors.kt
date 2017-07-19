package org.kangmo.http
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.channels.actor
import org.kangmo.helper.Excp
import org.slf4j.LoggerFactory
import java.util.*

sealed abstract class AbstractRequest(open val options : List<String>) {
	fun addNonceToHeader(httpHeaders : Map<String,String>, nonce : Long?) : Map<String,String> {
		val nonceHeader = if (nonce != null) ("Nonce" to nonce.toString()) else null
		if (nonceHeader == null )
			return httpHeaders
		else
			return httpHeaders + nonceHeader
	}

	abstract suspend fun execute(nonce : Long?) : Unit
}

abstract class PostRequest(
	open val urlStr:String,
	override val options : List<String>,
	open val postData:String = "",
	val httpHeaders : Map<String,String> = Collections.unmodifiableMap(HashMap()),
	open val callback : suspend (String) -> Unit) : AbstractRequest(options) {

	override suspend fun execute(nonce : Long?)  {
		val httpResponse = HTTP.post(urlStr, postData, addNonceToHeader(httpHeaders, nonce) )
		callback( httpResponse )
	}
}

abstract class GetRequest(
	open val urlStr:String,
	override val options : List<String>,
	val httpHeaders : Map<String,String> = Collections.unmodifiableMap(HashMap()),
	open val callback : suspend (String) -> Unit) : AbstractRequest(options) {

	override suspend fun execute(nonce : Long?) {
		val httpResponse = HTTP.get(urlStr, addNonceToHeader(httpHeaders, nonce) ) 
		callback( httpResponse )
	}
}

fun HTTPSerialWorker() = actor<AbstractRequest>(CommonPool) {
	val logger = LoggerFactory.getLogger(javaClass)
	var counter = 0 // actor state
	for (msg in channel) { // iterate over incoming messages
		when (msg) {
			is AbstractRequest -> {
				assert(msg.options.contains("AddNonceOption"))
					// If nonce is required, execute the request in serial with nonce set.
				try {
					msg.execute( System.currentTimeMillis() )
				} catch(e : Exception) {
					logger.error("Internal Error. " + e.message )
					logger.error(Excp.getStackTrace(e))
				}
			}
		}
	}
}


fun HTTPDispatcher() = actor<AbstractRequest>(CommonPool) {
	for (msg in channel) { // iterate over incoming messages
		when (msg) {
			is AbstractRequest -> {
				if (msg.options.contains("AddNonceOption")) {
					// If nonce is required, execute the request in serial with nonce set.
					HTTPActor.serialWorker.send(msg)
				} else {
					// If nonce is not required, execute the request concurrently.
					msg.execute( null )
				}
			}
		}
	}
}


object HTTPActor {
	val serialWorker = HTTPSerialWorker()
	val dispatcher = HTTPDispatcher()
}

