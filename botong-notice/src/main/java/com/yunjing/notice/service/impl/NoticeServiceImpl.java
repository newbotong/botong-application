package com.yunjing.notice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.notice.body.NoticeBody;
import com.yunjing.notice.common.NoticeConstant;
import com.yunjing.notice.entity.NoticeEntity;
import com.yunjing.notice.entity.NoticeUserEntity;
import com.yunjing.notice.feign.AuthorityFeign;
import com.yunjing.notice.mapper.NoticeMapper;
import com.yunjing.notice.service.NoticeService;
import com.yunjing.notice.service.NoticeUserService;
import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 公告
 *
 * @author 李双喜
 * @since 2018/03/20/.
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, NoticeEntity> implements NoticeService {
//    @Autowired
//    AuthorityFeign authorityFeign;

    /**
     * 公告mapper
     */
    @Autowired
    private NoticeUserService noticeUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertNotice(NoticeBody noticeBody) throws BaseException {
        if (StringUtils.isEmpty(noticeBody.getIssueUserId())){
            throw new BaseException("发布人的用户id不能为空");
        }
        //查看该用户是否有发公告权限(等应用生成后提供)
//        ResponseEntityWrapper responseEntityWrapper = authorityFeign.authority("appId",noticeBody.getIssueUserId());
        //判断返回的结果是否为管理员，如果是管理员方可进入下一步


        int i = 15;
        int j = 200;
        if (StringUtils.isEmpty(noticeBody.getTitle())){
            throw new BaseException("标题不能为空");
        }else if (noticeBody.getTitle().length() <= i){
            throw new BaseException("标题长度不得大于15个字");
        }
        if (StringUtils.isEmpty(noticeBody.getContent())){
            throw new BaseException("内容不能为空");
        }else if (noticeBody.getContent().length() <= j){
            throw new BaseException("内容长度不得超过200个字");
        }
        if (StringUtils.isEmpty(noticeBody.getUserIds())){
            throw new BaseException("成员不能空");
        }
        String[] userIdArray = noticeBody.getUserIds().split(",");
        NoticeEntity notice = new NoticeEntity();
        BeanUtils.copyProperties(noticeBody,notice);
        boolean result = notice.insert();
        List<NoticeUserEntity> longList = new LinkedList<>();
        for (String userId : userIdArray){
            Long a = Long.parseLong(userId);
            NoticeUserEntity noticeUserEntity = new NoticeUserEntity();
            noticeUserEntity.setNoticeId(notice.getId());
            noticeUserEntity.setUserId(a);
            noticeUserEntity.setState(NoticeConstant.NOTICE_NOT_READ);
            longList.add(noticeUserEntity);
        }
        boolean b = noticeUserService.insertBatch(longList);
        if (!result && b){
            throw new BaseException("发布失败");
        }
    }


}
