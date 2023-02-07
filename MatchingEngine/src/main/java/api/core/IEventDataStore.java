package api.core;

public interface IEventDataStore {
    public void incPlaceOrderCnt();
    public long getPlaceOrderCnt();
    public void incCancelOrderCnt();
    public long getCancelOrderCnt();
    public void incClosedOrderCnt();
    public long getClosedOrderCnt();
    public void incTradeCnt();
    public long getTradeCnt();
    public void setLastTradePrice(double price);
    public double getLastTradePrice();

}
