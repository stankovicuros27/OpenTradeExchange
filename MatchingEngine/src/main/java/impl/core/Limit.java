package impl.core;

import api.core.ILimit;
import api.core.IOrder;
import api.core.IOrderLookupCache;
import api.messages.internal.info.ILimitInfo;
import api.messages.internal.requests.IPlaceOrderRequest;
import api.messages.internal.responses.IOrderStatusResponse;
import api.messages.internal.responses.IResponse;
import api.messages.internal.responses.ITradeResponse;
import api.messages.internal.responses.OrderResponseStatus;
import api.sides.Side;
import api.time.ITimestampProvider;
import impl.messages.internal.info.LimitInfo;
import impl.messages.internal.responses.OrderStatusResponse;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Limit implements ILimit {
    private final Side side;
    private final double price;
    private final IOrderLookupCache orderLookupCache;
    private final ITimestampProvider timestampProvider;

    private int volume = 0;
    private final Queue<IOrder> orders = new LinkedList<>();

    Limit(Side side, double price, IOrderLookupCache orderLookupCache, ITimestampProvider timestampProvider) {
        this.side = side;
        this.price = price;
        this.orderLookupCache = orderLookupCache;
        this.timestampProvider = timestampProvider;
    }

    @Override
    public IOrderStatusResponse addOrder(IOrder order) {
        volume += order.getUnfilledVolume();
        orders.add(order);
        orderLookupCache.addOrder(order);
        int timestamp = timestampProvider.getTimestampNow();
        return new OrderStatusResponse(order.getUserID(), order.getOrderID(), OrderResponseStatus.PLACED_ORDER, timestamp);
    }

    @Override
    public IOrderStatusResponse cancelOrder(IOrder order) {
        volume -= order.getUnfilledVolume();
        orders.remove(order);
        orderLookupCache.removeOrder(order);
        int timestamp = timestampProvider.getTimestampNow();
        return new OrderStatusResponse(order.getUserID(), order.getOrderID(), OrderResponseStatus.CANCELLED_ORDER, timestamp);
    }

    @Override
    public List<IResponse> matchOrderRequest(IPlaceOrderRequest orderRequest) {
        List<IResponse> responses = new ArrayList<>();
        int timestamp = timestampProvider.getTimestampNow();
        while(!orderRequest.isMatched() && !orders.isEmpty()) {
            IOrder matchingOrder = orders.peek();
            ITradeResponse trade = Order.matchOrderRequest(matchingOrder, orderRequest, timestampProvider);
            volume -= trade.getVolume();
            responses.add(trade);
            if (matchingOrder.isClosed()) {
                orders.remove(matchingOrder);
                orderLookupCache.removeOrder(matchingOrder);
                responses.add((new OrderStatusResponse(matchingOrder.getUserID(), matchingOrder.getOrderID(), OrderResponseStatus.CLOSED_ORDER, timestamp)));
            }
        }
        if (orderRequest.isMatched()) {
            responses.add((new OrderStatusResponse(orderRequest.getUserID(), orderRequest.getOrderID(), OrderResponseStatus.CLOSED_ORDER, timestamp)));
        }
        return responses;
    }

    @Override
    public IOrder getBestOrder() {
        return orders.peek();
    }

    @Override
    public int getVolume() {
        return volume;
    }

    @Override
    public int getNumberOfOrders() {
        return orders.size();
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public boolean isEmpty() {
        return orders.isEmpty();
    }

    @Override
    public Side getSide() {
        return side;
    }

    @Override
    public ILimitInfo getLimitInfo() {
        int timestamp = timestampProvider.getTimestampNow();
        return new LimitInfo(side, price, volume, orders.size(), timestamp);
    }

}
