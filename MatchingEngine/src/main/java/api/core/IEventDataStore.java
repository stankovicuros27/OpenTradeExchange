package api.core;

import api.messages.internal.responses.IResponse;

import java.util.List;

public interface IEventDataStore {
    public void registerResponseEvents(List<IResponse> responses);
    public long getAndResetPlaceOrderCnt();
    public long getAndResetCancelOrderCnt();
    public long getAndResetClosedOrderCnt();
    public long getAndResetTradeCnt();
    public double getLastTradePrice();
}
