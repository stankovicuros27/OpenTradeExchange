package impl.core;

import api.core.*;
import api.messages.internal.info.ILimitCollectionInfo;
import api.messages.internal.info.IOrderBookInfo;
import api.messages.internal.requests.ICancelOrderRequest;
import api.messages.internal.requests.IPlaceOrderRequest;
import api.messages.internal.responses.IOrderStatusResponse;
import api.messages.internal.responses.IResponse;
import api.messages.internal.responses.OrderResponseStatus;
import api.sides.Side;
import api.time.ITimestampProvider;
import impl.messages.internal.info.OrderBookInfo;
import impl.messages.internal.responses.OrderStatusResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderBook implements IOrderBook {

    private static final Logger LOGGER = LogManager.getLogger(OrderBook.class);

    private final Map<Side, ILimitCollection> limitCollections = new HashMap<>();
    private final IOrderLookupCache orderLookupCache;
    private final ITimestampProvider timestampProvider;
    private final IEventDataStore eventDataStore;

    public OrderBook(IOrderLookupCache orderLookupCache, ITimestampProvider timestampProvider, IEventDataStore eventDataStore) {
        LOGGER.info("Creating OrderBook");
        this.orderLookupCache = orderLookupCache;
        this.timestampProvider = timestampProvider;
        this.eventDataStore = eventDataStore;
        limitCollections.put(Side.BUY, new LimitCollection(Side.BUY, orderLookupCache, timestampProvider));
        limitCollections.put(Side.SELL, new LimitCollection(Side.SELL, orderLookupCache, timestampProvider));
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
            return new OrderStatusResponse(cancelOrderRequest.getUserID(), cancelOrderRequest.getOrderID(), OrderResponseStatus.NULL_ORDER, timestamp);
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
        return new OrderBookInfo(buySideInfo, sellSideInfo, eventDataStore.getLastTradePrice(), timestamp);
    }

    @Override
    public synchronized IEventDataStore getEventDataStore() {
        return eventDataStore;
    }

}
