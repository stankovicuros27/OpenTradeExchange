package api.messages.info;

import api.core.Side;

import java.util.List;

public interface ILimitCollectionInfo extends IInfo {
    public List<ILimitInfo> getLimitInfos();
    Side getSide();
    int getVolume();
    int getNumberOfOrders();
}
