package impl.messages.info;

import api.messages.info.ILimitCollectionInfo;
import api.messages.info.IOrderBookInfo;
import api.core.Side;

import java.util.HashMap;

public class OrderBookInfo implements IOrderBookInfo {

    private final String bookID;
    private final HashMap<Side, ILimitCollectionInfo> limitDataMessages = new HashMap<>();
    private final int volume;
    private final int numberOfOrders;
    private final HashMap<Side, Double> bestPrices = new HashMap<>();
    private final double lastTradePrice;
    private final int timestamp;


    public OrderBookInfo(String bookID, ILimitCollectionInfo buySide, ILimitCollectionInfo sellSide, double lastTradePrice, int timestamp) {
        limitDataMessages.put(Side.BUY, buySide);
        limitDataMessages.put(Side.SELL, sellSide);
        bestPrices.put(Side.BUY, buySide.getBestPrice());
        bestPrices.put(Side.SELL, sellSide.getBestPrice());
        volume = buySide.getVolume() + sellSide.getVolume();
        numberOfOrders = buySide.getNumberOfOrders() + sellSide.getNumberOfOrders();
        this.bookID = bookID;
        this.lastTradePrice = lastTradePrice;
        this.timestamp = timestamp;
    }

    @Override
    public ILimitCollectionInfo getLimitCollectionInfo(Side side) {
        return limitDataMessages.get(side);
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
    public double getBestPrice(Side side) {
        return bestPrices.get(side);
    }

    @Override
    public double getLastTradePrice() {
        return lastTradePrice;
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
    public String toString() {
        return "OrderBookInfo{" +
                "limitDataMessages=" + limitDataMessages +
                ", volume=" + volume +
                ", numberOfOrders=" + numberOfOrders +
                ", bestPrices=" + bestPrices +
                ", lastTradePrice=" + lastTradePrice +
                ", timestamp=" + timestamp +
                '}';
    }
}
