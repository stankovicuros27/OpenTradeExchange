package performance;

import impl.core.OrderBook;
import impl.core.OrderLookupCache;
import impl.core.OrderRequestFactory;
import impl.util.InstantTimestampProvider;

public class PerformanceMain {

    private static final int NUMBER_OF_LIQUIDITY_PROVIDERS = 1000;
    private static final double BASE_PRICE = 10;
    private static final double PRICE_DEVIATION = 1;
    private static final int BASE_VOLUME = 51;
    private static final int VOLUME_DEVIATION = 50;
    private static final int OBSERVER_WAIT_TIME_MS = 1000;


    public static void main(String[] args) {

        OrderLookupCache orderLookupCache = new OrderLookupCache();
        InstantTimestampProvider timestampProvider = new InstantTimestampProvider();
        OrderBook orderBook = new OrderBook(orderLookupCache, timestampProvider);
        OrderRequestFactory orderRequestFactory = new OrderRequestFactory(timestampProvider);
        PerformanceDataStore performanceDataStore = new PerformanceDataStore();

        for (int i = 0; i < NUMBER_OF_LIQUIDITY_PROVIDERS; i++) {
            LiquidityProvider liquidityProvider = new LiquidityProvider(orderBook, orderRequestFactory, performanceDataStore, BASE_PRICE, PRICE_DEVIATION, BASE_VOLUME, VOLUME_DEVIATION);
            Thread liquidityProviderThread = new Thread(liquidityProvider);
            liquidityProviderThread.start();
        }

        /*OrderBookLogObserver orderBookLogObserver = new OrderBookLogObserver(orderBook, OBSERVER_WAIT_TIME_MS);
        Thread orderBookObserverThread = new Thread(orderBookLogObserver);
        orderBookObserverThread.start();*/

        MessageChartObserver messageChartObserver = new MessageChartObserver(orderBook, performanceDataStore, OBSERVER_WAIT_TIME_MS);
        Thread messageChartObserverThread = new Thread(messageChartObserver);
        messageChartObserverThread.start();

    }

}
