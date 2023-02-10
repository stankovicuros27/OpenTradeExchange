package impl.core;

import api.core.IOrderRequestFactory;
import api.messages.requests.ICancelOrderRequest;
import api.messages.requests.IPlaceOrderRequest;
import api.core.Side;
import api.time.ITimestampProvider;
import impl.messages.requests.CancelOrderRequest;
import impl.messages.requests.PlaceOrderRequest;

import java.util.HashMap;
import java.util.Map;

public class OrderRequestFactory implements IOrderRequestFactory {

    private final String bookID;
    private final ITimestampProvider timestampProvider;
    private final int roundDecimalPlaces;
    private final Map<Integer, Integer> userOrderIDs = new HashMap<>();

    public OrderRequestFactory(String bookID, ITimestampProvider timestampProvider, int roundDecimalPlaces) {
        this.bookID = bookID;
        this.timestampProvider = timestampProvider;
        this.roundDecimalPlaces = roundDecimalPlaces;
    }

    @Override
    public synchronized IPlaceOrderRequest createPlaceOrderRequest(int userID, double price, Side side, int totalVolume) {
        if (!userOrderIDs.containsKey(userID)) {
            userOrderIDs.put(userID, 0);
        }
        int orderID = userOrderIDs.get(userID);
        int timestamp = timestampProvider.getTimestampNow();
        double roundedPrice = roundPrice(price);
        PlaceOrderRequest orderRequest = new PlaceOrderRequest(bookID, userID, orderID, roundedPrice, side, totalVolume, 0, timestamp);
        userOrderIDs.put(userID, ++orderID);
        return orderRequest;
    }

    private double roundPrice(double price) {
        double roundFactor = Math.pow(10, roundDecimalPlaces);
        return Math.round(price * roundFactor) / roundFactor;
    }

    @Override
    public synchronized ICancelOrderRequest createCancelOrderRequest(int userID, int orderID) {
        int timestamp = timestampProvider.getTimestampNow();
        return new CancelOrderRequest(bookID, userID, orderID, timestamp);
    }

}
