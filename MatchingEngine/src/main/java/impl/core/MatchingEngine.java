package impl.core;

import api.core.IMatchingEngine;
import api.core.IOrderBook;
import api.core.IOrderLookupCache;
import api.time.ITimestampProvider;
import impl.time.InstantTimestampProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MatchingEngine implements IMatchingEngine {

    private static final Logger LOGGER = LogManager.getLogger(MatchingEngine.class);

    private final IOrderBook orderBook;

    public MatchingEngine() {
        LOGGER.info("Initializing Matching Engine");
        IOrderLookupCache orderLookupCache = new OrderLookupCache();
        ITimestampProvider timestampProvider = new InstantTimestampProvider();
        orderBook = new OrderBook(orderLookupCache, timestampProvider);
    }

    @Override
    public IOrderBook getOrderBook() {
        return orderBook;
    }

}
