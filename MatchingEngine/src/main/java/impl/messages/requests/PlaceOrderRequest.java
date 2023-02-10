package impl.messages.requests;

import api.messages.requests.IPlaceOrderRequest;
import api.messages.requests.RequestType;
import api.core.Side;

public class PlaceOrderRequest implements IPlaceOrderRequest {

    private final String bookID;
    private final int userID;
    private final int orderID;
    private final double price;
    private final Side side;
    private final int totalVolume;
    private int filledVolume;
    private final int timestamp;


    public PlaceOrderRequest(String bookID, int userID, int orderID, double price, Side side, int totalVolume, int filledVolume, int timestamp) {
        this.bookID = bookID;
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
    public String getBookID() {
        return bookID;
    }

    @Override
    public int getTimestamp() {
        return timestamp;
    }

}
