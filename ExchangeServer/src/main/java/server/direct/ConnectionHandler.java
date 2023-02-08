package server.direct;

import api.core.IMatchingEngine;
import api.core.IOrderBook;
import api.messages.external.ExternalRequestType;
import api.messages.external.IExternalCancelOrderRequest;
import api.messages.external.IExternalPlaceOrderRequest;
import api.messages.external.IExternalRequest;
import api.messages.IMessage;
import api.messages.internal.requests.ICancelOrderRequest;
import api.messages.internal.requests.IPlaceOrderRequest;
import api.messages.internal.responses.ICancelOrderAckResponse;
import api.messages.internal.responses.IPlaceOrderAckResponse;
import api.messages.internal.responses.IResponse;
import impl.messages.internal.responses.ErrorAckResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ConnectionHandler implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(ConnectionHandler.class);

    private final IMatchingEngine matchingEngine;
    private final Socket clientSocket;
    private final String clientIpAddress;
    private final BroadcastService broadcastService;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ConnectionHandler(IMatchingEngine matchingEngine, Socket clientSocket, String clientIpAddress, BroadcastService broadcastService) {
        this.matchingEngine = matchingEngine;
        this.clientSocket = clientSocket;
        this.clientIpAddress = clientIpAddress;
        this.broadcastService = broadcastService;
    }

    @Override
    public void run() {
        try {
            LOGGER.info("Starting client connection at IP address: " + clientIpAddress);
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            IExternalRequest externalRequest = (IExternalRequest) in.readObject();
            while (externalRequest != null) {
                handleExternalRequest(externalRequest);
                externalRequest = (IExternalRequest) in.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.info(e);
        } finally {
            LOGGER.info("Closing client connection at IP address: " + clientIpAddress);
            broadcastService.removeBrokerConnectionHandler(this);
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }
    }

    private void handleExternalRequest(IExternalRequest externalRequest) {
        List<IResponse> responses;
        if (externalRequest.getExternalRequestType() == ExternalRequestType.PLACE) {
            IExternalPlaceOrderRequest externalPlaceOrderRequest = (IExternalPlaceOrderRequest) externalRequest;
            responses = handleExternalPlaceOrderRequest(externalPlaceOrderRequest);
        } else {
            IExternalCancelOrderRequest externalCancelOrderRequest = (IExternalCancelOrderRequest) externalRequest;
            responses = handleExternalCancelOrderRequest(externalCancelOrderRequest);
        }
        for (IMessage message : responses) {
            broadcastService.broadcastMessages(message);
        }
    }

    private List<IResponse> handleExternalPlaceOrderRequest(IExternalPlaceOrderRequest externalPlaceOrderRequest) {
        String bookID = externalPlaceOrderRequest.getBookID();
        if (!matchingEngine.containsOrderBook(bookID)) {
            return List.of(new ErrorAckResponse("NULL_BOOK", externalPlaceOrderRequest.getUserID(), externalPlaceOrderRequest.getTimestamp()));
        }
        IOrderBook orderBook = matchingEngine.getOrderBook(bookID);
        IPlaceOrderRequest placeOrderRequest = orderBook.getOrderRequestFactory().createPlaceOrderRequest(
                externalPlaceOrderRequest.getUserID(),
                externalPlaceOrderRequest.getPrice(),
                externalPlaceOrderRequest.getSide(),
                externalPlaceOrderRequest.getVolume()
        );
        IPlaceOrderAckResponse placeOrderAckResponse = orderBook.getOrderRequestFactory().createPlaceOrderAckResponse(placeOrderRequest, externalPlaceOrderRequest.getTimestamp());
        sendMessage(placeOrderAckResponse);
        return orderBook.placeOrder(placeOrderRequest);
    }

    private List<IResponse> handleExternalCancelOrderRequest(IExternalCancelOrderRequest externalCancelOrderRequest) {
        String bookID = externalCancelOrderRequest.getBookID();
        if (!matchingEngine.containsOrderBook(bookID)) {
            return List.of(new ErrorAckResponse("NULL_BOOK", externalCancelOrderRequest.getUserID(), externalCancelOrderRequest.getTimestamp()));
        }
        IOrderBook orderBook = matchingEngine.getOrderBook(bookID);
        ICancelOrderRequest cancelOrderRequest = orderBook.getOrderRequestFactory().createCancelOrderRequest(
                externalCancelOrderRequest.getUserID(),
                externalCancelOrderRequest.getOrderID()
        );
        ICancelOrderAckResponse cancelOrderAckResponse = orderBook.getOrderRequestFactory().createCancelOrderAckResponse(cancelOrderRequest, externalCancelOrderRequest.getTimestamp());
        sendMessage(cancelOrderAckResponse);
        return List.of(orderBook.cancelOrder(cancelOrderRequest));
    }

    public void sendMessage(IMessage message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            LOGGER.info(e);
        }
    }

    public String getClientIpAddress() {
        return clientIpAddress;
    }

}
