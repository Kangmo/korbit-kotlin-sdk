package org.kangmo.tradeapi

import org.kangmo.http._

object URLPrefix {
	var prefix = "https://api.korbit.co.kr/v1/"
}

case class GetPublicResource(urlStr: String)(callback : String => Unit)
	extends GetRequest(URLPrefix.prefix + urlStr, Seq(), Map() ) ( callback )

case class GetUserResource(context : Context, urlStr: String)(callback : String => Unit) 
	extends GetRequest(URLPrefix.prefix + urlStr, Seq('AddNonceOption), Map("Authorization" -> s"Bearer ${context.accessToken}") ) ( callback )

case class PostUserResource(context : Context, urlStr: String, postData: String )(callback : String => Unit) 
	extends PostRequest(URLPrefix.prefix + urlStr, Seq('AddNonceOption), postData, Map("Authorization" -> s"Bearer ${context.accessToken}") ) ( callback )
