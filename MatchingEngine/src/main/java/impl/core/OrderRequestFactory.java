package impl.core;

import api.core.IOrderRequestFactory;
import api.core.Side;
import api.messages.requests.ICancelOrderRequest;
import api.messages.requests.IPlaceOrderRequest;
import api.util.ITimestampProvider;
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
        PlaceOrderRequest orderRequest = new PlaceOrderRequest(userID, orderID, price, timestamp, side, totalVolume, 0);
        userOrderIDs.put(userID, ++orderID);
        return orderRequest;
    }

    @Override
    public ICancelOrderRequest createCancelOrderRequest(int userID, int orderID) {
        return new CancelOrderRequest(userID, orderID);
    }
}
