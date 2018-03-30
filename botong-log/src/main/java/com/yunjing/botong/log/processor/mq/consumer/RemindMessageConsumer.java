package com.yunjing.botong.log.processor.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunjing.botong.log.config.LogConstant;
import com.yunjing.botong.log.processor.feign.handle.DangFeignClient;
import com.yunjing.botong.log.processor.feign.handle.OrgStructureFeignClient;
import com.yunjing.botong.log.processor.feign.handle.ThirdPartFeignClient;
import com.yunjing.botong.log.processor.feign.param.DangParam;
import com.yunjing.botong.log.processor.mq.configuration.RemindMessageConfiguration;
import com.yunjing.botong.log.vo.RemindVo;
import com.yunjing.message.annotation.MessageQueueDeclarable;
import com.yunjing.message.declare.consumer.AbstractMessageConsumerWithQueueDeclare;
import com.yunjing.message.model.Message;
import com.yunjing.mommon.base.PushParam;
import com.yunjing.mommon.base.SmSParam;
import com.yunjing.mommon.constant.StatusCode;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * <p>
 * <p> 提醒消息接受者
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/27.
 */
@Slf4j
@Component
@MessageQueueDeclarable
public class RemindMessageConsumer extends AbstractMessageConsumerWithQueueDeclare<Message, RemindMessageConfiguration> {

    /**
     * 组织结构rpc
     */
    @Autowired
    private OrgStructureFeignClient orgStructureFeignClient;

    /**
     * 第三方服务rpc
     */
    @Autowired
    private ThirdPartFeignClient thirdPartFeignClient;

    /**
     * dang 服务rpc
     */
    @Autowired
    private DangFeignClient dangFeignClient;


    @Autowired
    private StringRedisTemplate redisTemplate;

    public RemindMessageConsumer(RemindMessageConfiguration configuration) {
        super(configuration);
    }

    /**
     * 1. 校验当前member是否是管理员
     * <p>
     * 2. 如果不是管理员，根据remindMode提醒
     * <p>
     * 3. 如果是管理员
     * 3.1 查询出当天所有已经发送日志的人员
     * 3.2 查询出所有管理的人员
     * 3.3 3.1与3.2结果取交集，剩下的则是没有发送日志的
     * 3.4 根据remindMode处理3.3的结果
     * <p>
     * remindMode：提醒方式（0-应用内推送，1-短信，2-dang）
     *
     * @param message
     */

    @Override
    public void onMessageReceive(@Payload Message message) {
        log.info("接收任务调度参数:{}", JSON.toJSONString(message));
        String memberId = message.getWhat();

        Map<String, String> map = (Map<String, String>) JSONObject.parse(message.getObj().toString());
        String appId = map.get("record");

        Object json = redisTemplate.opsForHash().get(LogConstant.LOG_SET_REMIND + appId, memberId);
        if (json != null) {
            RemindVo remind = JSON.parseObject(json.toString(), RemindVo.class);
            handler(remind, memberId, appId);
        }
    }

    private void handler(RemindVo remind, String memberId, String appId) {
        // 开关状态
        if (remind.getRemindSwitch() == 0) {
            return;
        }
        ResponseEntityWrapper response = null;

        PushParam pushParam = null;
        SmSParam smSParam = null;
        DangParam dangParam = null;

        // 保存设置时不是管理员不需要校验管理员
        if (remind.getIsManager() == 0) {

            response = orgStructureFeignClient.findMemberInfo(memberId);
            if (verificationResult(response)) {
                // 获取成员信息

            }

            // 不是管理员，根据remindMode提醒
            switch (remind.getRemindMode()) {
                case 0:
                    // push
                    pushParam = new PushParam();
                    pushRemind(pushParam);
                    break;
                case 1:
                    // sms
                    smSParam = new SmSParam();
                    smsRemind(smSParam);
                    break;
                case 2:
                    // dang
                    dangParam = new DangParam();
                    dangRemind(dangParam);
                    break;
                default:
                    break;
            }
        } else {
            // 设置提醒时是管理员，校验是否是管理员
            response = orgStructureFeignClient.isManager(Long.parseLong(memberId), appId);

            if (response.getStatusCode() != StatusCode.SUCCESS.getStatusCode()) {
                verificationResult(response);
                return;
            }

            boolean isManager = (boolean) response.getData();
            if (isManager) {
                // 3. 如果是管理员
                // 3.1 查询出当天所有已经发送日志的人员
                // 3.2 查询出所有管理的人员
                // 3.3 3.1与3.2结果取交集，剩下的则是没有发送日志的
                // 3.4 根据remindMode处理3.3的结果
            } else {
                // 已经不是管理员，不做任何处理
            }
        }
    }


    private void pushRemind(PushParam param) {
        ResponseEntityWrapper response = thirdPartFeignClient.sendByAlias(param);
        verificationResult(response);
    }

    private void smsRemind(SmSParam param) {
        ResponseEntityWrapper response = thirdPartFeignClient.sendSms(param);
        verificationResult(response);
    }

    private void dangRemind(DangParam param) {
        ResponseEntityWrapper response = dangFeignClient.send(param);
        verificationResult(response);
    }

    private boolean verificationResult(ResponseEntityWrapper response) {
        String result = JSON.toJSONString(response);
        if (response.getStatusCode() != StatusCode.SUCCESS.getStatusCode()) {
            log.error("调用rpc失败:{}", result);
            return false;
        } else {
            log.info(result);
            return true;
        }
    }
}
