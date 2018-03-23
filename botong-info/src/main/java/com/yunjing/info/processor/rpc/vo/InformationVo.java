package com.yunjing.info.processor.rpc.vo;

import lombok.Data;

/**
 * <p>
 * <p> 资讯信息vo rpc 参数
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/23.
 */
@Data
public class InformationVo {

    /**
     * 收藏内容原始id
     */
    private Long originId;

    /**
     * 消息类型
     */
    private Integer type;

    /**
     * 资讯名称
     */
    private String name;

    /**
     * 资讯内容
     */
    private String content;

    /**
     * 图片
     */
    private String picture;
}
