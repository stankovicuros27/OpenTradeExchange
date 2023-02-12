package server.direct;

import api.core.IMatchingEngine;
import api.core.IOrderBook;
import api.core.Side;
import api.messages.authentication.IMicroFIXAuthenticationMessageFactory;
import api.messages.authentication.IMicroFIXAuthenticationRequest;
import api.messages.authentication.IMicroFIXAuthenticationResponse;
import api.messages.trading.MicroFIXSide;
import api.messages.trading.request.MicroFIXRequestType;
import api.messages.trading.request.IMicroFIXRequest;
import api.messages.trading.response.IMicroFIXResponse;
import api.messages.trading.response.IMicroFIXResponseFactory;
import api.messages.requests.ICancelOrderRequest;
import api.messages.requests.IPlaceOrderRequest;
import api.messages.responses.IResponse;
import impl.core.MatchingEngine;
import impl.messages.authentication.MicroFIXAuthenticationMessageFactory;
import impl.messages.trading.response.MicroFIXResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.messages.InternalToExternalResponseTranslator;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ConnectionHandler implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchingEngine.class);

    private final IMatchingEngine matchingEngine;
    private final Socket clientSocket;
    private final String clientIpAddress;
    private final ResponseSenderService responseSenderService;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int userID;

    private final IMicroFIXResponseFactory externalResponseFactory = new MicroFIXResponseFactory();
    private final IMicroFIXAuthenticationMessageFactory microFIXAuthenticationMessageFactory = new MicroFIXAuthenticationMessageFactory();

    public ConnectionHandler(IMatchingEngine matchingEngine, Socket clientSocket, String clientIpAddress, ResponseSenderService responseSenderService) {
        this.matchingEngine = matchingEngine;
        this.clientSocket = clientSocket;
        this.clientIpAddress = clientIpAddress;
        this.responseSenderService = responseSenderService;
    }

    @Override
    public void run() {
        try {
            LOGGER.info("Starting client connection at IP address: " + clientIpAddress);
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            if (!authenticateUser()) {
                return;
            }
            responseSenderService.registerConnectionHandler(this);
            IMicroFIXRequest externalRequest = (IMicroFIXRequest) in.readObject();
            while (externalRequest != null) {
                handleExternalRequest(externalRequest);
                externalRequest = (IMicroFIXRequest) in.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
    }

    private boolean authenticateUser() {
        try {
            IMicroFIXAuthenticationRequest microFIXAuthenticationRequest = (IMicroFIXAuthenticationRequest) in.readObject();
            userID = microFIXAuthenticationRequest.getUserID();
            String passwordHash = microFIXAuthenticationRequest.getPasswordHash();
            // TODO check
            LOGGER.info("Authenticated userID: " + userID);
            IMicroFIXAuthenticationResponse microFIXAuthenticationResponse = microFIXAuthenticationMessageFactory.getAuthenticationResponse(userID, true);
            out.writeObject(microFIXAuthenticationResponse);
            out.flush();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void closeConnection() {
        LOGGER.info("Closing client connection at IP address: " + clientIpAddress + ", with userID: " + userID);
        responseSenderService.removeConnectionHandler(this);
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            LOGGER.error(e.toString());
        }
    }

    private void handleExternalRequest(IMicroFIXRequest externalRequest) {
        String bookID = externalRequest.getBookID();
        if (!matchingEngine.containsOrderBook(bookID)) {
            IMicroFIXResponse errorAckResponse = externalResponseFactory.getErrorAckResponse(
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
        if (externalRequest.getExternalRequestType() == MicroFIXRequestType.PLACE) {
            responses = handleExternalPlaceOrderRequest(externalRequest);
        } else if (externalRequest.getExternalRequestType() == MicroFIXRequestType.CANCEL) {
            responses = handleExternalCancelOrderRequest(externalRequest);
        }
        if (responses != null) {
            for (IResponse response : responses) {
                responseSenderService.distributeMessages(InternalToExternalResponseTranslator.getExternalResponse(response));
            }
        }
    }

    private List<IResponse> handleExternalPlaceOrderRequest(IMicroFIXRequest externalPlaceOrderRequest) {
        String bookID = externalPlaceOrderRequest.getBookID();
        IOrderBook orderBook = matchingEngine.getOrderBook(bookID);
        Side side = externalPlaceOrderRequest.getSide() == MicroFIXSide.BUY ? Side.BUY : Side.SELL;
        IPlaceOrderRequest placeOrderRequest = orderBook.getOrderRequestFactory().createPlaceOrderRequest(
                externalPlaceOrderRequest.getUserID(),
                externalPlaceOrderRequest.getPrice(),
                side,
                externalPlaceOrderRequest.getVolume()
        );
        int externalTimestamp = externalPlaceOrderRequest.getExternalTimestamp();
        IMicroFIXResponse placeOrderAckResponse = externalResponseFactory.getReceivedPlaceOrderAckResponse(
                placeOrderRequest.getBookID(),
                placeOrderRequest.getUserID(),
                placeOrderRequest.getOrderID(),
                placeOrderRequest.getPrice(),
                placeOrderRequest.getSide() == Side.BUY ? MicroFIXSide.BUY : MicroFIXSide.SELL,
                placeOrderRequest.getTotalVolume(),
                externalTimestamp
        );
        sendMessage(placeOrderAckResponse);
        return orderBook.placeOrder(placeOrderRequest);
    }

    private List<IResponse> handleExternalCancelOrderRequest(IMicroFIXRequest externalCancelOrderRequest) {
        String bookID = externalCancelOrderRequest.getBookID();
        IOrderBook orderBook = matchingEngine.getOrderBook(bookID);
        ICancelOrderRequest cancelOrderRequest = orderBook.getOrderRequestFactory().createCancelOrderRequest(
                externalCancelOrderRequest.getUserID(),
                externalCancelOrderRequest.getOrderID()
        );
        int externalTimestamp = externalCancelOrderRequest.getExternalTimestamp();
        IMicroFIXResponse cancelOrderAckResponse = externalResponseFactory.getReceivedCancelOrderAckResponse(
                cancelOrderRequest.getBookID(),
                cancelOrderRequest.getUserID(),
                cancelOrderRequest.getOrderID(),
                externalTimestamp
        );
        sendMessage(cancelOrderAckResponse);
        return List.of(orderBook.cancelOrder(cancelOrderRequest));
    }

    public synchronized void sendMessage(IMicroFIXResponse externalResponse) {
        try {
            out.writeObject(externalResponse);
            out.flush();
        } catch (IOException e) {
            LOGGER.info(e.toString());
        }
    }

    public synchronized String getClientIpAddress() {
        return clientIpAddress;
    }

    public synchronized int getUserID() {
        return userID;
    }

}
