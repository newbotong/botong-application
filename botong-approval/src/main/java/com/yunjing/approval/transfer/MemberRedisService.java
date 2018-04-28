package com.yunjing.approval.transfer;

import com.common.redis.BaseRedisService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 曲天宇
 * @date 2018/4/9 15:31
 */
@Component
public class MemberRedisService extends BaseRedisService<MemberVo> {


    @Override
    protected String getRedisKey() {
        return "botong:org:member";
    }

    /**
     * 查看账户id
     *
     * @param memberIds 成员id
     * @return
     */
    public Map<String, MemberVo> getMemberMap(List<String> memberIds) {
        if (CollectionUtils.isNotEmpty(memberIds)) {
            List<MemberVo> list = multiGet(memberIds);
            if (CollectionUtils.isNotEmpty(list)) {
                Map<String, MemberVo> map = list.stream().collect(Collectors.toMap(MemberVo::getId, Function.identity()));
                return map;
            }
        }
        return null;
    }


}

