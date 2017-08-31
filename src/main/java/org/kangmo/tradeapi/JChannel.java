package org.kangmo.tradeapi;

public class JChannel {
	public JTradeChannel order;
	public JUserChannel  user;
	public JCoinChannel  coin;

	public JChannel(API.Channel channel) {
		order = new JTradeChannel(channel);
		user = new JUserChannel(channel);
		coin = new JCoinChannel(channel);
	}
}