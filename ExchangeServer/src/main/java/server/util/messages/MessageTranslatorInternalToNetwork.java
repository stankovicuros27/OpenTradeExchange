package server.util.messages;

import api.messages.responses.IOrderStatusResponse;
import api.messages.responses.ITradeResponse;
import api.messages.responses.OrderResponseStatus;
import networking.messages.responses.NetworkOrderResponseStatus;
import networking.messages.responses.NetworkOrderStatusResponse;
import networking.messages.responses.NetworkTradeResponse;

public class MessageTranslatorInternalToNetwork {

    public NetworkTradeResponse getNetworkTradeResponse(ITradeResponse tradeResponse) {
        return new NetworkTradeResponse(tradeResponse.getBuyUserID(),
                tradeResponse.getBuyOrderID(),
                tradeResponse.getSellUserID(),
                tradeResponse.getSellOrderID(),
                tradeResponse.getPrice(),
                tradeResponse.getVolume(),
                tradeResponse.getTimestamp());
    }

    public NetworkOrderStatusResponse getNetworkOrderStatusResponse(IOrderStatusResponse orderStatusResponse) {
        return new NetworkOrderStatusResponse(orderStatusResponse.getUserID(),
                orderStatusResponse.getOrderID(),
                getNetworkResponseStatus(orderStatusResponse.getStatus()));
    }

    private NetworkOrderResponseStatus getNetworkResponseStatus(OrderResponseStatus orderResponseStatus) {
        switch (orderResponseStatus) {
            case PLACED_ORDER -> { return NetworkOrderResponseStatus.PLACED_ORDER; }
            case CANCELLED_ORDER -> { return NetworkOrderResponseStatus.CANCELLED_ORDER; }
            case CLOSED_ORDER -> { return NetworkOrderResponseStatus.CLOSED_ORDER; }
            case ERROR_ORDER -> { return NetworkOrderResponseStatus.ERROR_ORDER; }
            case NULL_ORDER -> { return NetworkOrderResponseStatus.NULL_ORDER; }
        }
        return NetworkOrderResponseStatus.ERROR_ORDER;
    }

}
