package api.core;

import api.messages.info.IOrderBookInfo;
import api.messages.requests.ICancelOrderRequest;
import api.messages.requests.IPlaceOrderRequest;
import api.messages.responses.IOrderStatusResponse;
import api.messages.responses.IResponse;

import java.util.List;

public interface IOrderBook {
    public List<IResponse> placeOrder(IPlaceOrderRequest placeOrderRequest);
    public IOrderStatusResponse cancelOrder(ICancelOrderRequest cancelOrderRequest);
    public IOrderBookInfo getInfo();
}
