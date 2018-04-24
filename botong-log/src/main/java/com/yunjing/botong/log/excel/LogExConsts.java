package com.yunjing.botong.log.excel;




import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author liuxiaopeng
 * @date 2018/01/24
 */
public class LogExConsts {


    public static final String NOTICE = "日志数据报表";

    public static final String CELL_NAME_SENDER = "发送人";
    public static final String CELL_NAME_SENDER_EN = "sendUsers";

    public static final String CELL_NAME_DEPT = "部门";
    public static final String CELL_NAME_DEPT_EN = "sendDept";

    public static final String CELL_NAME_SENDTIME = "发送时间";
    public static final String CELL_NAME_SENDTIME_EN = "sendDate";

    public static final String CELL_NAME_IMG_EN = "sendImages";

    public static final String CELL_NAME_REMARK = "备注";
    public static final String CELL_NAME_REMARK_EN = "remark";

    public static final int ROW_NUM_NOTICE_0 = 0;
    public static final int ROW_NUM_TABLE_1 = 1;
    public static final int ROW_NUM_TITLE_2 = 2;
    public static final int ROW_NUM_ITEM_3 = 3;

    public static final int CELL_NUM_SENDER_0 = 0;
    public static final int CELL_NUM_DEPT_1 = 1;
    public static final int CELL_NUM_SENDTIME_2 = 2;

    public static final String TYPE_XLS = "xls";
    public static final String TYPE_XLSX = "xlsx";

    public static final String SEPARATOR_POINT = ".";
    public static final String SEPARATOR_COMMA = ",";
    public static final String SEPARATOR_HYPHEN = "-";
    public static final Integer EXCEL_ERROR_0 = 0;
    public static final Integer EXCEL_ERROR_1 = 1;

    private LogExConsts(){}

}
