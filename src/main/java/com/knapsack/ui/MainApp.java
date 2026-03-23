package com.knapsack.ui;
import com.formdev.flatlaf.FlatLightLaf;
import com.knapsack.entity.KnapsackInstance;
import com.knapsack.io.DataReader;
import com.knapsack.core.DPSolver;
import com.knapsack.core.DataProcessor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class MainApp extends JFrame {

    private JComboBox<String> fileBox, instanceBox;
    private JButton loadBtn, sortBtn, plotBtn, solveBtn, exportBtn;
    private JTextArea logArea;
    private List<KnapsackInstance> currentInstances;

    public MainApp() {
        // 设置窗口基础属性
        setTitle("D{0-1}KP 智能求解分析系统 v2.0");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 1. 初始化顶层容器 (带边距)
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // 2. 左侧控制面板 (功能区)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(280, 0));
        leftPanel.setBackground(new Color(245, 245, 247));
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(15, 15, 15, 15)));

        // --- 模块 A: 数据源 ---
        leftPanel.add(createSectionLabel("数据源选择"));
        String[] files = {"idkp1-10.txt", "sdkp1-10.txt", "udkp1-10.txt", "wdkp1-10.txt"};
        fileBox = new JComboBox<>(files);
        loadBtn = new JButton("加载本地文件");
        loadBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        leftPanel.add(fileBox);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(loadBtn);
        leftPanel.add(Box.createVerticalStrut(25));

        // --- 模块 B: 实例选择 ---
        leftPanel.add(createSectionLabel("选择具体实例"));
        instanceBox = new JComboBox<>(new String[]{"等待加载..."});
        leftPanel.add(instanceBox);
        leftPanel.add(Box.createVerticalStrut(25));

        // --- 模块 C: 功能操作 ---
        leftPanel.add(createSectionLabel("核心操作"));
        sortBtn = createStyledButton("比值降序排序", new Color(100, 149, 237));
        plotBtn = createStyledButton("绘制动态散点图", new Color(60, 179, 113));
        solveBtn = createStyledButton("开始 DP 求解", new Color(255, 140, 0));
        exportBtn = createStyledButton("导出实验报告", new Color(105, 105, 105));
        
        sortBtn.setEnabled(false);
        plotBtn.setEnabled(false);
        solveBtn.setEnabled(false);
        exportBtn.setEnabled(false);

        leftPanel.add(sortBtn);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(plotBtn);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(solveBtn);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(exportBtn);

        // 3. 右侧信息输出区
        JPanel rightPanel = new JPanel(new BorderLayout());
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        logArea.setMargin(new Insets(10, 10, 10, 10));
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(new Color(50, 255, 50)); // 经典黑客绿，方便区分报告内容
        
        // 👇 就是加上下面这两行！
        logArea.setLineWrap(true);       // 开启自动换行
        logArea.setWrapStyleWord(true);  // 开启断字换行（保证单词或标点不会被硬切成两半）
        
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createTitledBorder(" 实时运行日志 & 求解报告 "));
        rightPanel.add(scroll, BorderLayout.CENTER);

     // 👇 替换为支持鼠标拖拽改变宽度的 JSplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(280); // 设置初始分割线的位置
        splitPane.setDividerSize(8);       // 设置分割线的宽度（方便鼠标抓住）
        splitPane.setContinuousLayout(true); // 拖拽时画面实时平滑更新
        splitPane.setBorder(null);         // 去除自带的丑陋边框
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        
        log("[System]  ***欢迎使用 D{0-1}KP 智能求解分析系统 v2.0***");
        log("[System] ===========================================");
        log("[System] 当前状态: 系统已就绪，等待数据载入...");
        log("[System]   操作指引:");
        log("[System]    1. 请在左侧选择目标数据集 (如 idkp1-10.txt)");
        log("[System]    2. 点击【加载本地文件】按钮进行预处理");
        log("[System]    3. 解锁核心操作菜单，开启您的动态规划分析之旅！\n");

        bindEvents();
    }

    // 辅助方法：创建美化标签
    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        label.setBorder(new EmptyBorder(0, 0, 8, 0));
        return label;
    }

    // 辅助方法：创建美化按钮
    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(250, 40));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        return btn;
    }

    private void bindEvents() {
        loadBtn.addActionListener(e -> {
            String path = "src/main/resources/" + fileBox.getSelectedItem();
            log("[System] 正在读取文件: " + path);
            currentInstances = DataReader.readDataFile(path);
            if (currentInstances != null) {
                instanceBox.removeAllItems();
                currentInstances.forEach(i -> instanceBox.addItem(i.getName()));
                log("[Success] 成功解析 " + currentInstances.size() + " 组数据集！");
                
                sortBtn.setEnabled(true);
                plotBtn.setEnabled(true);
                solveBtn.setEnabled(true);
                exportBtn.setEnabled(true);
            }
        });

        sortBtn.addActionListener(e -> {
            KnapsackInstance inst = getSelected();
            if (inst != null) {
                DataProcessor.sortByThirdItemRatio(inst);
                log("[Action] 实例 " + inst.getName() + " 已按价值重量比降序重新排列。");
            }
        });

        plotBtn.addActionListener(e -> {
            KnapsackInstance inst = getSelected();
            if (inst != null) ScatterPlotViewer.display(inst);
        });

        solveBtn.addActionListener(e -> {
            KnapsackInstance inst = getSelected();
            if (inst != null) {
                log("[Solve] 正在启动 DP 引擎...");
                String report = DPSolver.solve(inst);
                log(report);
            }
        });

        exportBtn.addActionListener(e -> {
            KnapsackInstance inst = getSelected();
            if (inst != null) {
                String report = DPSolver.solve(inst); // 先算出结果
                JFileChooser jfc = new JFileChooser();
                jfc.setDialogTitle("导出 Excel 报告");
                // 后缀名改成.xlsx
                jfc.setSelectedFile(new File(inst.getName() + "_实验报告.xlsx")); 
                
                if (jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        File fileToSave = jfc.getSelectedFile();
                        com.knapsack.io.ExcelExporter.exportToExcel(report, fileToSave);
                        log("[Export] 🎉 Excel报告已成功导出至: " + fileToSave.getPath());
                    } catch (Exception ex) {
                        log("[Error] ❌ 导出失败: " + ex.getMessage());
                        ex.printStackTrace(); // 在后台打印具体错误方便排查
                    }
                }
            }
        });
    }

    private KnapsackInstance getSelected() {
        if (currentInstances == null) {
            JOptionPane.showMessageDialog(this, "请先点击‘加载本地文件’！");
            return null;
        }
        return currentInstances.get(instanceBox.getSelectedIndex());
    }

    private void log(String msg) {
        logArea.append(msg + "\n");
    }

    public static void main(String[] args) {
        FlatLightLaf.setup(); 
        SwingUtilities.invokeLater(() -> new MainApp().setVisible(true));
    }
}