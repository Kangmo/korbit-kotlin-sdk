package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import play.api.db._

import org.kangmo.tradeapi._
import anorm._

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import java.io.StringWriter

object Application extends Controller {
  val log = play.Logger.of("application")
  val config = Play.application.configuration
  val key = config.getString("trade.korbit.key").get
  val secret = config.getString("trade.korbit.secret").get
  val username = config.getString("trade.korbit.username").get
  val password = config.getString("trade.korbit.password").get

  def index = Action {

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
  	API.market.ticker() {
  		errorOrData => 
  		errorOrData match {
        case Left(error) => println("failure : " + error.toString)
  			case Right(data:Ticker) => println("success : " + data.toString)
  		}
  	}

    println("API : Get current price and low/high/volume of the recent 24 hours.")
    API.market.fullTicker() {
      errorOrData => 
      errorOrData match {
        case Left(error) => println("failure : " + error.toString)
        case Right(data:FullTicker) => println("success : " + data.toString)
      }
    }

    println("API : Get complete orderbook.")
    API.market.orderbook() {
      errorOrData => 
      errorOrData match {
        case Left(error) => println("failure : " + error.toString)
        case Right(data:OrderBook) => println("success : " + data.toString)
      }
    }

    println("API : Get transactions since transaction id 1.")
    val since = TransactionId(1)
    API.market.transactions(since) {
      errorOrData => 
      errorOrData match {
        case Left(error) => println("failure : " + error.toString)
        case Right(data:Seq[Transaction]) => println("success : " + data.toString)
      }
    }

    //////////////////////////////////////////////////////////////
    // APIs With Authentication : User Information
    //////////////////////////////////////////////////////////////
    println("Authentication : Get an authenticated channel with single user API-key.")
    // Note : Multi user API-keys are not supported in this library.
    val channel = API.createChannel(key, secret, username, password)

    println("API : Get user information.")
    channel.user.info() {
      errorOrData => 
      errorOrData match {
        case Left(error) => println("failure : " + error.toString)
        case Right(data:User) => println("success : " + data.toString)
      }
    }

    println("API : Get user wallet information.")
    channel.user.wallet() {
      errorOrData => 
      errorOrData match {
        case Left(error) => println("failure : " + error.toString)
        case Right(data:Wallet) => println("success : " + data.toString)
      }
    }

    //////////////////////////////////////////////////////////////
    // APIs With Authentication : Managing Orders
    //////////////////////////////////////////////////////////////
    println("API : Get transactions of the user.")
    channel.order.transactions() {
      errorOrData => 
      errorOrData match {
        case Left(error) => println("failure : " + error.toString)
        case Right(data:Seq[UserTransaction]) => println("success : " + data.toString)
      }
    }

    println("API : Get open orders of the user.")
    channel.order.openOrders() {
      errorOrData => 
      errorOrData match {
        case Left(error) => println("failure : " + error.toString)
        case Right(data:Seq[OpenOrder]) => println("success : " + data.toString)
      }
    }
    
    // These two implicit converters are necessary 
    // to convert Int or Double to BigDecimal parameters in Price and Amount case class.
    implicit def intToBigDecimal(value:Int) = new java.math.BigDecimal(value)
    implicit def doubleToBigDecimal(value:Double) = new java.math.BigDecimal(value)

    println("API : Place a limit order (buy).")
    channel.order.placeLimitOrder(BuyOrder(), Price("krw", 400000), Amount("btc", 0.01)) {
      errorOrData => 
      errorOrData match {
        case Left(error) => println("failure : " + error.toString)
        case Right(data:OrderId) => println("success : " + data.toString)
      }
    }

    println("API : Place a limit order (sell).")
    channel.order.placeLimitOrder(SellOrder(), Price("krw", 500000), Amount("btc", 0.01)) {
      errorOrData => 
      errorOrData match {
        case Left(error) => println("failure : " + error.toString)
        case Right(data:OrderId) => println("success : " + data.toString)
      }
    }

    println("API : Place a market order (buy).")
    channel.order.placeMarketOrder(BuyOrder(), Amount("krw", 10000)) {
      errorOrData => 
      errorOrData match {
        case Left(error) => println("failure : " + error.toString)
        case Right(data:OrderId) => println("success : " + data.toString)
      }
    }

    println("API : Place a market order (sell).")
    channel.order.placeMarketOrder(SellOrder(), Amount("btc", 0.01)) {
      errorOrData => 
      errorOrData match {
        case Left(error) => println("failure : " + error.toString)
        case Right(data:OrderId) => println("success : " + data.toString)
      }
    }

    println("API : Place an order, and cancel it right after it was placed.")
    channel.order.placeLimitOrder(BuyOrder(), Price("krw", 410000), Amount("btc", 0.01)) {
      errorOrData => 
      errorOrData match {
        case Left(error) => println("failure : " + error.toString)
        case Right(data:OrderId) => {
          println("success : " + data.toString)
          channel.order.cancelOrder(Seq(OrderId(data.id))) {
            result:Seq[CancelOrderResult] => result.map { r =>
              println(s"cancel order result : id=${r.orderId}, status=${r.status}")
            }
          }
        }
      }
    }

    println("API : Place an order, and check the order status right after it was placed.")
    channel.order.placeLimitOrder(BuyOrder(), Price("krw", 420000), Amount("btc", 0.01)) {
      errorOrData => 
      errorOrData match {
        case Left(error) => println("failure : " + error.toString)
        case Right(data:OrderId) => {
          println("success : " + data.toString)
          channel.order.transactions(Seq(FillsCategory()), Some(OrderId(data.id)), None) {
            errorOrData => 
            errorOrData match {
              case Left(error) => println("failure : " + error.toString)
              case Right(data:Seq[UserTransaction]) => {
                // data is Seq of UserTransaction class.
                // If it is empty, the order is not filled yet.
                println("success : " + data.toString)
              }
            }
          }
        }
      }
    }

    //////////////////////////////////////////////////////////////
    // APIs With Authentication : Managing Fiats
    //////////////////////////////////////////////////////////////

    println("API : Assign KRW Bank address to which the user can deposit KRW.")
    channel.fiat.assignInAddress() {
      errorOrData => 
      errorOrData match {
        case Left(error) => println("failure : " + error.toString)
        case Right(data:FiatAddress) => println("success : " + data.toString)
      }
    }

    println("API : Register KRW Bank address to which the user can withdraw KRW.")
    channel.fiat.registerOutAddress(FiatAddress("우리은행", "1001-100-100000", None)) {
      errorOrData => 
      errorOrData match {
        case Left(error) => println("failure : " + error.toString)
        case Right(data:FiatAddress) => println("success : " + data.toString)
      }
    }

    println("API : Request KRW withdrawal.")
    channel.fiat.requestFiatOut(Amount("krw", 10000)) {
      errorOrData => 
      errorOrData match {
        case Left(error) => println("failure : " + error.toString)
        case Right(req:FiatOutRequest) => {
          println("success : " + req.toString)
          // Query the request
          channel.fiat.queryFiatOut(Some(req)) {
            errorOrData => 
            errorOrData match {
              case Left(error) => println("failure : " + error.toString)
              case Right(data:Seq[FiatStatus]) => println("success : " + data.toString)
            }
          }

          // Cancel the request
          channel.fiat.cancelFiatOut(req) {
            errorOrData => 
            errorOrData match {
              case Some(error) => println("failure : " + error.toString)
              case None => println("success.")
            }
          }
        }
      }
    }

    //////////////////////////////////////////////////////////////
    // APIs With Authentication : Managing Coins
    //////////////////////////////////////////////////////////////

    println("API : Assign BTC address to which the user can deposit BTC.")
    channel.coin.assignInAddress() {
      errorOrData => 
      errorOrData match {
        case Left(error) => println("failure : " + error.toString)
        case Right(data:CoinAddress) => println("success : " + data.toString)
      }
    }
    
    println("API : Request BTC withdrawal.")
    val address = CoinAddress( 
                    if ( URLPrefix.prefix.contains("8080") ) "myxvvKU8FrYgkztK4h2xwU88atorcqybMn" // Testnet address
                    else "1anjg6B2XbpjHh8LFw8mXHATH54vrxs2F") // Bitcoin address
    channel.coin.requestCoinOut(Amount("btc", 0.01), address) {
      errorOrData => 
      errorOrData match {
        case Left(error) => println("failure : " + error.toString)
        case Right(req:CoinOutRequest) => {
          println("success : " + req.toString)
          channel.coin.queryCoinOut(Some(req)) {
            errorOrData => 
            errorOrData match {
              case Left(error) => println("failure : " + error.toString)
              case Right(data:Seq[CoinStatus]) => println("success : " + data.toString)
            }
          }

          channel.coin.cancelCoinOut(req) {
            errorOrData => 
            errorOrData match {
              case Some(error) => println("failure : " + error.toString)
              case None => println("success.")
            }
          }
        }
      }
    }

    Ok(views.html.index("Your new application is ready."))
  }
}