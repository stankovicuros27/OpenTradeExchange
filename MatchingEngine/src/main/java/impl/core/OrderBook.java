package impl.core;

import api.core.*;
import api.messages.info.ILimitCollectionInfo;
import api.messages.info.IOrderBookInfo;
import api.messages.requests.ICancelOrderRequest;
import api.messages.requests.IPlaceOrderRequest;
import api.messages.responses.IOrderStatusResponse;
import api.messages.responses.IResponse;
import api.messages.responses.OrderResponseStatus;
import api.util.ITimestampProvider;
import impl.messages.info.OrderBookInfo;
import impl.messages.responses.OrderStatusResponse;
import impl.util.InstantTimestampProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderBook implements IOrderBook {

    // TODO dependency injection
    private final Map<Side, ILimitCollection> limitCollections = new HashMap<>();
    private final IOrderLookupCache orderLookupCache = new OrderLookupCache();
    private final ITimestampProvider timestampProvider = new InstantTimestampProvider();

    public OrderBook() {
        limitCollections.put(Side.BUY, new LimitCollection(Side.BUY, orderLookupCache, timestampProvider));
        limitCollections.put(Side.SELL, new LimitCollection(Side.SELL, orderLookupCache, timestampProvider));
    }

    @Override
    public List<IResponse> placeOrder(IPlaceOrderRequest orderRequest) {
        ILimitCollection limitCollection = limitCollections.get((orderRequest.getSide().getCounterSide()));
        List<IResponse> responses = limitCollection.matchOrderRequest(orderRequest);
        if (!orderRequest.isMatched()) {
            IOrder order = Order.fromRequest(orderRequest);
            IOrderStatusResponse placedOrderResponse = limitCollections.get(order.getSide()).addOrder(order);
            responses.add(placedOrderResponse);
        }
        return responses;
    }

    @Override
    public IOrderStatusResponse cancelOrder(ICancelOrderRequest cancelOrderRequest) {
        IOrder order = orderLookupCache.getOrder(cancelOrderRequest.getUserID(), cancelOrderRequest.getOrderID());
        if (order == null) {
            return new OrderStatusResponse(cancelOrderRequest.getUserID(), cancelOrderRequest.getOrderID(), OrderResponseStatus.NULL_ORDER);
        }
        ILimitCollection limitCollection = limitCollections.get((order.getSide()));
        return limitCollection.cancelOrder(order);
    }

    @Override
    public IOrderBookInfo getInfo() {
        ILimitCollectionInfo buySideInfo = limitCollections.get(Side.BUY).getInfo();
        ILimitCollectionInfo sellSideInfo = limitCollections.get(Side.SELL).getInfo();
        return new OrderBookInfo(buySideInfo, sellSideInfo);
    }

}
