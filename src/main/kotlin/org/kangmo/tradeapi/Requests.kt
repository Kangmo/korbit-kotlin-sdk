package org.kangmo.tradeapi

import org.kangmo.http.GetRequest
import org.kangmo.http.PostRequest

object URLPrefix {
	var prefix = "https://api.korbit.co.kr/v1/"
}

data class GetPublicResource(override val urlStr: String, override val callback : suspend (String) -> Unit)
	: GetRequest(
			URLPrefix.prefix + urlStr,
			listOf(),
			mapOf(),
			callback )

data class GetUserResource(val context : Context, override val urlStr: String, override val callback : suspend (String) -> Unit)
	: GetRequest(
			URLPrefix.prefix + urlStr,
			listOf("AddNonceOption"),
			mapOf("Authorization" to "Bearer ${context.accessToken}"),
			callback )

data class PostUserResource(val context : Context, override val urlStr: String, override val postData: String, override val callback : suspend (String) -> Unit)
	: PostRequest(
			URLPrefix.prefix + urlStr,
			listOf("AddNonceOption"),
			postData,
			mapOf("Authorization" to "Bearer ${context.accessToken}"),
			callback )
