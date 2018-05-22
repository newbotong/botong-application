package com.yunjing.botong.log.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunjing.botong.log.cache.LogSendToRedisOperator;
import com.yunjing.botong.log.cache.MemberRedisOperator;
import com.yunjing.botong.log.entity.LogDetail;
import com.yunjing.botong.log.params.LogItemParam;
import com.yunjing.botong.log.params.LogParam;
import com.yunjing.botong.log.processor.okhttp.AppCenterService;
import com.yunjing.botong.log.service.LogService;
import com.yunjing.botong.log.service.LogTemplateService;
import com.yunjing.botong.log.vo.AppPushParam;
import com.yunjing.botong.log.vo.LogTemplateFieldVo;
import com.yunjing.botong.log.vo.LogTemplateVo;
import com.yunjing.botong.log.vo.Member;
import com.yunjing.mommon.global.exception.BaseRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日志服务实现
 *
 * @author 王开亮
 * @date 2018/4/9 9:53
 */
@Slf4j
@Service
public class LogServiceImpl implements LogService {

    @Value("${botong.log.look-log}")
    private String lookLog;

    /**
     * http://192.168.10.89:1300/#/logShare
     * <p>
     * http://192.168.10.89:1300/#/logShare?logId=xxxxxx&userId=这里是menberId
     */
    @Value("${botong.log.logShare}")
    private String logShare;

    @Autowired
    private LogTemplateService logTemplateService;

    @Autowired
    private LogSendToRedisOperator sendToRedisOperator;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AppCenterService appCenterService;

    @Autowired
    private MemberRedisOperator memberRedisOperator;

    @Value("${botong.log.appId}")
    private String appId;


    @Override
    public String createLog(LogParam logParam) {
        LogDetail entity = new LogDetail();
        LogTemplateVo vo = this.logTemplateService.queryLogTemplate(logParam.getTemplateId());
        entity.fromParam(logParam, vo);
        mongoTemplate.insert(entity);
        Member member = memberRedisOperator.getMember(logParam.getMemberId());
        if (member == null) {
            throw new BaseRuntimeException(500, "用户信息不存在！");
        }
        AppPushParam param = new AppPushParam();

        param.setAppId(appId);
        param.setCompanyId(member.getCompanyId());

        String title = member.getName() + "向你提交了日志：" + vo.getName() + "，请及时查阅！";
        param.setMsg(title);
        param.setTitle(title);
        param.setNotificationTitle(title);

        String[] userIdArray = new String[logParam.getSendToUser().size()];
        logParam.getSendToUser().toArray(userIdArray);

        log.info("提交日志 SendToUser:{}", JSON.toJSONString(logParam.getSendToUser()));

        List<Member> list = memberRedisOperator.getMemberList(logParam.getSendToUser());
        List<String> passportIdList = new ArrayList<>();
        String[] passportIdArray = new String[list.size()];
        for (Member m : list) {
            if (m != null) {
                passportIdList.add(m.getPassportId());
            }
        }
        for (int i = 0; i < passportIdList.size(); i++) {
            passportIdArray[i] = passportIdList.get(i);
        }
        param.setAlias(passportIdArray);
        sendToRedisOperator.putToUser(logParam.getMemberId(), logParam.getTemplateId(), logParam.getSendToUser());
        sendToRedisOperator.putToGroup(logParam.getMemberId(), logParam.getTemplateId(), logParam.getSendToGroup());

        Map<String, String> map = new HashMap<>(2);
        map.put("subModuleName", "日报提醒");
        map.put("url", logShare + "?logId=" + entity.getLogId() + "&userId=" + logParam.getMemberId());

        //日志提醒
        JSONArray array = new JSONArray();
        JSONObject json;

        json = new JSONObject();
        String logType;
        switch (vo.getSubmitType()) {
            case 1:
                logType = "日";
                break;
            case 2:
                logType = "周";
                break;
            case 3:
                logType = "月";
                break;
            case 4:
                logType = "季";
                break;
            case 5:
                logType = "年";
                break;
            default:
                logType = "日";
                break;
        }
        logType = member.getName() + "的" + logType + "报";
        json.put("subTitle", logType);
        json.put("type", "5");
        array.add(json);

        Map<String, String> fieldMap = new HashMap<>(16);
        for (LogTemplateFieldVo fieldVo : vo.getItems()) {
            fieldMap.put(fieldVo.getId(), fieldVo.getFieldLabel());
        }

        for (LogItemParam item : logParam.getLogValues()) {
            json = new JSONObject();
            json.put("title", fieldMap.get(item.getFiledId()));
            json.put("content", item.getValue());
            json.put("type", "0");
            array.add(json);
        }

        map.put("content", array.toJSONString());

        param.setMap(map);

        this.appCenterService.push(param);
        return entity.getLogId();
    }
}
