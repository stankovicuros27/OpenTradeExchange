package performance;

import impl.core.OrderBook;
import impl.core.OrderLookupCache;
import impl.core.OrderRequestFactory;
import impl.util.InstantTimestampProvider;
import performance.observers.chart.OrderBookChartObserver;
import performance.observers.console.OrderBookConsoleObserver;
import performance.observers.participants.LiquidityProvidersManager;

public class PerformanceMain {

    private static final int OBSERVER_WAIT_TIME_MS = 1000;


    public static void main(String[] args) {
        OrderLookupCache orderLookupCache = new OrderLookupCache();
        InstantTimestampProvider timestampProvider = new InstantTimestampProvider();
        OrderBook orderBook = new OrderBook(orderLookupCache, timestampProvider);
        OrderRequestFactory orderRequestFactory = new OrderRequestFactory(timestampProvider);
        PerformanceDataStore performanceDataStore = new PerformanceDataStore();

        LiquidityProvidersManager liquidityProvidersManager = new LiquidityProvidersManager(orderBook, orderRequestFactory, performanceDataStore);
        Thread liquidityProvidersManagerThread = new Thread(liquidityProvidersManager);
        liquidityProvidersManagerThread.start();

        OrderBookConsoleObserver orderBookConsoleObserver = new OrderBookConsoleObserver(orderBook, OBSERVER_WAIT_TIME_MS);
        Thread orderBookObserverThread = new Thread(orderBookConsoleObserver);
        orderBookObserverThread.start();

        OrderBookChartObserver orderBookChartObserver = new OrderBookChartObserver(orderBook, performanceDataStore, OBSERVER_WAIT_TIME_MS);
        Thread messageChartObserverThread = new Thread(orderBookChartObserver);
        messageChartObserverThread.start();
    }

}
