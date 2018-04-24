package com.yunjing.info.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <p>
 * <p> 加密工具
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/21.
 */
public class EncryptionUtil {

    private EncryptionUtil() {
    }

    public static String md5(String content) {
        StringBuilder buffer = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(content.getBytes());
            byte[] bs = md.digest();
            for (byte b : bs) {
                int v = b & 255;
                if (v < 16) {
                    buffer.append(0);
                }
                buffer.append(Integer.toHexString(v));
            }
        } catch (NoSuchAlgorithmException var10) {
            var10.printStackTrace();
        }
        return buffer.toString();
    }
}
