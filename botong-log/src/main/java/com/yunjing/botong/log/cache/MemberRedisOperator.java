package com.yunjing.botong.log.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yunjing.botong.log.config.LogConstant;
import com.yunjing.botong.log.vo.Member;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    public List<Member> getMemberList(Set userIds) {
        Type memberType = new TypeReference<List<Member>>() {
        }.getType();
        List listT = template.opsForHash().multiGet(com.yunjing.botong.log.constant.LogConstant.BOTONG_ORG_MEMBER, userIds);
        Member memberVO;
        List<Member> list = new ArrayList<>();
        if (listT != null && !listT.isEmpty()) {
            list = JSON.parseObject(listT.toString(), memberType);
        }
        return list;
    }

}
