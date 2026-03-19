package com.knapsack.entity;

/**
 * 项集实体类
 * 每个项集包含3个物品，且满足D{0-1}KP的特定约束
 */
public class ItemSet {
    private Item[] items;

    public ItemSet(Item item1, Item item2, Item item3) {
        this.items = new Item[]{item1, item2, item3};
    }

    public Item[] getItems() {
        return items;
    }

    /**
     * 获取第三项物品的 价值/重量 比值
     */
    public double getThirdItemRatio() {
        Item thirdItem = items[2];
        if (thirdItem.getWeight() == 0) {
            return 0.0;
        }
        return (double) thirdItem.getProfit() / thirdItem.getWeight();
    }
}