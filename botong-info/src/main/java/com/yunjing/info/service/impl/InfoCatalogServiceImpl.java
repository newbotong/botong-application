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
import com.yunjing.info.mapper.ContentColumnMapper;
import com.yunjing.info.mapper.ContentMapper;
import com.yunjing.info.mapper.InfoCatalogMapper;
import com.yunjing.info.mapper.InfoContentMapper;
import com.yunjing.info.model.*;
import com.yunjing.info.param.InfoCategoryParam;
import com.yunjing.info.processor.okhttp.AuthorityService;
import com.yunjing.info.service.InfoCatalogService;
import com.yunjing.mommon.constant.StatusCode;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.global.exception.BaseRuntimeException;
import com.yunjing.mommon.utils.IDUtils;
import com.yunjing.mommon.wrapper.PageWrapper;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
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

    @Autowired
    ContentColumnMapper contentColumnMapper;

    @Autowired
    ContentMapper contentMapper;


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
//        //OKHttp调用
//        Call<ResponseEntityWrapper> call = authorityService.authority(appId, userId);
//        Response<ResponseEntityWrapper> execute = call.execute();
//        ResponseEntityWrapper body = execute.body();
//        Boolean results = false;
//        if (null != body && null != body.getData()) {
//            //判断是否为管理员
//            results = (boolean) body.getData();
//        }
//        resultMap.put("admin", results);
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
                redisTemplate.opsForHash().put(InfoConstant.REDIS_CATALOG_ONE, infoDictionary.getId(), JSONObject.toJSONString(infoRedisInit));
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
            throw new BaseRuntimeException(StatusCode.ADMIN_USER_UPDATE_FAILED.getStatusCode(),"请先初始化公共类目缓存");
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
        updateInfoCategoryRedis(orgId, parentId, id);
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
//        Map<String, Object> infoContentMap = new HashMap<>(5);
        InfoCatalog infoCatalog = new InfoCatalog();
        infoCatalog.setOrgId(orgId);
        infoCatalog.setParentId(parentId);
        infoCatalog.setId(id);
        infoCatalog =  infoCatalogMapper.selectOne(infoCatalog);
        if (!ValidationUtil.isEmpty(infoCatalog)) {
            //先删除   botong:info:org:orgid:yijikey->object
            if (redisTemplate.hasKey(InfoConstant.BOTONG_INFO_CATALOG_LIST + orgId + InfoConstant.BOTONG_INFO_FIX + parentId)) {
                redisTemplate.opsForHash().delete(InfoConstant.BOTONG_INFO_CATALOG_LIST + orgId + InfoConstant.BOTONG_INFO_FIX + parentId, infoCatalog.getId());
                // 在更新
                redisTemplate.opsForHash().put(InfoConstant.BOTONG_INFO_CATALOG_LIST + orgId + InfoConstant.BOTONG_INFO_FIX + parentId, infoCatalog.getId(), JSON.toJSONString(infoCatalog));
            }else {
                //不存在放入缓存
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
            int sort1 = infoContent1.getSort();
            int sort2 = infoContent2.getSort();
            EntityWrapper<InfoContent> wrapper1 = new EntityWrapper<>();
            infoContent1.setUpdateTime(System.currentTimeMillis());
            infoContent1.setSort(sort2);
            wrapper1.where("org_id={0}", orgId).and("id={0}", id1).and("is_delete=0");
            flag = infoContentMapper.update(infoContent1, wrapper1);

            EntityWrapper<InfoContent> wrapper2 = new EntityWrapper<>();
            infoContent2.setUpdateTime(System.currentTimeMillis());
            infoContent2.setSort(sort1);
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
    public PageWrapper<InfoContentDto> selectParentPage(String orgId, String parentId, String catalogId,String title, Integer pageNo, Integer pageSize) throws BaseException {

        Integer count=0;
        //一级查询内容
        if(!ValidationUtil.isEmpty(parentId)){
            //一级查询资讯总数量
            count = infoContentMapper.selectParentPage(orgId, parentId, null, null, null, null).size();
            //一级内容下的内容
            catalogId =null;
        }else{
            //统计二级资讯内容总数
            Wrapper<InfoContent> wrapper = new EntityWrapper<>();
            wrapper.eq("org_id", orgId).and().eq("catalog_id", catalogId).and().eq("is_delete", InfoConstant.LOGIC_DELETE_NORMAL);
            count = infoContentMapper.selectCount(wrapper);
            //二级目录下的内容列表
            parentId = null;
        }
        //分页查询数据
        Page<InfoContentDto> page = new Page<>(pageNo, pageSize);
        List<InfoContentDto> infoContentDtoList = new ArrayList<>();
        if (!ValidationUtil.isEmpty(count) && count > 0) {
            //计算分页大小
            pageNo = (pageNo - 1) * pageSize;
            infoContentDtoList = infoContentMapper.selectParentPage(orgId, parentId, catalogId, title, pageNo, pageSize);
        }
        page.setRecords(infoContentDtoList);
        page.setTotal(count);
        PageWrapper pageWrapper = com.yunjing.mommon.utils.BeanUtils.mapPage(page, InfoContentDto.class);
        return pageWrapper;
    }


    /**
     * 初始化企业目录结构
     *
     * @param orgId 企业id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void initCompany(String orgId){
        Map<Object, Object> map = redisTemplate.opsForHash().entries(InfoConstant.REDIS_CATALOG_ONE);
        List<InfoCatalog> infoCatalogList = new ArrayList<>();
        //取一级目录的id和对应的属性
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            InfoRedisInit infoRedisInit = JSONObject.parseObject(entry.getValue().toString(), InfoRedisInit.class);
            InfoCatalog infoCatalog = new InfoCatalog();
            infoCatalog.setId(IDUtils.uuid());
            infoCatalog.setParentId(infoCatalog.getId());
            infoCatalog.setIsDelete(InfoConstant.LOGIC_DELETE_NORMAL);
            infoCatalog.setLevel(infoRedisInit.getLevel());
            infoCatalog.setName(infoRedisInit.getName());
            infoCatalog.setOrgId(orgId);
            infoCatalog.setSort(infoRedisInit.getSort());
            infoCatalog.setWhetherShow(1);
            infoCatalog.setCreateTime(System.currentTimeMillis());
            infoCatalog.setUpdateTime(System.currentTimeMillis());
            infoCatalogList.add(infoCatalog);
            List<String> list = redisTemplate.opsForList().range(InfoConstant.REDIS_CATALOG_TWO + ":" + entry.getKey(), 0, -1);
            redisTemplate.opsForHash().put(InfoConstant.COMPANY_INFO_REDIS+orgId,infoCatalog.getId(),JSONObject.toJSONString(infoCatalog));
            if (CollectionUtils.isEmpty(list)) {
                continue;
            }
            List<InfoDictionary> objList = JSONObject.parseArray(list.get(0), InfoDictionary.class);
            if (CollectionUtils.isNotEmpty(objList)) {
                for (InfoDictionary infoDictionary : objList) {
                    InfoCatalog info = new InfoCatalog();
                    info.setId(IDUtils.uuid());
                    info.setWhetherShow(1);
                    info.setSort(infoDictionary.getSort());
                    info.setLevel(infoDictionary.getLevel());
                    info.setName(infoDictionary.getName());
                    info.setOrgId(orgId);
                    info.setIsDelete(InfoConstant.LOGIC_DELETE_NORMAL);
                    info.setParentId(infoCatalog.getId());
                    info.setUpdateTime(System.currentTimeMillis());
                    info.setCreateTime(System.currentTimeMillis());
                    redisTemplate.opsForHash().put(InfoConstant.BOTONG_INFO_CATALOG_LIST+orgId+InfoConstant.BOTONG_INFO_FIX+infoCatalog.getId(),info.getId(),JSONObject.toJSONString(info));
                    infoCatalogList.add(info);
                }
            }
        }
        this.insertBatch(infoCatalogList);
    }


    /**
     * 资讯1.0数据转换
     */
    @Override
    public void intoV1DataTransfer(){
        //资讯分类V1.0列表
        Wrapper<ContentColumn> wrapper1 = new EntityWrapper<>();
        List<ContentColumn> contentColumnList = contentColumnMapper.selectList(wrapper1);
        //2.0资讯分类
        List<InfoCatalog> infoCatalogList = new ArrayList<>();


        if(!ValidationUtil.isEmpty(contentColumnList)){
            for (ContentColumn contentColumn:contentColumnList){
                InfoCatalog infoCatalog = new InfoCatalog();
                infoCatalog.setId(contentColumn.getColumnId());
                infoCatalog.setName(contentColumn.getColumnName());
                infoCatalog.setOrgId(contentColumn.getOrgId());
                if(ValidationUtil.isEmpty(contentColumn.getParentId())){
                    infoCatalog.setParentId(contentColumn.getColumnId());
                    //1级
                    infoCatalog.setLevel(1);
                }else{
                    infoCatalog.setParentId(contentColumn.getParentId());
                    //2级
                    infoCatalog.setLevel(2);
                }
                infoCatalog.setSort(contentColumn.getColumnType());
                infoCatalog.setWhetherShow(contentColumn.getColumnShowType()==1?1:0);
                infoCatalog.setIsDelete(0);

                if (ValidationUtil.isEmpty(contentColumn.getCreateTime())){
                    infoCatalog.setCreateTime(contentColumn.getCreateTime().getTime());
                    infoCatalog.setUpdateTime(contentColumn.getCreateTime().getTime());
                }
                infoCatalogList.add(infoCatalog);

                //遍历内容
                //根据 分类列表 遍历 内容 并排序
                //1.0资讯内容列表
                //2.0资讯内容列表
                List<InfoContent> infoContentList = new ArrayList<>();
                Wrapper<Content> wrapper = new EntityWrapper<>();
                wrapper.eq("column_id",contentColumn.getColumnId()).orderBy("create_time",true);
                List<Content> contentList = contentMapper.selectList(wrapper);
                if(!ValidationUtil.isEmpty(contentList)){
                    for (int i=0;i<contentList.size();i++){
                        InfoContent infoContent = new InfoContent();
                        infoContent.setId(contentList.get(i).getContentId());
                        infoContent.setCatalogId(contentList.get(i).getColumnId());
                        infoContent.setOrgId(infoCatalog.getOrgId());
                        infoContent.setDepartmentName("");
                        infoContent.setTitle(contentList.get(i).getContentTitle());
                        infoContent.setPictureUrl(contentList.get(i).getContentImg());
                        infoContent.setContent(contentList.get(i).getContentDetail());
                        infoContent.setReadNumber(0);
                        infoContent.setWhetherShow(contentList.get(i).getIsView());
                        infoContent.setSort(i+1);
                        infoContent.setIsDelete(contentList.get(i).getStatus());
                        if (!ValidationUtil.isEmpty(contentList.get(i).getCreateTime())){
                            infoContent.setCreateTime(contentList.get(i).getCreateTime().getTime());
                            infoContent.setUpdateTime(contentList.get(i).getLastMdfTime().getTime());
                        }else{
                            infoContent.setCreateTime(System.currentTimeMillis());
                            infoContent.setUpdateTime(System.currentTimeMillis());
                        }
                        infoContentList.add(infoContent);
                    }
                    //批量入库 资讯内容
                    InfoContentServiceImpl infoContentService = new InfoContentServiceImpl();
                    infoContentService.insertBatch(infoContentList);
                }
            }
            //批量入库资讯分类
            boolean flag = insertBatch(infoCatalogList);
            //成功后 将分类录入到缓存
            if(!flag){
                return;
            }
            for(InfoCatalog infoCatalog:infoCatalogList){
                if(ValidationUtil.equals(infoCatalog.getId(),infoCatalog.getParentId())){
                //初始化1级
                    redisTemplate.opsForHash().put(InfoConstant.COMPANY_INFO_REDIS+infoCatalog.getOrgId(),infoCatalog.getId(),JSONObject.toJSONString(infoCatalog));
                }else{
                    if (redisTemplate.hasKey(InfoConstant.BOTONG_INFO_CATALOG_LIST + infoCatalog.getOrgId() + InfoConstant.BOTONG_INFO_FIX + infoCatalog.getParentId())) {
                        redisTemplate.opsForHash().delete(InfoConstant.BOTONG_INFO_CATALOG_LIST + infoCatalog.getOrgId() + InfoConstant.BOTONG_INFO_FIX + infoCatalog.getParentId(),infoCatalog.getId());
                        // 在更新
                        redisTemplate.opsForHash().put(InfoConstant.BOTONG_INFO_CATALOG_LIST + infoCatalog.getOrgId() + InfoConstant.BOTONG_INFO_FIX +infoCatalog.getParentId(), infoCatalog.getId(), JSON.toJSONString(infoCatalog));
                    }else {
                        //不存在放入缓存
                        //2级初始化
                        redisTemplate.opsForHash().put(InfoConstant.BOTONG_INFO_CATALOG_LIST + infoCatalog.getOrgId() + InfoConstant.BOTONG_INFO_FIX +infoCatalog.getParentId(), infoCatalog.getId(), JSON.toJSONString(infoCatalog));
                    }

                }
            }

        }










    }
}
