package impl.core;

import api.core.*;
import api.core.IOrderRequestFactory;
import api.time.ITimestampProvider;
import impl.time.InstantTimestampProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchingEngine implements IMatchingEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchingEngine.class);

    private final Map<String, IOrderBook> orderBooks = new HashMap<>();

    public MatchingEngine(IMatchingEngineConfiguration matchingEngineConfiguration) {
        LOGGER.info("Initializing Matching Engine");
        initializeOrderBooks(matchingEngineConfiguration);
    }

    private void initializeOrderBooks(IMatchingEngineConfiguration matchingEngineConfiguration) {
        for (IOrderBookConfiguration orderBookConfiguration : matchingEngineConfiguration.getOrderBookConfigurations()) {
            registerOrderBook(orderBookConfiguration);
        }
    }

    @Override
    public synchronized void registerOrderBook(IOrderBookConfiguration orderBookConfiguration) {
        ITimestampProvider timestampProvider = new InstantTimestampProvider();
        IOrderRequestFactory orderRequestFactory = new OrderRequestFactory(orderBookConfiguration.getOrderBookID(), timestampProvider, orderBookConfiguration.getRoundDecimalPlaces());
        IOrderLookupCache orderLookupCache = new OrderLookupCache();
        EventDataStore eventDataStore = new EventDataStore();
        IOrderBook orderBook = new OrderBook(orderBookConfiguration.getOrderBookID(), orderRequestFactory, orderLookupCache, timestampProvider, eventDataStore);
        orderBooks.put(orderBookConfiguration.getOrderBookID(), orderBook);
    }

    @Override
    public synchronized boolean containsOrderBook(String bookID) {
        return orderBooks.containsKey(bookID);
    }

    @Override
    public synchronized IOrderBook getOrderBook(String bookID) {
        return orderBooks.get(bookID);
    }

    @Override
    public synchronized List<IOrderBook> getAllOrderBooks() {
        return new ArrayList<>(orderBooks.values());
    }

}
