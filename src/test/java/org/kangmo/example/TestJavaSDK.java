package org.kangmo.example

import org.kangmo.tradeapi.*;
/**
 * Hello world!
 *
 */
public class App 
{
	public static void main(String args[]) {

		String port = System.getenv("KORBIT_API_PORT");
		if (port == null || port.equals("")) {
			System.out.println("You need to set KORBIT_API_PORT environment variable.");
			System.out.println("To connect to api.korbit.co.kr:8080 (korbitdevday.herokuapp.com), set KORBIT_API_PORT to 8080.");
			System.out.println("To connect to api.korbit.co.kr (www.korbit.co.kr), set KORBIT_API_PORT to 443.");
		}

		String key = System.getenv("KORBIT_API_KEY"); 
		String secret = System.getenv("KORBIT_API_SECRET");
		if (key == null || key.equals("") || secret == null || secret.equals("")) {
			System.out.println("You need to set KORBIT_API_KEY and KORBIT_API_SECRET environment variable.");
			System.out.println("To get the key, see the following URL.");
			System.out.println("https://bitbucket.org/korbit/korbit-api/wiki/Home");	
		}

		String username = System.getenv("KORBIT_API_USERNAME");
		String password = System.getenv("KORBIT_API_PASSWORD");
		if (username == null || username.equals("") || password == null || password.equals("")) {
			System.out.println("You need to set KORBIT_API_USERNAME and KORBIT_API_PASSWORD environment variable.");
			System.out.println("To connect to api.korbit.co.kr:8080, use your username and password on korbitdevday.herokuapp.com.");
			System.out.println("To connect to api.korbit.co.kr, use your username and password on www.korbit.co.kr.");
		}

	    //////////////////////////////////////////////////////////////
	    // Set URL prefix. 
	    //////////////////////////////////////////////////////////////

	    // To use the test server, you need to override the default prefix value on the URLPrefix class.
		JConfig.setUrlPrefix("https://api.korbit.co.kr:"+port+"/v1/");

	    //////////////////////////////////////////////////////////////
	    // APIs Without Authentication
	    //////////////////////////////////////////////////////////////
	    System.out.println("API : Get API version");
	    try {
	      Version version = JAPI.version(); 
	      System.out.println(version.toString());
	    } catch(APIException e) {
	      System.out.println("failure : " + e.toString());
	    }

	    System.out.println("API : Get constants such as minimum amount of BTC you can transfer.");
	    try {
	      Constants constants = JAPI.constants(); 
	      System.out.println(constants.toString());
	    } catch(APIException e) {
	      System.out.println("failure : " + e.toString());
	    }

	    System.out.println("API : Get current price.");
	    try {
	      Ticker ticker = JAPI.market.ticker();
	      System.out.println("success : " + ticker);
	    } catch(APIException e) {
	      System.out.println("failure : " + e.toString());
	    }

	    System.out.println("API : Get current price and low/high/volume of the recent 24 hours.");
	    try {
	      FullTicker fullTicker = JAPI.market.fullTicker();
	      System.out.println("success : " + fullTicker);
	    } catch(APIException e) {
	      System.out.println("failure : " + e.toString());
	    }

	    System.out.println("API : Get complete orderbook.");
	    try {
	      OrderBook orderbook = JAPI.market.orderbook();
	      System.out.println("success : " + orderbook.toString());
	    } catch(APIException e) {
	      System.out.println("failure : " + e.toString());
	    }

	    System.out.println("API : Get transactions since transaction id 1.");
	    try {
	      long since = 1;
	      java.util.List<Transaction> transactions = JAPI.market.transactions(since) ;
	      System.out.println("success : " + transactions.toString());
	    } catch(APIException e) {
	      System.out.println("failure : " + e.toString());
	    }

	    //////////////////////////////////////////////////////////////
	    // APIs With Authentication : User Information
	    //////////////////////////////////////////////////////////////
	    System.out.println("Authentication : Get an authenticated channel with single user API-key.");
	    // Note : Multi user API-keys are not supported in this library.
	    JChannel channel = JAPI.createChannel(key, secret, username, password);

	    System.out.println("API : Get user information.");
	    try {
	      User user = channel.user.info();
	      System.out.println("success : " + user.toString());
	    } catch(APIException e) {
	      System.out.println("failure : " + e.toString());
	    }    

	    System.out.println("API : Get user wallet information.");
	    try {
	      Wallet wallet = channel.user.wallet();
	      System.out.println("success : " + wallet.toString());
	    } catch(APIException e) {
	      System.out.println("failure : " + e.toString());
	    }  

	    //////////////////////////////////////////////////////////////
	    // APIs With Authentication : Managing Orders
	    //////////////////////////////////////////////////////////////
	    System.out.println("API : Get transactions of the user.");
	    try {
	      java.util.List<UserTransaction> txs = channel.order.transactions();
	      System.out.println("success : " + txs.toString());
	    } catch(APIException e) {
	      System.out.println("failure : " + e.toString());
	    }  

	    System.out.println("API : Get open orders of the user.");
	    try {
	      java.util.List<OpenOrder> openOrders = channel.order.openOrders();
	      System.out.println("success : " + openOrders.toString());
	    } catch(APIException e) {
	      System.out.println("failure : " + e.toString());
	    }  
	    
	    System.out.println("API : Place a limit order (buy).");
	    try {
	      OrderId id = channel.order.placeLimitOrder(JORDER_TYPE.BUY, 400000, 0.01);
	      System.out.println("success : " + id.toString());
	    } catch(APIException e) {
	      System.out.println("failure : " + e.toString());
	    } 

	    System.out.println("API : Place a limit order (sell).");
	    try {
	      OrderId id = channel.order.placeLimitOrder(JORDER_TYPE.SELL, 500000, 0.01);
	      System.out.println("success : " + id.toString());
	    } catch(APIException e) {
	      System.out.println("failure : " + e.toString());
	    } 

	    System.out.println("API : Place a market order (buy).");
	    try {
	      OrderId id = channel.order.placeMarketOrder(JORDER_TYPE.BUY, 10000);
	      System.out.println("success : " + id.toString());
	    } catch(APIException e) {
	      System.out.println("failure : " + e.toString());
	    }  

	    System.out.println("API : Place a market order (sell).");
	    try {
	      OrderId id = channel.order.placeMarketOrder(JORDER_TYPE.SELL, 0.01);

	      System.out.println("success : " + id.toString());
	    } catch(APIException e) {
	      System.out.println("failure : " + e.toString());
	    }  

	    System.out.println("API : Place an order, and cancel it right after it was placed.");
	    try {
	      OrderId orderId = channel.order.placeLimitOrder(JORDER_TYPE.BUY, 410000, 0.01);
	      System.out.println("success(place order) : " + orderId.toString());

	      java.util.List<CancelOrderResult> result = channel.order.cancelOrder(java.util.Arrays.asList( new OrderId(orderId.id() ) ) );
	      System.out.println("result(cancel order) : " + result.toString());
	    } catch(APIException e) {
	      System.out.println("failure : " + e.toString());
	    } 

	    System.out.println("API : Place an order, and check the order status right after it was placed.");
	    try {    
	      OrderId orderId = channel.order.placeLimitOrder(JORDER_TYPE.BUY, 420000, 0.01);
	      System.out.println("success(place order) : " + orderId.toString());

	      java.util.List<UserTransaction> result = channel.order.transactions(orderId);
	      System.out.println("result(get order if filled) : " + result.toString());
	    } catch(APIException e) {
	      System.out.println("failure : " + e.toString());
	    } 

	    //////////////////////////////////////////////////////////////
	    // APIs With Authentication : Managing Fiats
	    //////////////////////////////////////////////////////////////

	    System.out.println("API : Assign KRW Bank address to which the user can deposit KRW.");
	    try {
	      FiatAddress inAddress = channel.fiat.assignInAddress();
	      System.out.println("success : " + inAddress.toString());
	    } catch(APIException e) {
	      System.out.println("failure : " + e.toString());
	    } 

	    System.out.println("API : Register KRW Bank address to which the user can withdraw KRW.");
	    try {
	      FiatAddress outAddress = channel.fiat.registerOutAddress("우리은행", "1001-100-100000");
	      System.out.println("success : " + outAddress.toString()); 
	    } catch(APIException e) {
	      System.out.println("failure : " + e.toString());
	    }  

	    System.out.println("API : Request KRW withdrawal.");
	    try {
	      FiatOutRequest req = channel.fiat.requestFiatOut(10000);
	      System.out.println("success : " + req.toString());

	      // Query the request
	      java.util.List<FiatStatus> status = channel.fiat.queryFiatOut(req);
	      System.out.println("result(status) : " + status.toString());

	      channel.fiat.cancelFiatOut(req);
	      System.out.println("result(cancel) : success");
	    } catch(APIException e) {
	      System.out.println("failure : " + e.toString());
	    } 

	    //////////////////////////////////////////////////////////////
	    // APIs With Authentication : Managing Coins
	    //////////////////////////////////////////////////////////////

	    System.out.println("API : Assign BTC address to which the user can deposit BTC.");
	    try {
	      CoinAddress inAddress = channel.coin.assignInAddress();
	      System.out.println("result(cancel) : " + inAddress.toString());
	    } catch(APIException e) {
	      System.out.println("failure : " + e.toString());
	    } 
	    
	    System.out.println("API : Request BTC withdrawal.");
	    try {
	      String address = null;

	      if ( JConfig.getUrlPrefix().contains("8080") )
	        address = "myxvvKU8FrYgkztK4h2xwU88atorcqybMn"; // Testnet address
	      else
	        address = "1anjg6B2XbpjHh8LFw8mXHATH54vrxs2F"; // Bitcoin address
	      
	      CoinOutRequest req = channel.coin.requestCoinOut(address, 0.01);
	      System.out.println("success : " + req.toString());

	      java.util.List<CoinStatus> status = channel.coin.queryCoinOut(req);
	      System.out.println("result(status) : " + status.toString());

	      channel.coin.cancelCoinOut(req);
	      System.out.println("result(cancel) : success");
	    } catch(APIException e) {
	      System.out.println("failure : " + e.toString());
	    }  		
	}
}
