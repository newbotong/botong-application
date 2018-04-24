package com.yunjing.botong.log.vo;

import lombok.Data;
import org.dozer.Mapping;

/**
 * <p>
 * <p> 管理范围成员数据
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/4/18.
 */
@Data
public class ManagerMemberInfoVo {

    /**
     * passportId
     */
    @Mapping("passportId")
    private String id;

    /**
     * 成员id
     */
    @Mapping("id")
    private String memberId;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 昵称
     */
    @Mapping("name")
    private String memberName;

    /**
     * 头像
     */
    private String profile;

    /**
     * 头像颜色
     */
    private String color;
}
