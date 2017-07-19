package org.kangmo.helper

object Excp {
	fun getStackTrace(e : Exception) : String {
		val sr = java.io.StringWriter();
		val writer = java.io.PrintWriter(sr);
		e.printStackTrace(writer)
		return sr.toString()
	}
}
