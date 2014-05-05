package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import play.api.db._

import org.kangmo.tradeapi._

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import java.io.StringWriter

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.util.{Success,Failure}

object ScalaExample extends Controller {
  val log = play.Logger.of("application")
  val config = Play.application.configuration
  val key = config.getString("trade.korbit.key").get
  val secret = config.getString("trade.korbit.secret").get
  val username = config.getString("trade.korbit.username").get
  val password = config.getString("trade.korbit.password").get


  def sync = Action {
    //////////////////////////////////////////////////////////////
    // Set URL prefix. 
    //////////////////////////////////////////////////////////////

    // To use the test server, you need to override the default prefix value on the URLPrefix class.
    URLPrefix.prefix = "https://api.korbit.co.kr:8080/v1/"

    //////////////////////////////////////////////////////////////
    // APIs Without Authentication
    //////////////////////////////////////////////////////////////
    println("API : Get API version")
    val version : Version = API.version(); 
    println(version.toString);

    println("API : Get constants such as minimum amount of BTC you can transfer.")
    val constants : Constants = API.constants(); 
    println(constants.toString);

    println("API : Get current price.");
    try {
      val ticker : Ticker = API.sync( API.market.ticker() )
      println("success : " + ticker)
    } catch {
      case e : APIException => println("failure : " + e.toString)
    }

    println("API : Get current price and low/high/volume of the recent 24 hours.")
    try {
      val fullTicker : FullTicker = API.sync( API.market.fullTicker() )
      println("success : " + fullTicker)
    } catch {
      case e : APIException => println("failure : " + e.toString)
    }

    println("API : Get complete orderbook.")
    try {
      val orderbook : OrderBook = API.sync( API.market.orderbook() )
      println("success : " + orderbook.toString)
    } catch {
      case e : APIException => println("failure : " + e.toString)
    }

    println("API : Get transactions since transaction id 1.")
    try {
      val since = TransactionId(1)
      val transactions : Seq[Transaction] = API.sync( API.market.transactions(since) )
      println("success : " + transactions.toString)
    } catch {
      case e : APIException => println("failure : " + e.toString)
    }

    //////////////////////////////////////////////////////////////
    // APIs With Authentication : User Information
    //////////////////////////////////////////////////////////////
    println("Authentication : Get an authenticated channel with single user API-key.")
    // Note : Multi user API-keys are not supported in this library.
    val channel = API.createChannel(key, secret, username, password)

    println("API : Get user information.")
    try {
      val user : User = API.sync( channel.user.info() )
      println("success : " + user.toString)
    } catch {
      case e : APIException => println("failure : " + e.toString)
    }    

    println("API : Get user wallet information.")
    try {
      val wallet : Wallet = API.sync( channel.user.wallet() )
      println("success : " + wallet.toString)
    } catch {
      case e : APIException => println("failure : " + e.toString)
    }    

    //////////////////////////////////////////////////////////////
    // APIs With Authentication : Managing Orders
    //////////////////////////////////////////////////////////////
    println("API : Get transactions of the user.")
    try {
      val txs : Seq[UserTransaction] = API.sync( channel.order.transactions() )
      println("success : " + txs.toString)
    } catch {
      case e : APIException => println("failure : " + e.toString)
    }   

    println("API : Get open orders of the user.")
    try {
      val openOrders : Seq[OpenOrder] = API.sync(channel.order.openOrders() )
      println("success : " + openOrders.toString)
    } catch {
      case e : APIException => println("failure : " + e.toString)
    }   
    
    // These two implicit converters are necessary 
    // to convert Int or Double to BigDecimal parameters in Price and Amount case class.
    implicit def intToBigDecimal(value:Int) = new java.math.BigDecimal(value)
    implicit def doubleToBigDecimal(value:Double) = new java.math.BigDecimal(value)

    println("API : Place a limit order (buy).")
    try {
      val id : OrderId = API.sync( channel.order.placeLimitOrder(BuyOrder(), Price("krw", 400000), Amount("btc", 0.01)) )
      println("success : " + id.toString)
    } catch {
      case e : APIException => println("failure : " + e.toString)
    }   

    println("API : Place a limit order (sell).")
    try {
      val id : OrderId = API.sync( channel.order.placeLimitOrder(SellOrder(), Price("krw", 500000), Amount("btc", 0.01)) )
      println("success : " + id.toString)
    } catch {
      case e : APIException => println("failure : " + e.toString)
    }   

    println("API : Place a market order (buy).")
    try {
      val id : OrderId = API.sync( channel.order.placeMarketOrder(BuyOrder(), Amount("krw", 10000)) )
      println("success : " + id.toString)
    } catch {
      case e : APIException => println("failure : " + e.toString)
    }   

    println("API : Place a market order (sell).")
    try {
      val id : OrderId = API.sync( channel.order.placeMarketOrder(SellOrder(), Amount("btc", 0.01)) )
      println("success : " + id.toString)
    } catch {
      case e : APIException => println("failure : " + e.toString)
    }   

    println("API : Place an order, and cancel it right after it was placed.")
    try {
      val orderId : OrderId = API.sync( channel.order.placeLimitOrder(BuyOrder(), Price("krw", 410000), Amount("btc", 0.01)) )
      println("success(place order) : " + orderId.toString)

      val result : Seq[CancelOrderResult] = API.sync( channel.order.cancelOrder(Seq(OrderId(orderId.id))) )
      println("result(cancel order) : " + result.toString)
    } catch {
      case e : APIException => println("failure : " + e.toString)
    }   

    println("API : Place an order, and check the order status right after it was placed.")
    try {    
      val orderId : OrderId = API.sync( channel.order.placeLimitOrder(BuyOrder(), Price("krw", 420000), Amount("btc", 0.01)) )
      println("success(place order) : " + orderId.toString)

      val result : Seq[UserTransaction] = API.sync( channel.order.transactions(Seq(FillsCategory()), Some(OrderId(orderId.id)), None) )
      println("result(get order if filled) : " + result.toString)
    } catch {
      case e : APIException => println("failure : " + e.toString)
    }   

    //////////////////////////////////////////////////////////////
    // APIs With Authentication : Managing Fiats
    //////////////////////////////////////////////////////////////

    println("API : Assign KRW Bank address to which the user can deposit KRW.")
    try {
      val inAddress : FiatAddress = API.sync( channel.fiat.assignInAddress() )
      println("success : " + inAddress.toString)      
    } catch {
      case e : APIException => println("failure : " + e.toString)
    }   

    println("API : Register KRW Bank address to which the user can withdraw KRW.")
    try {
      val outAddress : FiatAddress = API.sync( channel.fiat.registerOutAddress(FiatAddress("우리은행", "1001-100-100000", None)) )
      println("success : " + outAddress.toString)      
    } catch {
      case e : APIException => println("failure : " + e.toString)
    }   

    println("API : Request KRW withdrawal.")
    try {
      val req : FiatOutRequest = API.sync( channel.fiat.requestFiatOut(Amount("krw", 10000)) )
      println("success : " + req.toString)

      // Query the request
      val status : Seq[FiatStatus] = API.sync( channel.fiat.queryFiatOut(Some(req)) )
      println("result(status) : " + status.toString)

      API.sync( channel.fiat.cancelFiatOut(req) )
      println("result(cancel) : success")
    } catch {
      case e : APIException => println("failure : " + e.toString)
    }   

    //////////////////////////////////////////////////////////////
    // APIs With Authentication : Managing Coins
    //////////////////////////////////////////////////////////////

    println("API : Assign BTC address to which the user can deposit BTC.")
    try {
      val inAddress : CoinAddress = API.sync( channel.coin.assignInAddress() )
      println("result(cancel) : " + inAddress.toString)
    } catch {
      case e : APIException => println("failure : " + e.toString)
    }   
    
    println("API : Request BTC withdrawal.")
    try {
      val address = CoinAddress( 
                    if ( URLPrefix.prefix.contains("8080") ) "myxvvKU8FrYgkztK4h2xwU88atorcqybMn" // Testnet address
                    else "1anjg6B2XbpjHh8LFw8mXHATH54vrxs2F") // Bitcoin address
      val req : CoinOutRequest = API.sync( channel.coin.requestCoinOut(Amount("btc", 0.01), address) )
      println("success : " + req.toString)

      val status : Seq[CoinStatus] = API.sync( channel.coin.queryCoinOut(Some(req)) )
      println("result(status) : " + status.toString)

      API.sync( channel.coin.cancelCoinOut(req) )
      println("result(cancel) : success")
    } catch {
      case e : APIException => println("failure : " + e.toString)
    }   

    ///////////////////////////////////////////////////////////////////////
    // The last example shows how you can pass the result as a web response
    ///////////////////////////////////////////////////////////////////////
    try {
      val user : User = API.sync( channel.user.info() )
      val json = org.kangmo.helper.Json.serialize(user)
      Ok(json)
    } catch {
      case e : APIException => BadRequest("failure : " + e.toString)
    }        
  }

  def async = Action.async {

    //////////////////////////////////////////////////////////////
    // Set URL prefix. 
    //////////////////////////////////////////////////////////////

    // To use the test server, you need to override the default prefix value on the URLPrefix class.
    URLPrefix.prefix = "https://api.korbit.co.kr:8080/v1/"

    //////////////////////////////////////////////////////////////
    // APIs Without Authentication
    //////////////////////////////////////////////////////////////
    println("API : Get API version")
    val version : Version = API.version(); 
    println(version.toString);

    println("API : Get constants such as minimum amount of BTC you can transfer.")
    val constants : Constants = API.constants(); 
    println(constants.toString);

    println("API : Get current price.");
  	API.market.ticker() onComplete {
      case Success(data:Ticker) => println("success : " + data.toString)
      case Failure(error) => println("failure : " + error.toString)
  	}

    println("API : Get current price and low/high/volume of the recent 24 hours.")
    API.market.fullTicker() onComplete {
      case Success(data:FullTicker) => println("success : " + data.toString)
      case Failure(error) => println("failure : " + error.toString)
    }

    println("API : Get complete orderbook.")
    API.market.orderbook() onComplete {
      case Success(data:OrderBook) => println("success : " + data.toString)
      case Failure(error) => println("failure : " + error.toString)
    }

    println("API : Get transactions since transaction id 1.")
    val since = TransactionId(1)
    API.market.transactions(since) onComplete {
      case Success(data:Seq[Transaction]) => println("success : " + data.toString)
      case Failure(error) => println("failure : " + error.toString)
    }

    //////////////////////////////////////////////////////////////
    // APIs With Authentication : User Information
    //////////////////////////////////////////////////////////////
    println("Authentication : Get an authenticated channel with single user API-key.")
    // Note : Multi user API-keys are not supported in this library.
    val channel = API.createChannel(key, secret, username, password)

    println("API : Get user information.")
    channel.user.info() onComplete {
      case Success(data:User) => println("success : " + data.toString)
      case Failure(error) => println("failure : " + error.toString)
    }

    println("API : Get user wallet information.")
    channel.user.wallet() onComplete {
      case Success(data:Wallet) => println("success : " + data.toString)
      case Failure(error) => println("failure : " + error.toString)
    }

    //////////////////////////////////////////////////////////////
    // APIs With Authentication : Managing Orders
    //////////////////////////////////////////////////////////////
    println("API : Get transactions of the user.")
    channel.order.transactions() onComplete {
      case Success(data:Seq[UserTransaction]) => println("success : " + data.toString)
      case Failure(error) => println("failure : " + error.toString)
    }

    println("API : Get open orders of the user.")
    channel.order.openOrders() onComplete {
      case Success(data:Seq[OpenOrder]) => println("success : " + data.toString)
      case Failure(error) => println("failure : " + error.toString)
    }
    
    // These two implicit converters are necessary 
    // to convert Int or Double to BigDecimal parameters in Price and Amount case class.
    implicit def intToBigDecimal(value:Int) = new java.math.BigDecimal(value)
    implicit def doubleToBigDecimal(value:Double) = new java.math.BigDecimal(value)

    println("API : Place a limit order (buy).")
    channel.order.placeLimitOrder(BuyOrder(), Price("krw", 400000), Amount("btc", 0.01)) onComplete {
      case Success(data:OrderId) => println("success : " + data.toString)
      case Failure(error) => println("failure : " + error.toString)
    }

    println("API : Place a limit order (sell).")
    channel.order.placeLimitOrder(SellOrder(), Price("krw", 500000), Amount("btc", 0.01)) onComplete {
      case Success(data:OrderId) => println("success : " + data.toString)
      case Failure(error) => println("failure : " + error.toString)
    }

    println("API : Place a market order (buy).")
    channel.order.placeMarketOrder(BuyOrder(), Amount("krw", 10000)) onComplete {
      case Success(data:OrderId) => println("success : " + data.toString)
      case Failure(error) => println("failure : " + error.toString)
    }

    println("API : Place a market order (sell).")
    channel.order.placeMarketOrder(SellOrder(), Amount("btc", 0.01)) onComplete {
      case Success(data:OrderId) => println("success : " + data.toString)
      case Failure(error) => println("failure : " + error.toString)
    }

    println("API : Place an order, and cancel it right after it was placed.")
    channel.order.placeLimitOrder(BuyOrder(), Price("krw", 410000), Amount("btc", 0.01)) onComplete {
      case Success(data:OrderId) => {
        println("success : " + data.toString)
        channel.order.cancelOrder(Seq(OrderId(data.id))) onComplete {
          case Success(result:Seq[CancelOrderResult]) => result.map { r =>
            println(s"cancel order result : id=${r.orderId}, status=${r.status}")
          }
          case Failure(error) => println("failure : " + error.toString)
        }
      }
      case Failure(error) => println("failure : " + error.toString)
    }

    println("API : Place an order, and check the order status right after it was placed.")
    channel.order.placeLimitOrder(BuyOrder(), Price("krw", 420000), Amount("btc", 0.01)) onComplete {
      case Success(data:OrderId) => {
        println("success : " + data.toString)
        channel.order.transactions(Seq(FillsCategory()), Some(OrderId(data.id)), None) onComplete {
          case Success(data:Seq[UserTransaction]) => {
            // data is Seq of UserTransaction class.
            // If it is empty, the order is not filled yet.
            println("success : " + data.toString)
          }
          case Failure(error) => println("failure : " + error.toString)
        }
      }
      case Failure(error) => println("failure : " + error.toString)
    }

    //////////////////////////////////////////////////////////////
    // APIs With Authentication : Managing Fiats
    //////////////////////////////////////////////////////////////

    println("API : Assign KRW Bank address to which the user can deposit KRW.")
    channel.fiat.assignInAddress() onComplete {
      case Success(data:FiatAddress) => println("success : " + data.toString)
      case Failure(error) => println("failure : " + error.toString)
    }

    println("API : Register KRW Bank address to which the user can withdraw KRW.")
    channel.fiat.registerOutAddress(FiatAddress("우리은행", "1001-100-100000", None)) onComplete {
      case Success(data:FiatAddress) => println("success : " + data.toString)
      case Failure(error) => println("failure : " + error.toString)
    }

    println("API : Request KRW withdrawal.")
    channel.fiat.requestFiatOut(Amount("krw", 10000)) onComplete {
      case Success(req:FiatOutRequest) => {
        println("success : " + req.toString)
        // Query the request
        channel.fiat.queryFiatOut(Some(req)) onComplete {
          case Success(data:Seq[FiatStatus]) => println("success : " + data.toString)
          case Failure(error) => println("failure : " + error.toString)
        }

        // Cancel the request
        channel.fiat.cancelFiatOut(req) onComplete {
          case Success(data:FiatOutRequest) => println("success : " + data.toString)
          case Failure(error) => println("failure : " + error.toString)
        }
      }
      case Failure(error) => println("failure : " + error.toString)
    }

    //////////////////////////////////////////////////////////////
    // APIs With Authentication : Managing Coins
    //////////////////////////////////////////////////////////////

    println("API : Assign BTC address to which the user can deposit BTC.")
    channel.coin.assignInAddress() onComplete {
      case Success(data:CoinAddress) => println("success : " + data.toString)
      case Failure(error) => println("failure : " + error.toString)
    }
    
    println("API : Request BTC withdrawal.")
    val address = CoinAddress( 
                    if ( URLPrefix.prefix.contains("8080") ) "myxvvKU8FrYgkztK4h2xwU88atorcqybMn" // Testnet address
                    else "1anjg6B2XbpjHh8LFw8mXHATH54vrxs2F") // Bitcoin address
    channel.coin.requestCoinOut(Amount("btc", 0.01), address) onComplete {
      case Success(req:CoinOutRequest) => {
        println("success : " + req.toString)
        channel.coin.queryCoinOut(Some(req)) onComplete {
          case Success(data:Seq[CoinStatus]) => println("success : " + data.toString)
          case Failure(error) => println("failure : " + error.toString)
        }

        channel.coin.cancelCoinOut(req) onComplete {
          case Success(data:CoinOutRequest) => println("success : " + data.toString)
          case Failure(error) => println("failure : " + error.toString)
        }
      }
      case Failure(error) => println("failure : " + error.toString)
    }

    ///////////////////////////////////////////////////////////////////////
    // The last example shows how you can pass the result as a web response
    ///////////////////////////////////////////////////////////////////////
    channel.user.info() map { user =>
      val json = org.kangmo.helper.Json.serialize(user)
      Ok(json)
    }
  }
}