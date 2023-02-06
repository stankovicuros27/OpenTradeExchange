package charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.DynamicTimeSeriesCollection;
import org.jfree.data.time.Second;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class DynamicChart extends JPanel {

    private final DynamicTimeSeriesCollection dataset;

    public DynamicChart(String title, String[] seriesNames) {
        setBackground(Color.DARK_GRAY);
        dataset = new DynamicTimeSeriesCollection(seriesNames.length, 200, new Second());
        dataset.setTimeBase(new Second(new Date()));
        for (int i = 0; i < seriesNames.length; i++) {
            dataset.addSeries(new float[0], i, seriesNames[i]);
        }

        JFreeChart chart = ChartFactory.createTimeSeriesChart(title, "Time", title, dataset, true, true, false);
        final XYPlot plot = chart.getXYPlot();
        XYItemRenderer renderer = plot.getRenderer();
        renderer.setDefaultStroke(new BasicStroke(3.0f));
        ((AbstractRenderer) renderer).setAutoPopulateSeriesStroke(false);

        ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        axis.setFixedAutoRange(100000);

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(Color.GRAY);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(chartPanel);
    }

    public synchronized void update(float[] values) {
        dataset.advanceTime();
        dataset.appendData(values);
    }

}
