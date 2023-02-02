import networking.messages.requests.NetworkCancelOrderRequest;
import networking.messages.requests.NetworkRequestSide;
import networking.messages.requests.NetworkPlaceOrderRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class BrokerClient implements Runnable {

    private static final String EXCHANGE_SERVER_IP = "127.0.0.1";
    private static final int EXCHANGE_SERVER_SOCKET = 9999;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    @Override
    public void run() {
        try (Socket client = new Socket(EXCHANGE_SERVER_IP, EXCHANGE_SERVER_SOCKET)) {
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());

            out.writeObject(new NetworkPlaceOrderRequest(0, 10.0, NetworkRequestSide.BUY, 10));
            out.writeObject(new NetworkCancelOrderRequest(0, 0));
            out.flush();



        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        BrokerClient brokerClient = new BrokerClient();
        Thread brokerClientThread = new Thread(brokerClient);
        brokerClientThread.start();
    }

}
