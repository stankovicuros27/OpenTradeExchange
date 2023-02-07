package impl.messages.internal.requests;

import api.messages.internal.requests.IPlaceOrderRequest;
import api.messages.internal.requests.RequestType;
import api.sides.Side;

public class PlaceOrderRequest implements IPlaceOrderRequest {

    private final int userID;
    private final int orderID;
    private final double price;
    private final Side side;
    private final int totalVolume;
    private int filledVolume;
    private final int timestamp;


    public PlaceOrderRequest(int userID, int orderID, double price, Side side, int totalVolume, int filledVolume, int timestamp) {
        this.userID = userID;
        this.orderID = orderID;
        this.price = price;
        this.side = side;
        this.totalVolume = totalVolume;
        this.filledVolume = filledVolume;
        this.timestamp = timestamp;
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

    @Override
    public int getTimestamp() {
        return timestamp;
    }

}