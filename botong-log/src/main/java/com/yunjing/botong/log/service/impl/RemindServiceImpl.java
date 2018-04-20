package com.yunjing.botong.log.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.common.mybatis.service.impl.BaseServiceImpl;
import com.google.gson.Gson;
import com.yunjing.botong.log.config.AbstractRedisConfiguration;
import com.yunjing.botong.log.config.CycleType;
import com.yunjing.botong.log.config.LogConstant;
import com.yunjing.botong.log.entity.RemindEntity;
import com.yunjing.botong.log.mapper.RemindMapper;
import com.yunjing.botong.log.params.SchedulerParam;
import com.yunjing.botong.log.processor.mq.configuration.RemindMessageConfiguration;
import com.yunjing.botong.log.processor.okhttp.AppCenterService;
import com.yunjing.botong.log.service.IRemindService;
import com.yunjing.botong.log.vo.RemindVo;
import com.yunjing.mommon.global.exception.ParameterErrorException;
import com.yunjing.mommon.utils.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

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

    @Value("${botong.log.appId}")
    private String appId;

    @Autowired
    private AbstractRedisConfiguration redisLog;

    @Autowired
    private AppCenterService appCenterService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdate(RemindVo remind) {
        log.info("保存提醒接口参数：{}", JSON.toJSONString(remind));
        int res;
        RemindEntity entity = new RemindEntity();

        // 该成员在该企业的该模版类型下是否已经设置提醒
        entity.setMemberId(remind.getMemberId());
        entity.setAppId(appId);
        entity.setOrgId(remind.getOrgId());
        entity.setSubmitType(remind.getSubmitType());

        entity = baseMapper.selectOne(entity);
        if (entity == null) {
            entity = new RemindEntity();
            entity.beforeInsert();
            entity.setAppId(appId);
            BeanUtils.copy(remind, entity);
            res = baseMapper.insert(entity);
        } else {
            BeanUtils.copy(remind, entity);
            entity.setUpdateTime(System.currentTimeMillis());
            entity.setAppId(appId);
            Wrapper<RemindEntity> wrapper = new EntityWrapper<>();
            wrapper.eq("member_id", remind.getMemberId())
                    .and()
                    .eq("app_id", appId)
                    .and()
                    .eq("org_id", remind.getOrgId())
                    .and()
                    .eq("submit_type", remind.getSubmitType());
            res = baseMapper.update(entity, wrapper);
        }
        boolean flag = res > 0;
        if (flag) {
            setTask(remind);
        }
        return flag;
    }

    @Override
    public RemindVo info(String memberId, String appId, int submitType) {
        StringRedisTemplate redisTemplate = redisLog.getTemple();
        switch (submitType) {
            case 1:
                return JSON.parseObject(String.valueOf(redisTemplate.opsForHash().get(LogConstant.LOG_SET_DAY_REMIND + appId, memberId)), RemindVo.class);
            case 2:
                return JSON.parseObject(String.valueOf(redisTemplate.opsForHash().get(LogConstant.LOG_SET_WEEK_REMIND + appId, memberId)), RemindVo.class);
            case 3:
                return JSON.parseObject(String.valueOf(redisTemplate.opsForHash().get(LogConstant.LOG_SET_MONTH_REMIND + appId, memberId)), RemindVo.class);
            default:
                return null;
        }
    }

    @Override
    public boolean updateByMemberIdAndAppId(String taskId, String memberId, String appId) {
        RemindEntity entity = new RemindEntity();
        entity.setAppId(appId);
        entity.setMemberId(memberId);
        entity.setTaskId(taskId);
        Integer result = baseMapper.update(entity, new EntityWrapper<RemindEntity>().eq("app_id", appId).and().eq("member_id", memberId));
        return result > 0;
    }

    /**
     * 添加到任务队列
     *
     * @param remind
     */
    private void setTask(RemindVo remind) {
        log.info("提醒参数：{}", JSON.toJSONString(remind));
        StringRedisTemplate redisTemplate = redisLog.getTemple();
        Gson gson = new Gson();
        String key = String.valueOf(remind.getMemberId());

        // topic
        String jobTitle = RemindMessageConfiguration.REMIND_QUEUE_NAME;
        String taskId = null;
        RemindVo vo = null;
        switch (remind.getSubmitType()) {
            case 1:
                vo = gson.fromJson(String.valueOf(redisTemplate.opsForHash().get(LogConstant.LOG_SET_DAY_REMIND + appId, key)), RemindVo.class);
                break;
            case 2:
                vo = gson.fromJson(String.valueOf(redisTemplate.opsForHash().get(LogConstant.LOG_SET_WEEK_REMIND + appId, key)), RemindVo.class);
                break;
            case 3:
                vo = gson.fromJson(String.valueOf(redisTemplate.opsForHash().get(LogConstant.LOG_SET_MONTH_REMIND + appId, key)), RemindVo.class);
                break;
            default:
                break;
        }

        if (vo != null) {
            taskId = vo.getTaskId();
        }
        SchedulerParam param = new SchedulerParam();
        if (CycleType.DAY.toString().equalsIgnoreCase(remind.getCycleType())) {
            param.setCycle("*");
            param.setCycleType(CycleType.WEEK.toString());

        } else if (CycleType.WEEK.toString().equalsIgnoreCase(remind.getCycleType())) {
            // 周报提醒
            param.setCycle(remind.getCycle());
            param.setCycleType(CycleType.WEEK.toString());

        } else if (CycleType.MONTH.toString().equalsIgnoreCase(remind.getCycleType())) {

            param.setCycle(remind.getCycle());
            param.setCycleType(CycleType.DAY.toString());
        }


        param.setOutKey(key);
        Map<String, Object> map = new HashMap<>(2);
        map.put("appId", appId);
        map.put("submitType", remind.getSubmitType());
        param.setRecord(JSON.toJSONString(map));
        param.setRemark("");
        param.setJobTime(remind.getJobTime());
        param.setJobTitle(jobTitle);
        if (taskId != null) {
            param.setId(taskId);
        }
        log.info("设置任务调度参数:{}", gson.toJson(param));
        taskId = appCenterService.setTask(param);

        if (taskId != null) {
            boolean flag = updateByMemberIdAndAppId(taskId, remind.getMemberId(), appId);
            if (flag) {
                remind.setTaskId(taskId);
                String value = gson.toJson(remind);

                switch (remind.getSubmitType()) {
                    case 1:
                        // 日报
                        redisTemplate.opsForHash().put(LogConstant.LOG_SET_DAY_REMIND + appId, key, value);
                        break;
                    case 2:
                        // 周报
                        redisTemplate.opsForHash().put(LogConstant.LOG_SET_WEEK_REMIND + appId, key, value);
                        break;
                    case 3:
                        // 月报
                        redisTemplate.opsForHash().put(LogConstant.LOG_SET_MONTH_REMIND + appId, key, value);
                        break;
                    default:
                        break;
                }
            }
        } else {
            throw new ParameterErrorException("设置任务失败！");
        }
    }
}