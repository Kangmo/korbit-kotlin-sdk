package org.kangmo.tradeapi;

public class JChannel {
	public JTradeChannel order;
	public JUserChannel  user;
	public JFiatChannel  fiat;
	public JCoinChannel  coin;

	public JChannel(API.Channel channel) {
		order = new JTradeChannel(channel);
		user = new JUserChannel(channel);
		fiat = new JFiatChannel(channel);
		coin = new JCoinChannel(channel);
	}
}