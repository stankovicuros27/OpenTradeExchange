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

## Modules

#### Matching Engine
- Matching Engine is the core module of every electronic exchange, which is used to store (usually in memory) and match buy and sell orders for a given financial instrument. 
- When buy and sell orders are matched, trade takes place which triggers a sequence of events (such as persisting trade data, updating orders & order book, notifying users...).
- Matching Engine is usually the most critical part of trading systems when performance is considered, since each action must be performed sequentially (concurrency is not possible since strict order sequencing must be guaranteed).

 <img src="https://i.imgur.com/nXrAc8h.jpg" width="350" height="300">

- In this implementation, MatchingEngine is used as a container for multiple OrderBooks. OrderBooks of the same group (arbitrary) should be places inside one MatchingEngine (in future, it might be possible to add support for more complex order types including atomic orders for multiple OrderBooks inside the same MatchingEngine).
- OrderBook is used for all trading action related to one instrument, identified by bookID. It contains all BUY orders grouped on the one side, and all SELL orders on the other side. Orders are sorted based on priority (it's usually price-time priority). OrderBook knows how to handle new requests (PlaceOrder, CancelOrder etc.) and match orders (generate Trade events).

 <img src="https://i.imgur.com/4HwbBRG.jpg">

- Each OrderBook contains two LimitCollections - one for BUY side, and one for SELL side. The [spread](https://www.investopedia.com/terms/s/spread.asp "spread") is calculated as difference between the best (highest) buy order and the best (lowest) sell order. Besides spread, other important information include Volume (BUY, SELL), Number of orders (BUY, SELL), [Market depth](https://www.investopedia.com/terms/m/marketdepth.asp#:~:text=Market%20depth%20refers%20to%20a,trading%20within%20an%20individual%20security. "Market depth") (number of orders at each price level - limit) and others.
  - LimitCollections are implemented as sorted maps of Limits, providing most operations to run in O(1) and some in O(logN) complexity.
- Limits are essentially lists of Orders that share the same price (and inherently side). List is usually sorted based on some constraint. Here we have time priority - Orders which have been submitted earlier have greater priority to be matched. 
  > Other strategies might include volume priority (Orders with highest volume have the biggest priority), combination of volume and time, and others.
- Each Order contains information about the user who submitted the Order, price, volume, timestamps and filled volume. When all the volume is filled, Order status is changed to closed and Order is removed from the OrderBook. Furthermore, if user cancels the Order, it gets removed immediately.
- Besides these structural classes used to implement OrderBook functionalities, there are also OrderFactory (used for issuing new Orders for each user), OrderLookupCache (used for fast Order queries), and EventDataStore (used for tracking the number of events occured in the OrderBook).

#### Analytics
- Analytics module is used to measure the performance of MatchingEngine. It shows the current state of each of the OrderBooks inside the given MatchingEngine (buy price, sell price, last trade price, volumes, number of orders...), but also the number of events taking place each second for different event types, per OrderBook.

<img src="https://i.imgur.com/cutnIAt.jpg">

- In the picture above, MatchingEngine has 4 independent OrderBooks (instruments), with 5 users trading each instrument. Total number of events at each of the OrderBooks reaches **~2M/s**, with capabilities of **handling ~550k/s new PlaceOrder requests**.
- Performance testing was conducted on MacBook Pro with M1 chip (2022), showing **very good MatchingEngine performance** (even comparable with professional electronic exchange systems, but with far less robustness and functionalities).

