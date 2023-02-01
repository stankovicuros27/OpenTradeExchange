package api.messages.info;

import api.core.Side;

public interface IOrderBookInfo extends IInfo {
    public ILimitCollectionInfo getLimitCollectionInfo(Side side);
    int getVolume();
    int getNumberOfOrders();
    double getBestPrice(Side side);
}
