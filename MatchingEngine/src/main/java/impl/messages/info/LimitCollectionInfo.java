package impl.messages.info;

import api.messages.info.ILimitCollectionInfo;
import api.messages.info.ILimitInfo;
import api.core.Side;

import java.util.List;

public class LimitCollectionInfo implements ILimitCollectionInfo {

    private final String bookID;
    private final Side side;
    private final List<ILimitInfo> limitInfos;
    private final int volume;
    private final int numberOfOrders;
    private final double bestPrice;
    private final int timestamp;

    public LimitCollectionInfo(String bookID, Side side, List<ILimitInfo> limitInfos, int volume, int numberOfOrders, double bestPrice, int timestamp) {
        this.bookID = bookID;
        this.side = side;
        this.limitInfos = limitInfos;
        this.volume = volume;
        this.numberOfOrders = numberOfOrders;
        this.bestPrice = bestPrice;
        this.timestamp = timestamp;
    }

    @Override
    public List<ILimitInfo> getLimitInfos() {
        return limitInfos;
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
    public int getNumberOfOrders() {
        return numberOfOrders;
    }

    @Override
    public double getBestPrice() {
        return bestPrice;
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
