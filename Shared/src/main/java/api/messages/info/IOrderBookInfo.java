package api.messages.info;

import api.sides.Side;

public interface IOrderBookInfo extends IInfo {
    public ILimitCollectionInfo getLimitCollectionInfo(Side side);
    int getVolume();
    int getNumberOfOrders();
    double getBestPrice(Side side);
    double getLastTradePrice();
}
