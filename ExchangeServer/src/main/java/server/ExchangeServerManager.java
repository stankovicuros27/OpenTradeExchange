package server;

import api.core.IOrderLookupCache;
import api.time.ITimestampProvider;
import impl.core.OrderBook;
import impl.core.OrderLookupCache;
import impl.time.InstantTimestampProvider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExchangeServerManager {

    private static final int EXCHANGE_SERVER_PORT = 9999;

    // TODO read server config from properties
    public static void main(String[] args) {
        IOrderLookupCache orderLookupCache = new OrderLookupCache();
        ITimestampProvider timestampProvider = new InstantTimestampProvider();
        OrderBook orderBook = new OrderBook(orderLookupCache, timestampProvider);
        ExecutorService threadPool = Executors.newCachedThreadPool();
        BroadcastService broadcastService = new BroadcastService();
        ExchangeServer exchangeServer = new ExchangeServer(orderBook, threadPool, EXCHANGE_SERVER_PORT, broadcastService);
        Thread exchangeServerThread = new Thread(exchangeServer);
        exchangeServerThread.start();
    }

}
