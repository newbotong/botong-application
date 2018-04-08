package com.yunjing.info.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.info.common.InfoConstant;
import com.yunjing.info.config.InfoConstants;
import com.yunjing.info.dto.CompanyRedisCatalogDTO;
import com.yunjing.info.dto.InfoDTO;
import com.yunjing.info.dto.InfoRedisInit;
import com.yunjing.info.dto.ParentInfoDetailDTO;
import com.yunjing.info.mapper.InfoCatalogMapper;
import com.yunjing.info.model.InfoCatalog;
import com.yunjing.info.model.InfoDictionary;
import com.yunjing.info.param.InfoCategoryParam;
import com.yunjing.info.processor.okhttp.AuthorityService;
import com.yunjing.info.service.InfoCatalogService;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
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

    @Autowired
    private AuthorityService authorityService;

    @Value("${info.appId}")
    private String appId;

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
     * @param catalogId 目录id
     * @param userId    用户id
     * @param pageNo    当前页码
     * @param pageSize  每页显示条数
     * @return
     * @throws BaseException
     * @throws IOException
     */
    @Override
    public Map<String, Object> selectParentAll(Long orgId, Long catalogId, Long userId, Integer pageNo, Integer pageSize) throws BaseException, IOException {
        Map<String, Object> resultMap = new HashMap<>(4);
        Page<InfoDTO> page = new Page<>(pageNo, pageSize);
        Map<String, Object> map = new HashMap<>(4);
        map.put("orgId", orgId);
        map.put("catalogId", catalogId);
        List<InfoDTO> infoDTOList = infoCatalogMapper.selectInfoCatalog(map, page);
        page.setRecords(infoDTOList);
        resultMap.put("page", page);
        //OKHttp调用
        Call<ResponseEntityWrapper> call = authorityService.authority(appId, userId);
        Response<ResponseEntityWrapper> execute = call.execute();
        ResponseEntityWrapper body = execute.body();
        //判断是否为管理员
        boolean results = (boolean) body.getData();
        resultMap.put("admin", results);
        return resultMap;
    }


    /**
     * 初始化公共资讯类目
     *
     * @throws BaseException
     */
    public void init() throws BaseException {
        //查询出所有的一级目录
        List<InfoDictionary> infoDictionaries = new InfoDictionary().selectList(new EntityWrapper<InfoDictionary>().eq("is_delete", InfoConstant.LOGIC_DELETE_NORMAL).eq("level", 1).orderBy("sort"));
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
                            .eq("is_delete", InfoConstant.LOGIC_DELETE_NORMAL).eq("parent_id", id).orderBy("sort"));
                    if (CollectionUtils.isNotEmpty(redisInitList)) {
                        redisTemplate.delete(InfoConstant.REDIS_CATALOG_TWO + ":" + id);
                        redisTemplate.opsForList().rightPushAll(InfoConstant.REDIS_CATALOG_TWO + ":" + id, JSONObject.toJSONString(redisInitList));
                    }
                }
            }
        } else {
            throw new BaseException("请先初始化公共类目缓存");
        }
    }
}
