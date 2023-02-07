package api.core;

import api.messages.internal.info.IOrderBookInfo;
import api.messages.internal.requests.ICancelOrderRequest;
import api.messages.internal.requests.IPlaceOrderRequest;
import api.messages.internal.responses.IOrderStatusResponse;
import api.messages.internal.responses.IResponse;

import java.util.List;

public interface IOrderBook {
    public List<IResponse> placeOrder(IPlaceOrderRequest placeOrderRequest);
    public IOrderStatusResponse cancelOrder(ICancelOrderRequest cancelOrderRequest);
    public IOrderBookInfo getInfo();
    public IEventDataStore getEventDataStore();
}
