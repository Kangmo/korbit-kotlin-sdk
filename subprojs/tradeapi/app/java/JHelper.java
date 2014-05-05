package org.kangmo.tradeapi;

import scala.concurrent.Future;

class JHelper {
	public static <T> T wait(Future<T> future) throws APIException {
		return API$.MODULE$.sync( future );
	}
}