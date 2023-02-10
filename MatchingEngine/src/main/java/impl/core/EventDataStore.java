package impl.core;

import api.core.IEventDataStore;
import api.messages.responses.*;

import java.util.List;

public class EventDataStore implements IEventDataStore {

    private long placeOrderCnt = 0;
    private long cancelOrderCnt = 0;
    private long closedOrderCnt = 0;
    private long tradeCnt = 0;
    private double lastTradePrice = -1;

    @Override
    public synchronized void registerResponseEvents(List<IResponse> responses) {
        for (IResponse response : responses) {
            if (response.getType() == ResponseType.OrderStatusResponse) {
                IOrderStatusResponse orderStatusResponse = (IOrderStatusResponse) response;
                registerOrderStatusResponse(orderStatusResponse);
            } else if (response.getType() == ResponseType.TradeResponse) {
                ITradeResponse tradeResponse = (ITradeResponse) response;
                registerTradeResponse(tradeResponse);
            }
        }
    }

    private void registerOrderStatusResponse(IOrderStatusResponse orderStatusResponse) {
        if (orderStatusResponse.getStatus() == OrderResponseStatus.PLACED_ORDER) {
            placeOrderCnt++;
        } else if (orderStatusResponse.getStatus() == OrderResponseStatus.CANCELLED_ORDER) {
            cancelOrderCnt++;
        } else if (orderStatusResponse.getStatus() == OrderResponseStatus.CLOSED_ORDER) {
            closedOrderCnt++;
        }
    }

    private void registerTradeResponse(ITradeResponse tradeResponse) {
        tradeCnt++;
        lastTradePrice = tradeResponse.getPrice();
    }

    @Override
    public synchronized long getAndResetPlaceOrderCnt() {
        long ret = placeOrderCnt;
        placeOrderCnt = 0;
        return ret;
    }

    @Override
    public synchronized long getAndResetCancelOrderCnt() {
        long ret = cancelOrderCnt;
        cancelOrderCnt = 0;
        return ret;
    }

    @Override
    public synchronized long getAndResetClosedOrderCnt() {
        long ret = closedOrderCnt;
        closedOrderCnt = 0;
        return ret;
    }

    @Override
    public synchronized long getAndResetTradeCnt() {
        long ret = tradeCnt;
        tradeCnt = 0;
        return ret;
    }

    @Override
    public synchronized double getLastTradePrice() {
        return lastTradePrice;
    }
}
