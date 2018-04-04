package com.yunjing.info.init;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.JsonObject;
import com.yunjing.info.common.InfoConstant;
import com.yunjing.info.dto.InfoRedisInit;
import com.yunjing.info.dto.ParentInfoDetailDTO;
import com.yunjing.info.model.InfoCatalog;
import com.yunjing.info.model.InfoDictionary;
import com.yunjing.info.service.InfoCatalogService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.utils.IDUtils;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 初始化公司类目
 *
 * @author 李双喜
 * @date 2018/4/4 13:49
 */
@RestController
@RequestMapping("/info/init")
public class CompanyCatalogInit extends BaseController {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private InfoCatalogService infoCatalogService;

    /**
     * 初始化企业目录结构
     *
     * @param orgId 企业id
     * @return
     */
    @RequestMapping("/company-catalog")
    public ResponseEntityWrapper initCompany(@RequestParam Long orgId) throws BaseException {
        Map<Object, Object> map = redisTemplate.opsForHash().entries(InfoConstant.REDIS_CATALOG_ONE);
        if (null == map) {
            throw new BaseException("请先初始化公共类目");
        }
        List<InfoCatalog> infoCatalogList = new ArrayList<>();
        //取一级目录的id和对应的属性
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            InfoRedisInit infoRedisInit = JSONObject.parseObject(entry.getValue().toString(), InfoRedisInit.class);
            InfoCatalog infoCatalog = new InfoCatalog();
            infoCatalog.setId(IDUtils.getID());
            infoCatalog.setIsDelete(InfoConstant.LOGIC_DELETE_NOMAL);
            infoCatalog.setLevel(infoRedisInit.getLevel());
            infoCatalog.setName(infoRedisInit.getName());
            infoCatalog.setOrgId(orgId);
            infoCatalog.setSort(infoRedisInit.getSort());
            infoCatalog.setWhetherShow(1);
            infoCatalogList.add(infoCatalog);
            List<String> list = redisTemplate.opsForList().range(InfoConstant.REDIS_CATALOG_TWO + ":" + entry.getKey(), 0, -1);
            if (CollectionUtils.isEmpty(list)) {
                continue;
            }
            List<InfoDictionary> objList = JSONObject.parseArray(list.get(0), InfoDictionary.class);
            if (CollectionUtils.isNotEmpty(objList)) {
                for (InfoDictionary infoDictionary : objList) {
                    InfoCatalog info = new InfoCatalog();
                    info.setWhetherShow(1);
                    info.setSort(infoDictionary.getSort());
                    info.setLevel(infoDictionary.getLevel());
                    info.setName(infoDictionary.getName());
                    info.setOrgId(orgId);
                    info.setIsDelete(InfoConstant.LOGIC_DELETE_NOMAL);
                    info.setParentId(infoCatalog.getId());
                    infoCatalogList.add(info);
                }
            }
        }
        infoCatalogService.insertBatch(infoCatalogList);
        return success();
    }
}
