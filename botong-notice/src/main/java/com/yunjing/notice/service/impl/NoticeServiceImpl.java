package com.yunjing.notice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.mommon.base.PushParam;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.notice.body.*;
import com.yunjing.notice.common.NoticeConstant;
import com.yunjing.notice.entity.NoticeEntity;
import com.yunjing.notice.entity.NoticeUserEntity;
import com.yunjing.notice.entity.UserInfoEntity;
import com.yunjing.notice.mapper.NoticeMapper;
import com.yunjing.notice.mapper.UserInfoMapper;
import com.yunjing.notice.processor.feign.AuthorityFeign;
import com.yunjing.notice.processor.feign.DangFeign;
import com.yunjing.notice.processor.feign.InformFeign;
import com.yunjing.notice.processor.feign.param.DangParam;
import com.yunjing.notice.processor.okhttp.AuthorityService;
import com.yunjing.notice.processor.okhttp.DangService;
import com.yunjing.notice.processor.okhttp.InformService;
import com.yunjing.notice.service.NoticeService;
import com.yunjing.notice.service.NoticeUserService;
import com.yunjing.notice.service.UserInfoService;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * 公告Service实现类
 *
 * @author 李双喜
 * @since 2018/03/20/.
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, NoticeEntity> implements NoticeService {

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private DangService dangService;

    @Autowired
    private InformService informService;

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserInfoMapper userInfoMapper;
    /**
     * 绑定的公告appId
     */
    @Value("${notice.appId}")
    private String appId;

    /**
     * 公告mapper
     */
    @Autowired
    private NoticeUserService noticeUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertNotice(NoticeBody noticeBody) throws BaseException {
        UserInfoEntity userInfoEntity = new UserInfoEntity().selectOne(new EntityWrapper<UserInfoEntity>().eq("id", noticeBody.getIssueUserId()).eq("logic_delete", NoticeConstant.LOGIC_DELETE_NOMAL));
        if (null == userInfoEntity) {
            UserInfoEntity userInfoEntity1 = new UserInfoEntity();
            userInfoEntity1.setId(noticeBody.getIssueUserId());
            userInfoEntity1.setPhone(noticeBody.getPhone());
            userInfoEntity1.setImg(noticeBody.getImg());
            userInfoEntity1.setName(noticeBody.getName());
            userInfoEntity1.setLogicDelete(NoticeConstant.LOGIC_DELETE_NOMAL);
            userInfoEntity1.insert();
        } else {
            userInfoEntity.setPhone(noticeBody.getPhone());
            userInfoEntity.setImg(noticeBody.getImg());
            userInfoEntity.setName(noticeBody.getName());
            userInfoEntity.setLogicDelete(NoticeConstant.LOGIC_DELETE_NOMAL);
            userInfoEntity.updateById();
        }
        Type type = new TypeReference<List<UserInfoBody>>() {
        }.getType();
        List<UserInfoBody> userInfoBodyList = JSONObject.parseObject(noticeBody.getUserInfo(), type);
        List<Long> stringList = new ArrayList<>();
        List<ReceiveBody> receiveBodyList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(userInfoBodyList)) {
            //去重
            Set<UserInfoBody> userInfoBodySet = new HashSet<>(userInfoBodyList);
            List<UserInfoBody> userInfoBodies = new ArrayList<>(userInfoBodySet);
            //不存在的用户id
            List<UserInfoBody> notListUserId = new ArrayList<>();
            //存在的用户id
            List<UserInfoBody> listUserId = new ArrayList<>();
            List<Long> listAllUserId = userInfoMapper.selectUserIds();
            for (UserInfoBody userInfoBody : userInfoBodies) {
                ReceiveBody receiveBody = new ReceiveBody();
                receiveBody.setUserId(userInfoBody.getId());
                if (null != userInfoBody.getPhone()) {
                    receiveBody.setUserTelephone(userInfoBody.getPhone());
                }
                receiveBodyList.add(receiveBody);
                stringList.add(userInfoBody.getId());
                if (!CollectionUtils.isEmpty(listAllUserId)) {
                    if (listAllUserId.contains(userInfoBody.getId())) {
                        listUserId.add(userInfoBody);
                    } else {
                        notListUserId.add(userInfoBody);
                    }
                } else {
                    notListUserId.add(userInfoBody);
                }
            }
            //存在做批量更新
            if (!CollectionUtils.isEmpty(listUserId)) {
                List<UserInfoEntity> infoEntityList = new ArrayList<>();
                insertOrUpdate(listUserId, infoEntityList);
                userInfoService.updateBatchById(infoEntityList);
            }
            //不存在做批量新增
            if (!CollectionUtils.isEmpty(notListUserId)) {
                List<UserInfoEntity> infoEntityList = new ArrayList<>();
                insertOrUpdate(notListUserId, infoEntityList);
                userInfoService.insertBatch(infoEntityList);
            }
        } else {
            throw new BaseException("未选择成员");
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
        boolean result = notice.insert();
        List<NoticeUserEntity> longList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(stringList)) {
            for (Long userId : stringList) {
                NoticeUserEntity noticeUserEntity = new NoticeUserEntity();
                noticeUserEntity.setNoticeId(notice.getId());
                noticeUserEntity.setUserId(userId);
                noticeUserEntity.setState(NoticeConstant.NOTICE_NOT_READ);
                longList.add(noticeUserEntity);
            }
            noticeUserService.insertBatch(longList);
        }
        if (!result) {
            throw new BaseException("发布失败");
        }
        Map<String, String> map = new HashMap<>(32);
        if (StringUtils.isNotEmpty(notice.getCover())) {
            map.put("cover", notice.getCover());
        } else {
            map.put("cover", null);
        }
        map.put("title", notice.getTitle());
        map.put("id", notice.getId().toString());
        map.put("content", notice.getContent());
        map.put("createTime", notice.getCreateTime().toString());
        map.put("author", notice.getAuthor());
        if (StringUtils.isNotEmpty(notice.getPicture())) {
            String[] pictureArrays = notice.getPicture().split(",");
            map.put("accessory", pictureArrays.length + "");
        } else {
            map.put("accessory", null);
        }
        PushParam pushParam = new PushParam();
        pushParam.setTitle("公告");
        pushParam.setNotificationTitle(notice.getTitle());
        List<String> listUserId = new ArrayList<>();
        for (Long id : stringList) {
            listUserId.add(id + "");
        }
        pushParam.setAlias(listUserId.toArray(new String[0]));
        pushParam.setMap(map);
        pushParam.setMsg("");
        // okhttp调用工作通知
        informService.pushAllTargetByUser(pushParam);
        if (notice.getDangState() == 0) {
            DangParam dangParam = new DangParam();
            dangParam.setUserId(notice.getIssueUserId());
            dangParam.setBizId(notice.getId());
            dangParam.setBizType(1);
            dangParam.setReceiveBody(JSONObject.toJSONString(receiveBodyList));
            dangParam.setDangType(1);
            dangParam.setRemindType(1);
            dangParam.setSendType(1);
            dangParam.setSendTime(System.currentTimeMillis());
            dangParam.setSendContent(notice.getTitle());
            dangParam.setVoiceTimeLength(0);
            dangParam.setSendTelephone(noticeBody.getPhone());
            if (!StringUtils.isAnyBlank(noticeBody.getPicture(), noticeBody.getPictureName(), noticeBody.getSize())) {
                dangParam.setIsAccessory(1);
                dangParam.setAccessoryType(1);
                dangParam.setAccessoryName(noticeBody.getPictureName());
                dangParam.setAccessoryUrl(noticeBody.getPicture());
                dangParam.setAccessorySize(noticeBody.getSize());
            } else {
                dangParam.setIsAccessory(0);
                dangParam.setAccessoryType(0);
                dangParam.setAccessoryName("");
                dangParam.setAccessoryUrl("");
                dangParam.setAccessorySize("");
            }
            // okhttp调用发送dang消息
            dangService.sendDang(dangParam);
        }
    }

    private void insertOrUpdate(List<UserInfoBody> listUserId, List<UserInfoEntity> infoEntityList) {
        for (UserInfoBody userInfoBody : listUserId) {
            UserInfoEntity userInfoEntity1 = new UserInfoEntity();
            BeanUtils.copyProperties(userInfoBody, userInfoEntity1);
            userInfoEntity1.setLogicDelete(0);
            infoEntityList.add(userInfoEntity1);
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
     * @param ids 公告id,多个id逗号隔开
     * @throws BaseException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotice(String ids) throws BaseException {
        if (StringUtils.isEmpty(ids)) {
            throw new BaseException("公告id不能为空");
        }
        String[] idArrays = ids.split(",");
        //String数组转Long数组
        Long[] strArrNum = (Long[]) ConvertUtils.convert(idArrays, Long.class);
        //查询公告是否存在
        List<NoticeEntity> noticeEntityList = new NoticeEntity().selectList(new EntityWrapper<NoticeEntity>()
                .eq("logic_delete", NoticeConstant.LOGIC_DELETE_NOMAL).in("id", Arrays.asList(strArrNum)));
        if (CollectionUtils.isEmpty(noticeEntityList)) {
            throw new BaseException("公告已经被删除");
        }
        for (NoticeEntity noticeEntity : noticeEntityList) {
            noticeEntity.setLogicDelete(NoticeConstant.LOGIC_DELETE_DELETE);
        }
        boolean a = this.updateBatchById(noticeEntityList);
        List<NoticeUserEntity> noticeUserEntityList = new NoticeUserEntity().selectList(new EntityWrapper<NoticeUserEntity>()
                .eq("logic_delete", NoticeConstant.LOGIC_DELETE_NOMAL).in("notice_id", Arrays.asList(strArrNum)));
        boolean b = false;
        if (!CollectionUtils.isEmpty(noticeUserEntityList)) {
            for (NoticeUserEntity noticeUserEntity : noticeUserEntityList) {
                noticeUserEntity.setLogicDelete(NoticeConstant.LOGIC_DELETE_DELETE);
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
     * @param state    是否阅读 0为已读 1为未读 2为管理员
     * @param pageNo   当前页码
     * @param pageSize 每页显示条数
     * @return
     * @throws BaseException
     */
    @Override
    public Map<String, Object> selectNoticePage(Long userId, Integer state, Integer pageNo, Integer pageSize) throws BaseException {
        Page<NoticePageBody> page = new Page<>(pageNo, pageSize);
        Map<String, Object> map = new HashMap<>(4);
        Map<String, Object> maps = new HashMap<String, Object>(3);
        if (null == state) {
            List<NoticePageBody> noticePageBodyList = noticeMapper.selectNoticePage(map, page);
            page.setRecords(noticePageBodyList);
            maps.put("page", page);
            return maps;
        } else {
            //okhttp
            Call<ResponseEntityWrapper> call = authorityService.authority(appId, userId);
            ResponseEntityWrapper body = null;
            try {
                Response<ResponseEntityWrapper> execute = call.execute();
                body = execute.body();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //rpc调用【弃用】
//            ResponseEntityWrapper responseEntityWrapper = authorityFeign.authority(appId, userId);
//            Boolean results = JSONObject.parseObject(responseEntityWrapper.getData().toString(), Boolean.class);

            //判断是否为管理员
            boolean results = (boolean) body.getData();
            maps.put("admin", results);
            map.put("userId", userId);
            List<NoticePageBody> noticePageBodyList = new ArrayList<>();
            if (state == 0 || state == 1) {
                map.put("state", state);
                noticePageBodyList = noticeMapper.selectNoticePage(map, page);
            }
            if (results == true) {
                int i = 2;
                if (state == i) {
                    noticePageBodyList = noticeMapper.selectMangerNoticePage(map, page);
                }
            }
            page.setRecords(noticePageBodyList);
            maps.put("page", page);
            return maps;
        }
    }

    /**
     * 根据公告id查询已读未读用户接口
     *
     * @param id    公告id
     * @param state 是否阅读 0为已读 1为未读
     * @return
     * @throws BaseException
     */
    @Override
    public Page<UserInfoBody> selectNoticeUser(Long id, Integer state, Integer pageNo, Integer pageSize) throws BaseException {
        if (null == id && null == state) {
            throw new BaseException("参数错误");
        }
        Page<UserInfoBody> page = new Page(pageNo, pageSize);
        List<NoticeUserEntity> list = new NoticeUserEntity().selectList(new EntityWrapper<NoticeUserEntity>()
                .eq("logic_delete", NoticeConstant.LOGIC_DELETE_NOMAL).eq("notice_id", id).eq("state", state));
        List<UserInfoBody> userInfoBodyList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            List<Long> userIds = new ArrayList<>();
            for (NoticeUserEntity noticeUserEntity : list) {
                if (null != noticeUserEntity.getUserId()) {
                    userIds.add(noticeUserEntity.getUserId());
                }
            }
            if (!CollectionUtils.isEmpty(userIds)) {
                Map<String, Object> map = new HashMap<>(1);
                map.put("userIds", userIds);
                userInfoBodyList = userInfoMapper.selectUser(map, page);
            }
            page.setRecords(userInfoBodyList);
        }
        return page;
    }

    /**
     * 根据公告id查询公告详情接口
     *
     * @param id 公告id
     * @return
     * @throws BaseException
     */
    @Override
    public NoticeDetailBody selectNoticeDetail(Long id, Long userId) throws BaseException {
        if (null == id) {
            throw new BaseException("参数不能为空");
        }
        NoticeEntity noticeEntity = new NoticeEntity().selectOne(new EntityWrapper<NoticeEntity>().eq("id", id).eq("logic_delete", NoticeConstant.LOGIC_DELETE_NOMAL));
        if (null == noticeEntity) {
            throw new BaseException("该公告已被删除");
        }
        NoticeDetailBody noticeDetailBody = new NoticeDetailBody();
        BeanUtils.copyProperties(noticeEntity, noticeDetailBody);
        noticeDetailBody.setReadNumber(noticeEntity.getReadNum());
        noticeDetailBody.setNotReadNumber(noticeEntity.getNotReadNum());
        if (null != noticeEntity.getIssueUserId()) {
            UserInfoEntity userInfoEntity = new UserInfoEntity().selectOne(new EntityWrapper<UserInfoEntity>()
                    .eq("id", noticeEntity.getIssueUserId()).eq("logic_delete", NoticeConstant.LOGIC_DELETE_NOMAL));
            if (null != userInfoEntity && null != userInfoEntity.getName()) {
                noticeDetailBody.setIssueUserName(userInfoEntity.getName());
            }
        }
        //H5分享地址(?)
        if (noticeEntity.getSecrecyState() == 1) {
            noticeDetailBody.setNoticeH5Address("http://www.baidu.com" + "sort=13" + "&" + "id=" + id + "&" + "userId=" + userId);
        } else {
            noticeDetailBody.setNoticeH5Address(null);
        }
        return noticeDetailBody;
    }

    /**
     * web端公告id查询公告详情接口
     *
     * @param id 公告id
     */
    @Override
    public NoticeDetailsBody selectCNoticeDetail(Long id) throws BaseException {
        NoticeEntity noticeEntity = new NoticeEntity().selectOne(new EntityWrapper<NoticeEntity>().eq("id", id).eq("logic_delete", NoticeConstant.LOGIC_DELETE_NOMAL));
        NoticeDetailsBody noticeDetailsBody = new NoticeDetailsBody();
        BeanUtils.copyProperties(noticeEntity, noticeDetailsBody);
        return noticeDetailsBody;
    }
}
