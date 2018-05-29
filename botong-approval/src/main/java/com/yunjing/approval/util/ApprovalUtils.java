package com.yunjing.approval.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 刘小鹏
 * @date 2018/04/04
 */
public class ApprovalUtils {

    private static final Pattern EMOJI = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
            Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

    private static final Pattern P = Pattern.compile("[\u4e00-\u9fa5]");
    private static final Pattern DAY_NUM = Pattern.compile("^[0-9]+(.[0-9]{1})?$");

    /**
     * 将emoji表情替换成*
     *
     * @param source
     * @return 过滤后的字符串
     */
    public static String filterEmoji(String source) {

        if (source != null) {
            StringEscapeUtils.escapeJava(source.replace("\\\\u", "\\u"));
            Matcher emojiMatcher = EMOJI.matcher(source);
            if (emojiMatcher.find()) {
                source = emojiMatcher.replaceAll("*");
                return source;
            }
            source = source.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", "*");
            return source;
        }
        return source;
    }

    /**
     * 仅去重List集合中连续的元素
     *
     * @param sourceList 需要过滤的集合
     * @return List<T>
     */
    public static <T> List<T> distinctElements(List<T> sourceList) {
        if (sourceList == null || CollectionUtils.isEmpty(sourceList)) {
            return new ArrayList<>();
        }
        List<T> resultList = new ArrayList<>();
        Set<T> set = new HashSet<>();
        for (T temp : sourceList) {
            if (!set.contains(temp)) {
                set = new HashSet<>();
                set.add(temp);
                resultList.add(temp);
            }
        }
        return resultList;
    }

    /**
     * 过滤List集合中重复的且保留靠后的元素
     *
     * @param list 需要过滤的集合
     * @return List<T>
     */
    public static <T> List<T> removeDuplicate(List<T> list) {
        if (list == null || CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        Collections.reverse(list);
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).equals(list.get(i))) {
                    list.remove(j);
                }
            }
        }
        Collections.reverse(list);
        return list;
    }

    /**
     * 判断字符串中是否包含中文
     *
     * @param str 待校验字符串
     * @return 是否为中文
     * @warn 不能校验是否为中文标点符号
     */
    public static boolean isContainChinese(String str) {
        Matcher m = P.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static boolean validateDayNum(String str) {
        Matcher m = DAY_NUM.matcher(str);
        if (m.matches()){
            return true;
        }else {
            return false;
        }
    }


    public static void main(String[] arg) {
        try {
            String text = "This is a smiley \uD83C\uDFA6 face\uD860\uDD5D \uD860\uDE07 \uD860\uDEE2 \uD863\uDCCA \uD863\uDCCD \uD863\uDCD2 \uD867\uDD98 ";
            System.out.println(text);
            System.out.println(text.length());
            System.out.println(text.replaceAll("[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]", "*"));
            System.out.println(filterEmoji(text));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        /** -----------------测试仅去重List集合中相邻的元素-------------------------*/
        List<String> list = new ArrayList();
        list.add("DDDD");
        list.add("DDDD");
        list.add("DDDD");
        list.add("DDDD");
        list.add("BBBB");
        list.add("BBBB");
        list.add("BBBB");
        list.add("AAAA");
        list.add("AAAA");
        list.add("CCCC");
        list.add("DDDD");
        list.add("AAAA");
        list.add("AAAA");
        list.add("BBBB");
        list.add("AAAA");
        list.add("AAAA");
        list.add("BBBB");
        list.add("BBBB");
        list.add("BBBB");
        list.add("BBBB");
        list.add("BBBB");
        list.add("BBBB");
        List<String> strings = distinctElements(list);
        System.out.println(list);
        System.out.println("连续出现时自动去重："+strings);

        List<String> list1 = removeDuplicate(list);
        System.out.println("仅保留最后一个："+list1);

        /** 测试校验天数 */
        System.out.println(validateDayNum("1.3"));
        String s = "48afed8a755b44ed807378006906e783";
        System.out.println(s.length());
    }
}
