package api.messages.external;

import api.sides.Side;

public interface IExternalPlaceOrderRequest extends IExternalRequest {
    public String getBookID();
    public int getUserID();
    public double getPrice();
    public Side getSide();
    public int getVolume();
}
