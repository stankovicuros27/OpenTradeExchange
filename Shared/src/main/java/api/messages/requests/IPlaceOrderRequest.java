package api.messages.requests;

import api.sides.Side;

public interface IPlaceOrderRequest extends IRequest {
    public int getUserID();
    public int getOrderID();
    public double getPrice();
    public Side getSide();
    public int getTotalVolume();
    public int getUnfilledVolume();
    public void fillVolume(int volume);
    public boolean isMatched();
}
