package impl.messages.util;

import api.messages.util.IOrderRequestFactory;
import api.messages.requests.ICancelOrderRequest;
import api.messages.requests.IPlaceOrderRequest;
import api.sides.Side;
import api.time.ITimestampProvider;
import impl.messages.requests.CancelOrderRequest;
import impl.messages.requests.PlaceOrderRequest;

import java.util.HashMap;
import java.util.Map;

public class OrderRequestFactory implements IOrderRequestFactory {

    private final ITimestampProvider timestampProvider;
    private final Map<Integer, Integer> userOrderIDs = new HashMap<>();

    public OrderRequestFactory(ITimestampProvider timestampProvider) {
        this.timestampProvider = timestampProvider;
    }

    @Override
    public synchronized IPlaceOrderRequest createPlaceOrderRequest(int userID, double price, Side side, int totalVolume) {
        if (!userOrderIDs.containsKey(userID)) {
            userOrderIDs.put(userID, 0);
        }
        int orderID = userOrderIDs.get(userID);
        int timestamp = timestampProvider.getTimestampNow();
        PlaceOrderRequest orderRequest = new PlaceOrderRequest(userID, orderID, price, side, totalVolume, 0, timestamp);
        userOrderIDs.put(userID, ++orderID);
        return orderRequest;
    }

    @Override
    public ICancelOrderRequest createCancelOrderRequest(int userID, int orderID) {
        int timestamp = timestampProvider.getTimestampNow();
        return new CancelOrderRequest(userID, orderID, timestamp);
    }
}
