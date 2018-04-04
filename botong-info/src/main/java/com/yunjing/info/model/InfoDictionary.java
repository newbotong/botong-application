package com.yunjing.info.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.common.mybatis.model.BaseModel;
import lombok.Data;

/**
 * 资讯字典实体
 *
 * @author 李双喜
 * @date 2018/4/3 11:38
 */
@Data
@TableName("info_dictionary")
public class InfoDictionary extends BaseModel<InfoDictionary> {
    /**
     * 字典名称
     */
    @TableField("name")
    private String name;
    /**
     * 父id
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 级别
     */
    @TableField("level")
    private Integer level;

    /**
     * 展示顺序
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 是否删除 0：未删除；1：已删除
     */
    @TableField("is_delete")
    private Integer isDelete;

}
