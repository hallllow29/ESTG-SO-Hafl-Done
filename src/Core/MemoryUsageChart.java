package Core;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.util.List;

public class MemoryUsageChart extends JFrame {

    public MemoryUsageChart(String title, List<Integer> memoryUsageData) {
        super(title);

        DefaultCategoryDataset dataset = createDataset(memoryUsageData);
        JFreeChart chart = ChartFactory.createLineChart(
                "Memory Usage Over Time",
                "Time",
                "Memory Usage (MB)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        setContentPane(chartPanel);
    }

    private DefaultCategoryDataset createDataset(List<Integer> memoryUsageData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < memoryUsageData.size(); i++) {
            dataset.addValue(memoryUsageData.get(i), "Memory Usage", Integer.toString(i));
        }
        return dataset;
    }
}