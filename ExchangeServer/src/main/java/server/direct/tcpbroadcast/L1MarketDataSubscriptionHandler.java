package server.direct.tcpbroadcast;

import api.messages.authentication.IMicroFIXAuthenticationMessageFactory;
import api.messages.authentication.IMicroFIXAuthenticationRequest;
import api.messages.authentication.IMicroFIXAuthenticationResponse;
import api.messages.data.IMicroFIXDataMessageFactory;
import api.messages.data.IMicroFIXL1DataMessage;
import authenticationdb.AuthenticationDBConnection;
import impl.messages.authentication.MicroFIXAuthenticationMessageFactory;
import impl.messages.data.MicroFIXDataMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;

public class L1MarketDataSubscriptionHandler implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(L1MarketDataSubscriptionHandler.class);

    private final Socket clientSocket;
    private final String clientIpAddress;
    private final L1MarketDataSenderService l1MarketDataSenderService;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int userID;

    private final IMicroFIXDataMessageFactory microFIXDataMessageFactory = new MicroFIXDataMessageFactory();
    private final IMicroFIXAuthenticationMessageFactory microFIXAuthenticationMessageFactory = new MicroFIXAuthenticationMessageFactory();

    public L1MarketDataSubscriptionHandler(Socket clientSocket, String clientIpAddress, L1MarketDataSenderService l1MarketDataSenderService) throws IOException {
        this.clientSocket = clientSocket;
        this.clientIpAddress = clientIpAddress;
        this.l1MarketDataSenderService = l1MarketDataSenderService;
    }

    @Override
    public void run() {
        try {
            LOGGER.info("Starting client L1 TCP subscription at IP address: " + clientIpAddress);
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            if (!authenticateUser()) {
                closeConnection();
            }
            l1MarketDataSenderService.registerSubscriptionHandler(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void sendMessage(IMicroFIXL1DataMessage microFIXL1DataMessage) {
        try {
            out.writeObject(microFIXL1DataMessage);
            out.flush();
        } catch (IOException e) {
            LOGGER.info(e.toString());
            closeConnection();
        }
    }

    public String getClientIpAddress() {
        return clientIpAddress;
    }

    public int getUserID() {
        return userID;
    }

    private boolean authenticateUser() {
        try {
            IMicroFIXAuthenticationRequest microFIXAuthenticationRequest = (IMicroFIXAuthenticationRequest) in.readObject();
            userID = microFIXAuthenticationRequest.getUserID();
            String password = microFIXAuthenticationRequest.getPassword();
            if (AuthenticationDBConnection.getInstance().getUserType(userID, password) == -1) {
                LOGGER.info("Error authenticating userID: " + userID);
                IMicroFIXAuthenticationResponse microFIXAuthenticationResponse = microFIXAuthenticationMessageFactory.getAuthenticationResponse(userID, false);
                out.writeObject(microFIXAuthenticationResponse);
                out.flush();
                return false;
            }
            LOGGER.info("Authenticated userID: " + userID);
            IMicroFIXAuthenticationResponse microFIXAuthenticationResponse = microFIXAuthenticationMessageFactory.getAuthenticationResponse(userID, true);
            out.writeObject(microFIXAuthenticationResponse);
            out.flush();
            return true;
        } catch (IOException | ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void closeConnection() {
        LOGGER.info("Closing client L1 TCP subscription at IP address: " + clientIpAddress + ", with userID: " + userID);
        l1MarketDataSenderService.removeSubscriptionHandler(this);
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            LOGGER.error(e.toString());
        }
    }

}
