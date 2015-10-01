package main;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.net.URL;


/**
 * Class responsible for create and show a chart with garbage statistics,
 * using JFreeChart library. After being initializated, udpate the information
 * each 5 seconds.
 *
 * @author Rui Grandão  - ei11010@fe.up.pt
 * @author Tiago Coelho - ei11012@fe.up.pt
 * @see Runnable
 */
public class Chart implements Runnable {

    private final JPanel chartPanel;
    private DefaultCategoryDataset dataset;
    private int time = 0;

    /**
     * Constructor of Chart.
     */
    public Chart() {

        dataset = new DefaultCategoryDataset();
        ApplicationFrame af = new ApplicationFrame("Statistics");
        chartPanel = createChartPanel();
        af.setContentPane(chartPanel);
        af.pack();
        af.setVisible(false);

        Thread t = new Thread(this);
        t.start();
    }

    public JPanel getChartPanel() {
        return this.chartPanel;
    }

    private JPanel createChartPanel() {

        JFreeChart chart = createChart(dataset);
        ChartPanel panel = new ChartPanel(chart);

        return panel;
    }

    private JFreeChart createChart(DefaultCategoryDataset dataset) {

        JFreeChart chart = ChartFactory.createLineChart("Total Waste Deposited", "Time", "Waste Deposited", dataset, PlotOrientation.VERTICAL, true, true, false);
        TextTitle source = new TextTitle("Legend");
        source.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        source.setPosition(RectangleEdge.BOTTOM);
        source.setHorizontalAlignment(HorizontalAlignment.CENTER);
        chart.addSubtitle(source);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setRangeGridlinesVisible(true);
        URL imageURL = null;
        if (imageURL != null) {
            ImageIcon tmp = new ImageIcon(imageURL);
            chart.setBackgroundImage(tmp.getImage());
            plot.setBackgroundPaint(null);
        }
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        ChartUtilities.applyCurrentTheme(chart);
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setBaseShapesVisible(true);
        renderer.setDrawOutlines(true);
        renderer.setUseFillPaint(true);
        renderer.setBaseFillPaint(Color.white);
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        renderer.setSeriesOutlineStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesShape(0, new Ellipse2D.Double(-5.0, -5.0, 10.0, 10.0));
        return chart;
    }


    @Override
    public void run() {

        int elapsed_time = 0;

        while (elapsed_time <= 90) {

            if (GarbageCollector.getInstance().getGenerateStatistics()) {
                elapsed_time = (time++) * 5;
                dataset.addValue(GarbageCollector.getInstance().getTotalUndWaste(), "Undifferentiated", "" + elapsed_time);
                dataset.addValue(GarbageCollector.getInstance().getTotalPaperWaste(), "Paper", "" + elapsed_time);
                dataset.addValue(GarbageCollector.getInstance().getTotalGlassWaste(), "Glass", "" + elapsed_time);
                dataset.addValue(GarbageCollector.getInstance().getTotalPlasticWaste(), "Plastic", "" + elapsed_time);
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        Thread.interrupted();
    }
}
