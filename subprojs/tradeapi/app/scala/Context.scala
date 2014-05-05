package org.kangmo.tradeapi

case class Context(tokenType: String, var accessToken : String, expiresIn: Long, var refreshToken : String)