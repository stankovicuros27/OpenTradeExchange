package api.core;

import api.messages.internal.info.ILimitCollectionInfo;
import api.messages.internal.requests.IPlaceOrderRequest;
import api.messages.internal.responses.IOrderStatusResponse;
import api.messages.internal.responses.IResponse;
import api.sides.Side;

import java.util.List;

public interface ILimitCollection {
    public IOrderStatusResponse addOrder(IOrder order);
    public IOrderStatusResponse cancelOrder(IOrder order);
    public List<IResponse> matchOrderRequest(IPlaceOrderRequest orderRequest);
    public boolean containsLimit(double price);
    public Side getSide();
    public ILimitCollectionInfo getInfo();
}
