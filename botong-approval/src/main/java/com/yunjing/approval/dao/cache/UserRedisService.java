package com.yunjing.approval.dao.cache;

import com.common.redis.BaseRedisService;
import com.yunjing.approval.model.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 用户信息缓存业务
 * @author lxp
 */
@Service
public class UserRedisService extends BaseRedisService<UserVO> {

    private static final String REDIS_KEY = "GLOBAL_USER";

    @Autowired
    private UserIDRedisService userIDRedisService;

    @Override
    protected String getRedisKey() {
        return REDIS_KEY;
    }

    public UserVO getByMobile(String mobile){
        UserVO user = null;
        if(!StringUtils.isEmpty(mobile)) {
            user = super.get(mobile);
        }
        return user;
    }

    public List<UserVO> getByMobileList(String[] mobileList){
        List<UserVO> userList = new ArrayList<>();
        if(mobileList != null && mobileList.length > 0) {
            for(String mobile : mobileList){
                if (!StringUtils.isEmpty(mobile)) {
                    UserVO user = super.get(mobile);
                    if(user != null) {
                        userList.add(user);
                    }
                }
            }
        }
        return userList;
    }

    public UserVO getByUserId(String uid) {
        UserVO user = null;
        String mobile = userIDRedisService.get(uid);
        if(!StringUtils.isEmpty(mobile)) {
            user = super.get(mobile);
        }
        return user;
    }

    public List<UserVO> getByUserIdList(List<String> userIdList){
        List<UserVO> userList = new ArrayList<>();
        if(userIdList != null && userIdList.size() > 0) {
            for(String uid : userIdList){
                String mobile = userIDRedisService.get(uid);
                if (!StringUtils.isEmpty(mobile)) {
                    UserVO user = super.get(mobile);
                    userList.add(user);
                }
            }
        }
        return userList;
    }

    @Override
    public void put(String key, UserVO doamin, long expire) {
        throw new IllegalArgumentException("not support insert user");
    }

    @Override
    public void remove(String key) {
        throw new IllegalArgumentException("not support remove user");
    }


    @Service
    private class UserIDRedisService extends BaseRedisService<String> {
        private static final String REDIS_KEY = "USER_UID";

        @Override
        protected String getRedisKey() {
            return REDIS_KEY;
        }

        @Override
        public void put(String key, String doamin, long expire) {
            throw new IllegalArgumentException("not support insert userId");
        }

        @Override
        public void remove(String key) {
            throw new IllegalArgumentException("not support remove userId");
        }
    }
}