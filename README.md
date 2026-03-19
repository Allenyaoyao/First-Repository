# D{0-1}KP 算法求解与分析系统

本项目是基于 Java 开发的 **D{0-1}KP (Dimension 0-1 Knapsack Problem)** 问题的求解、分析与可视化系统。系统集成了动态规划算法、大规模数据处理、散点图绘制以及结果导出功能，旨在提供一个友好的用户界面来探索和解决复杂的背包优化问题。
## 🌟 核心功能

* **多维度数据读取**：支持读入 IDKP、SDKP、UDKP、WDKP 四类实验数据文件（共 40 组实例）。
* **高颜值可视化**：基于 JFreeChart 绘制物品重量与价值的散点图，采用半透明点阵设计，直观展示数据分布。
* **智能排序分析**：支持对项集按“价值/重量比”进行非递增排序，辅助贪心策略分析。
* **核心 DP 算法**：采用经典的 **动态规划 (Dynamic Programming)** 求解最优解，确保计算结果的精确性。
* **结果自动化导出**：一键将最优解、求解耗时、选中的物品明细导出为本地 `.txt` 文件。
* **现代化 GUI 界面**：使用 Java Swing 打造，通过下拉框和按钮实现全交互式操作。
## 🛠️ 项目架构 (Project Structure)
```text
com.knapsack
├── core
│   ├── DPSolver.java        # 核心动态规划求解引擎
│   └── DataProcessor.java    # 排序与数据预处理工具类
├── entity
│   ├── Item.java            # 基础物品实体类
│   ├── ItemSet.java         # 项集实体类（包含三项物品）
│   └── KnapsackInstance.java # 完整的背包问题实例模型
