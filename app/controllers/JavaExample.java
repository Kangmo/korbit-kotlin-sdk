package controllers;

import play.*;
import play.mvc.*;

import org.kangmo.tradeapi.*;

import scala.concurrent.Future;
import scala.concurrent.Promise;

public class JavaExample extends Controller {
  private static play.Logger.ALogger log = play.Logger.of("application");
  private static play.Configuration config = Play.application().configuration();
  private static String key = config.getString("trade.korbit.key");
  private static String secret = config.getString("trade.korbit.secret");
  private static String username = config.getString("trade.korbit.username");
  private static String password = config.getString("trade.korbit.password");

  public static Result sync() {
    //////////////////////////////////////////////////////////////
    // Set URL prefix. 
    //////////////////////////////////////////////////////////////

    // To use the test server, you need to override the default prefix value on the URLPrefix class.
    URLPrefix$.MODULE$.prefix_$eq("https://api.korbit.co.kr:8080/v1/");

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

    ///////////////////////////////////////////////////////////////////////
    // The last example shows how you can pass the result as a web response
    ///////////////////////////////////////////////////////////////////////
    try {
      User user = channel.user.info();
      String json = org.kangmo.helper.Json.serialize(user);
      return ok(json);
    } catch(APIException e) {
      return badRequest("failure : " + e.toString());
    }
  }
/*
  public static Promise<Result> async() {
    return null
  }
*/
/*
  public static Promise<Result> async() {

    //////////////////////////////////////////////////////////////
    // Set URL prefix. 
    //////////////////////////////////////////////////////////////

    // To use the test server, you need to override the default prefix value on the URLPrefix class.
    URLPrefix.prefix = "https://api.korbit.co.kr:8080/v1/"

    //////////////////////////////////////////////////////////////
    // APIs Without Authentication
    //////////////////////////////////////////////////////////////
    System.out.println("API : Get API version")
    val version : Version = API.version(); 
    System.out.println(version.toString());

    System.out.println("API : Get constants such as minimum amount of BTC you can transfer.")
    val constants : Constants = API.constants(); 
    System.out.println(constants.toString());

    System.out.println("API : Get current price.");
  	API$.MODULE$.market().ticker() onComplete {
      case Success(data:Ticker) => System.out.println("success : " + data.toString())
      case Failure(error) => System.out.println("failure : " + error.toString())
  	}

    System.out.println("API : Get current price and low/high/volume of the recent 24 hours.")
    API$.MODULE$.market().fullTicker() onComplete {
      case Success(data:FullTicker) => System.out.println("success : " + data.toString())
      case Failure(error) => System.out.println("failure : " + error.toString())
    }

    System.out.println("API : Get complete orderbook.")
    API$.MODULE$.market().orderbook() onComplete {
      case Success(data:OrderBook) => System.out.println("success : " + data.toString())
      case Failure(error) => System.out.println("failure : " + error.toString())
    }

    System.out.println("API : Get transactions since transaction id 1.")
    val since = TransactionId(1)
    API$.MODULE$.market().transactions(since) onComplete {
      case Success(data:Seq[Transaction]) => System.out.println("success : " + data.toString())
      case Failure(error) => System.out.println("failure : " + error.toString())
    }

    //////////////////////////////////////////////////////////////
    // APIs With Authentication : User Information
    //////////////////////////////////////////////////////////////
    System.out.println("Authentication : Get an authenticated channel with single user API-key.")
    // Note : Multi user API-keys are not supported in this library.
    val channel = API.createChannel(key, secret, username, password)

    System.out.println("API : Get user information.")
    channel.user.info() onComplete {
      case Success(data:User) => System.out.println("success : " + data.toString())
      case Failure(error) => System.out.println("failure : " + error.toString())
    }

    System.out.println("API : Get user wallet information.")
    channel.user.wallet() onComplete {
      case Success(data:Wallet) => System.out.println("success : " + data.toString())
      case Failure(error) => System.out.println("failure : " + error.toString())
    }

    //////////////////////////////////////////////////////////////
    // APIs With Authentication : Managing Orders
    //////////////////////////////////////////////////////////////
    System.out.println("API : Get transactions of the user.")
    channel.order.transactions() onComplete {
      case Success(data:Seq[UserTransaction]) => System.out.println("success : " + data.toString())
      case Failure(error) => System.out.println("failure : " + error.toString())
    }

    System.out.println("API : Get open orders of the user.")
    channel.order.openOrders() onComplete {
      case Success(data:Seq[OpenOrder]) => System.out.println("success : " + data.toString())
      case Failure(error) => System.out.println("failure : " + error.toString())
    }
    
    // These two implicit converters are necessary 
    // to convert Int or Double to BigDecimal parameters in Price and Amount case class.
    implicit def intToBigDecimal(value:Int) = new java.math.BigDecimal(value)
    implicit def doubleToBigDecimal(value:Double) = new java.math.BigDecimal(value)

    System.out.println("API : Place a limit order (buy).")
    channel.order.placeLimitOrder(BuyOrder(), Price("krw", 400000), Amount("btc", 0.01)) onComplete {
      case Success(data:OrderId) => System.out.println("success : " + data.toString())
      case Failure(error) => System.out.println("failure : " + error.toString())
    }

    System.out.println("API : Place a limit order (sell).")
    channel.order.placeLimitOrder(SellOrder(), Price("krw", 500000), Amount("btc", 0.01)) onComplete {
      case Success(data:OrderId) => System.out.println("success : " + data.toString())
      case Failure(error) => System.out.println("failure : " + error.toString())
    }

    System.out.println("API : Place a market order (buy).")
    channel.order.placeMarketOrder(BuyOrder(), Amount("krw", 10000)) onComplete {
      case Success(data:OrderId) => System.out.println("success : " + data.toString())
      case Failure(error) => System.out.println("failure : " + error.toString())
    }

    System.out.println("API : Place a market order (sell).")
    channel.order.placeMarketOrder(SellOrder(), Amount("btc", 0.01)) onComplete {
      case Success(data:OrderId) => System.out.println("success : " + data.toString())
      case Failure(error) => System.out.println("failure : " + error.toString())
    }

    System.out.println("API : Place an order, and cancel it right after it was placed.")
    channel.order.placeLimitOrder(BuyOrder(), Price("krw", 410000), Amount("btc", 0.01)) onComplete {
      case Success(data:OrderId) => {
        System.out.println("success : " + data.toString())
        channel.order.cancelOrder(Seq(OrderId(data.id))) onComplete {
          case Success(result:Seq[CancelOrderResult]) => result.map { r =>
            System.out.println(s"cancel order result : id=${r.orderId}, status=${r.status}")
          }
          case Failure(error) => System.out.println("failure : " + error.toString())
        }
      }
      case Failure(error) => System.out.println("failure : " + error.toString())
    }

    System.out.println("API : Place an order, and check the order status right after it was placed.")
    channel.order.placeLimitOrder(BuyOrder(), Price("krw", 420000), Amount("btc", 0.01)) onComplete {
      case Success(data:OrderId) => {
        System.out.println("success : " + data.toString())
        channel.order.transactions(Seq(FillsCategory()), Some(OrderId(data.id)), None) onComplete {
          case Success(data:Seq[UserTransaction]) => {
            // data is Seq of UserTransaction class.
            // If it is empty, the order is not filled yet.
            System.out.println("success : " + data.toString())
          }
          case Failure(error) => System.out.println("failure : " + error.toString())
        }
      }
      case Failure(error) => System.out.println("failure : " + error.toString())
    }

    //////////////////////////////////////////////////////////////
    // APIs With Authentication : Managing Fiats
    //////////////////////////////////////////////////////////////

    System.out.println("API : Assign KRW Bank address to which the user can deposit KRW.")
    channel.fiat.assignInAddress() onComplete {
      case Success(data:FiatAddress) => System.out.println("success : " + data.toString())
      case Failure(error) => System.out.println("failure : " + error.toString())
    }

    System.out.println("API : Register KRW Bank address to which the user can withdraw KRW.")
    channel.fiat.registerOutAddress(FiatAddress("우리은행", "1001-100-100000", None)) onComplete {
      case Success(data:FiatAddress) => System.out.println("success : " + data.toString())
      case Failure(error) => System.out.println("failure : " + error.toString())
    }

    System.out.println("API : Request KRW withdrawal.")
    channel.fiat.requestFiatOut(Amount("krw", 10000)) onComplete {
      case Success(req:FiatOutRequest) => {
        System.out.println("success : " + req.toString())
        // Query the request
        channel.fiat.queryFiatOut(Some(req)) onComplete {
          case Success(data:Seq[FiatStatus]) => System.out.println("success : " + data.toString())
          case Failure(error) => System.out.println("failure : " + error.toString())
        }

        // Cancel the request
        channel.fiat.cancelFiatOut(req) onComplete {
          case Success(data:FiatOutRequest) => System.out.println("success : " + data.toString())
          case Failure(error) => System.out.println("failure : " + error.toString())
        }
      }
      case Failure(error) => System.out.println("failure : " + error.toString())
    }

    //////////////////////////////////////////////////////////////
    // APIs With Authentication : Managing Coins
    //////////////////////////////////////////////////////////////

    System.out.println("API : Assign BTC address to which the user can deposit BTC.")
    channel.coin.assignInAddress() onComplete {
      case Success(data:CoinAddress) => System.out.println("success : " + data.toString())
      case Failure(error) => System.out.println("failure : " + error.toString())
    }
    
    System.out.println("API : Request BTC withdrawal.")
    val address = CoinAddress( 
                    if ( URLPrefix.prefix.contains("8080") ) "myxvvKU8FrYgkztK4h2xwU88atorcqybMn" // Testnet address
                    else "1anjg6B2XbpjHh8LFw8mXHATH54vrxs2F") // Bitcoin address
    channel.coin.requestCoinOut(Amount("btc", 0.01), address) onComplete {
      case Success(req:CoinOutRequest) => {
        System.out.println("success : " + req.toString())
        channel.coin.queryCoinOut(Some(req)) onComplete {
          case Success(data:Seq[CoinStatus]) => System.out.println("success : " + data.toString())
          case Failure(error) => System.out.println("failure : " + error.toString())
        }

        channel.coin.cancelCoinOut(req) onComplete {
          case Success(data:CoinOutRequest) => System.out.println("success : " + data.toString())
          case Failure(error) => System.out.println("failure : " + error.toString())
        }
      }
      case Failure(error) => System.out.println("failure : " + error.toString())
    }

    ///////////////////////////////////////////////////////////////////////
    // The last example shows how you can pass the result as a web response
    ///////////////////////////////////////////////////////////////////////
    Future<User> futureOfUser = channel.user.info() 
    return futureOfUser.map( 
      new Function<User, Result>() {
        public Result apply(User u) {
          String json = org.kangmo.helper.Json.serialize(u)
          Ok(json)
        }
      }
    );
  }
  */
}