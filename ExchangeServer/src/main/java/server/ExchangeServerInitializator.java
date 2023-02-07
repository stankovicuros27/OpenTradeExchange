package server;

import server.direct.ExchangeServerManager;
import trader.agents.ITraderAgentManager;
import trader.agents.controlled.ControlledTraderAgentManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExchangeServerInitializator {

    public static void initialize() {
        ExchangeServerContext.initialize();
        ExchangeServerManager exchangeServerManager = new ExchangeServerManager(ExchangeServerContext.getInstance());
        exchangeServerManager.startDirectExchangeServer();

        // Start dummy traders
        ExecutorService traderThreadPool = Executors.newCachedThreadPool();
        ITraderAgentManager controllerTraderAgentManager = new ControlledTraderAgentManager(ExchangeServerContext.getInstance().getMatchingEngine(), traderThreadPool);
        new Thread(controllerTraderAgentManager).start();
    }

}
