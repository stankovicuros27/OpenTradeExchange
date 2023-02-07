package api.messages.internal.util;

import api.messages.internal.requests.ICancelOrderRequest;
import api.messages.internal.requests.IPlaceOrderRequest;
import api.messages.internal.responses.ICancelOrderAckResponse;
import api.messages.internal.responses.IPlaceOrderAckResponse;
import api.sides.Side;

public interface IOrderRequestFactory {
    public IPlaceOrderRequest createPlaceOrderRequest(int userID, double price, Side side, int totalVolume);
    public IPlaceOrderAckResponse createPlaceOrderAckResponse(IPlaceOrderRequest placeOrderRequest);
    public ICancelOrderRequest createCancelOrderRequest(int userID, int orderID);
    public ICancelOrderAckResponse createCancelOrderAckResponse(ICancelOrderRequest cancelOrderRequest);
}
