package com.yunjing.info.common;

import lombok.Getter;

/**
 * 资讯常量
 *
 * @author 李双喜
 * @date 2018/4/3 13:50
 */
public interface InfoConstant {

    /**
     * 是否逻辑删除（0正常 1删除）
     */
    Integer LOGIC_DELETE_NORMAL = 0;
    Integer LOGIC_DELETE_DELETE = 1;

    /**
     * 初始化一级缓存
     */
    String REDIS_CATALOG_ONE = "botong:info:catalog";

    /**
     * 初始化二级缓存
     */
    String REDIS_CATALOG_TWO = "botong:info:second";

    /**
     * 资讯首页缓存
     */
    String REDIS_HOME = "botong:info:home";

    /**
     * 企业初始化一级缓存结构
     */
    String COMPANY_INFO_REDIS = "botong:info:company:";

    /**
     * 咨询类目、咨询内容：显示
     */
    Integer INFO_TYPE_DISPLAY = 1;

    /**
     * 咨询类目、咨询内容：隐藏
     */
    Integer INFO_TYPE_HIDE = 0;

    /**
     * 类目排序  向上
     */
    Integer INFO_TYPE_ORDER_UP = 0;
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
    String BOTONG_INFO_CATALOG_LIST = "botong:info:org:";
    /**
     * redis 分隔
     */
    String BOTONG_INFO_FIX = ":";

    String BOTONG_FAVOURITE_FAVID = "botong:favourite:favid";

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
