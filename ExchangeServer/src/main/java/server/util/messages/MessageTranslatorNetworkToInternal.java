package server.util.messages;

import api.core.IOrderRequestFactory;
import api.core.Side;
import api.messages.requests.ICancelOrderRequest;
import api.messages.requests.IPlaceOrderRequest;
import networking.messages.requests.NetworkCancelOrderRequest;
import networking.messages.requests.NetworkPlaceOrderRequest;
import networking.messages.requests.NetworkRequestSide;

public class MessageTranslatorNetworkToInternal {

    private final IOrderRequestFactory orderRequestFactory;

    public MessageTranslatorNetworkToInternal(IOrderRequestFactory orderRequestFactory) {
        this.orderRequestFactory = orderRequestFactory;
    }

    public IPlaceOrderRequest getPlaceOrderRequest(NetworkPlaceOrderRequest networkPlaceOrderRequest) {
        Side side = networkPlaceOrderRequest.getSide() == NetworkRequestSide.BUY ? Side.BUY : Side.SELL;
        return orderRequestFactory.createPlaceOrderRequest(networkPlaceOrderRequest.getUserID(),
                networkPlaceOrderRequest.getPrice(),
                side,
                networkPlaceOrderRequest.getVolume());
    }

    public ICancelOrderRequest getCancelOrderRequest(NetworkCancelOrderRequest networkCancelOrderRequest) {
        return orderRequestFactory.createCancelOrderRequest(networkCancelOrderRequest.getUserID(),
                networkCancelOrderRequest.getOrderID());
    }

}
