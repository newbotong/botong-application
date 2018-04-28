package com.yunjing.approval.transfer;

import com.alibaba.fastjson.JSON;
import com.yunjing.approval.config.RedisApproval;
import com.yunjing.approval.config.RedisReadonly;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @version: 1.0.0
 * @author: yangc
 * @date: 2018/4/28 22:29
 * @description:
 */
@Component
public class UserIdToMemberId {

    @Autowired
    MemberRedisService memberRedisService;
    
    @Autowired
    RedisReadonly redisReadonly;
    
    List<MemberVo> list;


    public void init() {
        List<Object> values = redisReadonly.getTemple().opsForHash().values("botong:org:member");
        if(CollectionUtils.isNotEmpty(values)){
            list = values.parallelStream().map(o -> JSON.parseObject(o.toString(), MemberVo.class)).collect(Collectors.toList());
        }
    }

    public String getMemberId(String orgId, String userId) {
        Optional<MemberVo> o = list.parallelStream().filter(m -> orgId.equals(m.getCompanyId()) && userId.equals(m.getPassportId())).findFirst();
        if(o.isPresent()) {
            return o.get().getId();
        }
        return orgId + userId;
    }

}
