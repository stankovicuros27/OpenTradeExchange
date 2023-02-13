package server.direct;

import api.core.IMatchingEngine;
import charts.MatchingEngineChartAnalytics;
import server.ExchangeServerContext;
import server.direct.multicast.L1MarketDataMulticastService;

import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExchangeServerManager {

    private final ExchangeServerContext exchangeServerContext;
    private final ExchangeServer exchangeServer;
    private final L1MarketDataMulticastService l1MarketDataMulticastService;
    private final MatchingEngineChartAnalytics matchingEngineChartAnalytics;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    public ExchangeServerManager(ExchangeServerContext exchangeServerContext) {
        this.exchangeServerContext = exchangeServerContext;

        // Exchange server
        IMatchingEngine matchingEngine = exchangeServerContext.getMatchingEngine();
        int exchangeServerPort = exchangeServerContext.getTcpExchangePort();
        ResponseSenderService responseSenderService = new ResponseSenderService();
        exchangeServer = new ExchangeServer(matchingEngine, exchangeServerPort, responseSenderService);

        // L1 Broadcast
        try {
            String multicastIpAddress = exchangeServerContext.getMulticastIp();
            int l1DataPort = exchangeServerContext.getL1DataMulticastPort();
            int l1TimeoutMs = exchangeServerContext.getL1DataTimeoutMs();
            l1MarketDataMulticastService = new L1MarketDataMulticastService(matchingEngine, multicastIpAddress, l1DataPort, l1TimeoutMs);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        // Analytics
        matchingEngineChartAnalytics = new MatchingEngineChartAnalytics(matchingEngine);

    }

    public void startDirectExchangeServer() {
        Thread exchangeServerThread = new Thread(exchangeServer);
        Thread exchangeInfoPublisherThread = new Thread(l1MarketDataMulticastService);
        Thread matchingEngineChartAnalyticsThread = new Thread(matchingEngineChartAnalytics);
        threadPool.execute(exchangeServerThread);
        threadPool.execute(exchangeInfoPublisherThread);
        if (exchangeServerContext.isAnalyticsEnabled()) {
            threadPool.execute(matchingEngineChartAnalyticsThread);
        }
    }

}
