package com.knapsack.io;

import com.knapsack.entity.Item;
import com.knapsack.entity.ItemSet;
import com.knapsack.entity.KnapsackInstance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据读取工具类
 * 负责解析 txt 文件并组装成面向对象的 KnapsackInstance 列表
 */
public class DataReader {

    public static List<KnapsackInstance> readDataFile(String filePath) {
        List<KnapsackInstance> instances = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String currentName = "";
            int currentCapacity = 0;
            
            // 使用 StringBuilder 把跨行的数据全部拼接到一起
            StringBuilder profitBuilder = new StringBuilder();
            StringBuilder weightBuilder = new StringBuilder();

            int readState = 0; // 0: 寻找名字, 1: 读价值, 2: 读重量

            while ((line = br.readLine()) != null) {
                line = line.trim();
                
                // 跳过开头的带星号的注释行
                if (line.startsWith("*")) {
					continue;
				}

                // 遇到空行，说明一组数据（如UDKP1）可能读取结束了，尝试将其打包
                if (line.isEmpty()) {
                    if (!currentName.isEmpty() && profitBuilder.length() > 0 && weightBuilder.length() > 0) {
                        instances.add(buildInstance(currentName, currentCapacity, profitBuilder.toString(), weightBuilder.toString()));
                        // 重置状态，准备读取下一组
                        currentName = "";
                        profitBuilder.setLength(0);
                        weightBuilder.setLength(0);
                        readState = 0;
                    }
                    continue;
                }

                // 1. 匹配实例名称 (正则匹配大写字母+数字+冒号，例如 UDKP1:)
                if (line.matches("^[A-Z]+\\d+:$")) {
                    currentName = line.replace(":", "");
                    readState = 0;
                }
                // 2. 匹配背包容量 (提取数字)
                else if (line.contains("cubage of knapsack is")) {
                    String[] parts = line.split("is");
                    if (parts.length > 1) {
                        // 利用正则把非数字的字符（比如句号）全部替换掉，只保留数字
                        String numStr = parts[1].replaceAll("[^0-9]", ""); 
                        currentCapacity = Integer.parseInt(numStr);
                    }
                }
                // 3. 匹配到价值开头 (容错处理，避开原文件的拼写错误)
                else if (line.contains("profit of")) {
                    readState = 1;
                }
                // 4. 匹配到重量开头
                else if (line.contains("weight of")) {
                    readState = 2;
                }
                // 5. 拼接待解析的数字行
                else {
                    if (readState == 1) {
                        profitBuilder.append(line);
                        // 确保拼接的地方有逗号分隔
                        if (!line.endsWith(",")) {
							profitBuilder.append(",");
						}
                    } else if (readState == 2) {
                        weightBuilder.append(line);
                        if (!line.endsWith(",")) {
							weightBuilder.append(",");
						}
                    }
                }
            }

            // 处理文件末尾最后一组数据（因为文件最后可能没有空行）
            if (!currentName.isEmpty() && profitBuilder.length() > 0 && weightBuilder.length() > 0) {
                instances.add(buildInstance(currentName, currentCapacity, profitBuilder.toString(), weightBuilder.toString()));
            }

        } catch (IOException e) {
            System.err.println("读取文件失败，请检查路径：" + filePath);
            e.printStackTrace();
        }

        return instances;
    }

    /**
     * 将长字符串切割并组装成面向对象的数据
     */
    private static KnapsackInstance buildInstance(String name, int capacity, String profitStr, String weightStr) {
        String[] profits = profitStr.split(",");
        String[] weights = weightStr.split(",");

        List<Integer> profitList = parseStringToIntList(profits);
        List<Integer> weightList = parseStringToIntList(weights);

        List<ItemSet> itemSets = new ArrayList<>();

        // 按题目要求，每 3 个物品组成一个“项集”(ItemSet)
        int minLen = Math.min(profitList.size(), weightList.size());
        for (int i = 0; i < minLen - 2; i += 3) {
            Item item1 = new Item(profitList.get(i), weightList.get(i));
            Item item2 = new Item(profitList.get(i + 1), weightList.get(i + 1));
            Item item3 = new Item(profitList.get(i + 2), weightList.get(i + 2));
            itemSets.add(new ItemSet(item1, item2, item3));
        }

        return new KnapsackInstance(name, capacity, itemSets);
    }

    /**
     * 辅助方法：过滤空字符串、清洗非数字字符并将 String 转换为 Integer
     */
    private static List<Integer> parseStringToIntList(String[] arr) {
        List<Integer> list = new ArrayList<>();
        for (String s : arr) {
            // 1. 去除首尾不可见字符
            s = s.trim();
            
            // 2. 利用正则表达式，把所有非数字的字符（比如那该死的句号）全部替换为空
            s = s.replaceAll("[^0-9]", ""); 
            
            // 3. 确保清洗后字符串不为空，再进行数字转换
            if (!s.isEmpty()) {
                list.add(Integer.parseInt(s));
            }
        }
        return list;
    }
    
 // 测试读取、排序、求解与画图的全流程功能
    public static void main(String[] args) {
        // 注意这里的 resources 结尾的 s！你现在测试的是 idkp1-10.txt
        String testFilePath = "src/main/resources/idkp1-10.txt"; 
        
        List<KnapsackInstance> list = readDataFile(testFilePath);
        System.out.println("成功读取到 " + list.size() + " 组数据集！\n");
        
        // 为了避免一下子打印太多，我们只拿第一个实例 (例如 IDKP1) 来进行测试
        if (!list.isEmpty()) {
            KnapsackInstance firstInstance = list.get(0);
            
            System.out.println("--- 1. 测试排序功能 ---");
            System.out.println("【排序前】第一项集的比值: " + firstInstance.getItemSets().get(0).getThirdItemRatio());
            // 调用你写好的排序模块
            com.knapsack.core.DataProcessor.sortByThirdItemRatio(firstInstance);
            System.out.println("【排序后】第一项集的比值: " + firstInstance.getItemSets().get(0).getThirdItemRatio() + "\n");
            
            System.out.println("--- 2. 测试核心 DP 求解算法 ---");
            // 调用动态规划求解器
            com.knapsack.core.DPSolver.solve(firstInstance);
            
            System.out.println("\n--- 3. 正在启动图形界面绘制散点图 ---");
            // 绘制该实例的散点图
            com.knapsack.ui.ScatterPlotViewer.display(firstInstance);
        }
    }
}