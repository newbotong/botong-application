package com.yunjing.approval.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 时间信息处理类
 *
 * @version 1.0.0
 * @author: zhangx
 * @date create in 2017/8/31 8:37
 * @description
 **/
public class DateUtil {

    /**
     * 时间格式1  yyyy-MM-dd HH:mm:ss
     */
    public final static String DATE_FORMAT_1 = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时间格式2  yyyy-MM-dd
     */
    public final static String DATE_FORMAT_2 = "yyyy-MM-dd";

    /**
     * 时间格式3  MM月dd日
     */
    public final static String DATE_FORMAT_3 = "MM月dd日";

    /**
     * 时间格式4  yyyy-MM
     */
    public final static String DATE_FORMAT_4 = "yyyy-MM";

    /**
     * 时间格式5  HH:mm:ss
     */
    public final static String DATE_FORMAT_5 = "HH:mm";

    /**
     * 时间格式6  yyyy-MM-dd HH:mm
     */
    public final static String DATE_FORMAT_6 = "yyyy-MM-dd HH:mm";
    /**
     * 时间格式3  MM月dd日
     */
    public final static String DATE_FORMAT_7 = "MM-dd";

    /**
     * 获取当前时间
     *
     * @return
     * @see <P>返回Timestamp类型时间</P>
     */
    public static Timestamp getCurrentTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * 把传入的时间格式化成需要的时间格式
     *
     * @param date    传入时间
     * @param pattern 需要转换的格式
     * @return
     */
    public static String dateFormmat(Date date, String pattern) {
        DateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * 转换时间格式
     *
     * @param date     需转换时间字符串
     * @param format   需转换时间字符串格式
     * @param toFormat 需得到时间字符串格式
     * @return
     */
    public static String dateFormat(String date, String format, String toFormat) {
        SimpleDateFormat toDateFormat = new SimpleDateFormat(toFormat);
        return toDateFormat.format(dateFormat(date, format));
    }

    /**
     * 把传入的时间格式化成需要的时间格式
     *
     * @param dateStr 传入时间
     * @param pattern 需要转换的格式
     * @return
     */
    public static Date dateFormat(String dateStr, String pattern) {
        DateFormat format = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 判断传入时间当天为周内的那一天
     *
     * @param date 传入的时间
     * @return
     */
    public static int getDayForWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day = c.get(7) - 1;
        if (day == 0) {
            day = 7;
        }
        return day;
    }

    /**
     * 获取昨天日期
     */
    public static String getYesterday() {
        Date nowDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(nowDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) - 1);
        return dateFormmat(date.getTime(), DATE_FORMAT_2);
    }


    /**
     *
     */
    public static Timestamp string2Timestamp(String str) {
        SimpleDateFormat sf = new SimpleDateFormat(DATE_FORMAT_6);
        try {
            Date date = sf.parse(str);
            return new Timestamp(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return new Timestamp(0);
        }
    }

    /**
     * 将给定的时间毫秒值转换为格式为时分秒（比如：1小时30分15秒）
     *
     * @param time 时间毫秒值
     * @return
     */
    public static String getTime(long time) {
        String str = "";
        time = time / 1000;
        int s = (int) (time % 60);
        int m = (int) (time / 60 % 60);
        int h = (int) (time / 3600 % 24);
        int d = (int) time / (3600 * 24) ;
        str = d + "天" + h + "小时" + m + "分" + s + "秒";
        return str;
    }

    /**
     * 得到本月的第一天
     */
    public static String getMonthFirstDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        SimpleDateFormat firstDay = new SimpleDateFormat("yyyy-MM-dd");
        return firstDay.format(calendar.getTime());
    }

    /**
     * 得到本月的最后一天
     */

    public static String getMonthLastDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        SimpleDateFormat lastDay = new SimpleDateFormat("yyyy-MM-dd");
        return lastDay.format(calendar.getTime());
    }

    /**
     * 根据给定的日期获取到该日期属于星期几
     *
     * @param date 时间格式（yyyy-MM-dd）
     * @return
     */
    public static String getWeekByDate(String date) {
        String dayOfweek = "-1";
        try {
            SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
            Date myDate = myFormatter.parse(date);
            SimpleDateFormat formatter = new SimpleDateFormat("E");
            String str = formatter.format(myDate);
            dayOfweek = str;

        } catch (Exception e) {
            System.out.println("错误!");
        }
        return dayOfweek;
    }

    /**
     * 获取某段时间内的所有日期
     *
     * @param dBegin 开始时间
     * @param dEnd   结束时间
     * @return
     */
    public static List<String> findDates(Date dBegin, Date dEnd) {
        List<Date> date = new ArrayList<Date>();
        date.add(dBegin);
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(dEnd);
        // 测试此日期是否在指定日期之后
        while (dEnd.after(calBegin.getTime())) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            date.add(calBegin.getTime());
        }
        List<String> dateList = new ArrayList();
        for (Date date1 : date) {
            String date2 = DateUtil.dateFormmat(date1, DateUtil.DATE_FORMAT_2);
            dateList.add(date2);
        }
        return dateList;
    }


    /**
     * 测试
     */
    public static void main(String[] args) {
        System.out.println(getMonthLastDay());
    }
}
