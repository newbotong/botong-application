package com.yunjing.botong.log.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunjing.botong.log.cache.MemberRedisOperator;
import com.yunjing.botong.log.entity.LogDetail;
import com.yunjing.botong.log.params.LogParam;
import com.yunjing.botong.log.processor.okhttp.AppCenterService;
import com.yunjing.botong.log.service.LogService;
import com.yunjing.botong.log.service.LogTemplateService;
import com.yunjing.botong.log.vo.AppPushParam;
import com.yunjing.botong.log.vo.LogTemplateVo;
import com.yunjing.botong.log.vo.Member;
import com.yunjing.mommon.global.exception.BaseRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 日志服务实现
 *
 * @author 王开亮
 * @date 2018/4/9 9:53
 */
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogTemplateService logTemplateService;

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

        param.setTitle(member.getName() + "向你提交了日志：" + vo.getName() + "，请及时查阅！");
        param.setNotificationTitle("伯通");
        String[] userIdArray = new String[logParam.getSendToUser().size()];
        logParam.getSendToUser().toArray(userIdArray);
        param.setAlias(userIdArray);


        Map<String, String> map = new HashMap<>(2);
        map.put("subModuleName", "日报提醒");
        // TODO 看日志地址
        map.put("url", "http://www.rizhi.com");

        //日志提醒
        JSONArray array = new JSONArray();
        JSONObject json = new JSONObject();
        json.put("subTitle", "您收到一条日报提醒");
        json.put("type", "5");
        array.add(json);
        map.put("content", array.toJSONString());

        param.setMap(map);


        this.appCenterService.push(param);
        return entity.getLogId();
    }
}
