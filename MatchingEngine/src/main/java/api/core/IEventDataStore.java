package api.core;

import api.messages.internal.responses.IResponse;

import java.util.List;

public interface IEventDataStore {
    public void registerResponseEvents(List<IResponse> responses);
    public long getPlaceOrderCnt();
    public long getCancelOrderCnt();
    public long getClosedOrderCnt();
    public long getTradeCnt();
    public double getLastTradePrice();
}
