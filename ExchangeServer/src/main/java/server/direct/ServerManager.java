package server.direct;

import api.core.IMatchingEngine;
import charts.MatchingEngineChartAnalytics;
import server.ExchangeServerContext;
import server.direct.core.ExchangeResponseSenderService;
import server.direct.core.ExchangeServer;
import server.direct.multicast.L1MarketDataMulticastServer;
import server.direct.tcpbroadcast.L1MarketDataSenderService;
import server.direct.tcpbroadcast.L1MarketDataTCPServer;

import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerManager {

    private final ExchangeServerContext exchangeServerContext;
    private final ExchangeServer exchangeServer;
    private final L1MarketDataTCPServer l1MarketDataTCPServer;
    private final L1MarketDataMulticastServer l1MarketDataMulticastServer;
    private final MatchingEngineChartAnalytics matchingEngineChartAnalytics;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    public ServerManager(ExchangeServerContext exchangeServerContext) {
        this.exchangeServerContext = exchangeServerContext;

        // Exchange server
        IMatchingEngine matchingEngine = exchangeServerContext.getMatchingEngine();
        int exchangeServerPort = exchangeServerContext.getTcpExchangePort();
        ExchangeResponseSenderService exchangeResponseSenderService = new ExchangeResponseSenderService();
        exchangeServer = new ExchangeServer(matchingEngine, exchangeServerPort, exchangeResponseSenderService);

        // L1 TCP Broadcast
        int l1DataTcpPort = exchangeServerContext.getL1DataTcpPort();
        int l1TimeoutMs = exchangeServerContext.getL1DataTimeoutMs();
        L1MarketDataSenderService l1MarketDataSenderService = new L1MarketDataSenderService();
        l1MarketDataTCPServer = new L1MarketDataTCPServer(matchingEngine, l1DataTcpPort, l1TimeoutMs, l1MarketDataSenderService);

        // L1 Multicast
        try {
            String multicastIpAddress = exchangeServerContext.getMulticastIp();
            int l1DataMulticastPort = exchangeServerContext.getL1DataMulticastPort();
            l1MarketDataMulticastServer = new L1MarketDataMulticastServer(matchingEngine, multicastIpAddress, l1DataMulticastPort, l1TimeoutMs);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        // Analytics
        matchingEngineChartAnalytics = new MatchingEngineChartAnalytics(matchingEngine);
    }

    public void startDirectExchangeServer() {
        threadPool.execute(exchangeServer);
        threadPool.execute(l1MarketDataTCPServer);
        if (exchangeServerContext.isMulticastEnabled()) {
            threadPool.execute(l1MarketDataMulticastServer);
        }
        if (exchangeServerContext.isAnalyticsEnabled()) {
            threadPool.execute(matchingEngineChartAnalytics);
        }
    }

}
