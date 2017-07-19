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
	public Wallet wallet() throws APIException {
		return API.sync( continuation -> {
			return channel.getUser().wallet(continuation);
		});
	}
}
