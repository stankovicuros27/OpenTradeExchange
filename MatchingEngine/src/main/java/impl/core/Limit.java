package impl.core;

import api.core.ILimit;
import api.core.IOrder;
import api.core.IOrderLookupCache;
import api.core.Side;
import api.messages.info.ILimitInfo;
import api.messages.requests.IPlaceOrderRequest;
import api.messages.responses.IOrderStatusResponse;
import api.messages.responses.IResponse;
import api.messages.responses.ITradeResponse;
import api.messages.responses.OrderResponseStatus;
import api.util.ITimestampProvider;
import impl.messages.info.LimitInfo;
import impl.messages.responses.OrderStatusResponse;

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
        return new OrderStatusResponse(order.getUserID(), order.getOrderID(), OrderResponseStatus.PLACED_ORDER);
    }

    @Override
    public IOrderStatusResponse cancelOrder(IOrder order) {
        volume -= order.getUnfilledVolume();
        orders.remove(order);
        orderLookupCache.removeOrder(order);
        return new OrderStatusResponse(order.getUserID(), order.getOrderID(), OrderResponseStatus.CANCELLED_ORDER);
    }

    @Override
    public List<IResponse> matchOrderRequest(IPlaceOrderRequest orderRequest) {
        List<IResponse> responses = new ArrayList<>();
        while(!orderRequest.isMatched() && !orders.isEmpty()) {
            IOrder matchingOrder = orders.peek();
            ITradeResponse trade = Order.matchOrderRequest(matchingOrder, orderRequest, timestampProvider);
            volume -= trade.getVolume();
            responses.add(trade);
            if (matchingOrder.isClosed()) {
                orders.remove(matchingOrder);
                orderLookupCache.removeOrder(matchingOrder);
                responses.add((new OrderStatusResponse(matchingOrder.getUserID(), matchingOrder.getOrderID(), OrderResponseStatus.CLOSED_ORDER)));
            }
        }
        if (orderRequest.isMatched()) {
            responses.add((new OrderStatusResponse(orderRequest.getUserID(), orderRequest.getOrderID(), OrderResponseStatus.CLOSED_ORDER)));
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
        return new LimitInfo(side, price, volume, orders.size());
    }

}
