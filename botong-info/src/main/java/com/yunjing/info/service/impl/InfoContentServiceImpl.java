package com.yunjing.info.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.info.common.InfoConstant;
import com.yunjing.info.dto.InfoContentDetailDTO;
import com.yunjing.info.dto.InfoDTO;
import com.yunjing.info.dto.ParentInfoDetailDTO;
import com.yunjing.info.mapper.InfoContentMapper;
import com.yunjing.info.model.InfoCatalog;
import com.yunjing.info.model.InfoContent;
import com.yunjing.info.param.InfoCategoryParam;
import com.yunjing.info.service.InfoContentService;
import com.yunjing.mommon.global.exception.BaseException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资讯内容Service实现类
 * @author 李双喜
 * @date 2018/3/30 15:39
 */
@Service
public class InfoContentServiceImpl extends ServiceImpl<InfoContentMapper, InfoContent> implements InfoContentService {
    @Autowired
    private InfoContentMapper infoContentMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 查询资讯详情接口
     *
     * @param id     资讯id
     * @param userId 用户id
     * @return
     * @throws BaseException
     */
    @Override
    public InfoContentDetailDTO selectDetail(Long id, Long userId) throws BaseException {
        InfoContent infoContent = new InfoContent().selectOne(new EntityWrapper<InfoContent>().eq("is_delete", InfoConstant.LOGIC_DELETE_NOMAL).eq("id", id));
        if (null == infoContent) {
            throw new BaseException("该资讯已被删除");
        }
        InfoContentDetailDTO infoContentDetailDTO = new InfoContentDetailDTO();
        BeanUtils.copyProperties(infoContent, infoContentDetailDTO);

        //调用收藏的OKHttp
        //现在定死了
        infoContentDetailDTO.setFavouriteState(false);
        return infoContentDetailDTO;
    }

    /**
     * 更新阅读数量接口
     *
     * @param id 资讯id
     * @return
     * @throws BaseException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNumber(Long id) throws BaseException {
        InfoContent infoContent = new InfoContent().selectOne(new EntityWrapper<InfoContent>().eq("is_delete", InfoConstant.LOGIC_DELETE_NOMAL).eq("id", id));
        if (null != infoContent) {
            infoContent.setReadNumber(infoContent.getReadNumber() + 1);
            this.updateById(infoContent);
        } else {
            throw new BaseException("该资讯已被删除");
        }
    }

    /**
     * 新增资讯接口
     *
     * @param infoCategoryParam 入参对象
     * @return
     */
    @Override
    public void insertInfo(InfoCategoryParam infoCategoryParam) throws BaseException {
        InfoContent infoContent = new InfoContent();
        BeanUtils.copyProperties(infoCategoryParam, infoContent);
        if (null == infoCategoryParam.getCatalogId()){
            infoContent.setCatalogId(infoCategoryParam.getOneCatalogId());
        }else {
            infoContent.setCatalogId(infoCategoryParam.getCatalogId());
        }
        infoContent.setIsDelete(InfoConstant.LOGIC_DELETE_NOMAL);
        infoContent.setReadNumber(0);
        infoContent.setWhetherShow(1);
        List<InfoContent> infoContentList = new InfoContent().selectList(new EntityWrapper<InfoContent>()
                .eq("is_delete", InfoConstant.LOGIC_DELETE_NOMAL).eq("catalog_id", infoCategoryParam.getCatalogId()).orderBy("sort", false));
        if (CollectionUtils.isNotEmpty(infoContentList)) {
            InfoContent infoContent1 = infoContentList.get(0);
            infoContent.setSort(infoContent1.getSort() + 1);
        } else {
            infoContent.setSort(1);
        }
        Boolean result = infoContent.insert();
        if (!result) {
            throw new BaseException("新增失败");
        }
        ParentInfoDetailDTO parentInfoDetailDTO = new ParentInfoDetailDTO();
        InfoCatalog infoCatalog = new InfoCatalog().selectOne(new EntityWrapper<InfoCatalog>().eq("is_delete",InfoConstant.LOGIC_DELETE_NOMAL).eq("id",infoCategoryParam.getOneCatalogId()));
        BeanUtils.copyProperties(infoContent,parentInfoDetailDTO);
        if (null != infoCatalog){
            parentInfoDetailDTO.setName(infoCatalog.getName());
            parentInfoDetailDTO.setCatalogSort(infoCatalog.getSort());
        }
        parentInfoDetailDTO.setCatalogId(infoCategoryParam.getOneCatalogId());
        redisTemplate.opsForHash().put(InfoConstant.REDIS_HOME+":"+infoCategoryParam.getOrgId(),infoCategoryParam.getOneCatalogId().toString(), JSON.toJSONString(parentInfoDetailDTO));
    }


    /**
     * 模糊查询资讯
     *
     * @param orgId    企业
     * @param title    标题
     * @param pageNo   当前页码
     * @param pageSize 每页显示条数
     * @return
     * @throws BaseException
     */
    @Override
    public Page<InfoDTO> searchPage(Long orgId, String title,Integer pageNo,Integer pageSize) {
        Page<InfoDTO> page = new Page<>(pageNo,pageSize);
        int i = 4;
        Map<String,Object> map = new HashMap<>(i);
        if (StringUtils.isNotEmpty(title)){
            map.put("title",title);
        }else {
            return page;
        }
        map.put("orgId",orgId);
        List<InfoDTO> infoDTOList = infoContentMapper.searchPage(map,page);
        page.setRecords(infoDTOList);
        return page;
    }
}
