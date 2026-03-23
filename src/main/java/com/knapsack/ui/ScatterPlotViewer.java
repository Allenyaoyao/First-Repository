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
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * 散点图绘制工具类
 */
public class ScatterPlotViewer extends JFrame {

    public ScatterPlotViewer(KnapsackInstance instance) {
        super("数据可视化分析 - " + instance.getName());

        // 1. 创建数据集
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries("Item Node"); 

        for (ItemSet itemSet : instance.getItemSets()) {
            for (Item item : itemSet.getItems()) {
                series.add(item.getWeight(), item.getProfit());
            }
        }
        dataset.addSeries(series);

        // 2. 利用工厂类创建基础散点图 
        JFreeChart scatterPlot = ChartFactory.createScatterPlot(
                null, // 隐藏图表自带的标题，用外置的高级UI代替
                "重 量 (Weight)",
                "价 值 (Profit)",
                dataset,
                PlotOrientation.VERTICAL,
                false, // 隐藏底部的图例 (Legend)
                true,  // 开启鼠标悬浮提示(Tooltip)
                false
        );

        // ================= 图表内部的深度美化 =================
        scatterPlot.setAntiAlias(true);
        scatterPlot.setBackgroundPaint(Color.WHITE);

        XYPlot plot = scatterPlot.getXYPlot();
        // 设置极简浅灰背景色
        plot.setBackgroundPaint(new Color(248, 250, 252));
        plot.setOutlinePaint(null); // 去除四周黑框

        // 将网格线改为高级的“虚线”
        BasicStroke dashedStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, 
                BasicStroke.JOIN_MITER, 10.0f, new float[]{5.0f, 5.0f}, 0.0f);
        plot.setDomainGridlinePaint(new Color(220, 226, 230));
        plot.setRangeGridlinePaint(new Color(220, 226, 230));
        plot.setDomainGridlineStroke(dashedStroke);
        plot.setRangeGridlineStroke(dashedStroke);

        // 坐标轴字体优化
        Font labelFont = new Font("Microsoft YaHei", Font.BOLD, 14);
        Font tickFont = new Font("Microsoft YaHei", Font.PLAIN, 12);
        plot.getDomainAxis().setLabelFont(labelFont);
        plot.getDomainAxis().setTickLabelFont(tickFont);
        plot.getRangeAxis().setLabelFont(labelFont);
        plot.getRangeAxis().setTickLabelFont(tickFont);

        // 散点样式优化：科技靛蓝 + 轮廓描边
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        // 填充色：半透明的靛蓝色
        renderer.setSeriesPaint(0, new Color(99, 102, 241, 160)); 
        // 增大点的尺寸
        renderer.setSeriesShape(0, new Ellipse2D.Double(-4.0, -4.0, 8.0, 8.0));
        // 开启点阵轮廓并设置深色边框，增强重叠时的立体感
        renderer.setUseOutlinePaint(true);
        renderer.setSeriesOutlinePaint(0, new Color(79, 70, 229, 220)); 

        // ================= 开始外部容器 (Dashboard) 布局重构 =================

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        // 顶部精美标题栏
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("数据分布散点图 - " + instance.getName());
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 22));
        titleLabel.setForeground(new Color(30, 41, 59));
        
        JLabel subTitleLabel = new JLabel("背包容量 (W): " + instance.getCapacity() + "   |    提示: 鼠标悬浮点阵可查看数值，拖拽鼠标可框选放大区域");
        subTitleLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        subTitleLabel.setForeground(new Color(100, 116, 139));
        subTitleLabel.setBorder(new EmptyBorder(5, 5, 0, 0));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subTitleLabel, BorderLayout.SOUTH);

        // 中间图表区
        ChartPanel chartPanel = new ChartPanel(scatterPlot);
        chartPanel.setPreferredSize(new Dimension(850, 550));
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));

        // 组装看板
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(chartPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        // 限制最小尺寸
        setMinimumSize(new Dimension(650, 450)); 
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