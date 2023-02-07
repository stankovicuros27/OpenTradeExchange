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
    public synchronized long getPlaceOrderCnt() {
        return placeOrderCnt;
    }

    @Override
    public synchronized long getCancelOrderCnt() {
        return cancelOrderCnt;
    }

    @Override
    public synchronized long getClosedOrderCnt() {
        return closedOrderCnt;
    }

    @Override
    public synchronized long getTradeCnt() {
        return tradeCnt;
    }

    @Override
    public synchronized double getLastTradePrice() {
        return lastTradePrice;
    }
}
