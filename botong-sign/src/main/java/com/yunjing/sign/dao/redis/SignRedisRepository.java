package com.yunjing.sign.dao.redis;

import com.common.redis.BaseRedisService;
import com.yunjing.sign.beans.model.SignConfigModel;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class SignRedisRepository extends BaseRedisService<SignConfigModel> {

    @Resource
    private SetOperations<String, String> setOperations;

    @Override
    protected String getRedisKey() {
        return "botong:permission:role:resource";
    }


    public void put(Long identity, List<Long> resourceIdList) {
        setOperations.add(getIdentity(identity), resourceIdList.stream().map(String::valueOf).toArray(String[]::new));
    }

    public void delete(Long identity) {
        redisTemplate.delete(getIdentity(identity));
    }

    public List<Long> getResourceListByRole(List<Long> roleIdList) {
        if (null == roleIdList || roleIdList.isEmpty()) {
            return new ArrayList<>(0);
        }

        Set<String> members;
        if (roleIdList.size() == 1) {
            //如果用户只有一个角色，直接按照key值获取
            members = setOperations.members(getIdentity(roleIdList.get(0)));
        } else {
            //如果用户存在多个角色，则需要通过union，计算出所有资源的并集
            String firstKey = getIdentity(roleIdList.get(0));
            members = setOperations.union(firstKey, roleIdList.subList(1, roleIdList.size()).stream().map(this::getIdentity).collect(Collectors.toList()));
        }
        return members.stream().map(Long::valueOf).collect(Collectors.toList());
    }

    private String getIdentity(Long identity) {
        return getRedisKey() + ":" + identity;
    }
}
