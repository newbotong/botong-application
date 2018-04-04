package com.yunjing.notice.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.common.mybatis.model.BaseModel;
import lombok.Data;

/**
 * 用户信息实体
 *
 * @author 李双喜
 * @date 2018/3/22 14:02
 */
@Data
@TableName("user_info")
public class UserInfoEntity extends BaseModel<UserInfoEntity> {
    /**
     * 用户头像地址
     */
    @TableField("img")
    private String img;
    /**
     * 用户名称
     */
    @TableField("name")
    private String name;

    /**
     * 用户手机号码
     */
    @TableField("phone")
    private Long phone;

    /**
     * 逻辑删除 0正常 1删除
     */
    @TableField("logic_delete")
    private Integer logicDelete;
}
