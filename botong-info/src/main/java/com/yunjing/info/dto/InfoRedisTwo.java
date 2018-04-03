package com.yunjing.info.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 二级缓存DTO
 *
 * @author 李双喜
 * @date 2018/4/3 11:12
 */
@Data
public class InfoRedisTwo implements Serializable {
    /**
     * 二级目录id
     */
    private Long id;
    /**
     * 二级目录名称
     */
    private String name;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 等级
     */
    private Integer level;

}
