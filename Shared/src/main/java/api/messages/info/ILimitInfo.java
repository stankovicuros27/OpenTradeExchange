package api.messages.info;

import api.sides.Side;

public interface ILimitInfo extends IInfo {
    Side getSide();
    double getPrice();
    int getVolume();
    int getNumberOfOrders();
}
