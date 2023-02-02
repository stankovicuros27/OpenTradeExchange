package api.messages.info;

import api.sides.Side;

import java.util.List;

public interface ILimitCollectionInfo extends IInfo {
    public List<ILimitInfo> getLimitInfos();
    Side getSide();
    int getVolume();
    int getNumberOfOrders();
    double getBestPrice();
}
