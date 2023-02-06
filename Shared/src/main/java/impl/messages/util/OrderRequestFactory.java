package impl.messages.util;

import api.messages.responses.ICancelOrderAckResponse;
import api.messages.responses.IPlaceOrderAckResponse;
import api.messages.util.IOrderRequestFactory;
import api.messages.requests.ICancelOrderRequest;
import api.messages.requests.IPlaceOrderRequest;
import api.sides.Side;
import api.time.ITimestampProvider;
import impl.messages.requests.CancelOrderRequest;
import impl.messages.requests.PlaceOrderRequest;
import impl.messages.responses.CancelOrderAckResponse;
import impl.messages.responses.PlaceOrderAckResponse;

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
    public synchronized IPlaceOrderAckResponse createPlaceOrderAckResponse(IPlaceOrderRequest placeOrderRequest) {
        return new PlaceOrderAckResponse(placeOrderRequest.getUserID(),
                placeOrderRequest.getOrderID(),
                placeOrderRequest.getPrice(),
                placeOrderRequest.getSide(),
                placeOrderRequest.getTotalVolume(),
                placeOrderRequest.getTimestamp());
    }

    @Override
    public ICancelOrderRequest createCancelOrderRequest(int userID, int orderID) {
        int timestamp = timestampProvider.getTimestampNow();
        return new CancelOrderRequest(userID, orderID, timestamp);
    }

    @Override
    public synchronized ICancelOrderAckResponse createCancelOrderAckResponse(ICancelOrderRequest cancelOrderRequest) {
        return new CancelOrderAckResponse(cancelOrderRequest.getUserID(),
                cancelOrderRequest.getOrderID(),
                cancelOrderRequest.getTimestamp());
    }

}
