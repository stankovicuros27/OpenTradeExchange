package impl.messages.info;

import api.core.Side;
import api.messages.info.ILimitInfo;

public class LimitInfo implements ILimitInfo {

    private final Side side;
    private final double price;
    private final int volume;
    private final int numberOfOrders;

    public LimitInfo(Side side, double price, int volume, int numberOfOrders) {
        this.side = side;
        this.price = price;
        this.volume = volume;
        this.numberOfOrders = numberOfOrders;
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

}
