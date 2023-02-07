package api.core;

import api.messages.internal.info.ILimitInfo;
import api.messages.internal.requests.IPlaceOrderRequest;
import api.messages.internal.responses.IOrderStatusResponse;
import api.messages.internal.responses.IResponse;
import api.sides.Side;

import java.util.List;

public interface ILimit {
    public IOrderStatusResponse addOrder(IOrder order);
    public IOrderStatusResponse cancelOrder(IOrder order);
    public List<IResponse> matchOrderRequest(IPlaceOrderRequest orderRequest);
    public IOrder getBestOrder();
    public int getVolume();
    public int getNumberOfOrders();
    public double getPrice();
    public boolean isEmpty();
    public Side getSide();
    public ILimitInfo getLimitInfo();
}
