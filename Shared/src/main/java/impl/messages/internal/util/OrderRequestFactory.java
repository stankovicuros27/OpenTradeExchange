package impl.messages.internal.util;

import api.messages.internal.responses.ICancelOrderAckResponse;
import api.messages.internal.responses.IPlaceOrderAckResponse;
import api.messages.internal.util.IOrderRequestFactory;
import api.messages.internal.requests.ICancelOrderRequest;
import api.messages.internal.requests.IPlaceOrderRequest;
import api.sides.Side;
import api.time.ITimestampProvider;
import impl.messages.internal.responses.PlaceOrderAckResponse;
import impl.messages.internal.requests.CancelOrderRequest;
import impl.messages.internal.requests.PlaceOrderRequest;
import impl.messages.internal.responses.CancelOrderAckResponse;

import java.util.HashMap;
import java.util.Map;

public class OrderRequestFactory implements IOrderRequestFactory {

    private static final int NUMBER_OF_DECIMAL_PLACES = 2;

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
        double roundedPrice = roundPrice(price);
        PlaceOrderRequest orderRequest = new PlaceOrderRequest(userID, orderID, roundedPrice, side, totalVolume, 0, timestamp);
        userOrderIDs.put(userID, ++orderID);
        return orderRequest;
    }

    private double roundPrice(double price) {
        double roundFactor = Math.pow(10, NUMBER_OF_DECIMAL_PLACES);
        return Math.round(price * roundFactor) / roundFactor;
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
    public synchronized ICancelOrderRequest createCancelOrderRequest(int userID, int orderID) {
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
