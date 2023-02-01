package server;

import api.core.IOrderBook;
import api.core.IOrderLookupCache;
import api.core.IOrderRequestFactory;
import api.util.ITimestampProvider;
import impl.core.OrderBook;
import impl.core.OrderLookupCache;
import impl.core.OrderRequestFactory;
import impl.util.InstantTimestampProvider;
import server.broadcast.InfoBroadcastService;
import server.broadcast.ResponseBroadcastService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

    private static final int EXCHANGE_SERVER_SOCKET = 9999;

    private final IOrderRequestFactory orderRequestFactory;
    private final IOrderBook orderBook;
    private final ResponseBroadcastService responseBroadcastService = new ResponseBroadcastService();
    private final InfoBroadcastService infoBroadcastService = new InfoBroadcastService();

    public Server() {
        IOrderLookupCache orderLookupCache = new OrderLookupCache();
        ITimestampProvider timestampProvider = new InstantTimestampProvider();
        orderBook = new OrderBook(orderLookupCache, timestampProvider);
        orderRequestFactory = new OrderRequestFactory(timestampProvider);
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(EXCHANGE_SERVER_SOCKET)) {
            while(true) {
                Socket brokerSocket = serverSocket.accept();

                System.out.println("ACCEPTED BROKER CONNECTION");   // TODO delete

                BrokerConnectionHandler handler = new BrokerConnectionHandler(orderBook, orderRequestFactory, brokerSocket, responseBroadcastService);
                responseBroadcastService.registerBrokerConnectionHandler(handler);
                infoBroadcastService.registerBrokerConnectionHandler(handler);
                Thread handlerThread = new Thread(handler);
                handlerThread.start();
            }
        } catch (IOException e) {
            // TODO handle
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        Thread serverThread = new Thread(server);
        serverThread.start();
    }

}
