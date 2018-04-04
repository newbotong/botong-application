package com.yunjing.info.config;


import lombok.Getter;

/**
 * 咨询模块常量
 * @author liushujie
 */
public interface InfoConstants {

    /**
     * //咨询类目、咨询内容：显示
     */
    Integer INFO_TYPE_DISPLAY = 1;

    /**
     * 咨询类目、咨询内容：隐藏
     */
    Integer INFO_TYPE_HIDE = 0;

    /**
     * 最小名字长度5
     */
    Integer INFO_NAME_MIX = 6;

    /**
     * 最大长度12
     */
    Integer INFO_NAME_MAX = 12;


    /**
     * 资讯类目缓存key
     */
    String BOTONG_INFO_CATALOG_LIST = "botong:info:";
    /**
     * redis 分隔
     */
    String BOTONG_INFO_FIX = ":";

    @Getter
    enum StateCode {

        /**
         * 新增、编辑成功
         */
        CODE_200(200, "成功!"),
        /**
         * 参数错误
         */
        CODE_400(400, "请传入正确的参数!"),

        /**
         * 新增、编辑失败
         */
        CODE_602(602, "失败!"),


        /**
         * 该分类已经存在
         */
        CODE_603(603, "该分类已经存在!"),

        /**
         * 分类最多六个
         */
        CODE_604(604, "分类最多六个!"),;


        int code;

        String message;

        StateCode(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }

}
