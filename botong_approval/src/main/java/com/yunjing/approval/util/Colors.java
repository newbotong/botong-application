package com.yunjing.approval.util;

import org.apache.http.util.TextUtils;

import java.util.Random;

/**
 * 生成颜色
 *
 * @author 刘小鹏
 * @date 2018/04/04
 */
public class Colors {

    public static String generateBeautifulColor(String mobile, String nick) {

        String phone = "88888888";
        String str = "^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$";
        if (mobile.matches(str)) {
            phone = mobile;
        }
        int rgb = 50;
        if (!TextUtils.isEmpty(nick)) {
            String convert = convert(nick) + phone;
            Double aLong = Double.valueOf(convert);
            rgb = (int) (aLong % 255);
            int i = 50;
            if (rgb < i) {
                rgb = 50;
            }
        }

        Random mRandom = new Random(rgb);
        int red = mRandom.nextInt(rgb);
        int green = mRandom.nextInt(rgb);
        int blue = mRandom.nextInt(rgb);
        return getHex(red, green, blue);
    }

    public static String convert(String input) {

        String reg = "[a-zA-Z]";
        String regs = "[0-9]";
        StringBuffer strBuf = new StringBuffer();
        if (null != input && !"".equals(input)) {
            String replace = input.replaceAll("[~!@%*(`)^%&'',;#=?$]+", "");
            String str = replace.trim().toLowerCase();
            for (char c : str.toCharArray()) {
                if (String.valueOf(c).matches(reg)) {
                    strBuf.append(c - 96);
                } else if (String.valueOf(c).matches(regs)) {
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
        int i = 2;
        while (builder.length() < i) {
            builder.append("0");
        }
        return builder.toString().toUpperCase();
    }

    public static void main(String[] args) {
        System.out.println(generateBeautifulColor("18658465842", "kawins"));
    }
}