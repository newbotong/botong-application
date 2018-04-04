package com.yunjing.notice.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.notice.entity.UserInfoEntity;
import com.yunjing.notice.mapper.UserInfoMapper;
import com.yunjing.notice.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 公告用户信息Service实现类
 *
 * @author 李双喜
 * @date 2018/3/22 14:20
 */
@Service
@Slf4j
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfoEntity> implements UserInfoService {
}
