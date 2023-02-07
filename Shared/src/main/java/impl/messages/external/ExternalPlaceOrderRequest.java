package impl.messages.external;

import api.messages.external.ExternalRequestType;
import api.messages.external.IExternalPlaceOrderRequest;
import api.sides.Side;

public class ExternalPlaceOrderRequest implements IExternalPlaceOrderRequest {

    private final String bookID;
    private final int userID;
    private final double price;
    private final Side side;
    private final int volume;
    private final int timestamp;

    public ExternalPlaceOrderRequest(String bookID, int userID, double price, Side side, int volume, int timestamp) {
        this.bookID = bookID;
        this.userID = userID;
        this.price = price;
        this.side = side;
        this.volume = volume;
        this.timestamp = timestamp;
    }

    @Override
    public String getBookID() {
        return bookID;
    }

    @Override
    public int getUserID() {
        return userID;
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
    public ExternalRequestType getExternalRequestType() {
        return ExternalRequestType.PLACE;
    }

    @Override
    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "ExternalPlaceOrderRequest{" +
                "bookID='" + bookID + '\'' +
                ", userID=" + userID +
                ", price=" + price +
                ", side=" + side +
                ", volume=" + volume +
                ", timestamp=" + timestamp +
                '}';
    }
}
