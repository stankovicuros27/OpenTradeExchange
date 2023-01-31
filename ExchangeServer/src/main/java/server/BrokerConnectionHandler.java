package server;

import api.core.IOrderBook;
import api.messages.requests.IRequest;
import api.messages.requests.RequestType;
import impl.messages.requests.CancelOrderRequest;
import impl.messages.requests.PlaceOrderRequest;

import java.io.*;
import java.net.Socket;

public class BrokerConnectionHandler implements Runnable {

    private final IOrderBook orderBook;
    private final Socket brokerSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public BrokerConnectionHandler(IOrderBook orderBook, Socket brokerSocket) {
        this.orderBook = orderBook;
        this.brokerSocket = brokerSocket;
    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(brokerSocket.getInputStream());
            out = new ObjectOutputStream(brokerSocket.getOutputStream());

            IRequest request = (IRequest) in.readObject();
            while (request != null) {
                if (request.getRequestType() == RequestType.PLACE) {
                    orderBook.placeOrder((PlaceOrderRequest) request);
                } else {
                    orderBook.cancelOrder((CancelOrderRequest) request);
                }
                request = (IRequest) in.readObject();
            }


        } catch (IOException e) {

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
