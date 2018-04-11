package com.yunjing.botong.log.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.yunjing.botong.log.constant.LogConstant;
import com.yunjing.botong.log.entity.LogTemplateEntity;
import com.yunjing.botong.log.entity.LogTemplateEnumEntity;
import com.yunjing.botong.log.entity.LogTemplateEnumItemEntity;
import com.yunjing.botong.log.entity.LogTemplateFieldEntity;
import com.yunjing.botong.log.mapper.LogTemplateEnumItemMapper;
import com.yunjing.botong.log.mapper.LogTemplateEnumMapper;
import com.yunjing.botong.log.mapper.LogTemplateFieldMapper;
import com.yunjing.botong.log.mapper.LogTemplateMapper;
import com.yunjing.botong.log.params.LogTemplateParam;
import com.yunjing.botong.log.params.SearchParam;
import com.yunjing.botong.log.service.LogTemplateService;
import com.yunjing.botong.log.vo.LogTemplateEnumItemVo;
import com.yunjing.botong.log.vo.LogTemplateFieldVo;
import com.yunjing.botong.log.vo.LogTemplateItemVo;
import com.yunjing.botong.log.vo.LogTemplateVo;
import com.yunjing.mommon.global.exception.BaseRuntimeException;
import com.yunjing.mommon.utils.DateUtil;
import com.yunjing.mommon.wrapper.PageWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 日志模板服务实现类
 *
 * @author 王开亮
 * @date 2018/4/2 10:55
 */
@Service
public class LogTemplateServiceImpl implements LogTemplateService {

    @Autowired
    private LogTemplateMapper logTemplateMapper ;

    @Autowired
    private LogTemplateFieldMapper logTemplateFieldMapper;

    @Autowired
    private LogTemplateEnumMapper logTemplateEnumMapper;

    @Autowired
    private LogTemplateEnumItemMapper logTemplateEnumItemMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createLogTemplate(LogTemplateParam logTemplateParam) {
        // 插入模板
        LogTemplateEntity entity = new LogTemplateEntity();
        entity.fromParam(logTemplateParam);
        entity.setId(IdWorker.get32UUID());
        entity.insert();

        // 批量插入字段
        List<LogTemplateFieldEntity> fields = logTemplateParam.getLogTemplateFieldEntity();

        List<LogTemplateEnumEntity> enums = new ArrayList<>();
        List<LogTemplateEnumItemEntity> enumItems = new ArrayList<>();

        for (int i = 0; i < fields.size(); i++) {
            LogTemplateFieldEntity fieldEntity = fields.get(i);
            fieldEntity.setTemplateId(entity.getId());
            if (fieldEntity.getFieldType() == 3) {
                enums.add(fieldEntity.getLogTemplateEnumEntity());
                enumItems.addAll(fieldEntity.getLogTemplateEnumEntity().getLogTemplateEnumItemEntities());
            }
        }
        // 批量插入字段
        this.logTemplateFieldMapper.batchInsertLogTemplateFields(fields);

        // 批量插入枚举列表
        if (!CollectionUtils.isEmpty(enums)) {
            logTemplateEnumMapper.batchInsertLogTemplateEnums(enums);
        }
        // 批量插入枚举项列表
        if (!CollectionUtils.isEmpty(enumItems)) {
            logTemplateEnumItemMapper.batchInsertLogTemplateEnumItems(enumItems);
        }

        return entity.getId();
    }

    @Override
    public boolean deleteLogTemplate(String id) {
        LogTemplateEntity logTemplateEntity = new LogTemplateEntity();
        logTemplateEntity.setId(id);
        logTemplateEntity.setDeleted(true);
        logTemplateEntity.updateById();
        return true;
    }

    @Override
    public PageWrapper<LogTemplateItemVo> queryAllLogTemplate(String orgId, int pageNo, int pageSize) {
        PageWrapper<LogTemplateItemVo> result = new PageWrapper<>();

        int offset = (pageNo-1)*pageSize;
        result.setCurrent(offset);
        result.setSize(pageSize);
        long total = this.logTemplateMapper.totalLogTemplateByOrgId(orgId);
        result.setTotal(total);
        result.setPages((int)((total+pageSize-1)/pageSize));
        result.setRecords(this.logTemplateMapper.listLogTemplateByOrgId(orgId,offset,pageSize));

        return result;
    }

    @Override
    public LogTemplateVo queryLogTemplate(String id) {
        // 查询日志模板
        LogTemplateEntity entity = new LogTemplateEntity();
        entity = entity.selectOne(new EntityWrapper().eq("id",id).eq("deleted",false));
        if(entity==null){
            return null;
        }

        // 组织返回结果
        LogTemplateVo result = new LogTemplateVo();
        result.fromEntity(entity);

        // 查询日志模板字段
        LogTemplateFieldEntity logTemplateFieldEntity = new LogTemplateFieldEntity();
        List<LogTemplateFieldEntity> fieldEntities = logTemplateFieldEntity.selectList(
                new EntityWrapper().eq("template_id",id).eq("deleted",false).orderBy("sort"));

        //组织字段列表返回值
        if(CollectionUtils.isNotEmpty(fieldEntities)){
            List<LogTemplateFieldVo> logTemplateFieldVoList = new ArrayList<>(fieldEntities.size());

            List<String> enumIdList = new ArrayList<>();
            for (int i = 0; i < fieldEntities.size(); i++) {
                LogTemplateFieldVo logTemplateFieldVo = new LogTemplateFieldVo();
                logTemplateFieldVo.fromEntity(fieldEntities.get(i));
                if(fieldEntities.get(i).getFieldType()==3){
                    enumIdList.add(fieldEntities.get(i).getEnumId());
                }
                logTemplateFieldVoList.add(logTemplateFieldVo);
            }

            // 查询日志模板字段枚举项
            if(CollectionUtils.isNotEmpty(enumIdList)){
                LogTemplateEnumItemEntity logTemplateEnumItemEntity = new LogTemplateEnumItemEntity();
                List<LogTemplateEnumItemEntity> logTemplateEnumItemEntities = logTemplateEnumItemEntity.selectList(
                        new EntityWrapper().in("enum_id",enumIdList).eq("deleted",false));

                // 循环日志字段列表，初始化枚举类型数据的枚举项
                for (int i = 0; i < logTemplateFieldVoList.size(); i++) {
                    LogTemplateFieldVo logTemplateFieldVo = logTemplateFieldVoList.get(i);
                    if(logTemplateFieldVo.getType()!=3){
                        continue;
                    }
                    for (int j = 0; j < logTemplateEnumItemEntities.size(); j++) {
                        if (logTemplateFieldVo.getEnumId().equals(logTemplateEnumItemEntities.get(j).getEnumId())){
                            if(logTemplateFieldVo.getEnumItems()==null){
                                logTemplateFieldVo.setEnumItems(new ArrayList<LogTemplateEnumItemVo>());
                            }
                            LogTemplateEnumItemVo logTemplateEnumItemVo = new LogTemplateEnumItemVo();
                            logTemplateEnumItemVo.fromEntity(logTemplateEnumItemEntities.get(i));
                            logTemplateFieldVo.getEnumItems().add(logTemplateEnumItemVo);
                        }
                    }
                }
            }
            result.setItems(logTemplateFieldVoList);
        }

        return result;
    }

    @Override
    public boolean updateLogTemplate(LogTemplateParam logTemplateParam) {
        //插入新记录
        LogTemplateEntity queryEntity = new LogTemplateEntity();
        queryEntity.setId(logTemplateParam.getId());
        queryEntity = queryEntity.selectById();
        if(queryEntity==null){
            throw new BaseRuntimeException(500,"更新的记录不存在");
        }

        //更新之前的版本的currently为false
        LogTemplateEntity updateEntity = new LogTemplateEntity();
        updateEntity.setId(logTemplateParam.getId());
        updateEntity.setCurrently(false);
        updateEntity.updateById();
        this.logTemplateFieldMapper.updateCurrentlyByLogTemplateId(logTemplateParam.getId(),false);


        LogTemplateEntity entity = new LogTemplateEntity();
        entity.fromParam(logTemplateParam);
        entity.setVersion(queryEntity.getVersion()+1);
        entity.insert();

        // 批量插入字段
        List<LogTemplateFieldEntity> fields = logTemplateParam.getLogTemplateFieldEntity();

        List<LogTemplateEnumEntity> enums = new ArrayList<>();
        List<LogTemplateEnumItemEntity> enumItems = new ArrayList<>();

        for (int i = 0; i < fields.size(); i++) {
            LogTemplateFieldEntity fieldEntity = fields.get(i);
            fieldEntity.setVersion(queryEntity.getVersion()+1);
            fieldEntity.setTemplateId(entity.getId());
            if (fieldEntity.getFieldType() == 3) {
                enums.add(fieldEntity.getLogTemplateEnumEntity());
                enumItems.addAll(fieldEntity.getLogTemplateEnumEntity().getLogTemplateEnumItemEntities());
            }
        }
        // 批量插入字段
        this.logTemplateFieldMapper.batchInsertLogTemplateFields(fields);

        // 批量插入枚举列表
        if (!CollectionUtils.isEmpty(enums)) {
            logTemplateEnumMapper.batchInsertLogTemplateEnums(enums);
        }
        // 批量插入枚举项列表
        if (!CollectionUtils.isEmpty(enumItems)) {
            logTemplateEnumItemMapper.batchInsertLogTemplateEnumItems(enumItems);
        }

        return true;
    }

    /**
     * 查询模板列头
     *
     * @param searchParam
     * @return
     */
    @Override
    public Map<String, List<LogTemplateFieldVo>> queryFields(SearchParam searchParam) {
        Long startDate = null;
        Long endDate = null;
        if (StringUtils.isNotEmpty(searchParam.getStartDate())) {
            startDate = DateUtil.stringToDate(searchParam.getStartDate()).getTime();
            endDate = DateUtil.stringToDate(searchParam.getEndDate()).getTime();
        }
        List<LogTemplateFieldVo>  templateFieldVoList = logTemplateFieldMapper.queryFields(startDate, endDate, searchParam.getSubmitType() != 0 ? searchParam.getSubmitType().toString() : "", "");
        if (templateFieldVoList == null) {
            templateFieldVoList = logTemplateFieldMapper.queryFields(null, null, searchParam.getSubmitType() != 0 ? searchParam.getSubmitType().toString() : "", LogConstant.BOTONG_ONE_STR);
        }
        Map<String, List<LogTemplateFieldVo>> result = new LinkedHashMap<>();
        List<LogTemplateFieldVo> list;
        for (LogTemplateFieldVo vo : templateFieldVoList) {
            if (result.get(vo.getTemplateName()) == null) {
                list = new ArrayList<>();
                list.add(vo);
                result.put(vo.getTemplateName(), list);
            } else {
                result.get(vo.getTemplateName()).add(vo);
            }

        }
        return result;
    }
}
