package com.yunjing.botong.log.processor.mq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.redis.share.UserInfo;
import com.yunjing.botong.log.cache.MemberRedisOperator;
import com.yunjing.botong.log.config.AbstractRedisConfiguration;
import com.yunjing.botong.log.config.LogConstant;
import com.yunjing.botong.log.dao.LogReportDao;
import com.yunjing.botong.log.params.DangParam;
import com.yunjing.botong.log.params.UserInfoModel;
import com.yunjing.botong.log.processor.mq.configuration.RemindMessageConfiguration;
import com.yunjing.botong.log.processor.okhttp.AppCenterService;
import com.yunjing.botong.log.service.ISMSService;
import com.yunjing.botong.log.vo.AppPushParam;
import com.yunjing.botong.log.vo.Member;
import com.yunjing.botong.log.vo.RemindVo;
import com.yunjing.message.annotation.MessageQueueDeclarable;
import com.yunjing.message.declare.consumer.AbstractMessageConsumerWithQueueDeclare;
import com.yunjing.message.model.Message;
import com.yunjing.mommon.base.SmSParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.*;

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


    private final static String[] MESSAGE = {"您当日的日报尚未提交，请及时提交。", "您本周的周报尚未提交，请及时提交。", "您本月的月报尚未提交，请及时提交。"};

    private final static String[] SMS_TEMPLATE = {"当日的日报", "本周的周报", "本月的月报"};


    @Value("${botong.log.write-log}")
    private String writeLog;

    @Value("${botong.log.appId}")
    private String appId;

    @Autowired
    private LogReportDao logReportDao;

    /**
     * 应用中心
     */
    @Autowired
    private AppCenterService appCenterService;

    @Autowired
    private MemberRedisOperator redisOperator;

    /**
     * 短信
     */
    @Autowired
    private ISMSService smsService;

    @Autowired
    private AbstractRedisConfiguration redisReadonly;


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

        StringRedisTemplate redisTemplate = redisReadonly.getTemple();

        log.error("接收任务调度参数:{}", JSON.toJSONString(message));
        String memberId = message.getWhat();

        Map<String, String> map = (Map<String, String>) JSONObject.parse(message.getObj().toString());
        Map<String, Object> data = JSON.parseObject(map.get("record"), Map.class);
        String appId = String.valueOf(data.get("appId"));
        int submitType = Integer.parseInt(String.valueOf(data.get("submitType")));
        Object json = null;
        switch (submitType) {
            case 1:
                json = redisTemplate.opsForHash().get(LogConstant.LOG_SET_DAY_REMIND + appId, memberId);
                break;
            case 2:
                json = redisTemplate.opsForHash().get(LogConstant.LOG_SET_WEEK_REMIND + appId, memberId);
                break;
            case 3:
                json = redisTemplate.opsForHash().get(LogConstant.LOG_SET_MONTH_REMIND + appId, memberId);
                break;
            default:
                break;
        }
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
        StringRedisTemplate redisTemplate = redisReadonly.getTemple();
        Member memberInfo = JSON.parseObject(String.valueOf(redisTemplate.opsForHash().get(LogConstant.LOG_MEMBER_INFO, memberId)), Member.class);

        // 保存设置时不是管理员不需要校验管理员
        if (remind.getIsManager() == 0) {
            if (memberInfo != null) {
                // 不是管理员，根据remindMode提醒
                switch (remind.getRemindMode()) {
                    case 1:
                        push(memberInfo.getCompanyId(), new String[]{memberInfo.getPassportId()}, remind.getSubmitType(), null);
                        break;
                    case 2:
                        List<String> phoneNumbers = new ArrayList<>();
                        phoneNumbers.add(memberInfo.getMobile());
                        sms(phoneNumbers, remind.getSubmitType());
                        break;
                    case 3:
                        // dang
                        List<UserInfoModel> infoModels = new ArrayList<>();
                        dang(infoModels, memberInfo.getPassportId(), memberInfo.getMobile(), remind.getSubmitType());
                        break;
                    default:
                        break;
                }
            } else {
                log.error("用户信息不存在!!!");
            }
        } else {
            // 设置提醒时是管理员，校验是否是管理员
            boolean isManager = appCenterService.isManager(appId, memberId);
            if (isManager) {
                // 3. 如果是管理员

                Date date = new Date();
                String current = DateFormatUtils.format(date, "yyyy-MM-dd");
                // 3.2 查询出所有管理的人员
                List<Member> memberInfos = appCenterService.manageScope(appId, memberId);
                // 3.3 3.1与3.2结果取交集，剩下的则是没有发送日志的
                List<String> manageScopeList = new ArrayList<>();
                for (Member info : memberInfos) {
                    manageScopeList.add(info.getId());
                }
                // 3.1 查询出当天所有已经发送日志的人员
                Set<String> submitList = logReportDao.submitList(memberInfo.getCompanyId(), remind.getSubmitType(), current, manageScopeList);

                // 求交集
                Collection intersection = CollectionUtils.intersection(submitList, manageScopeList);

                // 去除已经发送的
                manageScopeList.removeAll(intersection);


                Set<Object> set = new HashSet<>(manageScopeList);

                List<Object> list = redisTemplate.opsForHash().multiGet(LogConstant.LOG_MEMBER_INFO, set);
                List<String> passportIdList = new ArrayList<>();
                List<String> phoneNumbers = new ArrayList<>();
                for (Object o : list) {
                    Member member = JSON.parseObject(String.valueOf(o), Member.class);
                    passportIdList.add(member.getPassportId());
                    phoneNumbers.add(member.getMobile());
                }
                // 3.4 根据remindMode处理3.3的结果

                // 管理员，根据remindMode提醒
                switch (remind.getRemindMode()) {
                    case 1:
                        String[] idList = new String[passportIdList.size()];
                        for (int i = 0; i < passportIdList.size(); i++) {
                            idList[i] = passportIdList.get(i);
                        }
                        UserInfo info = redisOperator.getUserInfo(redisOperator.getMember(memberId).getPassportId());
                        push(memberInfo.getCompanyId(), idList, remind.getSubmitType(), info != null ? info.getNick() : null);
                        break;
                    case 2:
                        sms(phoneNumbers, remind.getSubmitType());
                        break;
                    case 3:
                        // dang
                        List<UserInfoModel> infoModels = new ArrayList<>();
                        UserInfoModel userInfoModel;
                        for (int i = 0; i < phoneNumbers.size(); i++) {
                            userInfoModel = new UserInfoModel();
                            userInfoModel.setUserId(passportIdList.get(i));

                            infoModels.add(userInfoModel);
                        }
                        dang(infoModels, memberInfo.getPassportId(), memberInfo.getMobile(), remind.getSubmitType());
                        break;
                    default:
                        break;
                }


            } else {
                // 已经不是管理员，不做任何处理
                log.warn("已经不是管理员!");
            }
        }
    }


    /**
     * 推送
     *
     * @param companyId
     * @param alias
     * @param submitType
     * @param managerName
     */
    private void push(String companyId, String[] alias, int submitType, String managerName) {

        AppPushParam pushParam = new AppPushParam();

        pushParam.setAppId(appId);
        pushParam.setCompanyId(companyId);

        if (StringUtils.isNotEmpty(managerName)) {
            pushParam.setMsg(managerName + "提醒您：" + MESSAGE[submitType - 1]);
        } else {
            pushParam.setMsg(MESSAGE[submitType - 1]);
        }
        pushParam.setTitle("您收到一条日志提醒");
        pushParam.setNotificationTitle("您收到一条日志提醒");
        pushParam.setAlias(alias);


        Map<String, String> map = new HashMap<>(2);
        map.put("subModuleName", "日报提醒");
        map.put("url", writeLog);

        //日志提醒
        JSONArray array = new JSONArray();
        JSONObject json = new JSONObject();
        json.put("subTitle", "您收到一条日报提醒");
        json.put("type", "5");

        array.add(json);
        map.put("content", array.toJSONString());
        pushParam.setMap(map);

        appCenterService.push(pushParam);
    }

    /**
     * 发送短信
     *
     * @param phoneNumbers
     * @param submitType
     */
    private void sms(List<String> phoneNumbers, int submitType) {
        // sms
        SmSParam param = new SmSParam();
        param.setPhoneNumbers(phoneNumbers);
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        //发送的内容
        map.put("message", SMS_TEMPLATE[submitType - 1]);
        param.setMapParam(map);
        param.setSignName("伯通");
        param.setTemplateId("SMS_130830537");
        smsService.sendSmSMessage(phoneNumbers, param.getTemplateId(), param.getSignName(), param.getMapParam());
    }

    /**
     * dang
     *
     * @param infoModels
     * @param userId
     * @param mobile
     * @param submitType
     */
    private void dang(List<UserInfoModel> infoModels, String userId, String mobile, int submitType) {
        DangParam param = new DangParam();
        param.setIsAccessory(0);
        param.setSendTelephone(Long.parseLong(mobile));
        param.setUserId(userId);
        param.setReceiveBody(infoModels);
        param.setDangType(1);
        param.setRemindType(1);
        param.setSendType(1);
        param.setBizId(UUID.randomUUID().toString().replace("-", ""));
        param.setSendContent(MESSAGE[submitType - 1]);
        appCenterService.dang(param);
    }
}
