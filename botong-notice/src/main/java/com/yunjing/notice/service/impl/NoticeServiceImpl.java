package com.yunjing.notice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.mommon.base.PushParam;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.utils.IDUtils;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.notice.body.*;
import com.yunjing.notice.common.NoticeConstant;
import com.yunjing.notice.config.RedisReadonly;
import com.yunjing.notice.entity.NoticeEntity;
import com.yunjing.notice.entity.NoticeUserEntity;
import com.yunjing.notice.mapper.NoticeMapper;
import com.yunjing.notice.processor.feign.param.DangParam;
import com.yunjing.notice.processor.okhttp.AuthorityService;
import com.yunjing.notice.processor.okhttp.DangService;
import com.yunjing.notice.processor.okhttp.InformService;
import com.yunjing.notice.processor.okhttp.OrgStructureService;
import com.yunjing.notice.service.NoticeService;
import com.yunjing.notice.service.NoticeUserService;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
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
    private RedisReadonly redisReadonly;
    /**
     * 绑定的公告appId
     */
    @Value("${notice.appId}")
    private String appId;

    @Value("${H5Address}")
    private String H5Address;

    @Autowired
    private OrgStructureService orgStructureService;

    /**
     * 公告mapper
     */
    @Autowired
    private NoticeUserService noticeUserService;

    /**
     * 新增公告
     *
     * @param noticeBody 新增入参
     * @throws BaseException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertNotice(NoticeBody noticeBody) throws BaseException {
        NoticeEntity noticeEntity = new NoticeEntity();
        noticeEntity.setId(IDUtils.uuid());
        BeanUtils.copyProperties(noticeBody, noticeEntity);
        noticeEntity.setLogicDelete(NoticeConstant.LOGIC_DELETE_NORMAL);
        if (StringUtils.isEmpty(noticeBody.getMemberIds()) && StringUtils.isEmpty(noticeBody.getDepartmentIds())){
            throw new BaseException("成员不能为空");
        }
        //okhttp
        Call<ResponseEntityWrapper> call = orgStructureService.findSubLists(noticeBody.getDepartmentIds(),noticeBody.getMemberIds(),0);
        ResponseEntityWrapper<List<Member>> body = null;
        try {
            Response<ResponseEntityWrapper> execute = call.execute();
            body = execute.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (null == body){
           throw new BaseException("调用失败");
        }
        List<Member> memberList = body.getData();
        String[] passportIds = memberList.stream().map(Member::getPassportId).toArray(String[]::new);
        String[] memberIds = memberList.stream().map(Member::getId).toArray(String[]::new);
        List<String> userInfoBodies = Arrays.asList(memberIds);
        if (CollectionUtils.isNotEmpty(userInfoBodies)) {
            noticeEntity.setNotReadNum(userInfoBodies.size());
            noticeEntity.setReadNum(0);
        } else {
            throw new BaseException("选择用户不能为空");
        }
        List<NoticeUserEntity> userInfoBodyList = new ArrayList<>();

        for (String userId : userInfoBodies) {
            NoticeUserEntity noticeUserEntity = new NoticeUserEntity();
            noticeUserEntity.setLogicDelete(NoticeConstant.LOGIC_DELETE_NORMAL);
            noticeUserEntity.setNoticeId(noticeEntity.getId());
            noticeUserEntity.setState(NoticeConstant.NOTICE_NOT_READ);
            noticeUserEntity.setUserId(userId);
            noticeUserEntity.setId(IDUtils.uuid());
            userInfoBodyList.add(noticeUserEntity);
        }
        noticeEntity.insert();
        noticeUserService.insertBatch(userInfoBodyList);
        //推送
        Map<String, String> map = new HashMap<>(32);
        if (StringUtils.isNotEmpty(noticeEntity.getCover())) {
            map.put("cover", noticeEntity.getCover());
        } else {
            map.put("cover", null);
        }
        map.put("title", noticeEntity.getTitle());
        map.put("id", noticeEntity.getId());
        map.put("content", noticeEntity.getContent());
        map.put("createTime", noticeEntity.getCreateTime().toString());
        map.put("author", noticeEntity.getAuthor());
        if (StringUtils.isNotEmpty(noticeEntity.getPicture())) {
            String[] pictureArrays = noticeEntity.getPicture().split(",");
            map.put("accessory", pictureArrays.length + "");
        } else {
            map.put("accessory", null);
        }
        PushParam pushParam = new PushParam();
        pushParam.setTitle("公告");
        pushParam.setNotificationTitle(noticeEntity.getTitle());
        pushParam.setAlias(passportIds);
        pushParam.setMap(map);
        pushParam.setMsg("");
        // okhttp调用工作通知
        informService.pushAllTargetByUser(pushParam);
        //Dang
        if (noticeEntity.getDangState() == 0) {
            //批量查询用户信息
            List<ReceiveBody> receiveBodyList = new ArrayList<>();
            for (Member object : memberList) {
                ReceiveBody receiveBody = new ReceiveBody();
                receiveBody.setUserId(object.getPassportId());
                if (null != object.getMobile()) {
                    receiveBody.setUserTelephone(Long.parseLong(object.getMobile()));
                }
                receiveBodyList.add(receiveBody);
            }
            DangParam dangParam = new DangParam();
            dangParam.setUserId(noticeEntity.getIssueUserId());
            dangParam.setBizId(noticeEntity.getId());
            dangParam.setBizType(1);
            dangParam.setReceiveBody(JSONObject.toJSONString(receiveBodyList));
            dangParam.setDangType(1);
            dangParam.setRemindType(1);
            dangParam.setSendType(1);
            dangParam.setSendTime(System.currentTimeMillis());
            dangParam.setSendContent(noticeEntity.getTitle());
            dangParam.setVoiceTimeLength(0);
            Call<ResponseEntityWrapper> call1 = orgStructureService.findSubLists("",noticeEntity.getIssueUserId(),0);
            ResponseEntityWrapper<List<Member>> b = null;
            try {
                Response<ResponseEntityWrapper> execute = call1.execute();
                b = execute.body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<Member> members = b.getData();
            Member member = members.get(0);
            if (null != member.getMobile()) {
                dangParam.setSendTelephone(Long.parseLong(member.getMobile()));
            }
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
    public void updateNoticeState(String userId, String id, Integer state) throws BaseException {
        if (null == userId && null == id && null == state) {
            throw new BaseException("参数不能为空");
        }
        NoticeUserEntity noticeUserEntity = new NoticeUserEntity().selectOne(new EntityWrapper<NoticeUserEntity>()
                .eq("logic_delete", NoticeConstant.LOGIC_DELETE_NORMAL)
                .eq("user_id", userId).eq("notice_id", id));
        if (null == noticeUserEntity) {
            throw new BaseException("未查询到相关记录");
        }
        noticeUserEntity.setState(0);
        boolean result = noticeUserEntity.updateById();
        NoticeEntity noticeEntity = new NoticeEntity().selectOne(new EntityWrapper<NoticeEntity>()
                .eq("logic_delete", NoticeConstant.LOGIC_DELETE_NORMAL).eq("id", id));
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
        //查询公告是否存在
        List<NoticeEntity> noticeEntityList = new NoticeEntity().selectList(new EntityWrapper<NoticeEntity>()
                .eq("logic_delete", NoticeConstant.LOGIC_DELETE_NORMAL).in("id", Arrays.asList(idArrays)));
        if (CollectionUtils.isEmpty(noticeEntityList)) {
            throw new BaseException("公告已经被删除");
        }
        for (NoticeEntity noticeEntity : noticeEntityList) {
            noticeEntity.setLogicDelete(NoticeConstant.LOGIC_DELETE_DELETE);
        }
        boolean a = this.updateBatchById(noticeEntityList);
        List<NoticeUserEntity> noticeUserEntityList = new NoticeUserEntity().selectList(new EntityWrapper<NoticeUserEntity>()
                .eq("logic_delete", NoticeConstant.LOGIC_DELETE_NORMAL).in("notice_id", Arrays.asList(idArrays)));
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
     * @param state    是否阅读 0为已读 1为未读
     * @param pageNo   当前页码
     * @param pageSize 每页显示条数
     * @param orgId    企业id
     * @return
     * @throws BaseException
     */
    @Override
    public Map<String, Object> selectNoticePage(String userId, Integer state, String orgId, Integer pageNo, Integer pageSize) throws BaseException {
        Page<NoticePageBody> page = new Page<>(pageNo, pageSize);
        Map<String, Object> map = new HashMap<>(4);
        Map<String, Object> maps = new HashMap<String, Object>(3);
        if (StringUtils.isEmpty(orgId)) {
            throw new BaseException("企业id不能为空");
        }
        map.put("orgId", orgId);
        if (null == state) {
            List<NoticePageBody> noticePageBodyList = noticeMapper.selectWebNoticePage(map, page);
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
            //判断是否为管理员
            boolean results = (boolean) body.getData();
            maps.put("admin", results);
            map.put("userId", userId);
            List<NoticePageBody> noticePageBodyList = new ArrayList<>();
            if (state == 0 || state == 1) {
                map.put("state", state);
                noticePageBodyList = noticeMapper.selectNoticePage(map, page);
            }
            if (results) {
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
    public Page<UserInfoBody> selectNoticeUser(String id, Integer state, Integer pageNo, Integer pageSize) throws BaseException {
        if (null == id && null == state) {
            throw new BaseException("参数错误");
        }
        Page<UserInfoBody> page = new Page<>(pageNo, pageSize);
        List<NoticeUserEntity> list = new NoticeUserEntity().selectList(new EntityWrapper<NoticeUserEntity>()
                .eq("logic_delete", NoticeConstant.LOGIC_DELETE_NORMAL).eq("notice_id", id).eq("state", state));
        if (CollectionUtils.isEmpty(list)) {
            return page;
        }
        List<String> objectList = new ArrayList<>();
        for (NoticeUserEntity noticeUserEntity : list) {
            objectList.add(noticeUserEntity.getUserId());
        }
        Call<ResponseEntityWrapper> call = orgStructureService.findSubLists("",objectList.toArray().toString(),0);
        ResponseEntityWrapper<List<Member>> body = null;
        try {
            Response<ResponseEntityWrapper> execute = call.execute();
            body = execute.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Member> memberList = body.getData();
        List<UserInfoBody> userInfoBodyList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(memberList)) {
            for (Member object : memberList) {
                UserInfoBody userInfoBody = new UserInfoBody();
                userInfoBody.setId(object.getId());
                if (StringUtils.isNotEmpty(object.getColor())) {
                    userInfoBody.setColor(object.getColor());
                }
                if (StringUtils.isNotEmpty(object.getProfile())){
                    userInfoBody.setImg(object.getProfile());
                }
                if (StringUtils.isNotEmpty(object.getMemberName())) {
                    userInfoBody.setName(object.getMemberName());
                }
                if (null != object.getMobile()) {
                    userInfoBody.setPhone(Long.parseLong(object.getMobile()));
                }
                userInfoBodyList.add(userInfoBody);
            }
        }
        page.setTotal(userInfoBodyList.size());
        page.setRecords(userInfoBodyList);
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
    public NoticeDetailBody selectNoticeDetail(String id, String userId) throws BaseException {
        if (null == id) {
            throw new BaseException("参数不能为空");
        }
        NoticeEntity noticeEntity = new NoticeEntity().selectOne(new EntityWrapper<NoticeEntity>().eq("id", id).eq("logic_delete", NoticeConstant.LOGIC_DELETE_NORMAL));
        if (null == noticeEntity) {
            throw new BaseException("该公告已被删除");
        }
        NoticeDetailBody noticeDetailBody = new NoticeDetailBody();
        BeanUtils.copyProperties(noticeEntity, noticeDetailBody);
        noticeDetailBody.setReadNumber(noticeEntity.getReadNum());
        noticeDetailBody.setNotReadNumber(noticeEntity.getNotReadNum());
        if (null != noticeEntity.getIssueUserId()) {
            Object object = redisReadonly.getTemple().opsForHash().get(NoticeConstant.USER_INFO_REDIS, noticeEntity.getIssueUserId());
            if (null != object) {
                UserInfoRedis userInfoRedis = JSONObject.parseObject(object.toString(), UserInfoRedis.class);
                if (StringUtils.isNotEmpty(userInfoRedis.getNick())) {
                    noticeDetailBody.setIssueUserName(userInfoRedis.getNick());
                }
            }
        }
        //H5分享地址(?)
        if (noticeEntity.getSecrecyState() == 1) {
            noticeDetailBody.setNoticeH5Address(H5Address + "?" + "id=" + id);
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
    public NoticeDetailsBody selectCNoticeDetail(String id) throws BaseException {
        NoticeEntity noticeEntity = new NoticeEntity().selectOne(new EntityWrapper<NoticeEntity>().eq("id", id).eq("logic_delete", NoticeConstant.LOGIC_DELETE_NORMAL));
        NoticeDetailsBody noticeDetailsBody = new NoticeDetailsBody();
        BeanUtils.copyProperties(noticeEntity, noticeDetailsBody);
        return noticeDetailsBody;
    }
}
