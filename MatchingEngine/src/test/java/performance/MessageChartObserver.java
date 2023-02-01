package performance;

import api.core.Side;
import impl.core.OrderBook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.*;

import javax.swing.*;
import java.awt.*;

public class MessageChartObserver implements Runnable {

    private final OrderBook orderBook;
    private final PerformanceDataStore performanceDataStore;
    private final int waitTimeMs;
    private final DynamicChart eventsPerSecondChart, tradeDataChart;

    public MessageChartObserver(OrderBook orderBook, PerformanceDataStore performanceDataStore, int waitTimeMs) {
        this.orderBook = orderBook;
        this.performanceDataStore = performanceDataStore;
        this.waitTimeMs = waitTimeMs;

        JFrame frame = new JFrame("MatchingEngine Performance Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(2, 1));

        eventsPerSecondChart = new DynamicChart("Events Per Second", new String[]{"Events Per Second"});
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
        float eventCnt = (float) performanceDataStore.getAndResetEventCnt();
        eventsPerSecondChart.update(new float[] {eventCnt});
    }

    private void updateTradeDataChart() {
        float buyPrice = (float) orderBook.getInfo().getBestPrice(Side.BUY);
        float lastTradePrice = (float) performanceDataStore.getLastTradePrice();
        float sellPrice = (float) orderBook.getInfo().getBestPrice(Side.SELL);
        tradeDataChart.update(new float[]{buyPrice, lastTradePrice, sellPrice});
    }

    static class DynamicChart extends JPanel {

        private final DynamicTimeSeriesCollection dataset;

        public DynamicChart(String title, String[] seriesNames) {
            dataset = new DynamicTimeSeriesCollection(seriesNames.length, 200, new Second());

            // TODO change
            dataset.setTimeBase(new Second(0, 0, 0, 1, 1, 1990)); // date 1st jan 0 mins 0 secs

            for (int i = 0; i < seriesNames.length; i++) {
                dataset.addSeries(new float[0], i, seriesNames[i]);
            }

            JFreeChart chart = ChartFactory.createTimeSeriesChart(title, "Time", title, dataset, true, true, false);
            final XYPlot plot = chart.getXYPlot();

            ValueAxis axis = plot.getDomainAxis();
            axis.setAutoRange(true);
            axis.setFixedAutoRange(30000); // proportional to scroll speed
            axis = plot.getRangeAxis();

            final ChartPanel chartPanel = new ChartPanel(chart);
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            add(chartPanel);
        }

        public void update(float[] values) {
            dataset.advanceTime();
            dataset.appendData(values);
        }

    }

}
