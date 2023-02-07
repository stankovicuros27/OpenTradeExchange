package impl.core;

import api.core.IOrderBookConfiguration;

public class OrderBookConfiguration implements IOrderBookConfiguration {

    private final String orderBookID;
    private final int roundDecimalPlaces;

    public OrderBookConfiguration(String orderBookID, int roundDecimalPlaces) {
        this.orderBookID = orderBookID;
        this.roundDecimalPlaces = roundDecimalPlaces;
    }

    @Override
    public String getOrderBookID() {
        return orderBookID;
    }

    @Override
    public int getRoundDecimalPlaces() {
        return roundDecimalPlaces;
    }

}
