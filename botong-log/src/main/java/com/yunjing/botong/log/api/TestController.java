package com.yunjing.botong.log.api;

import com.yunjing.botong.log.constant.LogConstant;
import com.yunjing.botong.log.mapper.LogTemplateFieldMapper;
import com.yunjing.botong.log.params.DangParam;
import com.yunjing.botong.log.params.UserInfoModel;
import com.yunjing.botong.log.processor.okhttp.AppCenterService;
import com.yunjing.botong.log.vo.AppPushParam;
import com.yunjing.botong.log.vo.LogTemplateFieldVo;
import com.yunjing.mommon.base.PushParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/30.
 */
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private AppCenterService appCenterService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private LogTemplateFieldMapper logTemplateFieldMapper;

    @PostConstruct
    public void init() {
    }


    @GetMapping("/field")
    public List<LogTemplateFieldVo> field() {
        List<LogTemplateFieldVo> fieldVos = logTemplateFieldMapper.queryFields(null, null, "1", LogConstant.BOTONG_ONE_STR);
        return fieldVos;
    }

    @RequestMapping("/push")
    public String push() {
        // 创建请求参数
        AppPushParam param = new AppPushParam();
        param.setTitle("Title");
        param.setNotificationTitle("NotificationTitle");
        param.setAlias(new String[]{"6386505037916409856"});
        appCenterService.push(param);
        return "success";
    }

    @RequestMapping("/dang")
    public String dang() {
        return "success";
    }


    @RequestMapping("/is-manager")
    public String isManager() {
        appCenterService.isManager("611564163546121654982", "6386505038969180166");
        return "success";
    }


    @RequestMapping("/find-all-org-member")
    public String findAllOrgMember() {
        appCenterService.findAllOrgMember("6384295807801102336", true);
        return "success";
    }

    @RequestMapping("/manage-scope")
    public Object manageScope() {
        return appCenterService.manageScope("226f65e5f6b0466cbbb131b9d9308b52", "6386505038969180166");
    }


    @RequestMapping("/test")
    public Object test() {
        List<Object> list = new ArrayList<>();
        list.add("6386837899156918272");
        list.add("6386821498899795968");
        list.add("6386837057284608000");
        List<Object> list1 = redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] rawKey = redisTemplate.getStringSerializer().serialize("botong:org:member");
                for (Object s : list) {

                    byte[] bytes = connection.hGet(rawKey, redisTemplate.getStringSerializer().serialize(s.toString()));

                    String deserialize = redisTemplate.getStringSerializer().deserialize(bytes);
                    log.info(deserialize);
                }


                return null;
            }
        });

        return list1;
    }
}
