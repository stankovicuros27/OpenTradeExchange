package server;

import api.core.IOrderBook;
import impl.core.OrderBook;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

    IOrderBook orderBook = new OrderBook();

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(9999);
            Socket brokerSocket = serverSocket.accept();
            BrokerConnectionHandler handler = new BrokerConnectionHandler(orderBook, brokerSocket);
            handler.run();
        } catch (IOException e) {
            // TODO handle
            throw new RuntimeException(e);
        }
    }

}
