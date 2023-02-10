package impl.core;

import api.core.*;
import api.messages.info.ILimitCollectionInfo;
import api.messages.info.IOrderBookInfo;
import api.messages.requests.ICancelOrderRequest;
import api.messages.requests.IPlaceOrderRequest;
import api.messages.responses.IOrderStatusResponse;
import api.messages.responses.IResponse;
import api.messages.responses.OrderResponseStatus;
import api.core.IOrderRequestFactory;
import api.core.Side;
import api.time.ITimestampProvider;
import impl.messages.info.OrderBookInfo;
import impl.messages.responses.OrderStatusResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderBook implements IOrderBook {

    private static final Logger LOGGER = LogManager.getLogger(OrderBook.class);

    private final String bookID;
    private final IOrderRequestFactory orderRequestFactory;
    private final IOrderLookupCache orderLookupCache;
    private final ITimestampProvider timestampProvider;
    private final IEventDataStore eventDataStore;
    private final Map<Side, ILimitCollection> limitCollections = new HashMap<>();

    public OrderBook(String bookID, IOrderRequestFactory orderRequestFactory, IOrderLookupCache orderLookupCache, ITimestampProvider timestampProvider, IEventDataStore eventDataStore) {
        LOGGER.info("Creating OrderBook: " + bookID);
        this.bookID = bookID;
        this.orderRequestFactory = orderRequestFactory;
        this.orderLookupCache = orderLookupCache;
        this.timestampProvider = timestampProvider;
        this.eventDataStore = eventDataStore;
        limitCollections.put(Side.BUY, new LimitCollection(bookID, Side.BUY, orderLookupCache, timestampProvider));
        limitCollections.put(Side.SELL, new LimitCollection(bookID, Side.SELL, orderLookupCache, timestampProvider));
    }

    @Override
    public String getBookID() {
        return bookID;
    }

    @Override
    public synchronized List<IResponse> placeOrder(IPlaceOrderRequest orderRequest) {
        ILimitCollection limitCollection = limitCollections.get(orderRequest.getSide().getCounterSide());
        List<IResponse> responses = limitCollection.matchOrderRequest(orderRequest);
        if (!orderRequest.isMatched()) {
            IOrder order = Order.fromRequest(orderRequest);
            IOrderStatusResponse placedOrderResponse = limitCollections.get(order.getSide()).addOrder(order);
            responses.add(placedOrderResponse);
        }
        eventDataStore.registerResponseEvents(responses);
        return responses;
    }

    @Override
    public synchronized IOrderStatusResponse cancelOrder(ICancelOrderRequest cancelOrderRequest) {
        IOrder order = orderLookupCache.getOrder(cancelOrderRequest.getUserID(), cancelOrderRequest.getOrderID());
        if (order == null) {
            int timestamp = timestampProvider.getTimestampNow();
            return new OrderStatusResponse(bookID, cancelOrderRequest.getUserID(), cancelOrderRequest.getOrderID(), OrderResponseStatus.NULL_ORDER, timestamp);
        }
        ILimitCollection limitCollection = limitCollections.get((order.getSide()));
        IOrderStatusResponse response = limitCollection.cancelOrder(order);
        eventDataStore.registerResponseEvents(List.of(response));
        return response;
    }

    @Override
    public synchronized IOrderBookInfo getInfo() {
        ILimitCollectionInfo buySideInfo = limitCollections.get(Side.BUY).getInfo();
        ILimitCollectionInfo sellSideInfo = limitCollections.get(Side.SELL).getInfo();
        int timestamp = timestampProvider.getTimestampNow();
        return new OrderBookInfo(bookID, buySideInfo, sellSideInfo, eventDataStore.getLastTradePrice(), timestamp);
    }

    @Override
    public synchronized IOrderRequestFactory getOrderRequestFactory() {
        return orderRequestFactory;
    }

    @Override
    public synchronized IEventDataStore getEventDataStore() {
        return eventDataStore;
    }

}
