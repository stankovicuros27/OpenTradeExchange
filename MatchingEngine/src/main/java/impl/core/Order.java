package impl.core;

import api.core.IOrder;
import api.core.Side;
import api.messages.requests.IPlaceOrderRequest;
import api.messages.responses.ITradeResponse;
import api.util.ITimestampProvider;
import impl.messages.responses.TradeResponse;

import java.time.Instant;

public class Order implements IOrder {

    private final int userID;
    private final int orderID;
    private final double price;
    private final int timestamp;
    private final Side side;
    private final int totalVolume;
    private int filledVolume;

    public Order(int userID, int orderID, double price, int timestamp, Side side, int totalVolume, int filledVolume) {
        this.userID = userID;
        this.orderID = orderID;
        this.price = price;
        this.timestamp = timestamp;
        this.side = side;
        this.totalVolume = totalVolume;
        this.filledVolume = filledVolume;
    }

    public static ITradeResponse matchOrderRequest(IOrder order, IPlaceOrderRequest orderRequest, ITimestampProvider timestampProvider) {
        int volume = Math.min(order.getUnfilledVolume(), orderRequest.getUnfilledVolume());
        order.fillVolume(volume);
        orderRequest.fillVolume(volume);
        int timestamp = timestampProvider.getTimestampNow();
        double price;
        if (orderRequest.getSide() == Side.BUY) {
            price = Math.min(order.getPrice(), orderRequest.getPrice());
            return new TradeResponse(orderRequest.getUserID(), orderRequest.getOrderID(), order.getUserID(), order.getOrderID(), price, volume, timestamp);
        } else {
            price = Math.max(order.getPrice(), orderRequest.getPrice());
            return new TradeResponse(order.getUserID(), order.getOrderID(), orderRequest.getUserID(), orderRequest.getOrderID(), price, volume, timestamp);
        }
    }

    @Override
    public int getUserID() {
        return userID;
    }

    @Override
    public int getOrderID() {
        return orderID;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public Side getSide() {
        return side;
    }

    @Override
    public int getTotalVolume() {
        return totalVolume;
    }

    @Override
    public int getUnfilledVolume()  {
        return totalVolume - filledVolume;
    }

    @Override
    public void fillVolume(int volume) {
        filledVolume += volume;
    }

    @Override
    public boolean isClosed() {
        return filledVolume >= totalVolume;
    }

    public static IOrder fromRequest(IPlaceOrderRequest orderRequest) {
        return new Order(orderRequest.getUserID(),
                orderRequest.getOrderID(),
                orderRequest.getPrice(),
                orderRequest.getTimestamp(),
                orderRequest.getSide(),
                orderRequest.getTotalVolume(),
                orderRequest.getTotalVolume() - orderRequest.getUnfilledVolume());
    }

}
