package server;

import api.core.IOrderBook;
import api.messages.IMessage;
import api.messages.requests.IRequest;
import api.messages.requests.RequestType;
import api.messages.util.IOrderRequestFactory;
import api.messages.responses.IResponse;
import impl.messages.requests.CancelOrderRequest;
import impl.messages.requests.PlaceOrderRequest;
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
            IRequest request = (IRequest) in.readObject();
            while (request != null && !brokerClientSocket.isClosed()) {
                handleRequest(request);
                request = (IRequest) in.readObject();
            }
        } catch (IOException e) {
            e.printStackTrace();
            shutdown();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            // TODO
        }
    }

    private void handleRequest(IRequest request) {
        List<IResponse> responses;
        if (request.getRequestType() == RequestType.PLACE) {
            System.out.println("Received place order request");   // TODO delete
            PlaceOrderRequest placeOrderRequest = (PlaceOrderRequest) request;
            responses = orderBook.placeOrder(placeOrderRequest);
        } else {
            System.out.println("Received cancel order request");   // TODO delete
            CancelOrderRequest cancelOrderRequest = (CancelOrderRequest) request;
            responses = List.of(orderBook.cancelOrder(cancelOrderRequest));
        }
        for (IMessage message : responses) {
            responseBroadcastService.broadcastMessages(message);
        }
    }

    public void sendMessage(IMessage message) {
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
