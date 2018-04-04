package com.yunjing.info.init;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.yunjing.info.common.InfoConstant;
import com.yunjing.info.dto.InfoRedisInit;
import com.yunjing.info.model.InfoDictionary;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Redis初始化字典
 *
 * @author 李双喜
 * @date 2018/4/3 10:52
 */
@RestController
@RequestMapping("/info/init")
public class RedisInit extends BaseController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping("/info-catalog")
    public ResponseEntityWrapper initCatalog() throws BaseException {
        //查询出所有的一级目录
        List<InfoDictionary> infoDictionaries = new InfoDictionary().selectList(new EntityWrapper<InfoDictionary>().eq("is_delete", InfoConstant.LOGIC_DELETE_NOMAL).eq("level", 1).orderBy("sort"));
        if (CollectionUtils.isNotEmpty(infoDictionaries)) {
            redisTemplate.delete(InfoConstant.REDIS_CATALOG_ONE);
            List<InfoRedisInit> list = new ArrayList<>();
            //将每一个一级目录全部存到redis
            List<Long> ids = new ArrayList<Long>();
            for (InfoDictionary infoDictionary : infoDictionaries) {
                InfoRedisInit infoRedisInit = new InfoRedisInit();
                BeanUtils.copyProperties(infoDictionary, infoRedisInit);
                list.add(infoRedisInit);
                ids.add(infoDictionary.getId());
                redisTemplate.opsForHash().put(InfoConstant.REDIS_CATALOG_ONE, infoDictionary.getId().toString(), JSONObject.toJSONString(infoRedisInit));
            }
            if (CollectionUtils.isNotEmpty(ids)) {
                for (Long id : ids) {
                    List<InfoDictionary> redisInitList = new InfoDictionary().selectList(new EntityWrapper<InfoDictionary>()
                            .eq("is_delete", InfoConstant.LOGIC_DELETE_NOMAL).eq("parent_id", id).orderBy("sort"));
                    if (CollectionUtils.isNotEmpty(redisInitList)) {
                        redisTemplate.delete(InfoConstant.REDIS_CATALOG_TWO + ":" + id);
                        redisTemplate.opsForList().rightPushAll(InfoConstant.REDIS_CATALOG_TWO + ":" + id, JSONObject.toJSONString(redisInitList));
                    }
                }
            }
        } else {
            throw new BaseException("无一级目录");
        }
        return success();
    }
}
