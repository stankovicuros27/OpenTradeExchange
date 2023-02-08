package impl.core;

import api.core.IOrderRequestFactory;
import api.messages.internal.responses.ICancelOrderAckResponse;
import api.messages.internal.responses.IErrorAckResponse;
import api.messages.internal.responses.IPlaceOrderAckResponse;
import api.messages.internal.requests.ICancelOrderRequest;
import api.messages.internal.requests.IPlaceOrderRequest;
import api.sides.Side;
import api.time.ITimestampProvider;
import impl.messages.internal.responses.ErrorAckResponse;
import impl.messages.internal.responses.PlaceOrderAckResponse;
import impl.messages.internal.requests.CancelOrderRequest;
import impl.messages.internal.requests.PlaceOrderRequest;
import impl.messages.internal.responses.CancelOrderAckResponse;

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
    public synchronized IPlaceOrderAckResponse createPlaceOrderAckResponse(IPlaceOrderRequest placeOrderRequest, int clientMessageTimestamp) {
        return new PlaceOrderAckResponse(bookID, placeOrderRequest.getUserID(),
                placeOrderRequest.getOrderID(),
                placeOrderRequest.getPrice(),
                placeOrderRequest.getSide(),
                placeOrderRequest.getTotalVolume(),
                clientMessageTimestamp);
    }

    @Override
    public synchronized ICancelOrderRequest createCancelOrderRequest(int userID, int orderID) {
        int timestamp = timestampProvider.getTimestampNow();
        return new CancelOrderRequest(bookID, userID, orderID, timestamp);
    }

    @Override
    public synchronized ICancelOrderAckResponse createCancelOrderAckResponse(ICancelOrderRequest cancelOrderRequest, int clientMessageTimestamp) {
        return new CancelOrderAckResponse(bookID, cancelOrderRequest.getUserID(),
                cancelOrderRequest.getOrderID(),
                clientMessageTimestamp);
    }

    @Override
    public synchronized IErrorAckResponse createErrorAckResponse(String bookID, int userID, int clientMessageTimestamp) {
        return new ErrorAckResponse(bookID, userID, clientMessageTimestamp);
    }

}
