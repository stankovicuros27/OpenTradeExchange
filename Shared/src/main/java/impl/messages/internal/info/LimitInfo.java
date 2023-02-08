package impl.messages.internal.info;

import api.messages.internal.info.ILimitInfo;
import api.sides.Side;

public class LimitInfo implements ILimitInfo {

    private final String bookID;
    private final Side side;
    private final double price;
    private final int volume;
    private final int numberOfOrders;
    private final int timestamp;


    public LimitInfo(String bookID, Side side, double price, int volume, int numberOfOrders, int timestamp) {
        this.bookID = bookID;
        this.side = side;
        this.price = price;
        this.volume = volume;
        this.numberOfOrders = numberOfOrders;
        this.timestamp = timestamp;
    }

    @Override
    public Side getSide() {
        return side;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public int getVolume() {
        return volume;
    }

    @Override
    public int getNumberOfOrders() {
        return numberOfOrders;
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
