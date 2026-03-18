package com.knapsack.entity;
import java.util.List;

public class KnapsackInstance {
    private String name;            
    private int capacity;           
    private List<ItemSet> itemSets; 

    public KnapsackInstance(String name, int capacity, List<ItemSet> itemSets) {
        this.name = name;
        this.capacity = capacity;
        this.itemSets = itemSets;
    }

    public String getName() { return name; }
    public int getCapacity() { return capacity; }
    public List<ItemSet> getItemSets() { return itemSets; }
    
    @Override
    public String toString() {
        return "实例: " + name + " | 容量: " + capacity + " | 项集数量: " + itemSets.size();
    }
}