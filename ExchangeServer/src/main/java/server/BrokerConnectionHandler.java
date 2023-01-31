package server;

import api.core.IOrderBook;
import api.core.IOrderRequestFactory;
import api.core.Side;
import api.messages.requests.ICancelOrderRequest;
import api.messages.requests.IPlaceOrderRequest;
import networking.messages.requests.*;

import java.io.*;
import java.net.Socket;

public class BrokerConnectionHandler implements Runnable {

    private final IOrderBook orderBook;
    private final IOrderRequestFactory orderRequestFactory;
    private final Socket brokerSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public BrokerConnectionHandler(IOrderBook orderBook, IOrderRequestFactory orderRequestFactory, Socket brokerSocket) {
        this.orderRequestFactory = orderRequestFactory;
        this.orderBook = orderBook;
        this.brokerSocket = brokerSocket;
    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(brokerSocket.getInputStream());
            out = new ObjectOutputStream(brokerSocket.getOutputStream());

            INetworkRequest networkRequest = (INetworkRequest) in.readObject();
            while (networkRequest != null) {
                if (networkRequest.getNetworkRequestType() == NetworkRequestType.PLACE) {
                    PlaceOrderNetworkRequest placeOrderNetworkRequest = (PlaceOrderNetworkRequest) networkRequest;
                    Side side = placeOrderNetworkRequest.getSide() == NetworkRequestSide.BUY ? Side.BUY : Side.SELL;
                    IPlaceOrderRequest internalPlaceOrderRequest = orderRequestFactory.createPlaceOrderRequest(placeOrderNetworkRequest.getUserID(),
                            placeOrderNetworkRequest.getPrice(),
                            side,
                            placeOrderNetworkRequest.getVolume());
                    // TODO handle responses
                    orderBook.placeOrder(internalPlaceOrderRequest);
                } else {
                    CancelOrderNetworkRequest cancelOrderNetworkRequest = (CancelOrderNetworkRequest) networkRequest;
                    ICancelOrderRequest internalCancelOrderRequest = orderRequestFactory.createCancelOrderRequest(cancelOrderNetworkRequest.getUserID(),
                            cancelOrderNetworkRequest.getOrderID());
                    // TODO handle responses
                    orderBook.cancelOrder(internalCancelOrderRequest);
                }
                networkRequest = (INetworkRequest) in.readObject();
            }


        } catch (IOException e) {

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
