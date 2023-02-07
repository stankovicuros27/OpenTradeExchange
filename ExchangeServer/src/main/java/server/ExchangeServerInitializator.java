package server;

import server.direct.ExchangeServerManager;
import trader.TraderAgentManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExchangeServerInitializator {

    public static void initialize() {
        ExchangeServerContext.initialize();
        ExchangeServerManager exchangeServerManager = new ExchangeServerManager(ExchangeServerContext.getInstance());
        exchangeServerManager.startDirectExchangeServer();
        ExecutorService traderThreadPool = Executors.newCachedThreadPool();
        TraderAgentManager traderAgentManager = new TraderAgentManager(ExchangeServerContext.getInstance().getMatchingEngine(), traderThreadPool);
        new Thread(traderAgentManager).start();
    }

}
