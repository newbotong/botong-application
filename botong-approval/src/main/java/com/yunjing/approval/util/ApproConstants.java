package com.yunjing.approval.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author 刘小鹏
 * @date 2017/12/22
 */
public class ApproConstants {

    public final static String BOTONG_ORG_MEMBER = "botong:org:dept:member";
    public final static String BOTONG_ORG = "botong:org:company";
    public final static String BOTONG_APPROVAL = "botong:approval:info";

    public final static String DEFAULT_COLOR = "#1E90FF";
    /**
     * 审批完成 （状态 0:审批中 1: 审批完成 2:已撤回）
     */
    public static final int APPROVAL_STATE_1 = 1;
    /**
     * 是系统默认模型
     */
    public static final int IS_SYSTEM_MODEL_1 = 1;
    /**
     * 不是系统默认模型
     */
    public static final int IS_SYSTEM_MODEL_0 = 0;

    /**
     * 不设置
     */
    public static final int SET_TYPE_2 = 2;

    /**
     * 模型项类型 （1-多行输入框 2-数字输入框 3-单选框 4-日期 5-日期区间 6-单行输入框 7-明细 8-说明文字 9-金额 10- 图片 11-附件）
     */
    public static final int MULTI_LINE_TYPE_1 = 1;

    /**
     * 2-数字输入框
     */
    public static final int NUMBER_TYPE_2 = 2;

    /**
     * 3-单选框
     */
    public static final int RADIO_TYPE_3 = 3;

    /**
     * 4-日期
     */
    public static final int DATE_TYPE_4 = 4;

    /**
     * 5-日期区间
     */
    public static final int TIME_INTERVAL_TYPE_5 = 5;

    /**
     * 6-单行输入框
     */
    public static final int SINGLE_LINE_TYPE_6 = 6;

    /**
     * 7-明细
     */
    public static final int DETAILED_TYPE_7 = 7;

    /**
     * 8-说明文字
     */
    public static final int EXPLAIN_TYPE_8 = 8;

    /**
     * 9-金额
     */
    public static final int MONEY_TYPE_9 = 9;

    /**
     * 10- 图片
     */
    public static final int PICTURE_TYPE_10 = 10;

    /**
     * 11-附件
     */
    public static final int ENCLOSURE_TYPE_11 = 11;

    public final static Integer BOTONG_ONE_NUM = 1;

    public final static Integer BOTONG_ZERO_NUM = 0;

}
