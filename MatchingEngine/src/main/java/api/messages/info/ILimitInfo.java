package api.messages.info;

import api.core.Side;

public interface ILimitInfo extends IInfo {
    Side getSide();
    double getPrice();
    int getVolume();
    int getNumberOfOrders();
}
