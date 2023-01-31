package api.core;

import api.messages.requests.ICancelOrderRequest;
import api.messages.requests.IPlaceOrderRequest;

public interface IOrderRequestFactory {
    public IPlaceOrderRequest createPlaceOrderRequest(int userID, double price, Side side, int totalVolume);
    public ICancelOrderRequest createCancelOrderRequest(int userID, int orderID);
}
