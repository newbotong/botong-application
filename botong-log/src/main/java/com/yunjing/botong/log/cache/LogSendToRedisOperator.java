package com.yunjing.botong.log.cache;

import com.yunjing.botong.log.config.LogConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @version: 1.0.0
 * @author: yangc
 * @date: 2018/5/21 14:47
 * @description:
 */
@Component
@Slf4j
public class LogSendToRedisOperator {

    @Resource(name = "redisLogTemplate")
    private StringRedisTemplate temple;

    /**
     * 获取缓存日志接收人
     * @param memberId 日志发送人
     * @param logTempId 日志模板编号
     */
    public Set<String> getToUser(String memberId, String logTempId) {
        return get(getToUserKey(memberId), logTempId);
    }

    /**
     * 获取缓存日志接收群
     * @param memberId 日志发送人
     * @param logTempId 日志模板编号
     */
    public Set<String> getToGroup(String memberId, String logTempId) {
        return get(getToGroupKey(memberId), logTempId);
    }

    /**
     * 获取日志接收人缓存key
     * @param memberId 成员编号
     * @return
     */
    private String getToUserKey(String memberId) {
        return LogConstant.LOG_SEND_TO_USER + memberId;
    }

    /**
     * 获取日志接收群组缓存key
     * @param memberId 成员编号
     * @return
     */
    private String getToGroupKey(String memberId) {
        return LogConstant.LOG_SEND_TO_GROUP + memberId;
    }

    /**
     * 缓存日志接收人
     * @param memberId 日志发送人
     * @param logTempId 日志模板编号
     * @param sendToUser 日志接收人
     */
    public void putToUser(String memberId, String logTempId, Set<String> sendToUser) {
        put(getToUserKey(memberId), logTempId, sendToUser);
    }

    /**
     * 缓存日志接收群
     * @param memberId 日志发送人
     * @param logTempId 日志模板编号
     * @param sendToGroup 日志接收群组
     */
    public void putToGroup(String memberId, String logTempId, Set<String> sendToGroup) {
        put(getToGroupKey(memberId), memberId, sendToGroup);
    }

    /**
     * 缓存日志接收集合
     * @param key 缓存key
     * @param memberId 成员编号
     * @param idSet id集合
     */
    private void put(String key, String memberId, Set<String> idSet) {
        if(CollectionUtils.isNotEmpty(idSet)) {
            String[] ids = idSet.toArray(new String[idSet.size()]);
            temple.opsForHash().put(key, memberId, StringUtils.join(ids, com.yunjing.botong.log.constant.LogConstant.SEPARATE_STR));
        }
    }

    /**
     * 获取缓存日志接收集合
     * @param key 缓存key
     * @param memberId 成员编号
     * @return
     */
    private Set<String> get(String key, String memberId) {
        Object o = temple.opsForHash().get(key, memberId);
        if(o != null) {
            String[] ids = StringUtils.split((String)o, com.yunjing.botong.log.constant.LogConstant.SEPARATE_STR);
            return new HashSet(Arrays.asList(ids));
        }
        return null;
    }

}
