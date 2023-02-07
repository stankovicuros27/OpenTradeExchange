package server;

import api.core.IMatchingEngineConfiguration;
import api.core.IOrderBookConfiguration;
import impl.core.MatchingEngineConfiguration;
import impl.core.OrderBookConfiguration;
import server.direct.ExchangeServerManager;
import trader.agents.ITraderAgentManager;
import trader.agents.controlled.ControlledTraderAgentManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExchangeServerInitializator {

    public static void initialize() {
        // TODO read configuration
        IOrderBookConfiguration orderBookConfiguration1 = new OrderBookConfiguration("Test1", 2);
        IOrderBookConfiguration orderBookConfiguration2 = new OrderBookConfiguration("Test2", 2);
        IMatchingEngineConfiguration matchingEngineConfiguration = new MatchingEngineConfiguration();
        matchingEngineConfiguration.registerOrderBookConfiguration(orderBookConfiguration1);
        matchingEngineConfiguration.registerOrderBookConfiguration(orderBookConfiguration2);
        ExchangeServerContext.initialize(matchingEngineConfiguration);

        ExchangeServerManager exchangeServerManager = new ExchangeServerManager(ExchangeServerContext.getInstance());
        exchangeServerManager.startDirectExchangeServer();

        // Start dummy traders
        ExecutorService traderThreadPool = Executors.newCachedThreadPool();
        ITraderAgentManager controllerTraderAgentManager = new ControlledTraderAgentManager(ExchangeServerContext.getInstance().getMatchingEngine(), traderThreadPool);
        new Thread(controllerTraderAgentManager).start();
    }

}
