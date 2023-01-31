package impl.messages.requests;

import api.core.Side;
import api.messages.requests.IPlaceOrderRequest;
import api.messages.requests.RequestType;

public class PlaceOrderRequest implements IPlaceOrderRequest {

    private final int userID;
    private final int orderID;
    private final double price;
    private final int timestamp;
    private final Side side;
    private final int totalVolume;
    private int filledVolume;

    public PlaceOrderRequest(int userID, int orderID, double price, int timestamp, Side side, int totalVolume, int filledVolume) {
        this.userID = userID;
        this.orderID = orderID;
        this.price = price;
        this.timestamp = timestamp;
        this.side = side;
        this.totalVolume = totalVolume;
        this.filledVolume = filledVolume;
    }

    @Override
    public int getUserID() {
        return userID;
    }

    @Override
    public int getOrderID() {
        return orderID;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public Side getSide() {
        return side;
    }

    @Override
    public int getTotalVolume() {
        return totalVolume;
    }

    @Override
    public int getUnfilledVolume()  {
        return totalVolume - filledVolume;
    }

    @Override
    public void fillVolume(int volume) {
        filledVolume += volume;
    }

    @Override
    public boolean isMatched() {
        return filledVolume >= totalVolume;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.PLACE;
    }
}
