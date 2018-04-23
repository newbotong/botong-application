package com.yunjing.sign.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.common.redis.share.UserInfo;
import com.yunjing.sign.beans.vo.SignUserInfoVO;
import com.yunjing.sign.constant.SignConstant;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author auth
 * @date 2018/4/10 17:23
 */
@Component
public class MemberRedisOperator {

    @Resource(name = "redisReadonlyTemplate")
    private StringRedisTemplate template;

    public SignUserInfoVO getMember(String memberId) {
        return JSON.parseObject(String.valueOf(template.opsForHash().get(SignConstant.BOTONG_ORG_MEMBER, memberId)), SignUserInfoVO.class);
    }

    public UserInfo getUserInfo(String passportId) {
        return JSON.parseObject(String.valueOf(template.opsForHash().get(SignConstant.BOTONG_ORG_USER, passportId)), UserInfo.class);
    }

    public List<SignUserInfoVO> getMemberList(Set userIds) {
        Type memberType = new TypeReference<List<SignUserInfoVO>>() {
        }.getType();
        List listT = template.opsForHash().multiGet(SignConstant.BOTONG_ORG_MEMBER, userIds);
        List<SignUserInfoVO> list = new ArrayList<>();
        if (listT != null && !listT.isEmpty()) {
            list = JSON.parseObject(listT.toString(), memberType);
        }
        Set<Object> passportIds = new HashSet<>();
        Map<String, SignUserInfoVO> map = new HashMap<>(16);
        for (SignUserInfoVO member : list) {
            if (member == null) {
                continue;
            }
            passportIds.add(member.getUserId());
            map.put(member.getUserId(), member);
        }
        List listPassport = template.opsForHash().multiGet(SignConstant.BOTONG_ORG_USER, passportIds);
        Type passportType = new TypeReference<List<UserInfo>>() {
        }.getType();
        List<UserInfo> passportList;
        if (listPassport != null && !listPassport.isEmpty()) {
            passportList = JSON.parseObject(listPassport.toString(), passportType);
            for (UserInfo userInfo : passportList) {
                map.get(userInfo.getPassportId()).setColor(userInfo.getColor());
                map.get(userInfo.getPassportId()).setProfile(userInfo.getProfile());
                map.get(userInfo.getPassportId()).setName(userInfo.getNick());
            }
        }
        return new ArrayList<>(map.values());
    }

}

