package com.knapsack.entity;

/**
 * 物品实体类
 * 记录单个物品的价值和重量
 */
public class Item {
    private int profit;
    private int weight;

    public Item(int profit, int weight) {
        this.profit = profit;
        this.weight = weight;
    }

    public int getProfit() { return profit; }
    public void setProfit(int profit) { this.profit = profit; }
    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    @Override
    public String toString() {
        return "Item{profit=" + profit + ", weight=" + weight + "}";
    }
}