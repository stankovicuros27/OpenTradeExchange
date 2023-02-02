package performance.observers.chart;

import api.messages.info.IOrderBookInfo;
import api.sides.Side;
import impl.core.OrderBook;
import performance.PerformanceDataStore;

import javax.swing.*;
import java.awt.*;

public class OrderBookChartObserver implements Runnable {

    private final OrderBook orderBook;
    private final PerformanceDataStore performanceDataStore;
    private final int waitTimeMs;
    private final DynamicChart eventsPerSecondChart, tradeDataChart;

    public OrderBookChartObserver(OrderBook orderBook, PerformanceDataStore performanceDataStore, int waitTimeMs) {
        this.orderBook = orderBook;
        this.performanceDataStore = performanceDataStore;
        this.waitTimeMs = waitTimeMs;

        JFrame frame = new JFrame("MatchingEngine Performance Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(Color.DARK_GRAY);
        frame.setLayout(new GridLayout(1, 2));

        eventsPerSecondChart = new DynamicChart("Events", new String[]{"PlaceOrder", "CancelOrder", "ClosedOrder", "Trade"});
        frame.add(eventsPerSecondChart);

        tradeDataChart = new DynamicChart("Trade Data", new String[]{"Buy", "Trade", "Sell"});
        frame.add(tradeDataChart);

        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(waitTimeMs);
                updateEventsPerSecondChart();
                updateTradeDataChart();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void updateEventsPerSecondChart() {
        float factor = (float) (1.0 / (waitTimeMs / 1000.0));
        float placeOrderCnt = (float) performanceDataStore.getAndResetPlaceOrderCnt() * factor;
        float cancelOrderCnt = (float) performanceDataStore.getAndResetCancelOrderCnt() * factor;
        float closedOrderCnt = (float) performanceDataStore.getAndResetClosedOrderCnt() * factor;
        float tradeCnt = (float) performanceDataStore.getAndResetTradeCnt() * factor;
        eventsPerSecondChart.update(new float[] {placeOrderCnt, cancelOrderCnt, closedOrderCnt, tradeCnt});
    }

    private void updateTradeDataChart() {
        IOrderBookInfo orderBookInfo = orderBook.getInfo();
        float buyPrice = (float) orderBookInfo.getBestPrice(Side.BUY);
        // TODO
        //float lastTradePrice = (float) performanceDataStore.getLastTradePrice();
        float sellPrice = (float) orderBookInfo.getBestPrice(Side.SELL);
        tradeDataChart.update(new float[]{buyPrice, (buyPrice + sellPrice) / 2, sellPrice});
    }

}
