package com.yunjing.botong.log.processor.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunjing.botong.log.config.LogConstant;
import com.yunjing.botong.log.http.AppCenterService;
import com.yunjing.botong.log.processor.feign.handle.OrgStructureFeignClient;
import com.yunjing.botong.log.processor.feign.param.DangParam;
import com.yunjing.botong.log.processor.feign.param.UserInfoModel;
import com.yunjing.botong.log.processor.mq.configuration.RemindMessageConfiguration;
import com.yunjing.botong.log.service.ISMSService;
import com.yunjing.botong.log.vo.MemberInfo;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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


    private final static String[] CONTENT = {"您当日的日报尚未提交，请及时提交。", "您本周的周报尚未提交，请及时提交。", "您本月的月报尚未提交，请及时提交。"};

    /**
     * 组织结构rpc
     */
    @Autowired
    @SuppressWarnings("all")
    private OrgStructureFeignClient orgStructureFeignClient;

    /**
     * 应用中心
     */
    @Autowired
    private AppCenterService appCenterService;

    /**
     * 短信
     */
    @Autowired
    private ISMSService smsService;


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

        // TODO 临时开关
        if (true) {
            return;
        }

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

        // 保存设置时不是管理员不需要校验管理员
        if (remind.getIsManager() == 0) {

            MemberInfo memberInfo = null;
            response = orgStructureFeignClient.findMemberInfo(memberId);
            if (verificationResult("查询成员信息", response)) {
                // 获取成员信息
                if (response.getData() != null) {
                    memberInfo = JSON.parseObject(response.getData().toString(), MemberInfo.class);
                    memberInfo = new MemberInfo();
                    memberInfo.setEmail("31231231@qq.com");
                    memberInfo.setId("6384302108069335040");
                    memberInfo.setPassportId("1000000000");
                    memberInfo.setCompanyId("6386505037916409856");
                    memberInfo.setName("李双喜");
                    memberInfo.setOrgType("MEMBER");
                    memberInfo.setMobile("18562818246");
                    memberInfo.setPosition("美国");
                }
            }
            if (memberInfo != null) {
                // 不是管理员，根据remindMode提醒
                switch (remind.getRemindMode()) {
                    case 0:
                        String type = remind.getCycleType();
                        push(new String[]{memberInfo.getPassportId()}, type);
                        break;
                    case 1:
                        List<String> phoneNumbers = new ArrayList<>();
                        phoneNumbers.add(memberInfo.getMobile());
                        sms(phoneNumbers);
                        break;
                    case 2:
                        // dang
                        List<UserInfoModel> infoModels = new ArrayList<>();
                        infoModels.add(new UserInfoModel(Long.parseLong(memberInfo.getPassportId()), Long.parseLong(memberInfo.getMobile())));
                        dang(infoModels, Long.parseLong(memberInfo.getPassportId()), Long.parseLong(memberInfo.getMobile()));
                        break;
                    default:
                        break;
                }
            } else {
                log.error("用户信息不存在!!!");
            }
        } else {
            // 设置提醒时是管理员，校验是否是管理员
            response = orgStructureFeignClient.isManager(Long.parseLong(memberId), appId);

            if (response.getStatusCode() != StatusCode.SUCCESS.getStatusCode()) {
                verificationResult("校验是否是管理员", response);
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
                log.warn("已经不是管理员!");
            }
        }
    }


    private void push(String[] alias, String type) {
        PushParam param = new PushParam();
        String title;
        if ("day".equalsIgnoreCase(type)) {
            title = CONTENT[0];
        } else if ("week".equalsIgnoreCase(type)) {
            title = CONTENT[1];
        } else {
            title = CONTENT[2];
        }
        param.setTitle(title);
        param.setNotificationTitle("伯通");
        param.setAlias(alias);
        appCenterService.push(param);
    }


    private void sms(List<String> phoneNumbers) {
        // sms
        SmSParam param = new SmSParam();
        param.setPhoneNumbers(phoneNumbers);
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        //发送的内容
        map.put("name", "该发送日志了!!!");
        param.setMapParam(map);
        param.setSignName("伯通");
        param.setTemplateId("SMS_121165500");
        smsService.sendSmSMessage(phoneNumbers, param.getTemplateId(), param.getSignName(), param.getMapParam());
    }

    private void dang(List<UserInfoModel> infoModels, long userId, long mobile) {
        DangParam param = new DangParam();
        param.setIsAccessory(0);
        param.setSendTelephone(mobile);
        param.setUserId(userId);
        param.setReceiveBody(infoModels);
        param.setDangType(1);
        param.setRemindType(1);
        param.setSendType(1);
        param.setSendContent("dang消息内容");
        appCenterService.dang(param);
    }

    private boolean verificationResult(String rpc, ResponseEntityWrapper response) {
        String result = JSON.toJSONString(response);
        log.warn("调用【{} rpc】结果:{}", rpc, result);
        return response.getStatusCode() == StatusCode.SUCCESS.getStatusCode();
    }
}
