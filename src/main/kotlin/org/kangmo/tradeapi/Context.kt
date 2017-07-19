package org.kangmo.tradeapi

data class Context(val tokenType: String, var accessToken : String, val expiresIn: Long, var refreshToken : String)