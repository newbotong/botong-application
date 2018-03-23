package com.yunjing.notice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.notice.body.NoticeBody;
import com.yunjing.notice.body.NoticeDetailBody;
import com.yunjing.notice.body.NoticePageBody;
import com.yunjing.notice.body.UserInfoBody;
import com.yunjing.notice.common.NoticeConstant;
import com.yunjing.notice.entity.NoticeEntity;
import com.yunjing.notice.entity.NoticeUserEntity;
import com.yunjing.notice.entity.UserInfoEntity;
import com.yunjing.notice.mapper.NoticeMapper;
import com.yunjing.notice.mapper.UserInfoMapper;
import com.yunjing.notice.service.NoticeService;
import com.yunjing.notice.service.NoticeUserService;
import com.yunjing.notice.service.UserInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserInfoMapper userInfoMapper;

    /**
     * 公告mapper
     */
    @Autowired
    private NoticeUserService noticeUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertNotice(NoticeBody noticeBody) throws BaseException {
        if (null == noticeBody.getIssueUserId()) {
            throw new BaseException("发布人的用户id不能为空");
        }
        //查看该用户是否有发公告权限(等应用生成后提供)
//        ResponseEntityWrapper responseEntityWrapper = authorityFeign.authority("appId",noticeBody.getIssueUserId());
        //判断返回的结果是否为管理员，如果是管理员方可进入下一步


        int i = 15;
        int j = 200;
        if (StringUtils.isEmpty(noticeBody.getTitle())) {
            throw new BaseException("标题不能为空");
        } else if (noticeBody.getTitle().length() <= i) {
            throw new BaseException("标题长度不得大于15个字");
        }
        if (StringUtils.isEmpty(noticeBody.getContent())) {
            throw new BaseException("内容不能为空");
        } else if (noticeBody.getContent().length() <= j) {
            throw new BaseException("内容长度不得超过200个字");
        }
        if (StringUtils.isEmpty(noticeBody.getUserInfo())) {
            throw new BaseException("成员不能空");
        }
        if (StringUtils.isAnyBlank(noticeBody.getImg(),noticeBody.getName())){
            throw new BaseException("发布人参数不能为空");
        }
        UserInfoEntity userInfoEntity = new UserInfoEntity();
        userInfoEntity.setId(noticeBody.getIssueUserId());
        userInfoEntity.setImg(noticeBody.getImg());
        userInfoEntity.setName(noticeBody.getName());
        userInfoEntity.setLogicDelete(NoticeConstant.LOGIC_DELETE_NOMAL);
        userInfoEntity.insert();
        List<UserInfoBody> userInfoBodyList = JSONObject.parseObject(noticeBody.getUserInfo(), List.class);
        List<Long> stringList = new ArrayList<>();
        if (userInfoBodyList.size() > 0) {
            //去重
            List<UserInfoBody> userInfoBodies = new ArrayList<>(new TreeSet<UserInfoBody>(userInfoBodyList));
            //不存在的用户id
            List<UserInfoBody> notListUserId = new LinkedList<>();
            //存在的用户id
            List<UserInfoBody> listUserId = new LinkedList<>();
            List<String> listAllUserId =  userInfoMapper.selectUserIds();
            for (UserInfoBody userInfoBody : userInfoBodies){
                stringList.add(userInfoBody.getId());
                if (listAllUserId.size()>0){
                    if (listAllUserId.contains(userInfoBody.getId())){
                        listUserId.add(userInfoBody);
                    }else{
                        notListUserId.add(userInfoBody);
                    }
                }else{
                    notListUserId.add(userInfoBody);
                }
            }
            //存在做批量更新
            insertOrUpdate(listUserId);
            //不存在做批量新增
            insertOrUpdate(notListUserId);
        }
        //保密后不能发Dang
        if (null != noticeBody.getSecrecyState() && noticeBody.getSecrecyState() == 0) {
            if (null != noticeBody.getDangState() && noticeBody.getDangState() == 0) {
                throw new BaseException("保密公告不能发DANG");
            }
        }
        NoticeEntity notice = new NoticeEntity();
        BeanUtils.copyProperties(noticeBody, notice);
        notice.setReadNum(0);
        notice.setNotReadNum(stringList.size());
        List<NoticeUserEntity> longList = new LinkedList<>();
        if (stringList.size()>0) {
            for (Long userId : stringList) {
                NoticeUserEntity noticeUserEntity = new NoticeUserEntity();
                noticeUserEntity.setNoticeId(notice.getId());
                noticeUserEntity.setUserId(userId);
                noticeUserEntity.setState(NoticeConstant.NOTICE_NOT_READ);
                longList.add(noticeUserEntity);
            }
            noticeUserService.insertBatch(longList);
        }
        boolean result = notice.insert();
        if (!result) {
            throw new BaseException("发布失败");
        }
        //调用张潇的工作通知RPC


        if (notice.getDangState() == 0) {
            //调用刘舒洁的RPC发送Dang
        }
    }

    private void insertOrUpdate(List<UserInfoBody> notListUserId) {
        if (notListUserId.size()>0){
            List<UserInfoEntity> infoEntityList = new LinkedList<>();
            for (UserInfoBody userInfoBody : notListUserId){
                UserInfoEntity userInfoEntity = new UserInfoEntity();
                BeanUtils.copyProperties(userInfoBody,userInfoEntity);
                userInfoEntity.setLogicDelete(0);
                infoEntityList.add(userInfoEntity);
            }
            userInfoService.updateBatchById(infoEntityList);
        }
    }

    /**
     * 更新已读和未读状态
     *
     * @param userId 用户id
     * @param id     公告id
     * @param state  是否阅读 0为已读 1为未读
     * @throws BaseException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNoticeState(Long userId, Long id, Integer state) throws BaseException {
        if (null == userId && null == id && null == state) {
            throw new BaseException("参数不能为空");
        }
        NoticeUserEntity noticeUserEntity = new NoticeUserEntity().selectOne(new EntityWrapper<NoticeUserEntity>()
                .eq("logic_delete", NoticeConstant.LOGIC_DELETE_NOMAL)
                .eq("user_id", userId).eq("notice_id", id));
        if (null == noticeUserEntity) {
            throw new BaseException("未查询到相关记录");
        }
        if (state != 0) {
            throw new BaseException("只能更新为已读");
        }
        noticeUserEntity.setState(state);
        boolean result = noticeUserEntity.updateById();
        NoticeEntity noticeEntity = new NoticeEntity().selectOne(new EntityWrapper<NoticeEntity>()
                .eq("logic_delete", NoticeConstant.LOGIC_DELETE_NOMAL).eq("id", id));
        if (null == noticeEntity) {
            throw new BaseException("未查询到相关记录");
        }
        noticeEntity.setNotReadNum(noticeEntity.getNotReadNum() > 0 ? noticeEntity.getNotReadNum() - 1 : 0);
        noticeEntity.setReadNum(noticeEntity.getReadNum() + 1);
        boolean b = noticeEntity.updateById();
        if (!result && !b) {
            throw new BaseException("更新失败");
        }
    }

    /**
     * 逻辑删除公告
     *
     * @param id 公告id
     * @throws BaseException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotice(Long id) throws BaseException {
        if (null == id) {
            throw new BaseException("公告id不能为空");
        }
        NoticeEntity noticeEntity = new NoticeEntity().selectOne(new EntityWrapper<NoticeEntity>()
                .eq("logic_delete", NoticeConstant.LOGIC_DELETE_NOMAL).eq("id", id));
        if (null == noticeEntity) {
            throw new BaseException("公告已经被删除");
        }
        noticeEntity.setLogicDelete(1);
        boolean a = noticeEntity.updateById();
        List<NoticeUserEntity> noticeUserEntityList = new NoticeUserEntity().selectList(new EntityWrapper<NoticeUserEntity>()
                .eq("logic_delete", NoticeConstant.LOGIC_DELETE_NOMAL).eq("notice_id", id));
        boolean b = false;
        if (null != noticeUserEntityList && noticeUserEntityList.size() > 0) {
            for (NoticeUserEntity noticeUserEntity : noticeUserEntityList) {
                noticeUserEntity.setLogicDelete(1);
            }
            b = noticeUserService.updateBatchById(noticeUserEntityList);
        }
        if (!a && !b) {
            throw new BaseException("删除失败");
        }
    }

    /**
     * 分页查询公告
     *
     * @param userId   用户id
     * @param state    是否阅读 0为已读 1为未读
     * @param pageNo   当前页码
     * @param pageSize 每页显示条数
     * @return
     * @throws BaseException
     */
    @Override
    public Page<NoticePageBody> selectNoticePage(Long userId, Integer state, Integer pageNo, Integer pageSize) throws BaseException {

        //调杨超的RPC查看该用户是否有权限删除

        Page<NoticePageBody> page = new Page<>(pageNo, pageSize);
        Map<String, Object> map = new HashMap<>(4);
        map.put("userId", userId);
        map.put("state", state);
        List<NoticePageBody> noticePageBodyList = noticeMapper.selectNoticePage(map, page);
        if (noticePageBodyList.size() > 0) {
            noticePageBodyList.forEach(noticePageBody -> {
                // noticePageBody.setWhetherDelete(); 权限的值
            });
        }
        page.setRecords(noticePageBodyList);
        return page;
    }

    /**
     * 根据公告id查询已读未读用户接口
     * @param id     公告id
     * @param state  是否阅读 0为已读 1为未读
     * @return
     * @throws BaseException
     */
    @Override
    public List<UserInfoBody> selectNoticeUser(Long id, Integer state) throws BaseException {
        if (null == id && null == state){
            throw new BaseException("参数错误");
        }
        List<NoticeUserEntity> list = new NoticeUserEntity().selectList(new EntityWrapper<NoticeUserEntity>()
                .eq("logic_delete",NoticeConstant.LOGIC_DELETE_NOMAL).eq("notice_id",id).eq("state",state));
        List<UserInfoBody> userInfoBodyList = new ArrayList<>();
        if (list.size()>0){
            List<Long> userIds = new ArrayList<>();
            for (NoticeUserEntity noticeUserEntity : list){
                if (null != noticeUserEntity.getUserId()) {
                    userIds.add(noticeUserEntity.getUserId());
                }
            }
            if (userIds.size()>0){
                Map<String,Object> map = new HashMap<>(1);
                map.put("userIds",userIds);
                userInfoBodyList = userInfoMapper.selectUser(map);
            }
        }
        return userInfoBodyList;
    }

    /**
     * 根据公告id查询公告详情接口
     * @param id             公告id
     * @return
     * @throws BaseException
     */
    @Override
    public NoticeDetailBody selectNoticeDetail(Long id) throws BaseException {
        if (null == id){
            throw new BaseException("参数不能为空");
        }
        NoticeEntity noticeEntity = new NoticeEntity().selectOne(new EntityWrapper<NoticeEntity>().eq("id",id).eq("logic_delete",NoticeConstant.LOGIC_DELETE_NOMAL));
        if (null == noticeEntity){
            throw new BaseException("该公告已被删除");
        }
        NoticeDetailBody noticeDetailBody = new NoticeDetailBody();
        BeanUtils.copyProperties(noticeEntity,noticeDetailBody);
        noticeDetailBody.setReadNumber(noticeEntity.getReadNum());
        noticeDetailBody.setNotReadNumber(noticeEntity.getNotReadNum());
        if (null != noticeEntity.getIssueUserId()){
            UserInfoEntity userInfoEntity = new UserInfoEntity().selectOne(new EntityWrapper<UserInfoEntity>()
                    .eq("id",noticeEntity.getIssueUserId()).eq("logic_delete",NoticeConstant.LOGIC_DELETE_NOMAL));
            if (null != userInfoEntity && null != userInfoEntity.getName()){
                noticeDetailBody.setIssueUserName(userInfoEntity.getName());
            }
        }
        //H5分享地址(?)
        noticeDetailBody.setNoticeH5Address("?");
        return noticeDetailBody;
    }
}
