package charts;

import api.core.IEventDataStore;
import api.core.IMatchingEngine;
import api.messages.internal.info.IOrderBookInfo;
import api.sides.Side;

import javax.swing.*;
import java.awt.*;

public class MatchingEngineChartAnalytics implements Runnable {

    private static final int REFRESH_RATE_MS = 1000;

    private final IMatchingEngine matchingEngine;
    private final DynamicChart eventDataChart, tradeDataChart;
    private int placeOrderCnt = 0;
    private int cancelOrderCnt = 0;
    private int closedOrderCnt = 0;
    private int tradeCnt = 0;

    public MatchingEngineChartAnalytics(IMatchingEngine matchingEngine) {
        this.matchingEngine = matchingEngine;

        JFrame frame = new JFrame("MatchingEngine Analytics");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(Color.DARK_GRAY);
        frame.setLayout(new GridLayout(1, 2));

        eventDataChart = new DynamicChart("Events per Second", new String[]{"PlaceOrder", "CancelOrder", "ClosedOrder", "Trade"});
        frame.add(eventDataChart);

        tradeDataChart = new DynamicChart("Trade Data", new String[]{"Buy", "Trade", "Sell"});
        frame.add(tradeDataChart);

        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(REFRESH_RATE_MS);
                updateEventDataChart();
                updateTradeDataChart();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void updateEventDataChart() {
        // New cnts
        IEventDataStore eventDataStore = matchingEngine.getOrderBook().getEventDataStore();
        int deltaPlaceOrderCnt = (int) (eventDataStore.getPlaceOrderCnt() - placeOrderCnt);
        int deltaCancelOrderCnt = (int) (eventDataStore.getCancelOrderCnt() - cancelOrderCnt);
        int deltaCloseOrderCnt = (int) (eventDataStore.getClosedOrderCnt() - closedOrderCnt);
        int deltaTradeCnt = (int) (eventDataStore.getTradeCnt() - tradeCnt);
        // Update old cnts
        placeOrderCnt = (int) (eventDataStore.getPlaceOrderCnt());
        cancelOrderCnt = (int) (eventDataStore.getCancelOrderCnt());
        closedOrderCnt = (int) (eventDataStore.getClosedOrderCnt());
        tradeCnt = (int) (eventDataStore.getTradeCnt());
        // Publish
        eventDataChart.update(new float[] {deltaPlaceOrderCnt, deltaCancelOrderCnt, deltaCloseOrderCnt, deltaTradeCnt});
    }

    private void updateTradeDataChart() {
        IOrderBookInfo orderBookInfo = matchingEngine.getOrderBook().getInfo();
        float buyPrice = (float) orderBookInfo.getBestPrice(Side.BUY);
        float lastTradePrice = (float) orderBookInfo.getLastTradePrice();
        float sellPrice = (float) orderBookInfo.getBestPrice(Side.SELL);
        tradeDataChart.update(new float[]{buyPrice, lastTradePrice, sellPrice});
    }

}
