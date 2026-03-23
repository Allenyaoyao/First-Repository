package com.knapsack.io;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Excel 导出工具类 
 */
public class ExcelExporter {

    public static void exportToExcel(String reportText, File file) throws Exception {
        // 创建一个全新的 .xlsx 工作簿
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("DP求解结果");

            // 设置表头标题的样式（加粗）
            CellStyle boldStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            boldStyle.setFont(font);

            // 巧妙的工程化处理：按行读取之前的报告字符串，拆分到 Excel 的单元格里
            String[] lines = reportText.split("\n");
            int rowNum = 0;
            
            for (String line : lines) {
                // 跳过毫无意义的 ======= 分割线
                if (line.contains("========")) continue;

                Row row = sheet.createRow(rowNum++);
                if (line.contains(": ")) {
                    String[] parts = line.split(": ", 2);
                    
                    Cell cellA = row.createCell(0);
                    cellA.setCellValue(parts[0] + ":");
                    cellA.setCellStyle(boldStyle); // A列属性名加粗

                    Cell cellB = row.createCell(1);
                    cellB.setCellValue(parts[1]);  // B列放具体数值
                } else {
                    row.createCell(0).setCellValue(line);
                }
            }

            // 自动调整一下两列的宽度，让文字不被遮挡
            sheet.setColumnWidth(0, 6000); 
            sheet.setColumnWidth(1, 20000);

            // 将拼装好的表格写出到用户选择的文件路径
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        }
    }
}