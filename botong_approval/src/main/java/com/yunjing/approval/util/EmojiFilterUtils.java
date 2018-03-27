package com.yunjing.approval.util;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EmojiFilterUtils {

	/**
	 * 将emoji表情替换成*
	 * 
	 * @param source
	 * @return 过滤后的字符串
	 */
	public static String filterEmoji(String source) {
		if (source != null) {
			Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
							Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
		StringEscapeUtils.escapeJava(source.replace("\\\\u", "\\u"));
			Matcher emojiMatcher = emoji.matcher(source);
			if (emojiMatcher.find()) {
				source = emojiMatcher.replaceAll("*");
				return source;
			}
			source = source.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]","*");
			return source;
		}
		return source;
	}
	public static void main(String[] arg) {
		try {
			String text = "This is a smiley \uD83C\uDFA6 face\uD860\uDD5D \uD860\uDE07 \uD860\uDEE2 \uD863\uDCCA \uD863\uDCCD \uD863\uDCD2 \uD867\uDD98 ";
//			System.out.println(text);
//			System.out.println(text.length());
//			System.out.println(text.replaceAll("[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]", "*"));
//			System.out.println(filterEmoji(text));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
