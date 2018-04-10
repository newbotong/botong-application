package com.yunjing.notice.body;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.List;

/**
 * 用户信息缓存表
 *
 * @author 李双喜
 * @date 2018/4/9 15:49
 */
@Data
public class UserInfoRedis implements Serializable {

    /**
     * 账户id
     */
    @Id
    String passportId;

    /**
     * 头像
     */
    String profile;

    /**
     * 昵称
     */
    String nick;

    /**
     * 性别   0：女，1：男
     */
    Integer gender;

    /**
     * 出生日期
     */
    Long birthday;

    /**
     * 创建时间
     */
    Long registerTime;

    /**
     * 手机号码
     */
    String mobile;

    /**
     * 邮件
     */
    String email;

    /**
     * 地区
     */
    String area;

    /**
     * 颜色
     */
    String color;

    /**
     * 删除标记
     */
    Short isDelete;

    /**
     * 特别关心人集合
     */
    List<String> careForIdList;
}
