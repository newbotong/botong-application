package com.yunjing.notice.body;

import com.yunjing.mommon.validate.annotation.Length;
import com.yunjing.mommon.validate.annotation.NotNullOrEmpty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * <p> 公告（新增入参）
 * </p>
 *
 * @author 李双喜
 * @since 2018/3/20.
 */
@Data
public class NoticeBody implements Serializable {
    /**
     * 发布人用户id
     */
    @NotNullOrEmpty(message = "发布人不能为空")
    private String issueUserId;

    /**
     * 封面图地址
     */
    private String cover;

    /**
     * 图片地址 ，多个图片逗号隔开
     */
    private String picture;

    /**
     * 图片名称 多个图片名称逗号隔开
     */
    private String pictureName;

    /**
     * 图片大小 多个大小用逗号隔开
     */
    private String size;

    /**
     * 多个成员id,用逗号隔开
     */
    private String memberIds;

    /**
     * 部门id,用逗号隔开
     */
    private String departmentIds;

    /**
     * 保密状态 0为保密 1为不保密
     */
    @NotNullOrEmpty(message = "保密状态不能为空")
    private Integer secrecyState;

    /**
     * Dang的状态 0为Dang 1为非Dang
     */
    @NotNullOrEmpty(message = "Dang的状态不能为空")
    private Integer dangState;

    /**
     * 公告标题
     */
    @NotNullOrEmpty(message = "公告标题不能为空")
    @Length(max=35, message = "公告标题长度不得大于35")
    private String title;

    /**
     * 公告内容
     */
    @NotNullOrEmpty(message = "公告内容不能为空")
    @Length(max=2000, message = "公告内容长度不得大于2000")
    private String content;

    /**
     * 公告作者
     */
    @NotNullOrEmpty(message = "公告作者不能为空")
    private String author;

    /**
     * 企业id
     */
    @NotNullOrEmpty(message = "企业id不能为空")
    private String orgId;

    /**
     * 发送人的手机号码
     */
    @NotNullOrEmpty(message = "发送者手机号码不能为空")
    private Long phone;

}
