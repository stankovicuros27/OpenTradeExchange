package client;

import api.messages.requests.IRequest;
import api.time.ITimestampProvider;
import impl.messages.util.OrderRequestFactory;
import impl.time.InstantTimestampProvider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ExchangeConnection {

    // TODO read client config from properties
    private static final String EXCHANGE_SERVER_IP = "127.0.0.1";
    private static final int EXCHANGE_SERVER_SOCKET = 9999;
    private static ExchangeConnection instance = null;

    private final Socket clientSocket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;

    private ExchangeConnection() throws IOException {
        clientSocket = new Socket(EXCHANGE_SERVER_IP, EXCHANGE_SERVER_SOCKET);
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());

        // Run read thread
        ExchangeConnectionListener exchangeConnectionListener = new ExchangeConnectionListener(in);
        Thread exchangeConnectionListenerThread = new Thread(exchangeConnectionListener);
        exchangeConnectionListenerThread.start();
    }

    public synchronized static ExchangeConnection getInstance() throws IOException {
        if (instance == null) {
            ITimestampProvider timestampProvider = new InstantTimestampProvider();
            OrderRequestFactory orderRequestFactory = new OrderRequestFactory(timestampProvider);
            instance = new ExchangeConnection();
        }
        return instance;
    }

    public synchronized void sendRequest(IRequest request) throws IOException {
        out.writeObject(request);
        out.flush();
    }

}
