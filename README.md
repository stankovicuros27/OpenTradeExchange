# Open Trade Exchange

 ![OpenTradeExchange](https://i.imgur.com/wWzKpGJ.jpg)

 </br>

## Simple financial electronic exchange system

- The goal of this project is to implement small but fully functional electronic exchange system.
- OpenTradeExchange includes most core functionalities seen in the professional electronic exchanges around the world, but with reduced scope and robustness in order to be easily understandable and readable.
- The code in OpenTradeExchange is based on a large number of simplifying assumptions - focusing more on the order matching subsystem instead of security, messaging, persistance etc.
- OpenTradeExchange is developed as a final University project by only one student, with limited resources and time constraints, and should be regarded as such.

 </br>

## Features & functionalities

#### Users
- Users first need to register in order to access Exchange data, send their orders and trade with other users. After user submits the registration request, admin or moderator (special user types) can accept the registration request making the user eligible for further interaction with the Exchange.
  - Example:  
	User: REGISTER (Username:trader, Password:trader123)  
	Admin/Moderator: ACCEPT (Username:trader)  
- Accepted users can send [place order requests](https://www.investopedia.com/ask/answers/073015/how-do-i-place-order-buy-or-sell-shares.asp "place order requests") with intention to buy or sell an instrument on the exchange (they must specify what they want to buy or sell, quantity, price). All orders are interpreted as [limit orders](https://www.investopedia.com/terms/l/limitorder.asp "limit orders"). 
  - Example: BUY (TSLA, QTY:10, PRICE:150$)  
- After user sent the place order request, provided there is a [counterparty](https://www.investopedia.com/terms/c/counterparty.asp#:~:text=A%20counterparty%20is%20simply%20the,their%20end%20of%20the%20transaction. "counterparty") which is willing to [trade](https://www.investopedia.com/terms/m/matchingorders.asp "trade") (price and quantity on the buy and sell side matches), trade is executed and counterparties are notified.
  - Example:  
  User1 : BUY (TSLA, QTY:10, PRICE:150$)  
  User2 : SELL (TSLA, QTY:5, PRICE:148$)  
  Trade : (User1:BUY, User2:SELL, TSLA, QTY:5, PRICE:150$?)  
  > There are multiple strategies on how to determine the trade price. Here, the aggressive counterparty (one which takes the other counterparty's order - in this case User2) gets the price improvement
- Users can query the exchange for all of their orders and previous trades with other counterparties.
- Users can send [cancel order requests](https://www.investopedia.com/terms/c/canceled_order.asp "cancel order requests") if they want to cancel previously submitted orders.

#### Exchange
- Exchange runs as a central server, exposing both web (HTTP) and direct (TCP) APIs for users.
- Main job of the exchange server is to run [Matching Engines](https://en.wikipedia.org/wiki/Order_matching_system "Matching Engines") for each tradeable instrument, alowing trading to happen between counterparties.
- Tradeable instrument can be registered on the exchange (designated [Order Book](https://www.investopedia.com/terms/o/order-book.aspm "Order Book") is created, after which users can buy and sell that instrument).
- Exchange regularly listens for new direct connections from users (TCP).
- Exchange regularly listens for new HTTP requests submitted by users. 
- Before each request is processed, exchange first authenticates the user, and only if authentication is valid further processing takes place.
- After user successfully sent the request (place/cancer order or query some data), ack message is sent back to the user. Furthermore, user gets notified whenever some of the orders gets matched, or the status changes.
- Exchange regularly broadcasts [L1 and L2 data](https://www.investopedia.com/terms/l/level1.asp "L1 and L2 data"), which can be also queried by HTTP request.
- Exchange monitors it's internal state & performance, notifying admins if something bad happens.
- Exchange persists User Data inside MySQL database, and Trading Data inside MongoDB database, exposing APIs for retreiving data for authorized users.

 </br>

## Core modules

- This project is written in Java and based on Maven and Docker. It uses two external Docker images (MySQL and Mongo), and creates one internal image (ExchangeServer). 

#### Matching Engine
- Matching Engine is the core module of every electronic exchange, which is used to store (usually in memory) and match buy and sell orders for a given financial instrument. 
- When buy and sell orders are matched, trade takes place which triggers a sequence of events (such as persisting trade data, updating orders & order book, notifying users...).
- Matching Engine is usually the most critical part of trading systems when performance is considered, since each action must be performed sequentially (concurrency is not possible since strict order sequencing must be guaranteed).
- <img src="https://i.imgur.com/nXrAc8h.jpg" width="350" height="300">
- In this implementation, MatchingEngine is used as a container for multiple OrderBooks. OrderBooks of the same group (arbitrary) should be places inside one MatchingEngine (in future, it might be possible to add support for more complex order types including atomic orders for multiple OrderBooks inside the same MatchingEngine).
- OrderBook is used for all trading action related to one instrument, identified by bookID. It contains all BUY orders grouped on the one side, and all SELL orders on the other side. Orders are sorted based on priority (it's usually price-time priority). OrderBook knows how to handle new requests (PlaceOrder, CancelOrder etc.) and match orders (generate Trade events).
- <img src="https://i.imgur.com/4HwbBRG.jpg">
- Each OrderBook contains two LimitCollections - one for BUY side, and one for SELL side. The [spread](https://www.investopedia.com/terms/s/spread.asp "spread") is calculated as difference between the best (highest) buy order and the best (lowest) sell order. Besides spread, other important information include Volume (BUY, SELL), Number of orders (BUY, SELL), [Market depth](https://www.investopedia.com/terms/m/marketdepth.asp#:~:text=Market%20depth%20refers%20to%20a,trading%20within%20an%20individual%20security. "Market depth") (number of orders at each price level - limit) and others.
  - LimitCollections are implemented as sorted maps of Limits, providing most operations to run in O(1) and some in O(logN) complexity.
- Limits are essentially lists of Orders that share the same price (and inherently side). List is usually sorted based on some constraint. Here we have time priority - Orders which have been submitted earlier have greater priority to be matched. 
  > Other strategies might include volume priority (Orders with highest volume have the biggest priority), combination of volume and time, and others.
- Each Order contains information about the user who submitted the Order, price, volume, timestamps and filled volume. When all the volume is filled, Order status is changed to closed and Order is removed from the OrderBook. Furthermore, if user cancels the Order, it gets removed immediately.
- Besides these structural classes used to implement OrderBook functionalities, there are also OrderFactory (used for issuing new Orders for each user), OrderLookupCache (used for fast Order queries), and EventDataStore (used for tracking the number of events occured in the OrderBook).

#### Analytics
- Analytics module is used to measure the performance of MatchingEngine. It shows the current state of each of the OrderBooks inside the given MatchingEngine (buy price, sell price, last trade price, volumes, number of orders...), but also the number of events taking place each second for different event types, per OrderBook.
- <img src="https://i.imgur.com/cutnIAt.jpg">
- In the picture above, MatchingEngine has 4 independent OrderBooks (instruments), with 5 users trading each instrument. Total number of events at each of the OrderBooks reaches **~2M/s**, with capabilities of **handling ~550k/s new PlaceOrder requests**.
- Performance testing was conducted on MacBook Pro with M1 chip (2022), showing **very good MatchingEngine performance** (even comparable with professional electronic exchange systems, but with far less robustness and functionalities).

#### ExchangeServer
- ExchangeServer Module is the entry point for starting the whole system. It depends on & includes other modules such as MatchingEngine and Database modules (MongoDB & MySQL).
- ExchangeServer consists of two parts:
  - Web server: handles HTTP requests (higher latency, authentication on every message)
  - Direct server: handles direct TCP port connections with users (low latency, once the authentication is completed and connection established, TCP port communication is continous)
- Both parts share the same databases and MatchingEngine, just providing different APIs for users to communicate with the system
- Web Server
  - Implemented as a set of Servlets, using Java Servlet features ([JakartaEE](https://jakarta.ee/ "JakartaEE")).
  - Running on [Jetty](https://www.eclipse.org/jetty/ "Jetty") server/servlet container (Jetty is run as a Maven plugin).
  - Each request contains authentication data, as well as the exchange request data (such as PlaceOrder, CancelOrder, GetOrders, GetL1Data, GetL2Data...).
  - After the request is processed, server returns the response which contains requested data (or error).
- Direct Server
  - Implemented using Java Socket API (using TCP port direct communication, and UDP broadcast).
  - Server opens a port (configurable via configuration file) which listens for new TCP connection. After the connection is established, Server first performs user authentication, and if validated proceeds to communicating trading requests and data.
  - After each TCP request sent by user is processed, Server returns the corresponding response (user action status, trade data, etc.). Communication remains open until user disconnects.
  - Along with sending responses for user requests, server also sends information about events and changes in user's orders (personalized message, sent only to affected participants). This keeps TCP connected users updated at all times.
  - Furthermore, Server broadcasts L1 and L2 market data using UDP Multicast protocol (ports and IP addresses configurable in configuration file, as well as timeout between sending data batches).
- </br>
- All Server messages use MicroFIX protocol templates (separate Module that defines message structure and constraints).
- Server updates databases after every action.

#### AuthenticationDB
- Authentication Database is a separate module used for managing MySQL user database. It contains information about users and user types.
- Since information about users & user types is not changed frequently, CRUD operations on this database should not affect performance dramatically.
- In future updates, this database could be extended with new information and relationships between users, exchange and other participants. That's why it's important to use relational database for this module.
- MySQL Database connection is established using JDBC, and database is run inside Docker.

#### TradingDataDB
- Trading Data Database is a separate module user for managing MongoDB database which holds information about orders, trades and other events inside the exchange system.
- Since trading data is updated very frequently, operations on this database can affect performance significantly, so non-relational and scalable DB engine like Mongo is selected for this module.
- Information inside this database is very simple and can be represented by json objects (it's non relational).
- In future updates, this database could be extended with new types of events and orders as well as various encryption & security techniques.

#### TraderAgents
- Trader Agents module contains individual (dummy) trading strategies which are using both TCP and HTTP requests to communicate with the exchange.
- Trader Agents are used for internal testing and performance testing.
- Code from this module can be used as an example on how to make custom new trading strategies, as well as how to connect to the exchange & receive exchange data.

</br>

## Configuration & starting the exchange
- In order to configure the exchange, change the server-config.properties file in the root folder.
  - Default config:
	```
	exchangeServer.ip=localhost
	matchingEngine.orderBooks=test1:1;test2:2;test3:3;test4:4
	exchangeTcpPort=9999
	exchangeMulticastIp=225.4.5.6
	l1DataTCPPort=9998
	l1DataMulticastPort=9997
	l1TimeoutMS=500
	l2DataTCPPort=9996
	l2DataMulticastPort=9995
	l2TimeoutMS=2500
	multicastEnabled=true
	analyticsEnabled=true
	authenticationDbEnabled=true
	tradingDbEnabled=true

	```

- In order to run the application, first create the exchange Docker image
  - Inside project root folder run:
	```
	docker build -t stankovicuros27/opentradeexchange .
	```

  - Then, run 
  ```
  docker-compose up 
  ```
  which runs the docker-compose.yaml

