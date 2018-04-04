package com.yunjing.info.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.info.common.InfoConstant;
import com.yunjing.info.dto.ParentInfoDetailDTO;
import com.yunjing.info.mapper.InfoCatalogMapper;
import com.yunjing.info.model.InfoCatalog;
import com.yunjing.info.param.InfoCategoryParam;
import com.yunjing.info.service.InfoCatalogService;
import com.yunjing.mommon.global.exception.BaseException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
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
        Map<String,Object> map = new HashMap<>(4);
        Map<Object,Object> objectMap = redisTemplate.opsForHash().entries(InfoConstant.REDIS_HOME+":"+orgId);
        List<ParentInfoDetailDTO> detailDTOList = new ArrayList<>();
        for (Map.Entry<Object,Object> entry : objectMap.entrySet()){
            entry.getKey().toString();
            ParentInfoDetailDTO parentInfoDetailDTO = JSONObject.parseObject(entry.getValue().toString(),ParentInfoDetailDTO.class);
            detailDTOList.add(parentInfoDetailDTO);
        }
        Collections.sort(detailDTOList);
        map.put("info",detailDTOList);
        return map;
    }

    @Override
    public Map<String, Object> selectParentAll(Long orgId, Long catalogId, Long userId,Integer pageNo,Integer pageSize) throws BaseException {
        return null;
    }
}
