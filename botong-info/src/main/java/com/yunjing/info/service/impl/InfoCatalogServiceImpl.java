package com.yunjing.info.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.info.common.InfoConstant;
import com.yunjing.info.common.ValidationUtil;
import com.yunjing.info.dto.*;
import com.yunjing.info.mapper.InfoCatalogMapper;
import com.yunjing.info.mapper.InfoContentMapper;
import com.yunjing.info.model.InfoCatalog;
import com.yunjing.info.model.InfoContent;
import com.yunjing.info.model.InfoDictionary;
import com.yunjing.info.param.InfoCategoryParam;
import com.yunjing.info.processor.okhttp.AuthorityService;
import com.yunjing.info.service.InfoCatalogService;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.utils.IDUtils;
import com.yunjing.mommon.wrapper.PageWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    InfoContentMapper infoContentMapper;

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
    public Map<String, Object> selectParent(String orgId, String userId) throws BaseException {
        Map<String, Object> map = new HashMap<>(4);
        //查询一级分类下的首页内容
        Map<Object, Object> objectMap = redisTemplate.opsForHash().entries(InfoConstant.REDIS_HOME + ":" + orgId);
        List<ParentInfoDetailDto> detailDTOList = new ArrayList<>();
        if (MapUtils.isNotEmpty(objectMap)) {
            for (Map.Entry<Object, Object> entry : objectMap.entrySet()) {
                entry.getKey().toString();
                ParentInfoDetailDto parentInfoDetailDto = JSONObject.parseObject(entry.getValue().toString(), ParentInfoDetailDto.class);
                detailDTOList.add(parentInfoDetailDto);
            }
        }
        Collections.sort(detailDTOList);
        map.put("info", detailDTOList);
        List<CompanyRedisCatalogDto> companyRedisCatalogDtos = this.selectParentCatalog(orgId);
        map.put("parent", companyRedisCatalogDtos);
        return map;
    }

    /**
     * 查询企业类目
     *
     * @param orgId 企业类目id
     * @return
     */
    @Override
    public List<CompanyRedisCatalogDto> selectParentCatalog(String orgId) {
        //查询目录结构(一级Id)
        Map<Object, Object> mapRedis = redisTemplate.opsForHash().entries(InfoConstant.COMPANY_INFO_REDIS + orgId);
        List<CompanyRedisCatalogDto> companyRedisCatalogDtos = new ArrayList<>();
        if (MapUtils.isNotEmpty(mapRedis)) {
            for (Map.Entry<Object, Object> entry : mapRedis.entrySet()) {
                CompanyRedisCatalogDto companyRedisCatalogDto = JSONObject.parseObject(entry.getValue().toString(), CompanyRedisCatalogDto.class);
                Map<Object, Object> towCatalogMap = redisTemplate.opsForHash().entries(InfoConstant.BOTONG_INFO_CATALOG_LIST + orgId + InfoConstant.BOTONG_INFO_FIX + companyRedisCatalogDto.getId());
                if (MapUtils.isNotEmpty(towCatalogMap)) {
                    List<InfoCatalog> infoCatalogList = new ArrayList<InfoCatalog>();
                    for (Map.Entry<Object, Object> en : towCatalogMap.entrySet()) {
                        InfoCatalog infoCatalog = JSONObject.parseObject(en.getValue().toString(), InfoCatalog.class);
                        infoCatalogList.add(infoCatalog);
                    }
                    Collections.sort(infoCatalogList);
                    companyRedisCatalogDto.setLower(infoCatalogList);
                }
                //针对二级结构排序
                companyRedisCatalogDtos.add(companyRedisCatalogDto);
            }
            //针对一级结构排序
            Collections.sort(companyRedisCatalogDtos);
        }
        return companyRedisCatalogDtos;
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
    public Map<String, Object> selectParentAll(String orgId, String catalogId, String userId, Integer pageNo, Integer pageSize) throws BaseException, IOException {
        Map<String, Object> resultMap = new HashMap<>(4);
        Page<InfoDto> page = new Page<>(pageNo, pageSize);
        Map<String, Object> map = new HashMap<>(4);
        map.put("orgId", orgId);
        map.put("catalogId", catalogId);
        List<InfoDto> infoDtoList = infoCatalogMapper.selectInfoCatalog(map, page);
        page.setRecords(infoDtoList);
        resultMap.put("page", page);
        //OKHttp调用
//        Call<ResponseEntityWrapper> call = authorityService.authority(appId, userId);
//        Response<ResponseEntityWrapper> execute = call.execute();
//        ResponseEntityWrapper body = execute.body();
//        //判断是否为管理员
//        boolean results = (boolean) body.getData();
        resultMap.put("admin", true);
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
            List<String> ids = new ArrayList<String>();
            for (InfoDictionary infoDictionary : infoDictionaries) {
                InfoRedisInit infoRedisInit = new InfoRedisInit();
                BeanUtils.copyProperties(infoDictionary, infoRedisInit);
                list.add(infoRedisInit);
                ids.add(infoDictionary.getId());
                redisTemplate.opsForHash().put(InfoConstant.REDIS_CATALOG_ONE, infoDictionary.getId().toString(), JSONObject.toJSONString(infoRedisInit));
            }
            if (CollectionUtils.isNotEmpty(ids)) {
                for (String id : ids) {
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

    /**
     * 新增分类
     *
     * @param orgId
     * @param parentId
     * @param name
     * @return
     * @throws BaseException
     */
    @Override
    public InfoConstant.StateCode insertInfoCategory(String orgId, String parentId, String name) throws BaseException {
        int i = 5;
        Map<String, Object> infoCatalogMap = new HashMap<>(i);
        infoCatalogMap.put("org_id", orgId);
        infoCatalogMap.put("is_delete", InfoConstant.LOGIC_DELETE_NORMAL);
        infoCatalogMap.put("parent_id", parentId);
        List<InfoCatalog> infoCatalogList = infoCatalogMapper.selectByMap(infoCatalogMap);
        //每个公司的分类不能超过6个
        if (ValidationUtil.isEmpty(infoCatalogList)) {
            //没有初始化 一级分类
            return InfoConstant.StateCode.CODE_400;
        }

        if (infoCatalogList.size() >= InfoConstant.INFO_NAME_MIX) {
            return InfoConstant.StateCode.CODE_604;
        }
        InfoCatalog infoCatalog = new InfoCatalog();
        infoCatalog.setOrgId(orgId);
        infoCatalog.setParentId(parentId);
        infoCatalog.setName(name);
        //默认显示
        infoCatalog.setWhetherShow(InfoConstant.INFO_TYPE_DISPLAY);
        //顺序+1
        infoCatalog.setSort(infoCatalogList.size() + 1);
        infoCatalog.setIsDelete(InfoConstant.LOGIC_DELETE_NORMAL);
        infoCatalog.setCreateTime(System.currentTimeMillis());
        infoCatalog.setUpdateTime(System.currentTimeMillis());
        infoCatalog.setId(IDUtils.uuid());
        int flag = infoCatalogMapper.insert(infoCatalog);
        //分类存入到缓存
        if (flag > 0) {
            updateInfoCategoryRedis(orgId, parentId, infoCatalog.getId());
        }
        return flag > 0 ? InfoConstant.StateCode.CODE_200 : InfoConstant.StateCode.CODE_602;
    }

    /**
     * 修改分类
     *
     * @param orgId       企业id
     * @param parentId    父id
     * @param id          二级id
     * @param name        名称
     * @return
     * @throws BaseException
     */
    @Override
    public InfoConstant.StateCode modifyInfoCategory(String orgId, String parentId, String id, String name) throws BaseException {
        EntityWrapper<InfoCatalog> wrapper = new EntityWrapper<>();
        InfoCatalog infoCatalog = new InfoCatalog();
        infoCatalog.setUpdateTime(System.currentTimeMillis());
        infoCatalog.setName(name);
        wrapper.where("org_id={0}", orgId).and("parent_id={0}", parentId).and("id={0}", id).and("is_delete={0}", InfoConstant.LOGIC_DELETE_NORMAL);
        int flag = infoCatalogMapper.update(infoCatalog, wrapper);
        //更新缓存
        if (flag > 0) {
            updateInfoCategoryRedis(orgId, parentId, id);
        }
        return flag > 0 ? InfoConstant.StateCode.CODE_200 : InfoConstant.StateCode.CODE_602;
    }

    /**
     * 删除分类
     *
     * @param orgId
     * @param parentId
     * @param id
     * @return
     * @throws BaseException
     */
    @Override
    public InfoConstant.StateCode deleteInfoCategory(String orgId, String parentId, String id) throws BaseException {
        EntityWrapper<InfoCatalog> wrapper = new EntityWrapper<>();
        InfoCatalog infoCatalog = new InfoCatalog();
        infoCatalog.setUpdateTime(System.currentTimeMillis());
        infoCatalog.setIsDelete(InfoConstant.LOGIC_DELETE_DELETE);
        wrapper.where("org_id={0}", orgId).and("parent_id={0}", parentId).and("id={0}", id);
        int flag = infoCatalogMapper.update(infoCatalog, wrapper);
        //删除成功 删除该分类下的内容
        if (flag > 0) {
            //先查询 该分类下是否有内容
            Map<String, Object> infoContentMap = new HashMap<>(4);
            infoContentMap.put("org_id", orgId);
            infoContentMap.put("catalog_id", id);
            List<InfoContent> infoContentList = infoContentMapper.selectByMap(infoContentMap);
            if (!ValidationUtil.isEmpty(infoContentList)) {
                //有则删除，无直接返回
                EntityWrapper<InfoContent> infoContentEntityWrapper = new EntityWrapper<>();
                InfoContent infoContent = new InfoContent();
                infoContent.setUpdateTime(System.currentTimeMillis());
                infoContent.setIsDelete(InfoConstant.LOGIC_DELETE_DELETE);
                infoContentEntityWrapper.where("org_id={0}", orgId).and("catalog_id={0}", id);
                flag = infoContentMapper.update(infoContent, infoContentEntityWrapper);
            }
            //更新分类缓存
            if (redisTemplate.hasKey(InfoConstant.BOTONG_INFO_CATALOG_LIST + orgId + InfoConstant.BOTONG_INFO_FIX + parentId)) {
                redisTemplate.opsForHash().delete(InfoConstant.BOTONG_INFO_CATALOG_LIST + orgId + InfoConstant.BOTONG_INFO_FIX + parentId, id);
            }
        }
        return flag > 0 ? InfoConstant.StateCode.CODE_200 : InfoConstant.StateCode.CODE_602;
    }

    /**
     * 删除资讯
     *
     * @param orgId
     * @param id
     * @return
     * @throws BaseException
     */
    @Override
    public InfoConstant.StateCode deleteInfoContent(String orgId, String id) throws BaseException {
        EntityWrapper<InfoContent> infoContentEntityWrapper = new EntityWrapper<>();
        InfoContent infoContent = new InfoContent();
        infoContent.setUpdateTime(System.currentTimeMillis());
        infoContent.setIsDelete(InfoConstant.LOGIC_DELETE_DELETE);
        infoContentEntityWrapper.where("org_id={0}", orgId).and("id={0}", id);
        int flag = infoContentMapper.update(infoContent, infoContentEntityWrapper);
        return flag > 0 ? InfoConstant.StateCode.CODE_200 : InfoConstant.StateCode.CODE_602;
    }

    /**
     * 显示、隐藏分类
     *
     * @param orgId
     * @param parentId
     * @param id
     * @param displayType 0：否，1：是
     * @return
     * @throws BaseException
     */
    @Override
    public InfoConstant.StateCode displayInfoCategory(String orgId, String parentId, String id, Integer displayType) throws BaseException {
        EntityWrapper<InfoCatalog> wrapper = new EntityWrapper<>();
        InfoCatalog infoCatalog = new InfoCatalog();
        infoCatalog.setUpdateTime(System.currentTimeMillis());
        infoCatalog.setIsDelete(InfoConstant.LOGIC_DELETE_NORMAL);
        infoCatalog.setWhetherShow(displayType);
        wrapper.where("org_id={0}", orgId).and("parent_id={0}", parentId).and("id={0}", id);
        int flag = infoCatalogMapper.update(infoCatalog, wrapper);
        //更新缓存
        //如果是隐藏则删除缓存数据
        if (InfoConstant.INFO_TYPE_DISPLAY.equals(displayType)) {
            updateInfoCategoryRedis(orgId, parentId, id);
        } else {
            if (redisTemplate.hasKey(InfoConstant.BOTONG_INFO_CATALOG_LIST + orgId + InfoConstant.BOTONG_INFO_FIX + parentId)) {
                redisTemplate.opsForHash().delete(InfoConstant.BOTONG_INFO_CATALOG_LIST + orgId + InfoConstant.BOTONG_INFO_FIX + parentId, id);
            }
        }
        //如果是显示则添加缓存数据
        return flag > 0 ? InfoConstant.StateCode.CODE_200 : InfoConstant.StateCode.CODE_602;
    }

    /**
     * 显示、隐藏资讯
     *
     * @param orgId
     * @param id
     * @param displayType 0：否，1：是
     * @return
     * @throws BaseException
     */
    @Override
    public InfoConstant.StateCode displayInfoContent(String orgId, String id, Integer displayType) throws BaseException {
        EntityWrapper<InfoContent> infoContentEntityWrapper = new EntityWrapper<>();
        InfoContent infoContent = new InfoContent();
        infoContent.setUpdateTime(System.currentTimeMillis());
        infoContent.setIsDelete(InfoConstant.LOGIC_DELETE_NORMAL);
        infoContent.setWhetherShow(displayType);
        infoContentEntityWrapper.where("org_id={0}", orgId).and("id={0}", id);
        int flag = infoContentMapper.update(infoContent, infoContentEntityWrapper);
        return flag > 0 ? InfoConstant.StateCode.CODE_200 : InfoConstant.StateCode.CODE_602;
    }



    /**
     * 更新资讯分类缓存操作
     *
     * @param orgId
     * @param parentId
     * @param id
     */
    private void updateInfoCategoryRedis(String orgId, String parentId, String id) {
        Map<String, Object> infoContentMap = new HashMap<>(5);
        infoContentMap.put("org_id", orgId);
        infoContentMap.put("parent_id", parentId);
        infoContentMap.put("catalog_id", id);
        InfoCatalog infoCatalog = (InfoCatalog) infoCatalogMapper.selectByMap(infoContentMap);
        if (!ValidationUtil.isEmpty(infoCatalog)) {
            //先删除   botong:info:org:orgid:yijikey->object
            if (redisTemplate.hasKey(InfoConstant.BOTONG_INFO_CATALOG_LIST + orgId + InfoConstant.BOTONG_INFO_FIX + parentId)) {
                redisTemplate.opsForHash().delete(InfoConstant.BOTONG_INFO_CATALOG_LIST + orgId + InfoConstant.BOTONG_INFO_FIX + parentId, infoCatalog.getId());
            } else {
                // 在更新
                redisTemplate.opsForHash().put(InfoConstant.BOTONG_INFO_CATALOG_LIST + orgId + InfoConstant.BOTONG_INFO_FIX + parentId, infoCatalog.getId(), JSON.toJSONString(infoCatalog));
            }
        }
    }

    /**
     * 类目排序
     *
     * @param orgId
     * @param parentId
     * @param catalogId1
     * @param catalogId2
     * @return
     * @throws BaseException
     */
    @Override
    public InfoConstant.StateCode updateCatalogSort(String orgId, String parentId, String catalogId1, String catalogId2) throws BaseException {

        JSONObject jsonObject1 = JSON.parseObject((String) redisTemplate.opsForHash().get(InfoConstant.BOTONG_INFO_CATALOG_LIST + orgId + InfoConstant.BOTONG_INFO_FIX + parentId, catalogId1));
        JSONObject jsonObject2 = JSON.parseObject((String) redisTemplate.opsForHash().get(InfoConstant.BOTONG_INFO_CATALOG_LIST + orgId + InfoConstant.BOTONG_INFO_FIX + parentId, catalogId2));
        int flag = 0;
        if (!ValidationUtil.isEmpty(jsonObject1) && !ValidationUtil.isEmpty(jsonObject2)) {
            EntityWrapper<InfoCatalog> wrapper1 = new EntityWrapper<>();
            InfoCatalog infoCatalog1 = new InfoCatalog();
            infoCatalog1.setUpdateTime(System.currentTimeMillis());

            EntityWrapper<InfoCatalog> wrapper2 = new EntityWrapper<>();
            InfoCatalog infoCatalog2 = new InfoCatalog();
            infoCatalog2.setUpdateTime(System.currentTimeMillis());

            infoCatalog1.setSort((Integer) jsonObject2.get("sort"));
            wrapper1.where("org_id={0}", orgId).and("parent_id={0}", parentId).and("id={0}", catalogId1).and("is_delete=0");
            flag = infoCatalogMapper.update(infoCatalog1, wrapper1);

            infoCatalog2.setSort((Integer) jsonObject1.get("sort"));
            wrapper2.where("org_id={0}", orgId).and("parent_id={0}", parentId).and("id={0}", catalogId2).and("is_delete=0");
            flag = infoCatalogMapper.update(infoCatalog2, wrapper2);
            //更新缓存
            updateInfoCategoryRedis(orgId, parentId, catalogId1);
            updateInfoCategoryRedis(orgId, parentId, catalogId2);
        }
        return flag > 0 ? InfoConstant.StateCode.CODE_200 : InfoConstant.StateCode.CODE_602;
    }

    /**
     * 资讯排序
     *
     * @param orgId
     * @param id1
     * @param id2
     * @return
     * @throws BaseException
     */
    @Override
    public InfoConstant.StateCode updateInfoSort(String orgId, String id1, String id2) throws BaseException {
        InfoContent infoContent1 = new InfoContent();
        infoContent1.setOrgId(orgId);
        infoContent1.setId(id1);
        infoContent1 = infoContentMapper.selectOne(infoContent1);

        InfoContent infoContent2 = new InfoContent();
        infoContent2.setOrgId(orgId);
        infoContent2.setId(id2);
        infoContent2 =  infoContentMapper.selectOne(infoContent2);
        int flag = 0;
        if (!ValidationUtil.isEmpty(infoContent1) && !ValidationUtil.isEmpty(infoContent2)) {
            EntityWrapper<InfoContent> wrapper1 = new EntityWrapper<>();
            infoContent1.setUpdateTime(System.currentTimeMillis());
            infoContent1.setSort(infoContent2.getSort());
            wrapper1.where("org_id={0}", orgId).and("id={0}", id1).and("is_delete=0");
            flag = infoContentMapper.update(infoContent1, wrapper1);

            EntityWrapper<InfoContent> wrapper2 = new EntityWrapper<>();
            infoContent2.setUpdateTime(System.currentTimeMillis());
            infoContent2.setSort(infoContent1.getSort());
            wrapper2.where("org_id={0}", orgId).and("id={0}", id2).and("is_delete=0");
            flag = infoContentMapper.update(infoContent2, wrapper2);
        }
        return flag > 0 ? InfoConstant.StateCode.CODE_200 : InfoConstant.StateCode.CODE_602;
    }

    /**
     * web端资讯分页模糊查询
     *
     * @param orgId
     * @param catalogId
     * @param title
     * @param pageNo
     * @param pageSize
     * @return
     * @throws BaseException
     */
    @Override
    public PageWrapper<InfoContentDto> selectParentPage(String orgId, String catalogId, String title, Integer pageNo, Integer pageSize) throws BaseException {
        //统计总数
        Wrapper<InfoContent> wrapper = new EntityWrapper<>();
        wrapper.eq("org_id", orgId).and().eq("catalog_id", catalogId).and().eq("is_delete", InfoConstant.LOGIC_DELETE_NORMAL);
        Integer count = infoContentMapper.selectCount(wrapper);
        //分页查询数据
        Page<InfoContentDto> page = new Page<>(pageNo, pageSize);
        List<InfoContentDto> infoContentDtoList = new ArrayList<>();
        if (!ValidationUtil.isEmpty(count) && count > 0) {
            //计算分页大小
            pageNo = (pageNo - 1) * pageSize;
            infoContentDtoList = infoContentMapper.selectParentPage(orgId, catalogId, title, pageNo, pageSize);
        }
        page.setRecords(infoContentDtoList);
        page.setTotal(count);
        PageWrapper pageWrapper = com.yunjing.mommon.utils.BeanUtils.mapPage(page, InfoContentDto.class);
        return pageWrapper;
    }
}
