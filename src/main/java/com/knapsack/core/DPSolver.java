package com.knapsack.core;

import com.knapsack.entity.Item;
import com.knapsack.entity.ItemSet;
import com.knapsack.entity.KnapsackInstance;

import java.util.ArrayList;
import java.util.List;

/**
 * 核心算法类：使用动态规划求解 D{0-1}KP 问题
 */
public class DPSolver {

    public static String solve(KnapsackInstance instance) {
        long startTime = System.nanoTime();

        List<ItemSet> itemSets = instance.getItemSets();
        int n = itemSets.size();
        int W = instance.getCapacity();

        int[][] dp = new int[n + 1][W + 1];

        for (int i = 1; i <= n; i++) {
            Item[] items = itemSets.get(i - 1).getItems();
            Item item1 = items[0];
            Item item2 = items[1];
            Item item3 = items[2];

            for (int v = 0; v <= W; v++) {
                int maxVal = dp[i - 1][v];

                if (v >= item1.getWeight()) {
                    maxVal = Math.max(maxVal, dp[i - 1][v - item1.getWeight()] + item1.getProfit());
                }
                if (v >= item2.getWeight()) {
                    maxVal = Math.max(maxVal, dp[i - 1][v - item2.getWeight()] + item2.getProfit());
                }
                if (v >= item3.getWeight()) {
                    maxVal = Math.max(maxVal, dp[i - 1][v - item3.getWeight()] + item3.getProfit());
                }

                dp[i][v] = maxVal;
            }
        }

        int maxProfit = dp[n][W];

        List<String> selectedItems = new ArrayList<>();
        int currentV = W;
        for (int i = n; i > 0; i--) {
            Item[] items = itemSets.get(i - 1).getItems();
            if (currentV >= items[0].getWeight() && dp[i][currentV] == dp[i - 1][currentV - items[0].getWeight()] + items[0].getProfit()) {
                selectedItems.add("项集" + i + "-物品1");
                currentV -= items[0].getWeight();
            } else if (currentV >= items[1].getWeight() && dp[i][currentV] == dp[i - 1][currentV - items[1].getWeight()] + items[1].getProfit()) {
                selectedItems.add("项集" + i + "-物品2");
                currentV -= items[1].getWeight();
            } else if (currentV >= items[2].getWeight() && dp[i][currentV] == dp[i - 1][currentV - items[2].getWeight()] + items[2].getProfit()) {
                selectedItems.add("项集" + i + "-物品3");
                currentV -= items[2].getWeight();
            }
        }

        long endTime = System.nanoTime();
        double timeMs = (endTime - startTime) / 1_000_000.0;

        // 结果
        StringBuilder sb = new StringBuilder();
        sb.append("================ 求解报告 ================\n");
        sb.append("数据实例: ").append(instance.getName()).append("\n");
        sb.append("最大总价值 (最优解): ").append(maxProfit).append("\n");
        sb.append("算法求解耗时: ").append(String.format("%.2f", timeMs)).append(" ms\n");
        sb.append("共选中物品数: ").append(selectedItems.size()).append(" 个\n");
        sb.append("物品选择明细: ").append(selectedItems.toString()).append("\n");
        sb.append("=========================================\n");

        return sb.toString();
    }
}