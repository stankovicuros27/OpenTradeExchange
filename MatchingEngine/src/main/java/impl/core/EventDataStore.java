package impl.core;

import api.core.IEventDataStore;

public class EventDataStore implements IEventDataStore {

    private long placeOrderCnt = 0;
    private long cancelOrderCnt = 0;
    private long closedOrderCnt = 0;
    private long tradeCnt = 0;
    private double lastTradePrice = -1;

    @Override
    public void incPlaceOrderCnt() {
        placeOrderCnt++;
    }

    @Override
    public long getPlaceOrderCnt() {
        return placeOrderCnt;
    }

    @Override
    public void incCancelOrderCnt() {
        cancelOrderCnt++;
    }

    @Override
    public long getCancelOrderCnt() {
        return cancelOrderCnt;
    }

    @Override
    public void intClosedOrderCnt() {
        closedOrderCnt++;
    }

    @Override
    public long getClosedOrderCnt() {
        return closedOrderCnt;
    }

    @Override
    public void incTradeCnt() {
        tradeCnt++;
    }

    @Override
    public long getTradeCnt() {
        return tradeCnt;
    }

    @Override
    public void setLastTradePrice(double price) {
        lastTradePrice = price;
    }

    @Override
    public double getLastTradePrice() {
        return lastTradePrice;
    }
}
