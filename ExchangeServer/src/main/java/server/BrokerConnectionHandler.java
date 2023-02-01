package server;

import api.core.IOrderBook;
import api.core.IOrderRequestFactory;
import api.core.Side;
import api.messages.requests.ICancelOrderRequest;
import api.messages.requests.IPlaceOrderRequest;
import api.messages.responses.IResponse;
import networking.messages.INetworkMessage;
import networking.messages.requests.*;
import server.broadcast.ResponseBroadcastService;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class BrokerConnectionHandler implements Runnable {

    private final IOrderBook orderBook;
    private final IOrderRequestFactory orderRequestFactory;
    private final Socket brokerSocket;
    private final ResponseBroadcastService responseBroadcastService;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public BrokerConnectionHandler(IOrderBook orderBook, IOrderRequestFactory orderRequestFactory, Socket brokerSocket, ResponseBroadcastService responseBroadcastService) {
        this.orderRequestFactory = orderRequestFactory;
        this.orderBook = orderBook;
        this.brokerSocket = brokerSocket;
        this.responseBroadcastService = responseBroadcastService;
    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(brokerSocket.getInputStream());
            out = new ObjectOutputStream(brokerSocket.getOutputStream());

            // Handler loop
            INetworkRequest networkRequest = (INetworkRequest) in.readObject();
            while (networkRequest != null) {
                handleRequest(networkRequest);
                networkRequest = (INetworkRequest) in.readObject();
            }

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleRequest(INetworkRequest networkRequest) {
        List<IResponse> responses;
        if (networkRequest.getNetworkRequestType() == NetworkRequestType.PLACE) {
            PlaceOrderNetworkRequest placeOrderNetworkRequest = (PlaceOrderNetworkRequest) networkRequest;
            responses = handlePlaceOrderRequest(placeOrderNetworkRequest);
        } else {
            CancelOrderNetworkRequest cancelOrderNetworkRequest = (CancelOrderNetworkRequest) networkRequest;
            responses = handleCancelOrderRequest(cancelOrderNetworkRequest);
        }
        // TODO convert to network messages
        //responseBroadcastService.broadcastMessages(responses);
    }

    private List<IResponse> handlePlaceOrderRequest(PlaceOrderNetworkRequest placeOrderNetworkRequest) {

        System.out.println("Received place order request");   // TODO delete

        Side side = placeOrderNetworkRequest.getSide() == NetworkRequestSide.BUY ? Side.BUY : Side.SELL;
        IPlaceOrderRequest internalPlaceOrderRequest = orderRequestFactory.createPlaceOrderRequest(placeOrderNetworkRequest.getUserID(),
                placeOrderNetworkRequest.getPrice(),
                side,
                placeOrderNetworkRequest.getVolume());
        return orderBook.placeOrder(internalPlaceOrderRequest);
    }

    private List<IResponse> handleCancelOrderRequest(CancelOrderNetworkRequest cancelOrderNetworkRequest) {

        System.out.println("Received cancel order request");   // TODO delete

        ICancelOrderRequest internalCancelOrderRequest = orderRequestFactory.createCancelOrderRequest(cancelOrderNetworkRequest.getUserID(),
                cancelOrderNetworkRequest.getOrderID());
        return List.of(orderBook.cancelOrder(internalCancelOrderRequest));
    }

    public void sendMessage(INetworkMessage message) {
        try {
            out.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
