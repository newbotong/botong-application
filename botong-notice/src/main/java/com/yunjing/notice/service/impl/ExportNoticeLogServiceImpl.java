package com.yunjing.notice.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.notice.entity.ExportNoticeEntity;
import com.yunjing.notice.entity.ExportNoticeLogEntity;
import com.yunjing.notice.entity.NoticeEntity;
import com.yunjing.notice.entity.NoticeUserEntity;
import com.yunjing.notice.mapper.ExportNoticeLogMapper;
import com.yunjing.notice.mapper.ExportNoticeMapper;
import com.yunjing.notice.service.ExportNoticeService;
import com.yunjing.notice.service.NoticeService;
import com.yunjing.notice.service.NoticeUserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 1.0版本数据导入2.0
 *
 * @author 李双喜
 * @date 2018/4/11 10:06
 */
@Service
public class ExportNoticeLogServiceImpl extends ServiceImpl<ExportNoticeMapper, ExportNoticeEntity> implements ExportNoticeService {
    @Autowired
    private NoticeService noticeService;

    @Autowired
    private NoticeUserService noticeUserService;

    @Autowired
    private ExportNoticeLogMapper exportNoticeLogMapper;

    @Autowired
    private ExportNoticeMapper exportNoticeMapper;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void importData(){
        //查询主表
        List<ExportNoticeEntity> entityList = exportNoticeMapper.selectList(new EntityWrapper<>());
        //查询用户表
        List<ExportNoticeLogEntity> entityLogList = exportNoticeLogMapper.selectList(new EntityWrapper<ExportNoticeLogEntity>());

        List<NoticeEntity> noticeEntityList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(entityList)){
            for (ExportNoticeEntity exportNoticeEntity : entityList){
                NoticeEntity noticeEntity = new NoticeEntity();
                noticeEntity.setId(exportNoticeEntity.getNoticeId());
                noticeEntity.setLogicDelete(exportNoticeEntity.getState());
                noticeEntity.setOrgId(exportNoticeEntity.getOrgId());
                if (StringUtils.isNotEmpty(exportNoticeEntity.getAuthor())) {
                    noticeEntity.setAuthor(exportNoticeEntity.getAuthor());
                }
                noticeEntity.setCover(exportNoticeEntity.getTitleImg());
                noticeEntity.setIssueUserId(exportNoticeEntity.getUserId());
                noticeEntity.setContent(exportNoticeEntity.getContent());
                noticeEntity.setDangState(1);
                noticeEntity.setSecrecyState(1);
                noticeEntity.setCreateTime(exportNoticeEntity.getSendTime().getTime());
                noticeEntity.setUpdateTime(exportNoticeEntity.getSaveTime().getTime());
                noticeEntity.setTitle(exportNoticeEntity.getTitle());
                Integer i = exportNoticeLogMapper.selectCount(new EntityWrapper<ExportNoticeLogEntity>().eq("notice_id",exportNoticeEntity.getNoticeId()).eq("is_read",0));
                noticeEntity.setNotReadNum(i);
                Integer j = exportNoticeLogMapper.selectCount(new EntityWrapper<ExportNoticeLogEntity>().eq("notice_id",exportNoticeEntity.getNoticeId()).eq("is_read",1));
                noticeEntity.setReadNum(j);
                noticeEntityList.add(noticeEntity);
            }
            noticeService.insertBatch(noticeEntityList);
        }
        List<NoticeUserEntity> noticeUserEntities = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(entityLogList)){
            for (ExportNoticeLogEntity noticeLogEntity : entityLogList){
                NoticeUserEntity entity = new NoticeUserEntity();
                entity.setId(noticeLogEntity.getLogId());
                entity.setUserId(noticeLogEntity.getUserId());
                if (noticeLogEntity.getIsRead()==0){
                    entity.setState(1);
                }else {
                    entity.setState(0);
                }
                entity.setLogicDelete(0);
                entity.setNoticeId(noticeLogEntity.getNoticeId());
                entity.setCreateTime(noticeLogEntity.getReadTime().getTime());
                entity.setUpdateTime(noticeLogEntity.getReadTime().getTime());
                noticeUserEntities.add(entity);
            }
            noticeUserService.insertBatch(noticeUserEntities);
        }
    }
}
