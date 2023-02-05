package client;

import api.messages.requests.IRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ExchangeClientConnection {

    private static final Logger LOGGER = LogManager.getLogger(ExchangeClientConnection.class);

    private static ExchangeClientConnection instance = null;

    private final String exchangeServerIp;
    private final int exchangeServerSocket;
    private final Socket clientSocket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final ExchangeClientDataListener exchangeClientDataListener;

    private ExchangeClientConnection(String exchangeServerIp, int exchangeServerSocket) throws IOException {
        this.exchangeServerIp = exchangeServerIp;
        this.exchangeServerSocket = exchangeServerSocket;
        clientSocket = new Socket(exchangeServerIp, exchangeServerSocket);
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
        this.exchangeClientDataListener = new ExchangeClientDataListener(in);
    }

    static void initializeConnection(String exchangeServerIp, int exchangeServerSocket) throws IOException {
        LOGGER.info("Connection initialized");
        if (instance != null) {
            throw new IOException();
        }
        instance = new ExchangeClientConnection(exchangeServerIp, exchangeServerSocket);
        instance.startExchangeDataListener();
    }

    static void closeConnection() throws IOException {
        LOGGER.info("Connection closed");
        if (instance == null) {
            throw new IOException();
        }
        instance.close();
        instance = null;
    }

    public static synchronized ExchangeClientConnection getInstance() throws IOException {
        if (instance == null) {
            throw new IOException();
        }
        return instance;
    }

    public synchronized void sendRequest(IRequest request) throws IOException {
        out.writeObject(request);
        out.flush();
    }

    private void startExchangeDataListener() {
        LOGGER.info("Start ExchangeDataListener");
        Thread exchangeConnectionListenerThread = new Thread(exchangeClientDataListener);
        exchangeConnectionListenerThread.start();
    }

    private void close() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
