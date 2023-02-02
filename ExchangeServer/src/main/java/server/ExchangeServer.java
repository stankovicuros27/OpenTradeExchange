package server;

import api.core.IOrderBook;
import api.core.IOrderLookupCache;
import api.messages.util.IOrderRequestFactory;
import api.time.ITimestampProvider;
import impl.core.OrderBook;
import impl.core.OrderLookupCache;
import impl.messages.util.OrderRequestFactory;
import impl.time.InstantTimestampProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.broadcast.InfoBroadcastService;
import server.broadcast.ResponseBroadcastService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExchangeServer implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(ExchangeServer.class);

    private static final int EXCHANGE_SERVER_SOCKET = 9999;

    private final IOrderRequestFactory orderRequestFactory;
    private final IOrderBook orderBook;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;

    private final ResponseBroadcastService responseBroadcastService = new ResponseBroadcastService();
    private final InfoBroadcastService infoBroadcastService = new InfoBroadcastService();
    private final List<BrokerConnectionHandler> brokerConnectionHandlers = new ArrayList<>();

    public ExchangeServer() {
        LOGGER.info("Creating ExchangeServer");
        IOrderLookupCache orderLookupCache = new OrderLookupCache();
        ITimestampProvider timestampProvider = new InstantTimestampProvider();
        orderBook = new OrderBook(orderLookupCache, timestampProvider);
        orderRequestFactory = new OrderRequestFactory(timestampProvider);
    }

    @Override
    public void run() {
        LOGGER.info("Starting ExchangeServer");
        try {
            serverSocket = new ServerSocket(EXCHANGE_SERVER_SOCKET);
            threadPool = Executors.newCachedThreadPool();
            while(!serverSocket.isClosed()) {
                Socket brokerSocket = serverSocket.accept();

                System.out.println("ACCEPTED BROKER CONNECTION");   // TODO delete

                BrokerConnectionHandler handler = new BrokerConnectionHandler(orderBook, orderRequestFactory, brokerSocket, responseBroadcastService);
                brokerConnectionHandlers.add(handler);
                responseBroadcastService.registerBrokerConnectionHandler(handler);
                infoBroadcastService.registerBrokerConnectionHandler(handler);
                threadPool.execute(handler);
            }
        } catch (IOException e) {
            e.printStackTrace();
            shutdownServer();
        }
    }

    private void shutdownServer() {
        try {
            if (!serverSocket.isClosed()) {
                serverSocket.close();
            }
            for (BrokerConnectionHandler brokerConnectionHandler : brokerConnectionHandlers) {
                if (!brokerConnectionHandler.isClosed()) {
                    brokerConnectionHandler.shutdown();
                }
            }
            if (!threadPool.isShutdown()) {
                threadPool.shutdown();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        ExchangeServer exchangeServer = new ExchangeServer();
        Thread serverThread = new Thread(exchangeServer);
        serverThread.start();
    }

}
