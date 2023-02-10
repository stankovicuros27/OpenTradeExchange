package api.core;

import api.messages.info.ILimitCollectionInfo;
import api.messages.requests.IPlaceOrderRequest;
import api.messages.responses.IOrderStatusResponse;
import api.messages.responses.IResponse;

import java.util.List;

public interface ILimitCollection {
    public IOrderStatusResponse addOrder(IOrder order);
    public IOrderStatusResponse cancelOrder(IOrder order);
    public List<IResponse> matchOrderRequest(IPlaceOrderRequest orderRequest);
    public boolean containsLimit(double price);
    public Side getSide();
    public ILimitCollectionInfo getInfo();
}
