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
    private final Socket brokerClientSocket;
    private final ResponseBroadcastService responseBroadcastService;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public BrokerConnectionHandler(IOrderBook orderBook, IOrderRequestFactory orderRequestFactory, Socket brokerClientSocket, ResponseBroadcastService responseBroadcastService) {
        this.orderBook = orderBook;
        this.orderRequestFactory = orderRequestFactory;
        this.brokerClientSocket = brokerClientSocket;
        this.responseBroadcastService = responseBroadcastService;
    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(brokerClientSocket.getInputStream());
            out = new ObjectOutputStream(brokerClientSocket.getOutputStream());
            INetworkRequest networkRequest = (INetworkRequest) in.readObject();
            while (networkRequest != null) {
                handleRequest(networkRequest);
                networkRequest = (INetworkRequest) in.readObject();
            }
        } catch (IOException e) {
            e.printStackTrace();
            shutdown();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            // TODO
        }
    }

    private void handleRequest(INetworkRequest networkRequest) {
        List<IResponse> responses;
        if (networkRequest.getNetworkRequestType() == NetworkRequestType.PLACE) {
            NetworkPlaceOrderRequest networkPlaceOrderRequest = (NetworkPlaceOrderRequest) networkRequest;
            responses = handlePlaceOrderRequest(networkPlaceOrderRequest);
        } else {
            NetworkCancelOrderRequest networkCancelOrderRequest = (NetworkCancelOrderRequest) networkRequest;
            responses = handleCancelOrderRequest(networkCancelOrderRequest);
        }
        // TODO convert to network messages
        //responseBroadcastService.broadcastMessages(responses);
    }

    private List<IResponse> handlePlaceOrderRequest(NetworkPlaceOrderRequest networkPlaceOrderRequest) {

        System.out.println("Received place order request");   // TODO delete

        Side side = networkPlaceOrderRequest.getSide() == NetworkRequestSide.BUY ? Side.BUY : Side.SELL;
        IPlaceOrderRequest internalPlaceOrderRequest = orderRequestFactory.createPlaceOrderRequest(networkPlaceOrderRequest.getUserID(),
                networkPlaceOrderRequest.getPrice(),
                side,
                networkPlaceOrderRequest.getVolume());
        return orderBook.placeOrder(internalPlaceOrderRequest);
    }

    private List<IResponse> handleCancelOrderRequest(NetworkCancelOrderRequest networkCancelOrderRequest) {

        System.out.println("Received cancel order request");   // TODO delete

        ICancelOrderRequest internalCancelOrderRequest = orderRequestFactory.createCancelOrderRequest(networkCancelOrderRequest.getUserID(),
                networkCancelOrderRequest.getOrderID());
        return List.of(orderBook.cancelOrder(internalCancelOrderRequest));
    }

    public void sendMessage(INetworkMessage message) {
        try {
            out.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {
        try {
            if (!brokerClientSocket.isClosed()) {
                brokerClientSocket.close();
                in.close();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean isClosed() {
        return brokerClientSocket.isClosed();
    }

}
