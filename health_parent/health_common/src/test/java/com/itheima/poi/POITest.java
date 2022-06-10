package com.itheima.poi;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;

public class POITest {

    /**
     * POI操作Excel对象
     *      XSSFWorkbook：工作簿
     *      XSSFSheet：工作表
     *      Row：行
     *      Cell：单元格
     */
    @Test
    public void testPOI01() throws IOException {

        //1. 创建工作簿
        XSSFWorkbook workbook = new XSSFWorkbook("D:\\tmp\\poi.xlsx");
        //2. 获取工作表，既可以根据工作表的顺序获取，也可以根据工作表的名称获取
        XSSFSheet sheet = workbook.getSheetAt(0);
        //遍历工作表获得行对象
        for (Row row : sheet) {
            //遍历行对象获取单元格对象
            for (Cell cell : row) {
                //获得单元格中的值
                String value = cell.getStringCellValue();
                System.out.println(value);
            }
        }
        workbook.close();
    }

    @Test
    public void testPOI02() throws IOException {
        //1. 创建工作簿
        XSSFWorkbook workbook = new XSSFWorkbook("D:\\tmp\\poi.xlsx");
        //2. 获取工作表，既可以根据工作表的顺序获取，也可以根据工作表的名称获取
        XSSFSheet sheet = workbook.getSheetAt(0);
        //3. 获取当前工作表最后一行的行号，行号从0开始
        int lastRowNum = sheet.getLastRowNum();
        for (int i = 0; i <= lastRowNum; i++) {
            //4. 根据行号获取行对象
            XSSFRow row = sheet.getRow(i);
            //5. 跟据当前行最后一个单元格索引
            short lastCellNum = row.getLastCellNum();
            for (short j = 0; j < lastCellNum; j++) {
                //6. 跟据单元格索引获取单元格数据
                String value = row.getCell(j).getStringCellValue();
                System.out.println(value);
            }
        }
        workbook.close();
    }

    @Test
    public void testPOI03() throws IOException {
        //1. 在内存中创建一个Excel文件
        XSSFWorkbook workbook = new XSSFWorkbook();
        //2. 创建工作表，指定工作表名称
        XSSFSheet sheet = workbook.createSheet("传智播客");

        //3. 创建行，0表示第一行
        XSSFRow row = sheet.createRow(0);
        //4. 创建单元格，0表示第一个单元格
        row.createCell(0).setCellValue("编号");
        row.createCell(1).setCellValue("名称");
        row.createCell(2).setCellValue("年龄");

        XSSFRow row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("1");
        row1.createCell(1).setCellValue("小明");
        row1.createCell(2).setCellValue("10");

        XSSFRow row2 = sheet.createRow(2);
        row2.createCell(0).setCellValue("2");
        row2.createCell(1).setCellValue("小王");
        row2.createCell(2).setCellValue("20");

        //5. 通过输出流将workbook对象下载到磁盘
        FileOutputStream out = new FileOutputStream("D:\\tmp\\poi-test.xlsx");
        workbook.write(out);
        out.flush();
        out.close();
        workbook.close();
    }
}
