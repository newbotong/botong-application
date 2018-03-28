package com.yunjing.sign.excel;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author liuxiaopeng
 * @date 2018/01/24
 */
public class SignExConsts {


    public static String NOTICE = "签到数据报表";
    public static String SHEET_NAME = "签到数据报表";

    public static final String CELL_NAME_NICK = "姓名";
    public static final String CELL_NAME_DEPT = "部门";
    public static final String CELL_NAME_DEPT_PATH = "完整部门";
    public static final String CELL_NAME_POSITION = "职位";
    public static final String CELL_NAME_SIGN_DATE = "日期";
    public static final String CELL_NAME_SIGN_TIME = "时间";
    public static final String CELL_NAME_LONGITUDE = "经度";
    public static final String CELL_NAME_LATITUDE = "纬度";
    public static final String CELL_NAME_ADDRESSTITL = "地点";
    public static final String CELL_NAME_ADDRESS = "详细地址";
    public static final String CELL_NAME_REMARK = "备注";
    public static final String CELL_NAME_DEVICE = "签到设备";

    public static int ROW_NUM_NOTICE_0 = 0;
    public static int ROW_NUM_TABLE_1 = 1;
    public static int ROW_NUM_TITLE_2 = 2;
    public static int ROW_NUM_ITEM_3 = 3;

    public static final int CELL_NUM_NICK_0 = 0;
    public static final int CELL_NUM_DEPT_1 = 1;
    public static final int CELL_NUM_DEPT_PATH_2 = 2;
    public static final int CELL_NUM_POSITION_3 = 3;
    public static final int CELL_NUM_DATE_4 = 4;
    public static final int CELL_NUM_TIME_5 = 5;
    public static final int CELL_NUM_LONGITUDE_6 = 6;
    public static final int CELL_NUM_LATITUDE_7 = 7;
    public static final int CELL_NUM_ADDRESSTITL_8 = 8;
    public static final int CELL_NUM_ADDRESS_9 = 9;
    public static final int CELL_NUM_REMARK_10 = 10;
    public static final int CELL_NUM_DEVICE_11 = 11;

    public static String Type_xls = "xls";
    public static String Type_xlsx = "xlsx";

    public static String SEPARATOR_POINT = ".";
    public static String SEPARATOR_COMMA = ",";
    public static String SEPARATOR_HYPHEN = "-";
    public static Integer EXCEL_ERROR_0 = 0;
    public static Integer EXCEL_ERROR_1 = 1;


    public static enum Type {
        HSSFWorkbook, XSSFWorkbook;

    }
}
