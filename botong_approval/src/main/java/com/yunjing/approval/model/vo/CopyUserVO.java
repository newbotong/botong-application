package com.yunjing.approval.model.vo;


import lombok.Data;

/**
 * @author 刘小鹏
 * @date 2018/03/23
 */
@Data
public class CopyUserVO {

    /**
     * 抄送人姓名
     */
    private String name;

    /**
     * 头像的背景颜色
     */
    private String color;

    /**
     * 抄送人头像
     */
    private String avatar;

    /**
     * 头像中的名字
     */
    private String avatarName;
}
