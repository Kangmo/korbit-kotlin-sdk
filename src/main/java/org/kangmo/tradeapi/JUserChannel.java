package org.kangmo.tradeapi;

import kotlin.coroutines.experimental.Continuation;

public class JUserChannel {
	private API.Channel channel;
	public JUserChannel(API.Channel channel) {
		this.channel = channel;
	}
	public User info() throws APIException {
		return API.sync( continuation -> {
			return channel.getUser().info(continuation) ;
		});
	}

	public Balances balances() throws APIException {
		return API.sync( continuation -> {
			return channel.getUser().balances(continuation);
		});
	}

	public Accounts accounts() throws APIException {
		return API.sync( continuation -> {
			return channel.getUser().accounts(continuation);
		});
	}

}

