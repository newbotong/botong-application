package com.yunjing.notice.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.esotericsoftware.minlog.Log;
import com.yunjing.mommon.constant.StatusCode;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.global.exception.BaseRuntimeException;
import com.yunjing.mommon.utils.DateUtil;
import com.yunjing.mommon.utils.IDUtils;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.notice.body.*;
import com.yunjing.notice.common.AppPushParam;
import com.yunjing.notice.common.NoticeConstant;
import com.yunjing.notice.entity.NoticeEntity;
import com.yunjing.notice.entity.NoticeUserEntity;
import com.yunjing.notice.mapper.NoticeMapper;
import com.yunjing.notice.processor.feign.param.DangParam;
import com.yunjing.notice.processor.feign.param.UserInfoModel;
import com.yunjing.notice.processor.okhttp.AuthorityService;
import com.yunjing.notice.processor.okhttp.DangService;
import com.yunjing.notice.processor.okhttp.InformService;
import com.yunjing.notice.processor.okhttp.OrgStructureService;
import com.yunjing.notice.service.NoticeService;
import com.yunjing.notice.service.NoticeUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@Slf4j
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, NoticeEntity> implements NoticeService {

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private DangService dangService;

    @Autowired
    private InformService informService;

    @Autowired
    private NoticeMapper noticeMapper;

    /**
     * 绑定的公告appId
     */
    @Value("${notice.appId}")
    private String appId;

    @Value("${h5Address}")
    private String h5Address;

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
     * @throws IOException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertNotice(NoticeBody noticeBody) throws BaseException, IOException {
        NoticeEntity noticeEntity = new NoticeEntity();
        noticeEntity.setId(IDUtils.uuid());
        BeanUtils.copyProperties(noticeBody, noticeEntity);
        noticeEntity.setLogicDelete(NoticeConstant.LOGIC_DELETE_NORMAL);
        if (StringUtils.isEmpty(noticeBody.getMemberIds()) && StringUtils.isEmpty(noticeBody.getDepartmentIds())) {
            throw new BaseException("成员不能为空");
        }
        //okhttp

        Call<ResponseEntityWrapper<List<Member>>> call = orgStructureService.findSubLists(noticeBody.getDepartmentIds(), noticeBody.getMemberIds(), 0);
        Log.info(call.toString());
        Response<ResponseEntityWrapper<List<Member>>> execute = call.execute();
        ResponseEntityWrapper<List<Member>> body = execute.body();
        List<Member> memberList;
        if (null != body.getData()) {
            memberList = body.getData();
        } else {
            throw new BaseException("该用户查询不到");
        }
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

        //推送请求URL
        String url = h5Address + "?" + "id=" + noticeEntity.getId();

        //添加公告模块子标题
        Map<String, String> map = new HashMap<>(32);
        map.put("subModuleName", "公告");
        map.put("url", url);

        //构建推送列表内容体
        JSONArray array = new JSONArray();
        JSONObject json;

        //添加公告标题
        json = new JSONObject();
        json.put("subTitle", noticeEntity.getTitle());
        json.put("type", "5");
        array.add(json);

        //判断是否存在图片
        if (StringUtils.isNotEmpty(noticeEntity.getCover())) {
            json = new JSONObject();
            json.put("imgPath", noticeEntity.getCover());
            json.put("type", "1");
            array.add(json);
        }

        //添加公告内容
        json = new JSONObject();
        json.put("description", noticeEntity.getContent());
        json.put("type", "2");
        array.add(json);

        //添加公告发送人
        json = new JSONObject();
        json.put("bottom", noticeEntity.getAuthor() + "  " + DateUtil.convert(System.currentTimeMillis()));
        json.put("type", "4");
        array.add(json);

        //保存公告内容体
        map.put("content", array.toJSONString());

        //构建发送公告参数
        AppPushParam pushParam = new AppPushParam();
        pushParam.setNotificationTitle("公告");
        pushParam.setAlias(passportIds);
        pushParam.setTitle(noticeEntity.getTitle());
        pushParam.setMap(map);
        pushParam.setMsg("您有一条新公告，请注意查收！");
        pushParam.setCompanyId(noticeBody.getOrgId());
        pushParam.setAppId(appId);
        // okhttp调用工作通知

        Call<ResponseEntityWrapper> push = informService.pushAllTargetByUser(pushParam);
        Response<ResponseEntityWrapper> ex = push.execute();
        ResponseEntityWrapper response = ex.body();
        if (null != response) {
            if (response.getStatusCode() != StatusCode.SUCCESS.getStatusCode()) {
                throw new BaseRuntimeException(response.getStatusCode(), response.getStatusMessage());
            }
        }
        //Dang
        if (noticeBody.getDangState() == 0) {
            //批量查询用户信息
            DangParam dangParam = new DangParam();
            Call<ResponseEntityWrapper<List<Member>>> ca = orgStructureService.findSubLists("", noticeBody.getIssueUserId(), 0);
            Response<ResponseEntityWrapper<List<Member>>> re = ca.execute();
            ResponseEntityWrapper<List<Member>> result = re.body();
            //查询发布人的账户id
            List<UserInfoModel> userInfoModelList = new ArrayList<>();
            for (Member member : memberList) {
                UserInfoModel userInfoModel = new UserInfoModel();
                if (StringUtils.isNotEmpty(member.getPassportId())) {
                    userInfoModel.setUserId(member.getPassportId());
                }
                if (StringUtils.isNotEmpty(member.getMobile())) {
                    userInfoModel.setUserTelephone(Long.parseLong(member.getMobile()));
                }
                userInfoModelList.add(userInfoModel);
            }
            if (null != result) {
                if (null != result.getData()) {
                    List<Member> listMember = result.getData();
                    String[] passportId = listMember.stream().map(Member::getPassportId).toArray(String[]::new);
                    dangParam.setUserId(passportId[0]);
                }
            }
            dangParam.setBizId(noticeEntity.getId());
            dangParam.setSendTelephone(noticeBody.getPhone());
            dangParam.setBizType(1);
            dangParam.setReceiveBody(userInfoModelList);
            dangParam.setDangType(1);
            dangParam.setRemindType(1);
            dangParam.setSendType(1);
            dangParam.setSendTime(System.currentTimeMillis());
            dangParam.setSendContent(noticeEntity.getTitle());
            dangParam.setVoiceTimeLength(0);
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
            Response<ResponseEntityWrapper> e = push.execute();
            ResponseEntityWrapper r = e.body();
            if (null != r) {
                if (r.getStatusCode() != StatusCode.SUCCESS.getStatusCode()) {
                    throw new BaseRuntimeException(r.getStatusCode(), r.getStatusMessage());
                }
            }
        }
    }

    /**
     * 更新已读和未读状态
     *
     * @param userId 用户id
     * @param id     公告id
     * @throws BaseException
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer updateNoticeState(String userId, String id) {
        //查询该用户下公告详情的未阅读状态
        NoticeUserEntity noticeUserEntity = new NoticeUserEntity().selectOne(new EntityWrapper<NoticeUserEntity>()
                .eq("logic_delete", NoticeConstant.LOGIC_DELETE_NORMAL)
                .eq("user_id", userId).eq("notice_id", id).eq("state", NoticeConstant.NOTICE_NOT_READ));
        if (null != noticeUserEntity) {
            //更新已读状态
            noticeUserEntity.setState(NoticeConstant.NOTICE_READ);
            noticeUserEntity.updateById();
            NoticeEntity noticeEntity = new NoticeEntity().selectOne(new EntityWrapper<NoticeEntity>()
                    .eq("logic_delete", NoticeConstant.LOGIC_DELETE_NORMAL).eq("id", id));
            //更新已读和未读数量
            noticeEntity.setNotReadNum(noticeEntity.getNotReadNum() > 0 ? noticeEntity.getNotReadNum() - 1 : 0);
            noticeEntity.setReadNum(noticeEntity.getReadNum() + 1);
            noticeEntity.updateById();
            return 1;
        }
        return 0;
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
     * @param orgId    企业id
     * @param pageNo   当前页码
     * @param pageSize 每页显示条数
     * @return
     * @throws BaseException
     * @throws IOException
     */
    @Override
    public Map<String, Object> selectNoticePage(String userId, Integer state, String orgId, Integer pageNo, Integer pageSize) throws BaseException, IOException {
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
            Response<ResponseEntityWrapper> execute = call.execute();
            ResponseEntityWrapper body = execute.body();
            //判断是否为管理员
            boolean results = false;
            if (null != body.getData()) {
                results = (boolean) body.getData();
            }
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
    public Page<UserInfoBody> selectNoticeUser(String id, Integer state, Integer pageNo, Integer pageSize) throws BaseException, IOException {
        Page<UserInfoBody> page = new Page<>(pageNo, pageSize);
        List<NoticeUserEntity> list = new NoticeUserEntity().selectList(new EntityWrapper<NoticeUserEntity>()
                .eq("logic_delete", NoticeConstant.LOGIC_DELETE_NORMAL).eq("notice_id", id).eq("state", state));
        if (CollectionUtils.isEmpty(list)) {
            return page;
        }
        String[] memberIds = list.stream().map(NoticeUserEntity::getUserId).toArray(String[]::new);
        Call<ResponseEntityWrapper<List<Member>>> call = orgStructureService.findSubLists("", StringUtils.join(memberIds, ","), 0);
        Response<ResponseEntityWrapper<List<Member>>> execute = call.execute();
        ResponseEntityWrapper<List<Member>> body = execute.body();
        List<UserInfoBody> userInfoBodyList = new ArrayList<>();
        List<Member> memberList;
        if (null != body.getData()) {
            memberList = body.getData();
            if (CollectionUtils.isNotEmpty(memberList)) {
                for (Member object : memberList) {
                    UserInfoBody userInfoBody = new UserInfoBody();
                    userInfoBody.setId(object.getId());
                    if (StringUtils.isNotEmpty(object.getColor())) {
                        userInfoBody.setColor(object.getColor());
                    }
                    if (StringUtils.isNotEmpty(object.getProfile())) {
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
        }
        page.setTotal(userInfoBodyList.size());
        page.setRecords(userInfoBodyList);
        return page;
    }

    /**
     * 根据公告id查询公告详情接口
     *
     * @param id     公告id
     * @param userId 用户id
     * @return
     * @throws BaseException
     */
    @Override
    public NoticeDetailBody selectNoticeDetail(String id, String userId) throws BaseException, IOException {
        NoticeEntity noticeEntity = new NoticeEntity().selectOne(new EntityWrapper<NoticeEntity>().eq("id", id).eq("logic_delete", NoticeConstant.LOGIC_DELETE_NORMAL));
        if (null == noticeEntity) {
            throw new BaseException("该公告已被删除");
        }
        Integer i = this.updateNoticeState(userId, id);
        NoticeDetailBody noticeDetailBody = new NoticeDetailBody();
        BeanUtils.copyProperties(noticeEntity, noticeDetailBody);
        noticeDetailBody.setReadNumber(i == 0 ? noticeEntity.getReadNum() : noticeEntity.getReadNum() + 1);
        noticeDetailBody.setNotReadNumber(i == 1 ? noticeEntity.getNotReadNum() - 1 : noticeEntity.getNotReadNum());
        if (null != noticeEntity.getIssueUserId()) {
            Call<ResponseEntityWrapper<List<Member>>> call = orgStructureService.findSubLists("", noticeEntity.getIssueUserId(), 0);
            Response<ResponseEntityWrapper<List<Member>>> execute = call.execute();
            ResponseEntityWrapper<List<Member>> body = execute.body();
            List<Member> memberList;
            if (null != body.getData()) {
                memberList = body.getData();
                if (CollectionUtils.isNotEmpty(memberList)) {
                    Member member = memberList.get(0);
                    if (null != member && StringUtils.isNotEmpty(member.getMemberName())) {
                        noticeDetailBody.setIssueUserName(member.getMemberName());
                    }
                }
            }
        }
        //H5分享地址(?)
        if (noticeEntity.getSecrecyState() == 1) {
            noticeDetailBody.setNoticeH5Address(h5Address + "?" + "id=" + id + "&" + userId);
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

    /**
     * 查询用户权限
     *
     * @param userId 成员id
     * @return
     * @throws BaseException
     * @throws IOException
     */
    @Override
    public Boolean selectAuthority(String userId) throws BaseException, IOException {
        Call<ResponseEntityWrapper> call = authorityService.authority(appId, userId);
        Response<ResponseEntityWrapper> execute = call.execute();
        ResponseEntityWrapper body = execute.body();
        //判断是否为管理员
        boolean results = false;
        if (null != body.getData()) {
            results = (boolean) body.getData();
        }
        return results;
    }
}
