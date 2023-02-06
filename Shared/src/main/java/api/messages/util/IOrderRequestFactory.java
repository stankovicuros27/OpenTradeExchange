package api.messages.util;

import api.messages.requests.ICancelOrderRequest;
import api.messages.requests.IPlaceOrderRequest;
import api.messages.responses.ICancelOrderAckResponse;
import api.messages.responses.IPlaceOrderAckResponse;
import api.sides.Side;

public interface IOrderRequestFactory {
    public IPlaceOrderRequest createPlaceOrderRequest(int userID, double price, Side side, int totalVolume);
    public IPlaceOrderAckResponse createPlaceOrderAckResponse(IPlaceOrderRequest placeOrderRequest);
    public ICancelOrderRequest createCancelOrderRequest(int userID, int orderID);
    public ICancelOrderAckResponse createCancelOrderAckResponse(ICancelOrderRequest cancelOrderRequest);
}
