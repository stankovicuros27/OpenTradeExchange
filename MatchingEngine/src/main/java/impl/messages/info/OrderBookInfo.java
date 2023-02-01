package impl.messages.info;

import api.core.Side;
import api.messages.info.ILimitCollectionInfo;
import api.messages.info.IOrderBookInfo;

import java.util.HashMap;

public class OrderBookInfo implements IOrderBookInfo {

    private final HashMap<Side, ILimitCollectionInfo> limitDataMessages = new HashMap<>();
    private final int volume;
    private final int numberOfOrders;
    private final HashMap<Side, Double> bestPrices = new HashMap<>();

    public OrderBookInfo(ILimitCollectionInfo buySide, ILimitCollectionInfo sellSide) {
        limitDataMessages.put(Side.BUY, buySide);
        limitDataMessages.put(Side.SELL, sellSide);
        bestPrices.put(Side.BUY, buySide.getBestPrice());
        bestPrices.put(Side.SELL, sellSide.getBestPrice());
        volume = buySide.getVolume() + sellSide.getVolume();
        numberOfOrders = buySide.getNumberOfOrders() + sellSide.getNumberOfOrders();
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

}
