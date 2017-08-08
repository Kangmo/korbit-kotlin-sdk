package controllers

import kotlinx.coroutines.experimental.runBlocking
import org.kangmo.helper.JsonUtil
import org.kangmo.tradeapi.*


object TestKotlinSDK {
  val key = System.getenv("API_KEY")
  val secret = System.getenv("API_SECRET")
  val username = System.getenv("API_USERNAME")
  val password = System.getenv("API_PASSWORD")

  // These converters are necessary
  // to convert Int or Double to BigDecimal parameters in Price and Amount case class.
  private fun toBig(value:Int) = java.math.BigDecimal(value)
  private fun toBig(value:Double) = java.math.BigDecimal(value)


  fun main(args : Array<String>) {
    sync()

    val userAsJson = runBlocking<String> {
      async()
    }

    println("User as json: ${userAsJson}")
  }

  fun sync() {
    //////////////////////////////////////////////////////////////
    // APIs Without Authentication
    //////////////////////////////////////////////////////////////
    println("API : Get API version")
    val version : Version = API.version();
    println(version.toString());

    println("API : Get constants such as minimum amount of BTC you can transfer.")
    val constants : Constants = API.constants();
    println(constants.toString());

    println("API : Get current price.");
    try {
      val ticker : Ticker = API.sync{ API.market.ticker() }
      println("success : " + ticker)
    } catch(e : APIException) {
      println("failure : " + e.toString())
    }

    println("API : Get current price and low/high/volume of the recent 24 hours.")
    try {
      val fullTicker : FullTicker = API.sync{ API.market.fullTicker() }
      println("success : " + fullTicker)
    } catch(e : APIException) {
      println("failure : " + e.toString())
    }

    println("API : Get complete orderbook.")
    try {
      val orderbook : OrderBook = API.sync{ API.market.orderbook() }
      println("success : " + orderbook.toString())
    } catch(e : APIException) {
      println("failure : " + e.toString())
    }

    println("API : Get transactions since transaction id 1.")
    try {
      val since = TransactionId(1)
      val transactions : List<Transaction> = API.sync{ API.market.transactions(since) }
      println("success : " + transactions.toString())
    } catch(e : APIException) {
      println("failure : " + e.toString())
    }

    //////////////////////////////////////////////////////////////
    // APIs With Authentication : User Information
    //////////////////////////////////////////////////////////////
    println("Authentication : Get an authenticated channel with single user API-key.")
    // Note : Multi user API-keys are not supported in this library.
    val channel = API.createChannel(key, secret, username, password)

    println("API : Get user information.")
    try {
      val user : User = API.sync{ channel.user.info() }
      println("success : " + user.toString())
    } catch(e : APIException) {
      println("failure : " + e.toString())
    }    

    println("API : Get user wallet information.")
    try {
      val wallet : Wallet = API.sync{ channel.user.wallet() }
      println("success : " + wallet.toString())
    } catch(e : APIException) {
      println("failure : " + e.toString())
    }    

    //////////////////////////////////////////////////////////////
    // APIs With Authentication : Managing Orders
    //////////////////////////////////////////////////////////////
    println("API : Get transactions of the user.")
    try {
      val txs : List<UserTransaction> = API.sync{ channel.order.transactions() }
      println("success : " + txs.toString())
    } catch(e : APIException) {
      println("failure : " + e.toString())
    }   

    println("API : Get open orders of the user.")
    try {
      val openOrders : List<OpenOrder> = API.sync{ channel.order.openOrders() }
      println("success : " + openOrders.toString())
    } catch(e : APIException) {
      println("failure : " + e.toString())
    }   
    
    println("API : Place a limit order (buy).")
    try {
      val id : OrderId = API.sync{ channel.order.placeLimitOrder(OrderSide.BuyOrder, Price("krw", toBig(400000)), Amount("btc", toBig(0.01))) }
      println("success : " + id.toString())
    } catch(e : APIException) {
      println("failure : " + e.toString())
    }   

    println("API : Place a limit order (sell).")
    try {
      val id : OrderId = API.sync{ channel.order.placeLimitOrder(OrderSide.SellOrder, Price("krw", toBig(500000)), Amount("btc", toBig(0.01))) }
      println("success : " + id.toString())
    } catch(e : APIException) {
      println("failure : " + e.toString())
    }   

    println("API : Place a market order (buy).")
    try {
      val id : OrderId = API.sync{ channel.order.placeMarketOrder(OrderSide.BuyOrder, Amount("krw", toBig(10000))) }
      println("success : " + id.toString())
    } catch(e : APIException) {
      println("failure : " + e.toString())
    }   

    println("API : Place a market order (sell).")
    try {
      val id : OrderId = API.sync{ channel.order.placeMarketOrder(OrderSide.SellOrder, Amount("btc", toBig(0.01))) }
      println("success : " + id.toString())
    } catch(e : APIException) {
      println("failure : " + e.toString())
    }   

    println("API : Place an order, and cancel it right after it was placed.")
    try {
      val orderId : OrderId = API.sync{ channel.order.placeLimitOrder(OrderSide.BuyOrder, Price("krw", toBig(410000)), Amount("btc", toBig(0.01))) }
      println("success(place order) : " + orderId.toString())

      val result : List<CancelOrderResult> = API.sync{ channel.order.cancelOrder( listOf(OrderId(orderId.id))) }
      println("result(cancel order) : " + result.toString())
    } catch(e : APIException) {
      println("failure : " + e.toString())
    }   

    println("API : Place an order, and check the order status right after it was placed.")
    try {    
      val orderId : OrderId = API.sync{ channel.order.placeLimitOrder(OrderSide.BuyOrder, Price("krw", toBig(420000)), Amount("btc", toBig(0.01))) }
      println("success(place order) : " + orderId.toString())

      val result : List<UserTransaction> = API.sync{ channel.order.transactions( listOf(TransactionCategory.FillsCategory), OrderId(orderId.id), null) }
      println("result(get order if filled) : " + result.toString())
    } catch(e : APIException) {
      println("failure : " + e.toString())
    }   

    //////////////////////////////////////////////////////////////
    // APIs With Authentication : Managing Fiats
    //////////////////////////////////////////////////////////////

    println("API : Assign KRW Bank address to which the user can deposit KRW.")
    try {
      val inAddress : FiatAddress = API.sync{ channel.fiat.assignInAddress() }
      println("success : " + inAddress.toString())
    } catch(e : APIException) {
      println("failure : " + e.toString())
    }   

    println("API : Register KRW Bank address to which the user can withdraw KRW.")
    try {
      val outAddress : FiatAddress = API.sync{ channel.fiat.registerOutAddress(FiatAddress("우리은행", "1001-100-100000", null)) }
      println("success : " + outAddress.toString())
    } catch(e : APIException) {
      println("failure : " + e.toString())
    }   

    println("API : Request KRW withdrawal.")
    try {
      val req : FiatOutRequest = API.sync{ channel.fiat.requestFiatOut(Amount("krw", toBig(10000))) }
      println("success : " + req.toString())

      // Query the request
      val status : List<FiatStatus> = API.sync{ channel.fiat.queryFiatOut( req ) }
      println("result(status) : " + status.toString())

      API.sync{ channel.fiat.cancelFiatOut(req) }
      println("result(cancel) : success")
    } catch(e : APIException) {
      println("failure : " + e.toString())
    }   

    //////////////////////////////////////////////////////////////
    // APIs With Authentication : Managing Coins
    //////////////////////////////////////////////////////////////

    println("API : Assign BTC address to which the user can deposit BTC.")
    try {
      val inAddress : CoinAddress = API.sync{ channel.coin.assignInAddress() }
      println("result(cancel) : " + inAddress.toString())
    } catch(e : APIException) {
      println("failure : " + e.toString());
    }   
    
    println("API : Request BTC withdrawal.")
    try {
      val address = CoinAddress("3FDwqjfu8AZuWP34AHjZFrREYW7DvfVZMY")
      val req : CoinOutRequest = API.sync{ channel.coin.requestCoinOut(Amount("btc", toBig(0.01)), address) }
      println("success : " + req.toString())

      val status : List<CoinStatus> = API.sync{ channel.coin.queryCoinOut(req) }
      println("result(status) : " + status.toString())

      API.sync{ channel.coin.cancelCoinOut(req) }
      println("result(cancel) : success")
    } catch(e : APIException) {
      println("failure : " + e.toString())
    }   

    ///////////////////////////////////////////////////////////////////////
    // The last example shows how you can pass the result as a web response
    ///////////////////////////////////////////////////////////////////////
    try {
      val user : User = API.sync{ channel.user.info() }
      val json = JsonUtil.get().toJson(user)
      println("current user: ${json}")
    } catch(e : APIException) {
      println("failure : " + e.toString())
    }
  }

  suspend fun async() : String {

    //////////////////////////////////////////////////////////////
    // APIs Without Authentication
    //////////////////////////////////////////////////////////////
    println("API : Get API version")
    val version : Version = API.version(); 
    println(version.toString());

    println("API : Get constants such as minimum amount of BTC you can transfer.")
    val constants : Constants = API.constants(); 
    println(constants.toString());

    println("API : Get current price.");
    val ticker:Ticker = API.market.ticker()
    println("success : " + ticker.toString())


    println("API : Get current price and low/high/volume of the recent 24 hours.")
    val fullTicker: FullTicker = API.market.fullTicker()
    println("success : " + fullTicker.toString())

    println("API : Get complete orderbook.")
    val orderBook : OrderBook = API.market.orderbook()
    println("success : " + orderBook.toString())

    println("API : Get transactions since transaction id 1.")
    val since = TransactionId(1)
    val txs: List<Transaction> = API.market.transactions(since)
    println("success : " + txs.toString())

    //////////////////////////////////////////////////////////////
    // APIs With Authentication : User Information
    //////////////////////////////////////////////////////////////
    println("Authentication : Get an authenticated channel with single user API-key.")
    // Note : Multi user API-keys are not supported in this library.
    val channel = API.createChannel(key, secret, username, password)

    println("API : Get user information.")
    val user: User = channel.user.info()
    println("success : " + user.toString())

    println("API : Get user wallet information.")
    val wallet: Wallet = channel.user.wallet()
    println("success : " + wallet.toString())

    //////////////////////////////////////////////////////////////
    // APIs With Authentication : Managing Orders
    //////////////////////////////////////////////////////////////
    println("API : Get transactions of the user.")
    val userTxs: List<UserTransaction> = channel.order.transactions()
    println("success : " + userTxs.toString())


    println("API : Get open orders of the user.")
    val openOrders: List<OpenOrder> = channel.order.openOrders()
    println("success : " + openOrders.toString())

    println("API : Place a limit order (buy).")
    val orderId1: OrderId = channel.order.placeLimitOrder(OrderSide.BuyOrder, Price("krw", toBig(400000)), Amount("btc", toBig(0.01)))
    println("success : " + orderId1.toString())

    println("API : Place a limit order (sell).")
    val orderId2: OrderId = channel.order.placeLimitOrder(OrderSide.SellOrder, Price("krw", toBig(500000)), Amount("btc", toBig(0.01)))
    println("success : " + orderId2.toString())

    println("API : Place a market order (buy).")
    val orderId3:OrderId = channel.order.placeMarketOrder(OrderSide.BuyOrder, Amount("krw", toBig(10000)))
    println("success : " + orderId3.toString())

    println("API : Place a market order (sell).")
    val orderId4: OrderId = channel.order.placeMarketOrder(OrderSide.SellOrder, Amount("btc", toBig(0.01)))
    println("success : " + orderId4.toString())

    println("API : Place an order, and cancel it right after it was placed.")
    val orderId5: OrderId = channel.order.placeLimitOrder(OrderSide.BuyOrder, Price("krw", toBig(410000)), Amount("btc", toBig(0.01)))
    println("success : " + orderId5.toString())
    val cancelOrderResult: List<CancelOrderResult> = channel.order.cancelOrder(listOf(OrderId(orderId5.id)))
    cancelOrderResult.forEach { r ->
      println("cancel order result : id=${r.orderId}, status=${r.status}")
    }

    println("API : Place an order, and check the order status right after it was placed.")
    val orderId6: OrderId = channel.order.placeLimitOrder(OrderSide.BuyOrder, Price("krw", toBig(420000)), Amount("btc", toBig(0.01)))
    println("success : " + orderId6.toString())
    val userTxs2: List<UserTransaction> = channel.order.transactions(listOf(TransactionCategory.FillsCategory), OrderId(orderId6.id), null)
    // data is Seq of UserTransaction class.
    // If it is empty, the order is not filled yet.
    println("success : " + userTxs2.toString())

    //////////////////////////////////////////////////////////////
    // APIs With Authentication : Managing Fiats
    //////////////////////////////////////////////////////////////

    println("API : Assign KRW Bank address to which the user can deposit KRW.")
    val fiatInAddress: FiatAddress = channel.fiat.assignInAddress()
    println("success : " + fiatInAddress.toString())

    println("API : Register KRW Bank address to which the user can withdraw KRW.")
    val fiatOutAddress: FiatAddress = channel.fiat.registerOutAddress(FiatAddress("우리은행", "1001-100-100000", null))
    println("success : " + fiatOutAddress.toString())

    println("API : Request KRW withdrawal.")
    val fiatOutReq: FiatOutRequest = channel.fiat.requestFiatOut(Amount("krw", toBig(10000)))
    println("success : " + fiatOutReq.toString())

    val fiatOutStatuses: List<FiatStatus> = channel.fiat.queryFiatOut(fiatOutReq)
    println("success : " + fiatOutStatuses.toString())

    val fiatOutCancelReq: FiatOutRequest = channel.fiat.cancelFiatOut(fiatOutReq)
    println("success : " + fiatOutCancelReq.toString())


    //////////////////////////////////////////////////////////////
    // APIs With Authentication : Managing Coins
    //////////////////////////////////////////////////////////////

    println("API : Assign BTC address to which the user can deposit BTC.")
    val coinInAddress: CoinAddress = channel.coin.assignInAddress()
    println("success : " + coinInAddress.toString())


    println("API : Request BTC withdrawal.")
    val address = CoinAddress( "3FDwqjfu8AZuWP34AHjZFrREYW7DvfVZMY" ) // Bitcoin address

    val coinOutReq: CoinOutRequest = channel.coin.requestCoinOut(Amount("btc", toBig(0.01)), address)
    println("success : " + coinOutReq.toString())

    val coinStatuses: List<CoinStatus> = channel.coin.queryCoinOut(coinOutReq)
    println("success : " + coinStatuses.toString())

    val coinOutCancelReq: CoinOutRequest = channel.coin.cancelCoinOut(coinOutReq)
    println("success : " + coinOutCancelReq.toString())

    ///////////////////////////////////////////////////////////////////////
    // The last example shows how you can pass the result as a return value
    ///////////////////////////////////////////////////////////////////////
    val userInfo = channel.user.info()
    val userAsJson = JsonUtil.get().toJson( userInfo )

    return userAsJson
  }
}
