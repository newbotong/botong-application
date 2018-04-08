package com.yunjing.info.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.info.common.InfoConstant;
import com.yunjing.info.config.InfoConstants;
import com.yunjing.info.dto.CompanyRedisCatalogDTO;
import com.yunjing.info.dto.InfoDTO;
import com.yunjing.info.dto.ParentInfoDetailDTO;
import com.yunjing.info.mapper.InfoCatalogMapper;
import com.yunjing.info.model.InfoCatalog;
import com.yunjing.info.param.InfoCategoryParam;
import com.yunjing.info.service.InfoCatalogService;
import com.yunjing.mommon.global.exception.BaseException;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 资讯目录Service实现类
 *
 * @author 李双喜
 * @date 2018/3/30 15:38
 */
@Service
public class InfoCatalogServiceImpl extends ServiceImpl<InfoCatalogMapper, InfoCatalog> implements InfoCatalogService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private InfoCatalogMapper infoCatalogMapper;

    /**
     * 新增类目
     *
     * @param infoCategoryParam 入参
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertCategory(InfoCategoryParam infoCategoryParam) throws BaseException {

    }

    /**
     * 查询资讯父级目录
     *
     * @param orgId  企业id
     * @param userId 用户id
     * @return
     * @throws BaseException
     */
    @Override
    public Map<String, Object> selectParent(Long orgId, Long userId) throws BaseException {
        Map<String, Object> map = new HashMap<>(4);
        //查询一级分类下的首页内容
        Map<Object, Object> objectMap = redisTemplate.opsForHash().entries(InfoConstant.REDIS_HOME + ":" + orgId);
        List<ParentInfoDetailDTO> detailDTOList = new ArrayList<>();
        if (MapUtils.isNotEmpty(objectMap)) {
            for (Map.Entry<Object, Object> entry : objectMap.entrySet()) {
                entry.getKey().toString();
                ParentInfoDetailDTO parentInfoDetailDTO = JSONObject.parseObject(entry.getValue().toString(), ParentInfoDetailDTO.class);
                detailDTOList.add(parentInfoDetailDTO);
            }
        }
        Collections.sort(detailDTOList);
        map.put("info", detailDTOList);
        //查询目录结构(一级Id)
        Map<Object, Object> mapRedis = redisTemplate.opsForHash().entries(InfoConstant.COMPANY_INFO_REDIS + orgId);
        List<CompanyRedisCatalogDTO> companyRedisCatalogDTOS = new ArrayList<>();
        if (MapUtils.isNotEmpty(mapRedis)) {
            for (Map.Entry<Object, Object> entry : mapRedis.entrySet()) {
                CompanyRedisCatalogDTO companyRedisCatalogDTO = JSONObject.parseObject(entry.getValue().toString(), CompanyRedisCatalogDTO.class);
                Map<Object, Object> towCatalogMap = redisTemplate.opsForHash().entries(InfoConstants.BOTONG_INFO_CATALOG_LIST + orgId + InfoConstants.BOTONG_INFO_FIX + companyRedisCatalogDTO.getId());
                if (MapUtils.isNotEmpty(towCatalogMap)) {
                    List<InfoCatalog> infoCatalogList = new ArrayList<InfoCatalog>();
                    for (Map.Entry<Object, Object> en : towCatalogMap.entrySet()) {
                        InfoCatalog infoCatalog = JSONObject.parseObject(en.getValue().toString(), InfoCatalog.class);
                        infoCatalogList.add(infoCatalog);
                    }
                    Collections.sort(infoCatalogList);
                    companyRedisCatalogDTO.setLower(infoCatalogList);
                }
                //针对二级结构排序
                companyRedisCatalogDTOS.add(companyRedisCatalogDTO);
            }
            //针对一级结构排序
            Collections.sort(companyRedisCatalogDTOS);
        }
        map.put("parent", companyRedisCatalogDTOS);
        return map;
    }

    /**
     * 查询资讯父级目录下分页列表
     *
     * @param orgId     企业id
     * @param catalogId 类目id
     * @param userId    用户id
     * @param pageNo    当前页码
     * @param pageSize  每页显示条数
     * @return
     * @throws BaseException
     */
    @Override
    public Map<String, Object> selectParentAll(Long orgId, Long catalogId, Long userId, Integer pageNo, Integer pageSize) throws BaseException {
        Map<String,Object> resultMap = new HashMap<>(4);
        Page<InfoDTO> page = new Page<>(pageNo, pageSize);
        Map<String,Object> map = new HashMap<>(4);
        map.put("orgId",orgId);
        map.put("catalogId",catalogId);
        List<InfoDTO> infoDTOList = infoCatalogMapper.selectInfoCatalog(map,page);
        page.setRecords(infoDTOList);
        resultMap.put("page",page);
        //OKHttp
        resultMap.put("admin",true);
        return resultMap;
    }
}
