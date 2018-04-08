package com.yunjing.info.common;

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

}
