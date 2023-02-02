import api.sides.Side;
import impl.messages.util.OrderRequestFactory;
import impl.time.InstantTimestampProvider;

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

        OrderRequestFactory orderRequestFactory = new OrderRequestFactory(new InstantTimestampProvider());

        try (Socket client = new Socket(EXCHANGE_SERVER_IP, EXCHANGE_SERVER_SOCKET)) {
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());

            out.writeObject(orderRequestFactory.createPlaceOrderRequest(0, 10.0, Side.BUY, 10));
            out.writeObject(orderRequestFactory.createCancelOrderRequest(0, 0));
            out.flush();
            Object obj;
            while ((obj = in.readObject()) != null) {
                System.out.println(obj);
            }



        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        BrokerClient brokerClient = new BrokerClient();
        Thread brokerClientThread = new Thread(brokerClient);
        brokerClientThread.start();
    }

}
