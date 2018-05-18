package com.yunjing.approval.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 刘小鹏
 * @date 2018/04/04
 */
public class ApprovalUtils {

    private static final Pattern EMOJI = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
            Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

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
     * 仅去重List集合中相邻的元素
     *
     * @param sourceList 需要过滤的集合
     * @return List<T>
     */
    public static <T> List<T> distinctElements(List<T> sourceList) {
        if (sourceList != null || CollectionUtils.isEmpty(sourceList)) {
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
        System.out.println(strings);
    }
}
