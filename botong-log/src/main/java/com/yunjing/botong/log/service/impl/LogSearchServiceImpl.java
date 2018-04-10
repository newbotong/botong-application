package com.yunjing.botong.log.service.impl;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.common.mongo.dao.Page;
import com.common.mongo.util.BeanUtils;
import com.common.mongo.util.PageWrapper;
import com.netflix.discovery.converters.Auto;
import com.yunjing.botong.log.constant.LogConstant;
import com.yunjing.botong.log.dao.LogDetailDao;
import com.yunjing.botong.log.entity.LogDetail;
import com.yunjing.botong.log.params.ReceviedParam;
import com.yunjing.botong.log.params.SearchParam;
import com.yunjing.botong.log.processor.okhttp.AppCenterService;
import com.yunjing.botong.log.service.ILogSearchService;
import com.yunjing.botong.log.vo.LogDetailVO;
import com.yunjing.botong.log.vo.Member;
import com.yunjing.botong.log.vo.MemberVO;
import com.yunjing.botong.log.vo.UserVO;
import com.yunjing.mommon.utils.ListUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 日志搜索服务实现类
 *
 * @author jingwj
 * @date 2018/4/2 10:55
 */
@Service
public class LogSearchServiceImpl implements ILogSearchService {

    @Autowired
    private LogDetailDao logDetailDao;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    AppCenterService appCenterService;

    private Type memberType = new TypeReference<List<Member>>() {
    }.getType();



    /**
     * 我收到的日志列表
     *
     * @param receviedParam         收到的参数对象
     * @return                      分页日志列表
     */
    @Override
    public PageWrapper<LogDetailVO> receivePage(ReceviedParam receviedParam) {
        PageWrapper<LogDetail> detailResult = logDetailDao.find(receviedParam.getPageNo(), receviedParam.getPageSize(), receviedParam.getOrgId(), receviedParam.getUserId(), receviedParam.getReadStatus(), receviedParam.getSendUserIds(), LogConstant.BOTONG_ONE_STR);
        PageWrapper<LogDetailVO> result = convertResults(receviedParam, detailResult);
        return result;
    }

    /**
     * 查询我发送的日志列表
     *
     * @param receviedParam 日志参数对象
     * @return 日志明细列表
     */
    @Override
    public PageWrapper<LogDetailVO> sendPage(ReceviedParam receviedParam) {
        PageWrapper<LogDetail> detailResult = logDetailDao.find(receviedParam.getPageNo(), receviedParam.getPageSize(), receviedParam.getOrgId(), null, null, null, LogConstant.BOTONG_ZERO_STR);
        PageWrapper<LogDetailVO> result = convertResults(receviedParam, detailResult);
        return result;
    }

    /**
     * 查询我发送的日志列表
     *
     * @param logId 日志Id
     * @return 日志明细列表
     */
    @Override
    public boolean read(String logId, String userId) {
        logDetailDao.updateReadByLogId(logId, userId);
        return true;
    }

    /**
     * 未读设置为已读
     *
     * @param receviedParam 日志参数对象
     * @return 成功与否
     */
    @Override
    public boolean read(ReceviedParam receviedParam) {
        logDetailDao.updateReadAll(receviedParam.getOrgId(), receviedParam.getUserId(), receviedParam.getSendUserIds());
        return true;
    }

    /**
     * 删除日志
     *
     * @param logId  日志id
     * @param userId 用户id
     * @return 成功与否
     */
    @Override
    public boolean delete(String logId, String userId) {
        logDetailDao.delete(logId, userId);
        return true;
    }

    /**
     * 根据条件查询日志列表
     *
     * @param searchParam 日志参数对象
     * @return 日志明细列表
     */
    @Override
    public PageWrapper<LogDetailVO> findPage(SearchParam searchParam) {
        //当没有选择范围则查询该企业下所有的员工
        if (searchParam.getDeptIds().length == 0 && searchParam.getUserIds().length == 0) {
            searchParam.getDeptIds()[0] = searchParam.getOrgId();
        }
        List<Member> memList = appCenterService.findSubLists(searchParam.getDeptIds(), searchParam.getUserIds());
        List<String> memIds = new ArrayList<>();
        Map<String, Member> memberMap = new HashMap<>();
        for(Member member : memList) {
            memIds.add(member.getId());
            memberMap.put(member.getId(), member);
        }

        PageWrapper<LogDetail> detailResult = logDetailDao.findByConditionPage(searchParam.getPageNo(), searchParam.getPageSize(), searchParam.getOrgId(), memIds, searchParam.getSubmitType(), searchParam.getStartDate(), searchParam.getEndDate());
        PageWrapper<LogDetailVO> result = new PageWrapper<>();
        if (detailResult.getRecords() != null && detailResult.getSize() > 0) {
            LogDetailVO vo;
            List<LogDetailVO> resultRecord = new ArrayList<>();
            List<MemberVO> userList;
            for (LogDetail detail : detailResult.getRecords()) {
                vo = BeanUtils.map(detail, LogDetailVO.class);
                vo.setUser(memberMap.get(detail.getMemberId()));
                resultRecord.add(vo);
            }
            result.setCurrent(detailResult.getCurrent());
            result.setPages(detailResult.getPages());
            result.setSize(detailResult.getSize());
            result.setTotal(detailResult.getTotal());
            result.setRecords(resultRecord);
        }
        return result;
    }

    /**
     * mongo中拿到的实体转为返回的vo列表
     * @param receviedParam         参数对象
     * @param detailResult          明细结果
     * @return                      带分页参数的明细列表
     */
    private PageWrapper<LogDetailVO> convertResults(ReceviedParam receviedParam, PageWrapper<LogDetail> detailResult){
        PageWrapper<LogDetailVO> result = new PageWrapper<>();
        if (detailResult.getRecords() != null && detailResult.getSize() > 0) {
            LogDetailVO vo;
            List<LogDetailVO> resultRecord = new ArrayList<>();
            Member userVO;
            Set<Object> userIds = new HashSet<>();
            for (LogDetail detail : detailResult.getRecords()) {
                //设置读取状态
                if(detail.getReadUserId().contains(String.valueOf(receviedParam.getUserId()))) {
                    detail.setState(LogConstant.BOTONG_ONE_STR);
                } else {
                    detail.setState(LogConstant.BOTONG_ZERO_STR);
                }
                //放入发送人，已读，未读人员集合
                userIds.add(detail.getMemberId().toString());
                userIds.addAll(detail.getReadUserId());
                userIds.addAll(detail.getSendToUserId());
                userIds.addAll(detail.getUnreadUserId());
            }

            List listT = redisTemplate.opsForHash().multiGet(LogConstant.BOTONG_ORG_MEMBER, userIds);
            Member memberVO;
            List<Member> list = new ArrayList<>();
            if (listT != null && !listT.isEmpty()) {
                list = JSON.parseObject(listT.toString(), memberType);
            }

            Map<String, Member> map = new HashMap<>();
            for (Member objVO : list) {
                if (objVO == null) {
                    continue;
                }
                map.put(objVO.getId(), objVO);
            }
            List<Member> userList;
            for (LogDetail detail : detailResult.getRecords()) {
                vo = BeanUtils.map(detail, LogDetailVO.class);
                userList = new ArrayList<>();
                for (String userId : detail.getReadUserId()) {
                    userVO = map.get(userId);
                    if (userVO != null) {
                        userList.add(userVO);
                    }
                }
                vo.setUser(map.get(detail.getMemberId().toString()));
                vo.setReadUsers(userList);
                resultRecord.add(vo);
            }
            result.setCurrent(detailResult.getCurrent());
            result.setPages(detailResult.getPages());
            result.setSize(detailResult.getSize());
            result.setTotal(detailResult.getTotal());
            result.setRecords(resultRecord);
        }
        return result;
    }

}
