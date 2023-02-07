package trader;

import api.core.IMatchingEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import trader.agents.ControlledAgentLiquidityProvider;
import trader.agents.ITraderAgent;
import trader.runners.ITraderAgentRunner;
import trader.runners.LocalTraderAgentRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class TraderAgentManager implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(TraderAgentManager.class);

    private static final int NUMBER_OF_TRADER_AGENTS = 5;
    private static final int PRICE_ADJUSTMENT_SLEEP_TIME_MS = 1000;
    private static double basePrice = 100;
    private static final double PRICE_DEVIATION_FACTOR = 0.25;
    private static final int BASE_VOLUME = 51;
    private static final int VOLUME_DEVIATION = 30;
    private static final int MAX_ORDERS = 100;

    private final IMatchingEngine matchingEngine;
    private final ExecutorService threadPool;
    private final List<ITraderAgent> traderAgents = new ArrayList<>();

    public TraderAgentManager(IMatchingEngine matchingEngine, ExecutorService threadPool) {
        this.matchingEngine = matchingEngine;
        this.threadPool = threadPool;
    }

    @Override
    public void run() {
        LOGGER.info("Starting TraderAgents");
        for (int i = 0; i < NUMBER_OF_TRADER_AGENTS; i++) {
            double priceDeviation = PRICE_DEVIATION_FACTOR * basePrice * Math.random();
            ITraderAgent traderAgent = new ControlledAgentLiquidityProvider(basePrice, priceDeviation, BASE_VOLUME, VOLUME_DEVIATION, MAX_ORDERS);
            traderAgents.add(traderAgent);
            ITraderAgentRunner traderAgentRunner = new LocalTraderAgentRunner(traderAgent, matchingEngine);
            threadPool.execute(traderAgentRunner);
        }
    }
}
