package performance;

import impl.core.OrderBook;
import impl.core.OrderLookupCache;
import impl.core.OrderRequestFactory;
import impl.util.InstantTimestampProvider;

public class PerformanceMain {

    private static final int NUMBER_OF_LIQUIDITY_PROVIDERS = 10;
    private static final double BASE_PRICE = 101;
    private static final double PRICE_DEVIATION = 100;
    private static final int BASE_VOLUME = 51;
    private static final int VOLUME_DEVIATION = 50;
    private static final int OBSERVER_WAIT_TIME_MS = 3000;


    public static void main(String[] args) {

        OrderLookupCache orderLookupCache = new OrderLookupCache();
        InstantTimestampProvider timestampProvider = new InstantTimestampProvider();
        OrderBook orderBook = new OrderBook(orderLookupCache, timestampProvider);
        OrderRequestFactory orderRequestFactory = new OrderRequestFactory(timestampProvider);

        for (int i = 0; i < NUMBER_OF_LIQUIDITY_PROVIDERS; i++) {
            LiquidityProvider liquidityProvider = new LiquidityProvider(orderBook, orderRequestFactory, BASE_PRICE, PRICE_DEVIATION, BASE_VOLUME, VOLUME_DEVIATION);
            Thread liquidityProviderThread = new Thread(liquidityProvider);
            liquidityProviderThread.start();
        }

        OrderBookObserver orderBookObserver = new OrderBookObserver(orderBook, OBSERVER_WAIT_TIME_MS);
        Thread orderBookObserverThread = new Thread(orderBookObserver);
        orderBookObserverThread.start();

    }

}
