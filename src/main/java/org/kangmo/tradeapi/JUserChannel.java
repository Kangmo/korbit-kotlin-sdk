package org.kangmo.tradeapi;

public class JUserChannel {
	private API.Channel channel;
	public JUserChannel(API.Channel channel) {
		this.channel = channel;
	}
	public User info() throws APIException {
		return JHelper.wait( channel.user().info() );
	}
	public Wallet wallet() throws APIException {
		return JHelper.wait( channel.user().wallet() );
	}
}
