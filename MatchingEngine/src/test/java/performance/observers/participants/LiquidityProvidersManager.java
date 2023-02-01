package performance.observers.participants;

import api.core.IOrderBook;
import api.core.IOrderRequestFactory;
import performance.PerformanceDataStore;

import java.util.ArrayList;
import java.util.List;

public class LiquidityProvidersManager implements Runnable {

    private static final int NUMBER_OF_LIQUIDITY_PROVIDERS = 1000;
    private static final int PRICE_ADJUSTMENT_SLEEP_TIME_MS = 500;

    private static double basePrice = 10;
    private static final double PRICE_DEVIATION_FACTOR = 0.15;
    private static final int BASE_VOLUME = 51;
    private static final int VOLUME_DEVIATION = 10;

    private final IOrderBook orderBook;
    private final IOrderRequestFactory orderRequestFactory;
    private final PerformanceDataStore performanceDataStore;
    private final List<LiquidityProvider> liquidityProviders = new ArrayList<>();

    public LiquidityProvidersManager(IOrderBook orderBook, IOrderRequestFactory orderRequestFactory, PerformanceDataStore performanceDataStore) {
        this.orderBook = orderBook;
        this.orderRequestFactory = orderRequestFactory;
        this.performanceDataStore = performanceDataStore;
    }

    @Override
    public void run() {
        for (int i = 0; i < NUMBER_OF_LIQUIDITY_PROVIDERS; i++) {
            double priceDeviation = PRICE_DEVIATION_FACTOR * basePrice * Math.random();
            LiquidityProvider liquidityProvider = new LiquidityProvider(orderBook, orderRequestFactory, performanceDataStore, basePrice, priceDeviation, BASE_VOLUME, VOLUME_DEVIATION);
            liquidityProviders.add(liquidityProvider);
            Thread liquidityProviderThread = new Thread(liquidityProvider);
            liquidityProviderThread.start();
        }

        while(true) {
            try {
                Thread.sleep(PRICE_ADJUSTMENT_SLEEP_TIME_MS);
                if (Math.random() > 0.5) {
                    basePrice = basePrice * (1 + Math.random() * 0.05);
                } else {
                    basePrice = basePrice * (1 - Math.random() * (1 - 1 / 1.05));
                }
                for(LiquidityProvider liquidityProvider : liquidityProviders) {
                    adjustBasePrice(liquidityProvider);
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void adjustBasePrice(LiquidityProvider liquidityProvider) {
        double basePrice = liquidityProvider.getPriceBase();
        if (basePrice > LiquidityProvidersManager.basePrice) {
            liquidityProvider.updatePriceBase(basePrice * (1 - Math.random() * (1 - 1 / 1.05)));
        } else {
            liquidityProvider.updatePriceBase(basePrice * (1 + Math.random() * 0.05));
        }
    }

}
