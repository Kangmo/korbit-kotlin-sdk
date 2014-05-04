package org.kangmo.helper

object Excp {
	def getStackTrace(e : Exception) = {
		val sr = new java.io.StringWriter();
		val writer = new java.io.PrintWriter(sr);
		e.printStackTrace(writer)
		sr.toString
	}
}
