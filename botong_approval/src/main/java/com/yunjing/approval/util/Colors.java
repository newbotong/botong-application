package com.yunjing.approval.util;

import org.apache.http.util.TextUtils;

import java.util.Random;

public class Colors {

	public static String generateBeautifulColor(String mobile, String nick) {
		
		String _mobile = "88888888";
		if (mobile.matches("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$")) {
			_mobile = mobile;
		}
		int RGB = 50;
		if (!TextUtils.isEmpty(nick)) {
			String convert = convert(nick) + _mobile;
			Double aLong = Double.valueOf(convert);
			RGB = (int) (aLong % 255);
			if (RGB < 50) {
				RGB = 50;
			}
		}
		
		Random mRandom = new Random(RGB);
		int red = mRandom.nextInt(RGB);
		int green = mRandom.nextInt(RGB);
		int blue = mRandom.nextInt(RGB);
		return getHex(red, green, blue);
	}

	public static String convert(String input) {

		String reg = "[a-zA-Z]";
		String _reg = "[0-9]";
		StringBuffer strBuf = new StringBuffer();
		if (null != input && !"".equals(input)) {
			String replace = input.replaceAll("[~!@%*(`)^%&'',;#=?$]+", "");
			String str = replace.trim().toLowerCase();
			for (char c : str.toCharArray()) {
				if (String.valueOf(c).matches(reg)) {
					strBuf.append(c - 96);
				} else if (String.valueOf(c).matches(_reg)) {
					strBuf.append(c);
				}
			}
			return strBuf.toString();
		} else {
			return "88888888";
		}
	}

	public static String getHex(int r, int g, int b) {
		return "#" + toBrowserHexValue(r) + toBrowserHexValue(g) + toBrowserHexValue(b);
	}

	private static String toBrowserHexValue(int number) {
		StringBuilder builder = new StringBuilder(Integer.toHexString(number & 0xff));
		while (builder.length() < 2) {
			builder.append("0");
		}
		return builder.toString().toUpperCase();
	}

	public static void main(String[] args) {
		System.out.println(generateBeautifulColor("18658465842", "kawins"));
	}
}