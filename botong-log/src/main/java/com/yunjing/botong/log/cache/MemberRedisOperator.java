package com.yunjing.botong.log.cache;

import com.alibaba.fastjson.JSON;
import com.yunjing.botong.log.config.LogConstant;
import com.yunjing.botong.log.vo.Member;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author auth
 * @date 2018/4/10 17:23
 */
@Component
public class MemberRedisOperator {

    @Resource(name = "redisReadonlyTemplate")
    private StringRedisTemplate template;

    public Member getMember(String memberId) {
        return JSON.parseObject(String.valueOf(template.opsForHash().get(LogConstant.LOG_MEMBER_INFO, memberId)), Member.class);
    }

}
