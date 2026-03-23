package com.knapsack.core;

import com.knapsack.entity.ItemSet;
import com.knapsack.entity.KnapsackInstance;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 数据处理工具类
 * 负责对实例数据进行各种加工，例如排序
 */
public class DataProcessor {

    /**
     * 按项集第三项的价值/重量比进行非递增(降序)排序
     * @param instance 需要排序的背包实例
     */
    public static void sortByThirdItemRatio(KnapsackInstance instance) {
        List<ItemSet> itemSets = instance.getItemSets();

        // 使用 Java 内置的排序算法，结合 Lambda 表达式自定义比较规则
        Collections.sort(itemSets, new Comparator<ItemSet>() {
            @Override
            public int compare(ItemSet o1, ItemSet o2) {
                // 降序，所以用 o2 的比值去比 o1 的比值
                return Double.compare(o2.getThirdItemRatio(), o1.getThirdItemRatio());
            }
        });
        
        System.out.println("成功对实例 [" + instance.getName() + "] 进行了按价值/重量比降序排序！");
    }
}