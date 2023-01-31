package server;

import api.core.IOrderBook;
import api.core.IOrderLookupCache;
import api.core.IOrderRequestFactory;
import api.util.ITimestampProvider;
import impl.core.OrderBook;
import impl.core.OrderLookupCache;
import impl.core.OrderRequestFactory;
import impl.util.InstantTimestampProvider;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

    private final IOrderLookupCache orderLookupCache;
    private final ITimestampProvider timestampProvider;
    private final IOrderRequestFactory orderRequestFactory;
    private final IOrderBook orderBook;

    public Server() {
        orderLookupCache = new OrderLookupCache();
        timestampProvider = new InstantTimestampProvider();
        orderBook = new OrderBook(orderLookupCache, timestampProvider);
        orderRequestFactory = new OrderRequestFactory(timestampProvider);
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(9999);
            Socket brokerSocket = serverSocket.accept();
            BrokerConnectionHandler handler = new BrokerConnectionHandler(orderBook, orderRequestFactory, brokerSocket);
            handler.run();
        } catch (IOException e) {
            // TODO handle
            throw new RuntimeException(e);
        }
    }

}
