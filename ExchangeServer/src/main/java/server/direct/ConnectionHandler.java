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
import api.messages.responses.IResponse;
import impl.messages.external.response.ExternalResponseFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.messages.InternalToExternalResponseTranslator;

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
            IExternalResponse errorAckResponse = externalResponseFactory.getErrorAckResponse(
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
        List<IResponse> responses = null;
        if (externalRequest.getExternalRequestType() == ExternalRequestType.PLACE) {
            responses = handleExternalPlaceOrderRequest(externalRequest);
        } else if (externalRequest.getExternalRequestType() == ExternalRequestType.CANCEL) {
            responses = handleExternalCancelOrderRequest(externalRequest);
        }
        if (responses != null) {
            for (IResponse response : responses) {
                broadcastService.broadcastMessages(InternalToExternalResponseTranslator.getExternalResponse(response));
            }
        }
    }

    private List<IResponse> handleExternalPlaceOrderRequest(IExternalRequest externalPlaceOrderRequest) {
        String bookID = externalPlaceOrderRequest.getBookID();
        IOrderBook orderBook = matchingEngine.getOrderBook(bookID);
        Side side = externalPlaceOrderRequest.getSide() == ExternalSide.BUY ? Side.BUY : Side.SELL;
        IPlaceOrderRequest placeOrderRequest = orderBook.getOrderRequestFactory().createPlaceOrderRequest(
                externalPlaceOrderRequest.getUserID(),
                externalPlaceOrderRequest.getPrice(),
                side,
                externalPlaceOrderRequest.getVolume()
        );
        int externalTimestamp = externalPlaceOrderRequest.getExternalTimestamp();
        IExternalResponse placeOrderAckResponse = externalResponseFactory.getReceivedPlaceOrderAckResponse(
                placeOrderRequest.getBookID(),
                placeOrderRequest.getUserID(),
                placeOrderRequest.getOrderID(),
                placeOrderRequest.getPrice(),
                placeOrderRequest.getSide() == Side.BUY ? ExternalSide.BUY : ExternalSide.SELL,
                placeOrderRequest.getTotalVolume(),
                externalTimestamp
        );
        sendMessage(placeOrderAckResponse);
        return orderBook.placeOrder(placeOrderRequest);
    }

    private List<IResponse> handleExternalCancelOrderRequest(IExternalRequest externalCancelOrderRequest) {
        String bookID = externalCancelOrderRequest.getBookID();
        IOrderBook orderBook = matchingEngine.getOrderBook(bookID);
        ICancelOrderRequest cancelOrderRequest = orderBook.getOrderRequestFactory().createCancelOrderRequest(
                externalCancelOrderRequest.getUserID(),
                externalCancelOrderRequest.getOrderID()
        );
        int externalTimestamp = externalCancelOrderRequest.getExternalTimestamp();
        IExternalResponse cancelOrderAckResponse = externalResponseFactory.getReceivedCancelOrderAckResponse(
                cancelOrderRequest.getBookID(),
                cancelOrderRequest.getUserID(),
                cancelOrderRequest.getOrderID(),
                externalTimestamp
        );
        sendMessage(cancelOrderAckResponse);
        return List.of(orderBook.cancelOrder(cancelOrderRequest));
    }

    public synchronized void sendMessage(IExternalResponse externalResponse) {
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
