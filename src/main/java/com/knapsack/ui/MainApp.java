package com.knapsack.ui;

import com.knapsack.entity.KnapsackInstance;
import com.knapsack.io.DataReader;
import com.knapsack.core.DPSolver;
import com.knapsack.core.DataProcessor;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class MainApp extends JFrame {

    private JComboBox<String> fileComboBox;
    private JComboBox<String> instanceComboBox;
    private JButton loadButton, sortButton, plotButton, solveButton, exportButton;
    private JTextArea consoleArea;
    private List<KnapsackInstance> currentInstances;

    public MainApp() {
        super("D{0-1}KP 算法求解与分析系统 (完全版)");

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        String[] files = {"idkp1-10.txt", "sdkp1-10.txt", "udkp1-10.txt", "wdkp1-10.txt"};
        fileComboBox = new JComboBox<>(files);
        loadButton = new JButton("加载文件");
        instanceComboBox = new JComboBox<>();
        instanceComboBox.addItem("请先加载文件...");
        
        topPanel.add(new JLabel("1. 选择数据文件:"));
        topPanel.add(fileComboBox);
        topPanel.add(loadButton);
        topPanel.add(new JLabel("  2. 选择具体实例:"));
        topPanel.add(instanceComboBox);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        sortButton = new JButton("按比值降序排序");
        plotButton = new JButton("绘制数据散点图");
        solveButton = new JButton("运行 DP 求解最优解");
        exportButton = new JButton("导出结果为 TXT"); // 【新增的导出按钮】
        
        actionPanel.add(sortButton);
        actionPanel.add(plotButton);
        actionPanel.add(solveButton);
        actionPanel.add(exportButton);

        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.add(topPanel, BorderLayout.NORTH);
        controlPanel.add(actionPanel, BorderLayout.CENTER);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        consoleArea = new JTextArea(15, 60);
        consoleArea.setEditable(false);
        consoleArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(consoleArea);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(controlPanel, BorderLayout.NORTH);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        bindEvents();

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void bindEvents() {
        loadButton.addActionListener(e -> {
            String selectedFile = (String) fileComboBox.getSelectedItem();
            String path = "src/main/resources/" + selectedFile;
            log("正在加载文件: " + path + " ...");
            currentInstances = DataReader.readDataFile(path);
            if (currentInstances != null && !currentInstances.isEmpty()) {
                log("成功加载 " + currentInstances.size() + " 组数据！");
                instanceComboBox.removeAllItems();
                for (KnapsackInstance inst : currentInstances) {
                    instanceComboBox.addItem(inst.getName());
                }
            } else {
                log("读取失败或文件为空！请检查路径。");
            }
        });

        sortButton.addActionListener(e -> {
            KnapsackInstance selected = getSelectedInstance();
            if (selected != null) {
                DataProcessor.sortByThirdItemRatio(selected);
                log("已成功对实例 [" + selected.getName() + "] 进行了降序排序！");
            }
        });

        plotButton.addActionListener(e -> {
            KnapsackInstance selected = getSelectedInstance();
            if (selected != null) {
                log("正在启动图形引擎绘制 [" + selected.getName() + "] 散点图...");
                com.knapsack.ui.ScatterPlotViewer.display(selected);
            }
        });

        solveButton.addActionListener(e -> {
            KnapsackInstance selected = getSelectedInstance();
            if (selected != null) {
                log("开始计算 [" + selected.getName() + "] ...");
                String result = DPSolver.solve(selected); // 拿到结果报告
                log(result); // 打印到界面的文本框里
            }
        });

        // 【新增的导出功能逻辑】
        exportButton.addActionListener(e -> {
            KnapsackInstance selected = getSelectedInstance();
            if (selected != null) {
                // 先计算出结果
                String result = DPSolver.solve(selected);
                
                // 弹出 Windows 原生的文件保存窗口
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("保存求解结果");
                // 默认保存文件名，比如 UDKP1_Result.txt
                fileChooser.setSelectedFile(new File(selected.getName() + "_Result.txt"));
                
                int userSelection = fileChooser.showSaveDialog(MainApp.this);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    try (FileWriter fw = new FileWriter(fileToSave)) {
                        fw.write(result);
                        log("🎉 成功！最优解、求解时间已成功保存至: " + fileToSave.getAbsolutePath());
                    } catch (Exception ex) {
                        log("❌ 保存失败: " + ex.getMessage());
                    }
                }
            }
        });
    }

    private KnapsackInstance getSelectedInstance() {
        if (currentInstances == null || currentInstances.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请先加载数据文件！", "提示", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int index = instanceComboBox.getSelectedIndex();
        if (index >= 0 && index < currentInstances.size()) {
            return currentInstances.get(index);
        }
        return null;
    }

    private void log(String message) {
        consoleArea.append(message + "\n");
        consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainApp app = new MainApp();
            app.setVisible(true);
        });
    }
}