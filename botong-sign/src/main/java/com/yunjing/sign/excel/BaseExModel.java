package com.yunjing.sign.excel;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;

/**
 * Excel 模板基类
 *
 * @author yangc
 * @date 2017/10/23.
 */
@Data
public abstract class BaseExModel {

    /**
     * Excel 文件的 Workbook 对象
     */
    protected Workbook excel;

    private String fileName;

    /**
     * 抽象方法
     * @return
     * @throws Exception
     */
    public abstract Workbook createWorkbook() throws Exception;

    /**
     * 创建 HSSF 格式的Excel 文件，即 xls 格式
     *
     * @return
     */
    public Workbook createHSSFWorkbook(List<ExcelModel> excelModelList) {

        // 第一步，创建一个webbook，对应一个Excel文件
        excel = new HSSFWorkbook();
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        CreationHelper createHelper = excel.getCreationHelper();

        // create userIndex new sheet
        excelModelList.forEach(excelModel -> {
            List<String> titles = excelModel.getTitles();
            List<List<String>> items = excelModel.getItems();

            Sheet s = excel.createSheet(excelModel.getSheetName());
            // declare userIndex row object reference
//            Row r = null;
            // declare userIndex cell object reference
//            Cell c = null;
            // create 2 cell styles
            CellStyle cs = excel.createCellStyle();
            CellStyle cs2 = excel.createCellStyle();
            DataFormat df = excel.createDataFormat();

            // create 2 fonts objects
            Font f = excel.createFont();
            Font f2 = excel.createFont();

            // Set font 1 to 12 point type, blue and bold
            f.setFontHeightInPoints((short) 12);
            f.setColor(IndexedColors.RED.getIndex());
            f.setBoldweight(Font.BOLDWEIGHT_BOLD);

            // Set font 2 to 10 point type, red and bold
            f2.setFontHeightInPoints((short) 10);
            f2.setColor(IndexedColors.RED.getIndex());
            f2.setBoldweight(Font.BOLDWEIGHT_BOLD);

            // Set cell style and formatting
            cs.setFont(f);
            cs.setDataFormat(df.getFormat("#,##0.0"));

            // Set the other cell style and formatting
            cs2.setBorderBottom(CellStyle.BORDER_THIN);
            cs2.setDataFormat(df.getFormat("text"));
            cs2.setFont(f2);
            // style center
            // style center
            CellStyle tableStyle = excel.createCellStyle();
            tableStyle.setAlignment(CellStyle.ALIGN_CENTER);
            CellStyle noticeStyle = excel.createCellStyle();
            noticeStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
            noticeStyle.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
            Font font = excel.createFont();
            font.setFontHeightInPoints((short) 20);
            font.setColor(HSSFColor.WHITE.index);
            font.setFontName("楷体");
            font.setBoldweight((short) 8);
            noticeStyle.setFont(font);
            noticeStyle.setAlignment(CellStyle.ALIGN_CENTER);
            noticeStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            s.addMergedRegion(new CellRangeAddress(SignExConsts.ROW_NUM_NOTICE_0, SignExConsts.ROW_NUM_NOTICE_0, 0, excelModel.getTitles().size() - 1));
            s.addMergedRegion(new CellRangeAddress(SignExConsts.ROW_NUM_TABLE_1, SignExConsts.ROW_NUM_TABLE_1, 0, excelModel.getTitles().size() - 1));
            // Define userIndex few rows
            Row headerRow = s.createRow(0);
            headerRow.setHeight((short) 500);
            Cell noticeCell = headerRow.createCell(0);
            noticeCell.setCellValue(excelModel.notice);
            noticeCell.setCellStyle(noticeStyle);

            Row tableRow = s.createRow(SignExConsts.ROW_NUM_TABLE_1);
            Cell tableCell = tableRow.createCell(0);
            CellStyle tableHeader = excel.createCellStyle();
            tableCell.setCellValue(excelModel.getTableHeader());
            tableHeader.setFillPattern(CellStyle.SOLID_FOREGROUND);
            tableHeader.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
            tableCell.setCellStyle(tableHeader);
            if (titles != null && titles.size() > 0) {
                createRow(s, SignExConsts.ROW_NUM_TITLE_2, titles);
            }
            if (items != null && items.size() > 0) {
                for (int i = 0; i < items.size(); i++) {
                    int rowNum = SignExConsts.ROW_NUM_ITEM_3 + i;
                    createRow(s, rowNum, items.get(i));
                }
            }
        });
        return excel;
    }

    /**
     * 创建 Excel 其中一行
     *
     * @param s     指定 Excel 工作表
     * @param row   指定行数
     * @param items 本行数据列表
     */
    protected void createRow(Sheet s, int row, List<String> items) {
        Row tileRow = s.createRow(row);
        s.setColumnWidth((short) 0, (short) 2500);
        s.setColumnWidth((short) 1, (short) 2500);
        s.setColumnWidth((short) 2, (short) 4000);
        s.setColumnWidth((short) 3, (short) 2500);
        s.setColumnWidth((short) 4, (short) 2900);
        s.setColumnWidth((short) 5, (short) 2600);
        s.setColumnWidth((short) 6, (short) 2900);
        s.setColumnWidth((short) 7, (short) 2900);
        s.setColumnWidth((short) 8, (short) 5000);
        s.setColumnWidth((short) 9, (short) 10000);
        s.setColumnWidth((short) 10, (short) 4000);
        s.setColumnWidth((short) 11, (short) 4000);
        s.setColumnWidth((short) 12, (short) 1300);
        s.setColumnWidth((short) 13, (short) 1300);
        s.setColumnWidth((short) 14, (short) 1300);
        s.setColumnWidth((short) 15, (short) 1300);
        s.setColumnWidth((short) 16, (short) 1300);
        s.setColumnWidth((short) 17, (short) 1300);
        CellStyle style = excel.createCellStyle();
        Font font = excel.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("新宋体");
        font.setBoldweight((short) 0.8);
        if (row == SignExConsts.ROW_NUM_TITLE_2) {
            tileRow.setHeight((short) 400);
            font.setColor(IndexedColors.ROYAL_BLUE.getIndex());
            font.setBold(true);
            font.setFontName("新宋体");
            font.setFontHeightInPoints((short) 11);
            font.setColor(HSSFColor.BLACK.index);
            style.setAlignment(CellStyle.ALIGN_CENTER);
            style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
            style.setFillPattern(CellStyle.SOLID_FOREGROUND);
            style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        }
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setFont(font);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        // 超链接样式
        CellStyle urlStyle = excel.createCellStyle();
        urlStyle.setAlignment(CellStyle.ALIGN_CENTER);
        urlStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        urlStyle.setBorderTop(CellStyle.BORDER_THIN);
        urlStyle.setBorderBottom(CellStyle.BORDER_THIN);
        urlStyle.setBorderLeft(CellStyle.BORDER_THIN);
        urlStyle.setBorderRight(CellStyle.BORDER_THIN);
        urlStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        urlStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        urlStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        urlStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        Font urlFont = excel.createFont();
        urlFont.setColor(HSSFColor.BLUE.index);
        urlFont.setUnderline(Font.U_SINGLE);
        urlStyle.setFont(urlFont);
        for (int i = 0; i < items.size(); i++) {
            Cell c = tileRow.createCell(i);
            if (StringUtils.isNotBlank(items.get(i)) && items.get(i).startsWith("okhttp")) {
                c.setCellType(HSSFCell.CELL_TYPE_FORMULA);
                c.setCellFormula("HYPERLINK(\"" + items.get(i) + "\",\"" + "图" + "\")");
                c.setCellStyle(urlStyle);
            } else {
                c.setCellValue(items.get(i));
                c.setCellStyle(style);
            }
        }
    }

}
