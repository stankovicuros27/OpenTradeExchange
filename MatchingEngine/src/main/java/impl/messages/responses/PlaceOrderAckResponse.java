package impl.messages.responses;

import api.messages.responses.IPlaceOrderAckResponse;
import api.messages.responses.ResponseType;
import api.core.Side;

public class PlaceOrderAckResponse implements IPlaceOrderAckResponse {

    private final String bookID;
    private final int userID;
    private final int orderID;
    private final double price;
    private final Side side;
    private final int volume;
    private final int timestamp;

    public PlaceOrderAckResponse(String bookID, int userID, int orderID, double price, Side side, int volume, int timestamp) {
        this.bookID = bookID;
        this.userID = userID;
        this.orderID = orderID;
        this.price = price;
        this.side = side;
        this.volume = volume;
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
    public int getVolume() {
        return volume;
    }

    @Override
    public String getBookID() {
        return bookID;
    }

    @Override
    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public ResponseType getType() {
        return ResponseType.PlaceOrderAckResponse;
    }

}
