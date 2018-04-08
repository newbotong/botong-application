package com.yunjing.info.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.info.common.InfoConstant;
import com.yunjing.info.common.ValidationUtil;
import com.yunjing.info.config.InfoConstants;
import com.yunjing.info.dto.InfoCatalogDTO;
import com.yunjing.info.dto.InfoContentDTO;
import com.yunjing.info.mapper.InfoCatalogMapper;
import com.yunjing.info.mapper.InfoContentMapper;
import com.yunjing.info.model.InfoCatalog;
import com.yunjing.info.model.InfoContent;
import com.yunjing.info.service.InfoCatalogServiceV2;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.utils.BeanUtils;
import com.yunjing.mommon.utils.IDUtils;
import com.yunjing.mommon.wrapper.PageWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资讯目录Service实现类
 *
 * @author 李双喜
 * @date 2018/3/30 15:38
 */
@Service
public class InfoCatalogServiceImplV2 extends ServiceImpl<InfoCatalogMapper, InfoCatalog> implements InfoCatalogServiceV2 {

    @Autowired
    InfoCatalogMapper infoCatalogMapper;

    @Autowired
    InfoContentMapper infoContentMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;


    /**
     * 新增分类
     * @param orgId
     * @param parentId
     * @param name
     * @return
     * @throws BaseException
     */
    @Override
    public InfoConstants.StateCode insertInfoCategory(Long orgId,Long parentId,String name) throws BaseException {
        int i = 5;
        Map<String, Object> infoCatalogMap = new HashMap<>(i);
        infoCatalogMap.put("org_id", orgId);
        infoCatalogMap.put("is_delete", InfoConstant.LOGIC_DELETE_NORMAL);
        infoCatalogMap.put("parent_id",parentId);
        List<InfoCatalog> infoCatalogList = infoCatalogMapper.selectByMap(infoCatalogMap);
        //每个公司的分类不能超过6个
        if(ValidationUtil.isEmpty(infoCatalogList)){
            //没有初始化 一级分类
            return InfoConstants.StateCode.CODE_400;
        }

        if(infoCatalogList.size()>=InfoConstants.INFO_NAME_MIX){
            return InfoConstants.StateCode.CODE_604;
        }
        InfoCatalog infoCatalog = new InfoCatalog();
        infoCatalog.setOrgId(orgId);
        infoCatalog.setParentId(parentId);
        infoCatalog.setName(name);
        //默认显示
        infoCatalog.setWhetherShow(InfoConstants.INFO_TYPE_DISPLAY);
        //顺序+1
        infoCatalog.setSort(infoCatalogList.size()+1);
        infoCatalog.setIsDelete(InfoConstant.LOGIC_DELETE_NORMAL);
        infoCatalog.setCreateTime(System.currentTimeMillis());
        infoCatalog.setUpdateTime(System.currentTimeMillis());
        infoCatalog.setId(IDUtils.getID());
        int falg = infoCatalogMapper.insert(infoCatalog);
        //分类存入到缓存
        if(falg>0){
            updateInfoCategoryRedis(orgId,parentId,infoCatalog.getId());
        }
        return falg > 0 ? InfoConstants.StateCode.CODE_200 : InfoConstants.StateCode.CODE_602;
    }

    /**
     * 修改分类
     * @param orgId
     * @param parentId
     * @param id
     * @param name
     * @return
     * @throws BaseException
     */
    @Override
    public InfoConstants.StateCode modifyInfoCategory(Long orgId, Long parentId, Long id, String name) throws BaseException {
        EntityWrapper<InfoCatalog> wrapper = new EntityWrapper<>();
        InfoCatalog infoCatalog = new InfoCatalog();
        infoCatalog.setUpdateTime(System.currentTimeMillis());
        infoCatalog.setName(name);
        wrapper.where("org_id={0}", orgId).and("parent_id={0}",parentId).and("id={0}", id).and("is_delete={0}", InfoConstant.LOGIC_DELETE_NORMAL);
        int falg = infoCatalogMapper.update(infoCatalog, wrapper);
        //更新缓存
        if(falg>0){
            updateInfoCategoryRedis(orgId,parentId,id);
        }
        return falg > 0 ? InfoConstants.StateCode.CODE_200 : InfoConstants.StateCode.CODE_602;
    }

    /**
     * 删除分类
     * @param orgId
     * @param parentId
     * @param id
     * @return
     * @throws BaseException
     */
    @Override
    public InfoConstants.StateCode deleteInfoCategory(Long orgId, Long parentId, Long id) throws BaseException {
        EntityWrapper<InfoCatalog> wrapper = new EntityWrapper<>();
        InfoCatalog infoCatalog = new InfoCatalog();
        infoCatalog.setUpdateTime(System.currentTimeMillis());
        infoCatalog.setIsDelete(InfoConstant.LOGIC_DELETE_DELETE);
        wrapper.where("org_id={0}", orgId).and("parent_id={0}",parentId).and("id={0}", id);
        int falg = infoCatalogMapper.update(infoCatalog, wrapper);
        //删除成功 删除该分类下的内容
        if(falg>0){
            //先查询 该分类下是否有内容
            Map<String, Object> infoContentMap = new HashMap<>(4);
            infoContentMap.put("org_id", orgId);
            infoContentMap.put("catalog_id", id);
            List<InfoContent> infoContentList = infoContentMapper.selectByMap(infoContentMap);
            if(!ValidationUtil.isEmpty(infoContentList)){
                //有则删除，无直接返回
                EntityWrapper<InfoContent> infoContentEntityWrapper = new EntityWrapper<>();
                InfoContent infoContent = new InfoContent();
                infoContent.setUpdateTime(System.currentTimeMillis());
                infoContent.setIsDelete(InfoConstant.LOGIC_DELETE_DELETE);
                infoContentEntityWrapper.where("org_id={0}", orgId).and("catalog_id={0}",id);
                falg = infoContentMapper.update(infoContent,infoContentEntityWrapper);
            }
            //更新分类缓存
            if (redisTemplate.hasKey(InfoConstants.BOTONG_INFO_CATALOG_LIST+orgId.toString()+InfoConstants.BOTONG_INFO_FIX+parentId.toString())) {
                redisTemplate.opsForHash().delete(InfoConstants.BOTONG_INFO_CATALOG_LIST + orgId.toString() + InfoConstants.BOTONG_INFO_FIX + parentId.toString(), id.toString());
            }
        }
        return falg > 0 ? InfoConstants.StateCode.CODE_200 : InfoConstants.StateCode.CODE_602;
    }

    /**
     * 删除资讯
     * @param orgId
     * @param id
     * @return
     * @throws BaseException
     */
    @Override
    public InfoConstants.StateCode deleteInfoContent(Long orgId, Long id) throws BaseException {
        EntityWrapper<InfoContent> infoContentEntityWrapper = new EntityWrapper<>();
        InfoContent infoContent = new InfoContent();
        infoContent.setUpdateTime(System.currentTimeMillis());
        infoContent.setIsDelete(InfoConstant.LOGIC_DELETE_DELETE);
        infoContentEntityWrapper.where("org_id={0}", orgId).and("id={0}",id);
        int falg = infoContentMapper.update(infoContent,infoContentEntityWrapper);
        return falg > 0 ? InfoConstants.StateCode.CODE_200 : InfoConstants.StateCode.CODE_602;
    }

    /**
     * 显示、隐藏分类
     * @param orgId
     * @param parentId
     * @param id
     * @param displayType 0：否，1：是
     * @return
     * @throws BaseException
     */
    @Override
    public InfoConstants.StateCode displayInfoCategory(Long orgId, Long parentId, Long id, Integer displayType) throws BaseException {
        EntityWrapper<InfoCatalog> wrapper = new EntityWrapper<>();
        InfoCatalog infoCatalog = new InfoCatalog();
        infoCatalog.setUpdateTime(System.currentTimeMillis());
        infoCatalog.setIsDelete(InfoConstant.LOGIC_DELETE_NORMAL);
        infoCatalog.setWhetherShow(displayType);
        wrapper.where("org_id={0}", orgId).and("parent_id={0}",parentId).and("id={0}", id);
        int falg = infoCatalogMapper.update(infoCatalog, wrapper);
        //更新缓存
        //如果是隐藏则删除缓存数据
        if(InfoConstants.INFO_TYPE_DISPLAY.equals(displayType)){
            updateInfoCategoryRedis(orgId,parentId,id);
        }else{
            if (redisTemplate.hasKey(InfoConstants.BOTONG_INFO_CATALOG_LIST+orgId.toString()+InfoConstants.BOTONG_INFO_FIX+parentId.toString())) {
                redisTemplate.opsForHash().delete(InfoConstants.BOTONG_INFO_CATALOG_LIST + orgId.toString() + InfoConstants.BOTONG_INFO_FIX + parentId.toString(), id.toString());
            }
        }
        //如果是显示则添加缓存数据
        return falg > 0 ? InfoConstants.StateCode.CODE_200 : InfoConstants.StateCode.CODE_602;
    }

    /**
     * 显示、隐藏资讯
     * @param orgId
     * @param id
     * @param displayType  0：否，1：是
     * @return
     * @throws BaseException
     */
    @Override
    public InfoConstants.StateCode displayInfoContent(Long orgId, Long id, Integer displayType) throws BaseException {
        EntityWrapper<InfoContent> infoContentEntityWrapper = new EntityWrapper<>();
        InfoContent infoContent = new InfoContent();
        infoContent.setUpdateTime(System.currentTimeMillis());
        infoContent.setIsDelete(InfoConstant.LOGIC_DELETE_NORMAL);
        infoContent.setWhetherShow(displayType);
        infoContentEntityWrapper.where("org_id={0}", orgId).and("id={0}",id);
        int falg = infoContentMapper.update(infoContent,infoContentEntityWrapper);
        return falg > 0 ? InfoConstants.StateCode.CODE_200 : InfoConstants.StateCode.CODE_602;
    }


    /**
     * 获取一级下所有的分类 根据序号升序
     * @param orgId
     * @return
     */
    @Override
    public List<InfoCatalogDTO> getInfoCatalogList(Long orgId) throws BaseException{

        //方法 需要根据缓存获取
        //1、根据orgId 获取1级目录
        // 2、根据1级目录Id 获取2级目录

//        EntityWrapper<InfoCatalog> wrapper = new EntityWrapper<>();
//        wrapper.where("org_id={0}", orgId).and("level=1").and("is_delete=0").orderBy("sort",true);
//        //所有1级
//        List<InfoCatalog> infoCatalogList1 =  infoCatalogMapper.selectList(wrapper);
//
//        //所有2级
//        EntityWrapper<InfoCatalog> wrapper2 = new EntityWrapper<>();
//        wrapper2.where("org_id={0}", orgId).and("level=2").and("is_delete=0").orderBy("sort",true);
//        List<InfoCatalog> infoCataloglist2 =  infoCatalogMapper.selectList(wrapper2);
//
//        List<InfoCatalogDTO> infoCatalogDTOList1 = new ArrayList<>();
//
//        List<InfoCatalogDTO> infoCatalogDTOList2 = new ArrayList<>();
//
//        BeanUtils.copy(infoCatalogList1,infoCatalogDTOList1);
//
//        BeanUtils.copy(infoCataloglist2,infoCatalogDTOList2);
//
//        for(int i=0;i<infoCatalogDTOList1.size();i++){
//            if(!ValidationUtil.isEmpty(infoCatalogDTOList1.get(i))){
//                List<InfoCatalogDTO> infoCatalogDTOList3 = new ArrayList<>();
//                for(int j=0;j<infoCatalogDTOList2.size();j++){
//                    if(infoCatalogDTOList1.get(i).getId().equals(infoCatalogDTOList2.get(j).getParentId())){
//                        infoCatalogDTOList3.add(infoCatalogDTOList2.get(j));
//                    }
//                }
//                infoCatalogDTOList1.get(i).setLower(infoCatalogDTOList3);
//            }
//        }
//        return infoCatalogDTOList1;
        return null;
    }

    /**
     * 更新资讯分类缓存操作
     * @param orgId
     * @param parentId
     * @param id
     */
    private void updateInfoCategoryRedis(Long orgId,Long parentId,Long id){
        Map<String, Object> infoContentMap = new HashMap<>(5);
        infoContentMap.put("org_id", orgId);
        infoContentMap.put("parent_id", parentId);
        infoContentMap.put("catalog_id", id);
        InfoCatalog infoCatalog = (InfoCatalog) infoCatalogMapper.selectByMap(infoContentMap);
        if(!ValidationUtil.isEmpty(infoCatalog)){
            //先删除   botong:info:org:orgid:yijikey->object
            if (redisTemplate.hasKey(InfoConstants.BOTONG_INFO_CATALOG_LIST+orgId.toString()+InfoConstants.BOTONG_INFO_FIX+parentId.toString())) {
                redisTemplate.opsForHash().delete(InfoConstants.BOTONG_INFO_CATALOG_LIST+orgId.toString()+InfoConstants.BOTONG_INFO_FIX+parentId.toString(),infoCatalog.getId().toString());
            }else{
                // 在更新
                redisTemplate.opsForHash().put(InfoConstants.BOTONG_INFO_CATALOG_LIST+orgId.toString()+InfoConstants.BOTONG_INFO_FIX+parentId.toString(),infoCatalog.getId().toString(), JSON.toJSONString(infoCatalog));
            }
        }
    }

    /**
     * 类目排序
     * @param orgId
     * @param parentId
     * @param catalogId1
     * @param catalogId2
     * @return
     * @throws BaseException
     */
    @Override
    public InfoConstants.StateCode updateCatalogSort(Long orgId, Long parentId, Long catalogId1, Long catalogId2) throws BaseException {

        JSONObject jsonObject1 = JSON.parseObject((String) redisTemplate.opsForHash().get(InfoConstants.BOTONG_INFO_CATALOG_LIST+orgId.toString()+InfoConstants.BOTONG_INFO_FIX+parentId.toString(), catalogId1.toString()));
        JSONObject jsonObject2 = JSON.parseObject((String) redisTemplate.opsForHash().get(InfoConstants.BOTONG_INFO_CATALOG_LIST+orgId.toString()+InfoConstants.BOTONG_INFO_FIX+parentId.toString(), catalogId2.toString()));
        int falg = 0;
        if(!ValidationUtil.isEmpty(jsonObject1) && !ValidationUtil.isEmpty(jsonObject2)){
            EntityWrapper<InfoCatalog> wrapper1 = new EntityWrapper<>();
            InfoCatalog infoCatalog1 = new InfoCatalog();
            infoCatalog1.setUpdateTime(System.currentTimeMillis());

            EntityWrapper<InfoCatalog> wrapper2 = new EntityWrapper<>();
            InfoCatalog infoCatalog2 = new InfoCatalog();
            infoCatalog2.setUpdateTime(System.currentTimeMillis());

            infoCatalog1.setSort((Integer) jsonObject2.get("sort"));
            wrapper1.where("org_id={0}", orgId).and("parent_id={0}",parentId).and("id={0}", catalogId1).and("is_delete=0");
            falg = infoCatalogMapper.update(infoCatalog1, wrapper1);

            infoCatalog2.setSort((Integer) jsonObject1.get("sort"));
            wrapper2.where("org_id={0}", orgId).and("parent_id={0}",parentId).and("id={0}", catalogId2).and("is_delete=0");
            falg = infoCatalogMapper.update(infoCatalog2, wrapper2);
            //更新缓存
            updateInfoCategoryRedis(orgId,parentId,catalogId1);
            updateInfoCategoryRedis(orgId,parentId,catalogId2);
        }
        return falg > 0 ? InfoConstants.StateCode.CODE_200 : InfoConstants.StateCode.CODE_602;
    }

    /**
     * 资讯排序
     * @param orgId
     * @param id1
     * @param id2
     * @return
     * @throws BaseException
     */
    @Override
    public InfoConstants.StateCode updateInfoSort(Long orgId, Long id1, Long id2) throws BaseException {
        Map<String, Object> infoContentMap1 = new HashMap<>(3);
        infoContentMap1.put("org_id", orgId);
        infoContentMap1.put("id", id1);
        InfoContent infoContent1 = (InfoContent) infoContentMapper.selectByMap(infoContentMap1);

        Map<String, Object> infoContentMap2 = new HashMap<>(3);
        infoContentMap2.put("org_id", orgId);
        infoContentMap2.put("id", id2);
        InfoContent infoContent2 = (InfoContent) infoContentMapper.selectByMap(infoContentMap2);
        int falg = 0;
        if(!ValidationUtil.isEmpty(infoContent1) && !ValidationUtil.isEmpty(infoContent2)){
            EntityWrapper<InfoContent> wrapper1 = new EntityWrapper<>();
            infoContent1.setUpdateTime(System.currentTimeMillis());
            infoContent1.setSort(infoContent2.getSort());
            wrapper1.where("org_id={0}", orgId).and("id={0}", id1).and("is_delete=0");
            falg = infoContentMapper.update(infoContent1, wrapper1);

            EntityWrapper<InfoContent> wrapper2 = new EntityWrapper<>();
            infoContent2.setUpdateTime(System.currentTimeMillis());
            infoContent2.setSort(infoContent1.getSort());
            wrapper2.where("org_id={0}", orgId).and("id={0}", id2).and("is_delete=0");
            falg = infoContentMapper.update(infoContent2, wrapper2);
        }
        return falg > 0 ? InfoConstants.StateCode.CODE_200 : InfoConstants.StateCode.CODE_602;
    }

    /**
     * web端资讯分页模糊查询
     * @param orgId
     * @param catalogId
     * @param title
     * @param pageNo
     * @param pageSize
     * @return
     * @throws BaseException
     */
    @Override
    public PageWrapper<InfoContentDTO> selectParentPage(Long orgId, Long catalogId, String title,Integer pageNo, Integer pageSize) throws BaseException {
        //统计总数
        Wrapper<InfoContent> wrapper = new EntityWrapper<>();
        wrapper.eq("org_id", orgId).and().eq("catalog_id",catalogId).and().eq("is_delete",InfoConstant.LOGIC_DELETE_NORMAL);
        Integer count = infoContentMapper.selectCount(wrapper);
        //分页查询数据
        Page<InfoContentDTO> page = new Page<>(pageNo, pageSize);
        List<InfoContentDTO> infoContentDTOList = new ArrayList<>();
        if (!ValidationUtil.isEmpty(count) && count > 0) {
            //计算分页大小
            pageNo = (pageNo-1) * pageSize;
            infoContentDTOList = infoContentMapper.selectParentPage(orgId,catalogId,title,pageNo,pageSize);
        }
        page.setRecords(infoContentDTOList);
        page.setTotal(count);
        PageWrapper pageWrapper = BeanUtils.mapPage(page, InfoContentDTO.class);
        return pageWrapper;
    }
}
