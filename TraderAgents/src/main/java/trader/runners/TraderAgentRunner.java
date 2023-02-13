package trader.runners;

import api.core.IOrderBook;
import trader.agents.ITraderAgent;

public abstract class TraderAgentRunner implements Runnable {

    protected final ITraderAgent traderAgent;
    protected final IOrderBook orderBook;
    protected final int timeoutMs;

    protected TraderAgentRunner(ITraderAgent traderAgent, IOrderBook orderBook, int timeoutMs) {
        this.traderAgent = traderAgent;
        this.orderBook = orderBook;
        this.timeoutMs = timeoutMs;
    }

}
