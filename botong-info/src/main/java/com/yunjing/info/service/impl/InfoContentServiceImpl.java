package com.yunjing.info.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.info.common.InfoConstant;
import com.yunjing.info.dto.InfoContentDetailDto;
import com.yunjing.info.dto.InfoDto;
import com.yunjing.info.dto.Member;
import com.yunjing.info.dto.ParentInfoDetailDto;
import com.yunjing.info.mapper.InfoContentMapper;
import com.yunjing.info.model.InfoCatalog;
import com.yunjing.info.model.InfoContent;
import com.yunjing.info.param.InfoCategoryEditParam;
import com.yunjing.info.param.InfoCategoryParam;
import com.yunjing.info.processor.okhttp.CollectService;
import com.yunjing.info.processor.okhttp.OrgStructureService;
import com.yunjing.info.service.InfoContentService;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.utils.IDUtils;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资讯内容Service实现类
 *
 * @author 李双喜
 * @date 2018/3/30 15:39
 */
@Service
public class InfoContentServiceImpl extends ServiceImpl<InfoContentMapper, InfoContent> implements InfoContentService {
    @Autowired
    private InfoContentMapper infoContentMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CollectService collectService;

    @Autowired
    private OrgStructureService orgStructureService;

    /**
     * 查询资讯详情接口
     *
     * @param id     资讯id
     * @param userId 成员id
     * @return
     * @throws BaseException
     * @throws IOException
     */
    @Override
    public InfoContentDetailDto selectDetail(String id, String userId) throws BaseException, IOException {
        this.updateNumber(id);
        InfoContent infoContent = new InfoContent().selectOne(new EntityWrapper<InfoContent>().eq("is_delete", InfoConstant.LOGIC_DELETE_NORMAL).eq("id", id));
        if (null == infoContent) {
            throw new BaseException("该资讯已被删除");
        }
        InfoContentDetailDto infoContentDetailDto = new InfoContentDetailDto();
        BeanUtils.copyProperties(infoContent, infoContentDetailDto);
        //调用收藏的OKHttp
        Call<ResponseEntityWrapper<List<Member>>> call1 = orgStructureService.findSubLists("", userId, 0);
        Response<ResponseEntityWrapper<List<Member>>> execute1 = call1.execute();
        ResponseEntityWrapper<List<Member>> body1 = execute1.body();
        List<Member> memberList;
        if (null != body1.getData()) {
            memberList = body1.getData();
            String[] passportIds = memberList.stream().map(Member::getPassportId).toArray(String[]::new);
            Call<ResponseEntityWrapper> call = collectService.collectState(passportIds[0], id);
            Response<ResponseEntityWrapper> execute = call.execute();
            ResponseEntityWrapper body = execute.body();
            Boolean result;
            if (null != body.getData()) {
                result = (Boolean) body.getData();
                infoContentDetailDto.setFavouriteState(result);
            }
        } else {
            infoContentDetailDto.setFavouriteState(false);
        }
        return infoContentDetailDto;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateNumber(String id) {
        InfoContent infoContent = new InfoContent().selectOne(new EntityWrapper<InfoContent>().eq("is_delete", InfoConstant.LOGIC_DELETE_NORMAL).eq("id", id));
        if (null != infoContent) {
            infoContent.setReadNumber(infoContent.getReadNumber() + 1);
            this.updateById(infoContent);
        }
    }

    /**
     * 新增资讯接口
     *
     * @param infoCategoryParam 入参对象
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertInfo(InfoCategoryParam infoCategoryParam) throws BaseException {
        InfoContent infoContent = new InfoContent();
        infoContent.setId(IDUtils.uuid());
        BeanUtils.copyProperties(infoCategoryParam, infoContent);
        if (null == infoCategoryParam.getCatalogId()) {
            infoContent.setCatalogId(infoCategoryParam.getOneCatalogId());
        } else {
            infoContent.setCatalogId(infoCategoryParam.getCatalogId());
        }
        infoContent.setIsDelete(InfoConstant.LOGIC_DELETE_NORMAL);
        infoContent.setReadNumber(0);
        infoContent.setWhetherShow(1);
        Integer i = new InfoContent().selectCount(new EntityWrapper<InfoContent>()
                .eq("is_delete", InfoConstant.LOGIC_DELETE_NORMAL).eq("catalog_id", infoContent.getCatalogId()).orderBy("sort", false));
        infoContent.setSort(i + 1);
        Boolean result = infoContent.insert();
        if (!result) {
            throw new BaseException("新增失败");
        }
        ParentInfoDetailDto parentInfoDetailDto = new ParentInfoDetailDto();
        InfoCatalog infoCatalog = new InfoCatalog().selectOne(new EntityWrapper<InfoCatalog>().eq("is_delete", InfoConstant.LOGIC_DELETE_NORMAL).eq("id", infoCategoryParam.getOneCatalogId()));
        BeanUtils.copyProperties(infoContent, parentInfoDetailDto);
        if (null != infoCatalog) {
            parentInfoDetailDto.setName(infoCatalog.getName());
            parentInfoDetailDto.setCatalogSort(infoCatalog.getSort());
        }
        parentInfoDetailDto.setCatalogId(infoCategoryParam.getOneCatalogId());
        redisTemplate.opsForHash().put(InfoConstant.REDIS_HOME + ":" + infoCategoryParam.getOrgId(), infoCategoryParam.getOneCatalogId(), JSON.toJSONString(parentInfoDetailDto));
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
    public Page<InfoDto> searchPage(String orgId, String title, Integer pageNo, Integer pageSize) {
        Page<InfoDto> page = new Page<>(pageNo, pageSize);
        int i = 4;
        Map<String, Object> map = new HashMap<>(i);
        if (StringUtils.isNotEmpty(title)) {
            map.put("title", title);
        } else {
            return page;
        }
        map.put("orgId", orgId);
        List<InfoDto> infoDtoList = infoContentMapper.searchPage(map, page);
        page.setRecords(infoDtoList);
        return page;
    }

    /**
     * 修改资讯信息
     *
     * @param infoCategoryParam 实体入参
     * @return
     * @throws BaseException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void infoEdit(InfoCategoryEditParam infoCategoryParam) throws BaseException {
        if (null == infoCategoryParam.getId()) {
            throw new BaseException("资讯id不能为空");
        }
        InfoContent infoContent = new InfoContent().selectOne(new EntityWrapper<InfoContent>().eq("is_delete", InfoConstant.LOGIC_DELETE_NORMAL).eq("id", infoCategoryParam.getId()));
        if (null == infoContent) {
            throw new BaseException("该资讯已经被删除");
        }
        BeanUtils.copyProperties(infoCategoryParam, infoContent);
        boolean result = infoContent.updateById();
        if (!result) {
            throw new BaseException("修改失败");
        }
        Object object = redisTemplate.opsForHash().get(InfoConstant.REDIS_HOME + ":" + infoCategoryParam.getOrgId(), infoCategoryParam.getOneCatalogId());
        ParentInfoDetailDto infoDTO = JSONObject.parseObject(object.toString(), ParentInfoDetailDto.class);
        if (null != infoDTO) {
            if (infoCategoryParam.getId().equals(infoDTO.getId())) {
                redisTemplate.opsForHash().delete(InfoConstant.REDIS_HOME + ":" + infoCategoryParam.getOrgId(), infoCategoryParam.getOneCatalogId());
                ParentInfoDetailDto parentInfoDetailDto = new ParentInfoDetailDto();
                InfoCatalog infoCatalog = new InfoCatalog().selectOne(new EntityWrapper<InfoCatalog>().eq("is_delete", InfoConstant.LOGIC_DELETE_NORMAL).eq("id", infoCategoryParam.getOneCatalogId()));
                BeanUtils.copyProperties(infoContent, parentInfoDetailDto);
                if (null != infoCatalog) {
                    parentInfoDetailDto.setName(infoCatalog.getName());
                    parentInfoDetailDto.setCatalogSort(infoCatalog.getSort());
                }
                parentInfoDetailDto.setCatalogId(infoCategoryParam.getOneCatalogId());
                redisTemplate.opsForHash().put(InfoConstant.REDIS_HOME + ":" + infoCategoryParam.getOrgId(), infoCategoryParam.getOneCatalogId(), JSONObject.toJSONString(parentInfoDetailDto));
            }
        }
    }

    /**
     * 根据id查询详情
     *
     * @param id 根据id查询详情
     * @return
     * @throws BaseException
     */
    @Override
    public InfoContent selectWebDetail(String id) throws BaseException {
        InfoContent infoContent = new InfoContent().selectOne(new EntityWrapper<InfoContent>().eq("is_delete", InfoConstant.LOGIC_DELETE_NORMAL).eq("id", id));
        if (null == infoContent) {
            throw new BaseException("该资讯已被删除");
        }
        return infoContent;
    }
}
