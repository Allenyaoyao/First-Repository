package com.knapsack.ui;

import com.knapsack.entity.Item;
import com.knapsack.entity.ItemSet;
import com.knapsack.entity.KnapsackInstance;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * 散点图绘制工具类 (UI 优化版)
 */
public class ScatterPlotViewer extends JFrame {

    public ScatterPlotViewer(KnapsackInstance instance) {
        super("D{0-1}KP 散点图 - " + instance.getName());

        // 1. 创建数据集
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries("物品点阵");

        for (ItemSet itemSet : instance.getItemSets()) {
            for (Item item : itemSet.getItems()) {
                series.add(item.getWeight(), item.getProfit());
            }
        }
        dataset.addSeries(series);

        // 2. 利用工厂类创建基础散点图
        JFreeChart scatterPlot = ChartFactory.createScatterPlot(
                instance.getName() + " 数据散点图",
                "重量 (Weight)",
                "价值 (Profit)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        // ================= 开始 UI 深度优化 =================
        
        // 开启抗锯齿，让图像和文字更圆滑
        scatterPlot.setAntiAlias(true);

        // 设置现代中文字体
        Font titleFont = new Font("Microsoft YaHei", Font.BOLD, 20);
        Font labelFont = new Font("Microsoft YaHei", Font.PLAIN, 14);
        Font tickFont = new Font("Microsoft YaHei", Font.PLAIN, 12);

        scatterPlot.getTitle().setFont(titleFont);
        scatterPlot.getLegend().setItemFont(labelFont);

        // 获取图表的绘制区域 (Plot)
        XYPlot plot = scatterPlot.getXYPlot();
        
        // 优化背景与网格线：纯白背景 + 极浅灰色网格
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(new Color(230, 230, 230));
        plot.setRangeGridlinePaint(new Color(230, 230, 230));
        plot.setOutlinePaint(null); // 去掉图表区域的黑色边框

        // 应用坐标轴字体
        plot.getDomainAxis().setLabelFont(labelFont);
        plot.getDomainAxis().setTickLabelFont(tickFont);
        plot.getRangeAxis().setLabelFont(labelFont);
        plot.getRangeAxis().setTickLabelFont(tickFont);

        // 优化散点的样式 (Renderer)
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        // 设置点的颜色：钢蓝色 (Steel Blue)，并加上 Alpha 值 (150) 实现半透明效果
        renderer.setSeriesPaint(0, new Color(70, 130, 180, 150)); 
        // 将点的形状修改为稍大一点的圆形 (直径 6px)
        renderer.setSeriesShape(0, new Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0));
        
        // ================= UI 优化结束 =================

        // 3. 将图表装入面板
        ChartPanel chartPanel = new ChartPanel(scatterPlot);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // 增加外边距留白
        setContentPane(chartPanel);

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public static void display(KnapsackInstance instance) {
        SwingUtilities.invokeLater(() -> {
            ScatterPlotViewer viewer = new ScatterPlotViewer(instance);
            viewer.setVisible(true);
        });
    }
}