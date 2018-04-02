package com.yunjing.botong.log.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.common.mybatis.service.impl.BaseServiceImpl;
import com.google.gson.Gson;
import com.yunjing.botong.log.config.LogConstant;
import com.yunjing.botong.log.entity.RemindEntity;
import com.yunjing.botong.log.mapper.RemindMapper;
import com.yunjing.botong.log.processor.feign.handle.SchedulerFeignClient;
import com.yunjing.botong.log.processor.feign.param.SchedulerParam;
import com.yunjing.botong.log.processor.mq.configuration.RemindMessageConfiguration;
import com.yunjing.botong.log.service.IRemindService;
import com.yunjing.botong.log.vo.RemindVo;
import com.yunjing.mommon.constant.StatusCode;
import com.yunjing.mommon.global.exception.ParameterErrorException;
import com.yunjing.mommon.global.exception.RequestFailureException;
import com.yunjing.mommon.utils.BeanUtils;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 提醒 服务实现类
 * </p>
 *
 * @author tao.zeng.
 * @since 2018-03-28.
 */
@Slf4j
@Service
public class RemindServiceImpl extends BaseServiceImpl<RemindMapper, RemindEntity> implements IRemindService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 任务调度
     */
    @Autowired
    private SchedulerFeignClient schedulerFeignClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdate(RemindVo remind) {
        int res;
        RemindEntity entity = new RemindEntity();

        // 该成员在该企业的该模版类型下是否已经设置提醒
        entity.setMemberId(remind.getMemberId());
        entity.setAppId(remind.getAppId());
        entity.setSubmitType(remind.getSubmitType());

        entity = baseMapper.selectOne(entity);
        // 没有保存过设置则新增
        if (entity == null) {
            entity = new RemindEntity();
            entity.beforeInsert();
            BeanUtils.copy(remind, entity);
            res = baseMapper.insert(entity);
        } else {
            // 修改
            BeanUtils.copy(remind, entity);
            entity.setUpdateTime(System.currentTimeMillis());
            Wrapper<RemindEntity> wrapper = new EntityWrapper<>();
            wrapper.eq("member_id", remind.getMemberId())
                    .and()
                    .eq("app_id", remind.getAppId())
                    .and()
                    .eq("submit_type", remind.getSubmitType());

            res = baseMapper.update(entity, wrapper);
        }
        boolean flag = res > 0;
        if (flag) {
            String key = String.valueOf(remind.getMemberId());
            String value = new Gson().toJson(remind);
            redisTemplate.opsForHash().put(LogConstant.LOG_SET_REMIND + remind.getAppId(), key, value);

            task(key, value, remind);
        }
        return flag;
    }

    @Override
    public RemindVo info(String memberId, String appId) {
        return JSON.parseObject(redisTemplate.opsForHash().get(LogConstant.LOG_SET_REMIND + appId, memberId).toString(), RemindVo.class);
    }

    /**
     * 添加到任务队列
     *
     * @param key
     * @param value
     * @param remind
     */
    private void task(String key, String value, RemindVo remind) {
        SchedulerParam param = new SchedulerParam();
        param.setCycle(remind.getCycle());
        param.setCycleType(remind.getCycleType());
        param.setOutKey(key);
        param.setRecord(remind.getAppId());
        param.setRemark(value);
        param.setJobTime(remind.getJobTime());

        // topic
        param.setJobTitle(RemindMessageConfiguration.REMIND_QUEUE_NAME);

        log.info("任务调度参数:{}", JSON.toJSONString(param));

        ResponseEntityWrapper response;
        if (remind.getRemindSwitch() == 0) {
            response = schedulerFeignClient.cancel(param);
        } else if (remind.getRemindSwitch() == 1) {
            response = schedulerFeignClient.set(param);
        } else {
            throw new ParameterErrorException("参数错误!");
        }

        log.info("设置任务结果:{}", JSON.toJSONString(response));
        if (StatusCode.SUCCESS.getStatusCode() != response.getStatusCode()) {
            throw new RequestFailureException(response.getStatusCode(), response.getStatusMessage());
        }
    }
}
