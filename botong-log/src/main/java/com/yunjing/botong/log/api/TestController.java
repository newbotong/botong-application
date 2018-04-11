package com.yunjing.botong.log.api;

import com.yunjing.botong.log.params.DangParam;
import com.yunjing.botong.log.params.UserInfoModel;
import com.yunjing.botong.log.processor.okhttp.AppCenterService;
import com.yunjing.mommon.base.PushParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
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

    @PostConstruct
    public void init() {
    }


    @RequestMapping("/push")
    public String push() {
        // 创建请求参数
        PushParam param = new PushParam();
        param.setTitle("Title");
        param.setNotificationTitle("NotificationTitle");
        param.setAlias(new String[]{"6386505037916409856"});
        appCenterService.push(param);
        return "success";
    }

    @RequestMapping("/dang")
    public String dang() {
        DangParam param = new DangParam();
        param.setIsAccessory(0);
        param.setSendTelephone(18562818246L);
        param.setUserId(6386505037916409856L);
        List<UserInfoModel> infoModels = new ArrayList<>();
        infoModels.add(new UserInfoModel(18562818246L, 6386505037916409856L));
        param.setReceiveBody(infoModels);
        param.setDangType(1);
        param.setRemindType(1);
        param.setSendType(1);
        param.setSendContent("dang消息内容");
        appCenterService.dang(param);
        return "success";
    }


    @RequestMapping("/is-manager")
    public String isManager() {
        appCenterService.isManager("611564163546121654982", "6386505038969180166", true);
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
