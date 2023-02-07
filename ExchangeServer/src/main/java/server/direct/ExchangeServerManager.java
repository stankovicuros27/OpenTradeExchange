package server.direct;

import api.core.IMatchingEngine;
import charts.MatchingEngineChartAnalytics;
import server.ExchangeServerContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExchangeServerManager {

    private static final int EXCHANGE_SERVER_PORT = 9999;
    private static final int INFO_BROADCAST_TIMEOUT_MS = 1000;

    private final ExchangeServer exchangeServer;
    private final ExchangeInfoPublisher exchangeInfoPublisher;
    private final MatchingEngineChartAnalytics matchingEngineChartAnalytics;

    public ExchangeServerManager(ExchangeServerContext exchangeServerContext) {
        IMatchingEngine matchingEngine = exchangeServerContext.getMatchingEngine();
        ExecutorService threadPool = Executors.newCachedThreadPool();
        BroadcastService broadcastService = new BroadcastService();
        exchangeServer = new ExchangeServer(matchingEngine, threadPool, EXCHANGE_SERVER_PORT, broadcastService);
        exchangeInfoPublisher = new ExchangeInfoPublisher(matchingEngine, broadcastService, INFO_BROADCAST_TIMEOUT_MS);
        matchingEngineChartAnalytics = new MatchingEngineChartAnalytics(ExchangeServerContext.getInstance().getMatchingEngine());
    }

    public void startDirectExchangeServer() {
        Thread exchangeServerThread = new Thread(exchangeServer);
        exchangeServerThread.start();
        Thread exchangeInfoPublisherThread = new Thread(exchangeInfoPublisher);
        exchangeInfoPublisherThread.start();
        Thread matchingEngineChartAnalyticsThread = new Thread(matchingEngineChartAnalytics);
        matchingEngineChartAnalyticsThread.start();
    }

}
