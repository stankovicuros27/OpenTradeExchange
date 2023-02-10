package server.direct;

import api.core.IMatchingEngine;
import api.core.IOrderBook;
import api.core.Side;
import api.messages.external.ExternalSide;
import api.messages.external.request.ExternalRequestType;
import api.messages.external.request.IExternalRequest;
import api.messages.external.response.IExternalResponse;
import api.messages.external.response.IExternalResponseFactory;
import api.messages.requests.ICancelOrderRequest;
import api.messages.requests.IPlaceOrderRequest;
import impl.messages.external.response.ExternalResponseFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ConnectionHandler implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(ConnectionHandler.class);

    private final IExternalResponseFactory externalResponseFactory = new ExternalResponseFactory();
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
        String bookID = externalRequest.getBookID();
        if (!matchingEngine.containsOrderBook(bookID)) {
            IExternalResponse errorAckResponse = externalResponseFactory.getErrorResponse(
                    externalRequest.getBookID(),
                    externalRequest.getUserID(),
                    externalRequest.getPrice(),
                    externalRequest.getSide(),
                    externalRequest.getVolume(),
                    externalRequest.getExternalTimestamp()
            );
            sendMessage(errorAckResponse);
            return;
        }
        List<IExternalResponse> responses = null;
        if (externalRequest.getExternalRequestType() == ExternalRequestType.PLACE) {
            responses = handleExternalPlaceOrderRequest(externalRequest);
        } else if (externalRequest.getExternalRequestType() == ExternalRequestType.CANCEL) {
            responses = handleExternalCancelOrderRequest(externalRequest);
        }
        if (responses != null) {
            for (IExternalResponse message : responses) {
                broadcastService.broadcastMessages(message);
            }
        }
    }

    private List<IExternalResponse> handleExternalPlaceOrderRequest(IExternalRequest externalPlaceOrderRequest) {
        String bookID = externalPlaceOrderRequest.getBookID();
        IOrderBook orderBook = matchingEngine.getOrderBook(bookID);
        Side side = externalPlaceOrderRequest.getSide() == ExternalSide.BUY ? Side.BUY : Side.SELL;
        IPlaceOrderRequest placeOrderRequest = orderBook.getOrderRequestFactory().createPlaceOrderRequest(
                externalPlaceOrderRequest.getUserID(),
                externalPlaceOrderRequest.getPrice(),
                side,
                externalPlaceOrderRequest.getVolume()
        );
        IExternalResponse placeOrderAckResponse = externalResponseFactory.getReceivedPlaceOrderAckResponse(
                externalPlaceOrderRequest.getBookID(),
                externalPlaceOrderRequest.getUserID(),
                externalPlaceOrderRequest.getPrice(),
                externalPlaceOrderRequest.getSide(),
                externalPlaceOrderRequest.getVolume(),
                externalPlaceOrderRequest.getExternalTimestamp()
        );
        sendMessage(placeOrderAckResponse);
        // TODO map internal to external responses
        // return orderBook.placeOrder(placeOrderRequest);
        orderBook.placeOrder(placeOrderRequest);
        return null;
    }

    private List<IExternalResponse> handleExternalCancelOrderRequest(IExternalRequest externalCancelOrderRequest) {
        String bookID = externalCancelOrderRequest.getBookID();
        IOrderBook orderBook = matchingEngine.getOrderBook(bookID);
        ICancelOrderRequest cancelOrderRequest = orderBook.getOrderRequestFactory().createCancelOrderRequest(
                externalCancelOrderRequest.getUserID(),
                externalCancelOrderRequest.getOrderID()
        );
        IExternalResponse cancelOrderAckResponse = externalResponseFactory.getReceivedCancelOrderAckResponse(
                externalCancelOrderRequest.getBookID(),
                externalCancelOrderRequest.getUserID(),
                externalCancelOrderRequest.getOrderID(),
                externalCancelOrderRequest.getPrice(),
                externalCancelOrderRequest.getSide(),
                externalCancelOrderRequest.getVolume(),
                externalCancelOrderRequest.getExternalTimestamp()
        );
        sendMessage(cancelOrderAckResponse);
        // TODO map internal to external responses
        // return orderBook.placeOrder(placeOrderRequest);
        orderBook.cancelOrder(cancelOrderRequest);
        return null;
    }

    public void sendMessage(IExternalResponse externalResponse) {
        try {
            out.writeObject(externalResponse);
            out.flush();
        } catch (IOException e) {
            LOGGER.info(e);
        }
    }

    public String getClientIpAddress() {
        return clientIpAddress;
    }

}
