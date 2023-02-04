package server;

import api.core.IOrderBook;
import api.messages.IMessage;
import api.messages.requests.IRequest;
import api.messages.requests.RequestType;
import api.messages.responses.IResponse;
import impl.messages.requests.CancelOrderRequest;
import impl.messages.requests.PlaceOrderRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class BrokerConnectionHandler implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(BrokerConnectionHandler.class);

    private final IOrderBook orderBook;
    private final Socket brokerClientSocket;
    private final String ipAddress;
    private final BroadcastService broadcastService;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public BrokerConnectionHandler(IOrderBook orderBook, Socket brokerClientSocket, String ipAddress, BroadcastService broadcastService) {
        this.orderBook = orderBook;
        this.brokerClientSocket = brokerClientSocket;
        this.ipAddress = ipAddress;
        this.broadcastService = broadcastService;
    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(brokerClientSocket.getInputStream());
            out = new ObjectOutputStream(brokerClientSocket.getOutputStream());
            IRequest request = (IRequest) in.readObject();
            while (request != null) {
                handleRequest(request);
                request = (IRequest) in.readObject();
            }
        } catch (IOException e) {
            LOGGER.info(e);
        } catch (ClassNotFoundException e) {
            LOGGER.warn(e);
        } finally {
            LOGGER.info("Closing broker connection at IP address: " + ipAddress);
            broadcastService.removeBrokerConnectionHandler(this);
            try {
                in.close();
                out.close();
                brokerClientSocket.close();
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }
    }

    private void handleRequest(IRequest request) {
        List<IResponse> responses;
        if (request.getRequestType() == RequestType.PLACE) {
            PlaceOrderRequest placeOrderRequest = (PlaceOrderRequest) request;
            responses = orderBook.placeOrder(placeOrderRequest);
        } else {
            CancelOrderRequest cancelOrderRequest = (CancelOrderRequest) request;
            responses = List.of(orderBook.cancelOrder(cancelOrderRequest));
        }
        for (IMessage message : responses) {
            broadcastService.broadcastMessages(message);
        }
    }

    public void sendMessage(IMessage message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String getIpAddress() {
        return ipAddress;
    }
}
