package com.yunjing.botong.log.enums;

import lombok.Getter;

/**
 * <p>
 * <p> 提醒模式
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/30.
 */
@Getter
public enum RemindMode {

    /**
     * 推送
     */
    PUSH(0, "应用内推送"),
    /**
     * 短信
     */
    SMS(1, "短信"),
    /**
     * dang
     */
    DANG(2, "dang"),;

    RemindMode(int mode, String info) {
        this.mode = mode;
        this.info = info;
    }

    private int mode;

    private String info;
}
