package com.yunjing.info.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.common.mybatis.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 资讯V1.0目录实体
 *
 * @author 李双喜
 * @date 2018/3/20 17:07
 */
@Data
@TableName("content_column")
public class ContentColumn{


    /**
     * 分类ID
     */
    @TableField("column_id")
    private String columnId;

    /**
     * 分类名称
     */
    @TableField("column_name")
    private String columnName;



    /**
     * 是否展示、1显示  2不显示
     */
    @TableField("column_show_type")
    private Integer columnShowType;

    /**
     * 展示顺序
     */
    @TableField("column_type")
    private Integer columnType;



    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;


    /**
     * 机构id
     */
    @TableField("org_id")
    private String orgId;

    /**
     * 父id
     */
    @TableField("parent_id")
    private String parentId;





    /**
     * 修改时间
     */
    @TableField("sort")
    private Date sort;


}
