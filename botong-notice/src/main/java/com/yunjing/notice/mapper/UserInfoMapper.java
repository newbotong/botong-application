package com.yunjing.notice.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.yunjing.notice.body.UserInfoBody;
import com.yunjing.notice.entity.UserInfoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author 李双喜
 * @date 2018/3/22 14:23
 */
@Repository
public interface UserInfoMapper extends BaseMapper<UserInfoEntity> {
    /**
     * 查询用户id
     * @return
     */
    List<String> selectUserIds();

    /**
     * 查询用户属性
     * @param map
     * @return
     */
    List<UserInfoBody> selectUser(Map<String,Object> map);
}
