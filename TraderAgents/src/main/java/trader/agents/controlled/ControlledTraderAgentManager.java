package trader.agents.controlled;

import api.core.IMatchingEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import trader.agents.ITraderAgentManager;
import trader.runners.ITraderAgentRunner;
import trader.runners.TCPTraderAgentRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class ControlledTraderAgentManager implements ITraderAgentManager {

    private static final Logger LOGGER = LogManager.getLogger(ControlledTraderAgentManager.class);

    private static final int NUMBER_OF_TRADER_AGENTS = 5;
    private static final int PRICE_ADJUSTMENT_SLEEP_TIME_MS = 1000;
    private static double basePrice = 100;
    private static final double PRICE_DEVIATION_FACTOR = 0.25;
    private static final int BASE_VOLUME = 51;
    private static final int VOLUME_DEVIATION = 30;
    private static final int MAX_ORDERS = 100;

    private final IMatchingEngine matchingEngine;
    private final ExecutorService threadPool;
    private final List<ControlledTraderAgent> controlledTraderAgents = new ArrayList<>();

    public ControlledTraderAgentManager(IMatchingEngine matchingEngine, ExecutorService threadPool) {
        this.matchingEngine = matchingEngine;
        this.threadPool = threadPool;
    }

    @Override
    public void run() {
        LOGGER.info("Starting TraderAgents");
        for (int i = 0; i < NUMBER_OF_TRADER_AGENTS; i++) {
            double priceDeviation = PRICE_DEVIATION_FACTOR * basePrice * Math.random();
            ControlledTraderAgent traderAgent = new ControlledAgentLiquidityProvider(basePrice, priceDeviation, BASE_VOLUME, VOLUME_DEVIATION, MAX_ORDERS);
            controlledTraderAgents.add(traderAgent);
            ITraderAgentRunner traderAgentRunner = new TCPTraderAgentRunner(traderAgent, matchingEngine);
            threadPool.execute(traderAgentRunner);
        }

        while(true) {
            try {
                Thread.sleep(PRICE_ADJUSTMENT_SLEEP_TIME_MS);
                adjustBasePrice();
                for(ControlledTraderAgent controlledTraderAgent : controlledTraderAgents) {
                    adjustLiquidityProvidersBasePrice(controlledTraderAgent);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void adjustBasePrice() {
        if (Math.random() >= 0.5) {
            basePrice = basePrice * (1 + Math.random() * 0.05);
        } else {
            basePrice = basePrice * (1 - Math.random() * (1 - 1 / 1.05));
        }
    }

    private void adjustLiquidityProvidersBasePrice(ControlledTraderAgent controlledTraderAgent) {
        double agentPriceBase = controlledTraderAgent.getPriceBase();
        if (agentPriceBase > basePrice) {
            controlledTraderAgent.setPriceBase(basePrice * (1 - Math.random() * (1 - 1 / 1.05)));
        } else {
            controlledTraderAgent.setPriceBase(basePrice * (1 + Math.random() * 0.05));
        }
    }

}
