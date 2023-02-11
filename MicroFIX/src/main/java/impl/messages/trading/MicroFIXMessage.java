package impl.messages.trading;

import api.messages.trading.MicroFIXSide;
import api.messages.trading.IMicroFIXMessage;

public abstract class MicroFIXMessage implements IMicroFIXMessage {

    protected final String bookID;
    protected final int userID;
    protected final int orderID;
    protected final double price;
    protected final MicroFIXSide side;
    protected final int volume;
    protected final int timestamp;
    protected final int externalTimestamp;

    protected MicroFIXMessage(String bookID, int userID, int orderID, double price, MicroFIXSide side, int volume, int timestamp, int externalTimestamp) {
        this.bookID = bookID;
        this.userID = userID;
        this.orderID = orderID;
        this.price = price;
        this.side = side;
        this.volume = volume;
        this.timestamp = timestamp;
        this.externalTimestamp = externalTimestamp;
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
    public int getOrderID() {
        return orderID;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public MicroFIXSide getSide() {
        return side;
    }

    @Override
    public int getVolume() {
        return volume;
    }

    @Override
    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public int getExternalTimestamp() {
        return externalTimestamp;
    }

}
