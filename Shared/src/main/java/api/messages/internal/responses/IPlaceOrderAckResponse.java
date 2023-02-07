package api.messages.internal.responses;

import api.sides.Side;

public interface IPlaceOrderAckResponse extends IResponse {
    public int getUserID();
    public int getOrderID();
    public double getPrice();
    public Side getSide();
    public int getVolume();
}
