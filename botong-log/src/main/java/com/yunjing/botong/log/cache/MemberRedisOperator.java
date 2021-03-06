package com.yunjing.botong.log.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.common.redis.share.UserInfo;
import com.yunjing.botong.log.config.LogConstant;
import com.yunjing.botong.log.vo.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author 王开亮
 * @date 2018/4/10 17:23
 */
@Component
@Slf4j
public class MemberRedisOperator {

    @Resource(name = "redisReadonlyTemplate")
    private StringRedisTemplate template;

    public Member getMember(String memberId) {
        return JSON.parseObject(String.valueOf(template.opsForHash().get(LogConstant.LOG_MEMBER_INFO, memberId)), Member.class);
    }

    public UserInfo getUserInfo(String passportId) {
        return JSON.parseObject(String.valueOf(template.opsForHash().get(com.yunjing.botong.log.constant.LogConstant.BOTONG_ORG_USER, passportId)), UserInfo.class);
    }

    public List<Member> getMemberList(Set userIds) {
        Type memberType = new TypeReference<List<Member>>() {
        }.getType();
        List listT = template.opsForHash().multiGet(com.yunjing.botong.log.constant.LogConstant.BOTONG_ORG_MEMBER, userIds);
        List<Member> list = new ArrayList<>();
        if (listT != null && !listT.isEmpty()) {
            list = JSON.parseObject(listT.toString(), memberType);
        }
        Set<Object> passportIds = new HashSet<>();
        Map<String, Member> map = new HashMap<>(16);
        for (Member member : list) {
            if (member == null) {
                continue;
            }
            member.setRealName(member.getName());
            passportIds.add(member.getPassportId());
            map.put(member.getPassportId(), member);
        }
        List listPassport = template.opsForHash().multiGet(com.yunjing.botong.log.constant.LogConstant.BOTONG_ORG_USER, passportIds);
        Type passportType = new TypeReference<List<UserInfo>>() {
        }.getType();
        List<UserInfo> passportList;
        if (listPassport != null && !listPassport.isEmpty()) {
            passportList = JSON.parseObject(listPassport.toString(), passportType);
            for (UserInfo userInfo : passportList) {
                map.get(userInfo.getPassportId()).setColor(userInfo.getColor());
                map.get(userInfo.getPassportId()).setProfile(userInfo.getProfile());
                //map.get(userInfo.getPassportId()).setName(userInfo.getNick());
            }
        }
        return new ArrayList<>(map.values());
    }

}

