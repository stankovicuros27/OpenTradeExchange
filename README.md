## Open Trade Exchange

### Simple financial electronic exchange system

- The goal of this project is to implement small but fully functional electronic exchange system.
- OpenTradeExchange includes most core functionalities seen in the professional electronic exchanges around the world, but with reduced scope and robustness in order to be easily understandable and readable.
- The code in OpenTradeExchange is based on a large number of simplifying assumptions - focusing more on the order matching subsystem instead of security, messaging, persistance etc.
- OpenTradeExchange is developed as a final University project by only one student, with limited resources and time constraints, and should be regarded as such.


### Features & functionalities
##### Users

- Users first need to register in order to access Exchange data, send their orders and trade with other users. After the user submits the registration request, admin or moderator (special user types) can accept the registration request making the user eligible for further interaction with the Exchange.
 - Example: 
	User: REGISTER Username:trader, Password:trader123  
	Admin/Moderator: ACCEPT Username:trader  
- Accepted users can send [place order requests](https://www.investopedia.com/ask/answers/073015/how-do-i-place-order-buy-or-sell-shares.asp "place order requests") with intention to buy or sell an instrument on the exchange (they must specify what they want to buy or sell, quantity, price). All orders are interpreted as [limit orders](https://www.investopedia.com/terms/l/limitorder.asp "limit orders"). 
 - Example: BUY TSLA, QTY:10, PRICE:150$  
- After user sent the place order request, and there is a [counterparty](https://www.investopedia.com/terms/c/counterparty.asp#:~:text=A%20counterparty%20is%20simply%20the,their%20end%20of%20the%20transaction. "counterparty") which is willing to [trade](https://www.investopedia.com/terms/m/matchingorders.asp "trade") (price and quantity on the buy and sell side matches), trade is executed and counterparties are notified.
 - Example:
 User1 : BUY TSLA, QTY:10, PRICE:150$  
 User2 : SELL TSLA, QTY:5, PRICE:148$  
 Trade : (User1:BUY, User2:SELL, TSLA, QTY:5, PRICE:150$?)  
*There are multiple strategies on how to determine the price. Here, the aggressive counterparty (one which takes the other counterparty's order - in this case User2) gets the price improvement*  
- Users can query the exchange for all of their orders and previous trades with other counterparties.
- Users can send [cancel order requests](https://www.investopedia.com/terms/c/canceled_order.asp#:~:text=Investors%20cancel%20orders%20through%20an,the%2Dother%20(OCO) if they want to cancel previously submitted orders.

##### Exchange
- Exchange runs as a central server, exposing both web and direct APIs for users.
- Main job of the exchange server is to run [Matching Engines](https://en.wikipedia.org/wiki/Order_matching_system "Matching Engines") for each tradeable instrument, persist all trading and user data and communicate it's internal state to the users.
- Tradeable instruments can be registered on the exchange, after which users can buy and sell that instrument.
- Exchange regularly listens for new direct connections from users (TCP).
- Exchange regularly listens for new HTTP requests submitted by users. 
- Before each request is processed, exchange first authenticates the user, and only if authentication passes further processing takes place.
- Exchange regularly broadcasts [L1 and L2 data](https://www.investopedia.com/terms/l/level1.asp "L1 and L2 data"), which can be also queried by HTTP request.
- Exchange monitors it's internal state & performance, notifying admins if something bad happens.
