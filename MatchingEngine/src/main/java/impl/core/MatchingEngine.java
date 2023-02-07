package impl.core;

import api.core.IEventDataStore;
import api.core.IMatchingEngine;
import api.core.IOrderBook;
import api.core.IOrderLookupCache;
import api.messages.util.IOrderRequestFactory;
import api.time.ITimestampProvider;
import impl.messages.util.OrderRequestFactory;
import impl.time.InstantTimestampProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MatchingEngine implements IMatchingEngine {

    private static final Logger LOGGER = LogManager.getLogger(MatchingEngine.class);

    private final IOrderBook orderBook;
    private final IOrderRequestFactory orderRequestFactory;
    private final IEventDataStore eventDataStore;

    public MatchingEngine() {
        LOGGER.info("Initializing Matching Engine");
        IOrderLookupCache orderLookupCache = new OrderLookupCache();
        ITimestampProvider timestampProvider = new InstantTimestampProvider();
        orderRequestFactory = new OrderRequestFactory(timestampProvider);
        eventDataStore = new EventDataStore();
        orderBook = new OrderBook(orderLookupCache, timestampProvider, eventDataStore);
    }

    @Override
    public IOrderBook getOrderBook() {
        return orderBook;
    }

    @Override
    public IOrderRequestFactory getOrderRequestFactory() {
        return orderRequestFactory;
    }

    @Override
    public IEventDataStore getEventDataStore() {
        return eventDataStore;
    }

}
