package charts;

import api.core.IEventDataStore;
import api.core.IMatchingEngine;
import api.core.IOrderBook;
import api.messages.info.IOrderBookInfo;
import api.core.Side;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MatchingEngineChartAnalytics implements Runnable {

    private static final int REFRESH_RATE_MS = 1000;
    private static final int FRAME_WIDTH = 1200;
    private static final int FRAME_HEIGHT = 600;

    private final IMatchingEngine matchingEngine;
    private final Map<String, JFrame> frames = new HashMap<>();
    private final Map<String, DynamicChart> eventDataCharts = new HashMap<>();
    private final Map<String, DynamicChart> tradeDataCharts = new HashMap<>();

    public MatchingEngineChartAnalytics(IMatchingEngine matchingEngine) {
        this.matchingEngine = matchingEngine;
        initDynamicCharts(matchingEngine);
    }

    private void initDynamicCharts(IMatchingEngine matchingEngine) {
        for (IOrderBook orderBook : matchingEngine.getAllOrderBooks()) {
            String bookID = orderBook.getBookID();
            JFrame frame = new JFrame(bookID);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setBackground(Color.DARK_GRAY);
            frame.setLayout(new GridLayout(1, 2));

            DynamicChart eventDataChart = new DynamicChart("Events", new String[]{"PlaceOrder", "CancelOrder", "ClosedOrder", "Trade"});
            frame.add(eventDataChart);
            eventDataCharts.put(bookID, eventDataChart);

            DynamicChart tradeDataChart = new DynamicChart("Trade Data", new String[]{"Buy", "Trade", "Sell"});
            frame.add(tradeDataChart);
            tradeDataCharts.put(bookID, tradeDataChart);

            frame.pack();
            frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
            frame.setVisible(true);
            frames.put(bookID, frame);
        }
    }

    @Override
    public void run() {
        while(true) {
            try {
                updateEventDataCharts();
                updateTradeDataCharts();
                Thread.sleep(REFRESH_RATE_MS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void updateEventDataCharts() {
        for (IOrderBook orderBook : matchingEngine.getAllOrderBooks()) {
            String bookID = orderBook.getBookID();
            IEventDataStore eventDataStore = orderBook.getEventDataStore();
            DynamicChart eventDataChart = eventDataCharts.get(bookID);
            eventDataChart.update(new float[]
                    {
                            eventDataStore.getAndResetPlaceOrderCnt(),
                            eventDataStore.getAndResetCancelOrderCnt(),
                            eventDataStore.getAndResetClosedOrderCnt(),
                            eventDataStore.getAndResetTradeCnt()
                    }
            );
        }
    }

    private void updateTradeDataCharts() {
        for (IOrderBook orderBook : matchingEngine.getAllOrderBooks()) {
            String bookID = orderBook.getBookID();
            IOrderBookInfo orderBookInfo = orderBook.getInfo();
            DynamicChart tradeDataChart = tradeDataCharts.get(bookID);
            float buyPrice = (float) orderBookInfo.getBestPrice(Side.BUY);
            float lastTradePrice = (float) orderBookInfo.getLastTradePrice();
            float sellPrice = (float) orderBookInfo.getBestPrice(Side.SELL);
            tradeDataChart.update(new float[]{buyPrice, lastTradePrice, sellPrice});
        }
    }

}
