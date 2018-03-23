package com.yunjing.notice.api;

import com.baomidou.mybatisplus.plugins.Page;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.notice.body.*;
import com.yunjing.notice.service.NoticeService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * <p> 公告api接口
 * </p>
 *
 * @author 李双喜
 * @since 2018/3/20.
 */
@RestController
@RequestMapping("/api/notice")
public class NoticeApi extends BaseController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 新增公告接口
     *
     * @param body 公告实体类
     * @throws BaseException
     */
    @PostMapping("/insertNotice")
    public ResponseEntityWrapper insertNotice(@RequestBody NoticeBody body) throws BaseException {
        //noticeService.insertNotice(body);
        return success();
    }

    /**
     * 更新已读和未读状态
     * @param userId   用户id
     * @param id       公告id
     * @param state    是否阅读 0为已读 1为未读
     * @throws BaseException
     */
    @PostMapping("/updateNoticeState")
    public ResponseEntityWrapper updateNoticeState(@RequestParam Long userId,@RequestParam Long id,@RequestParam Integer state) throws BaseException {
        //noticeService.updateNoticeState(userId,id,state);
        return success();
    }
    /**
     * 逻辑删除公告
     * @param id             公告id
     * @throws BaseException
     */
    @PostMapping("/deleteNotice")
    public ResponseEntityWrapper deleteNotice(@RequestParam Long id) throws BaseException{
        //noticeService.deleteNotice(id);
        return success();
    }
    /**
     *
     * 查询公告列表接口
     *
     */
    @PostMapping("/selectNoticePage")
    public ResponseEntityWrapper selectNoticePage(@RequestParam Long userId,@RequestParam Integer state,@RequestParam Integer pageNo,@RequestParam Integer pageSize) throws BaseException{
        Page<NoticePageBody> page1 = new Page<>(pageNo,pageSize);
        NoticePageBody noticePageBody = new NoticePageBody();
        noticePageBody.setAuthor("夏天");
        noticePageBody.setCover("http://image.baidu.com/search/detail?ct=503316480&z=0&ipn=false&word=%E7%BE%8E%E5%A5%B3%E5%9B%BE%E7%89%87&step_word=&hs=0&pn=0&spn=0&di=196450544840&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=2&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=-1&cs=1485834719%2C2425074308&os=180212775%2C1626379634&simid=4251467861%2C760086419&adpicid=0&lpn=0&ln=3948&fr=&fmq=1521623325758_R&fm=result&ic=0&s=undefined&se=&sme=&tab=0&width=&height=&face=undefined&ist=&jit=&cg=girl&bdtype=0&oriquery=&objurl=http%3A%2F%2Fpic.qiantucdn.com%2F58pic%2F22%2F06%2F55%2F57b2d98e109c6_1024.jpg&fromurl=ippr_z2C%24qAzdH3FAzdH3Fooo_z%26e3Bcbrtv_z%26e3Bv54AzdH3FprfvAzdH3Fddamccad_z%26e3Bip4s&gsm=0&rpstart=0&rpnum=0");
        noticePageBody.setCreateTime(1521623384L);
        noticePageBody.setId(1521256645L);
        noticePageBody.setWhetherDelete(0);
        noticePageBody.setTitle("你的益达，不，是你的益达");
        List<NoticePageBody> noticePageBodyList = new ArrayList<>();
        noticePageBodyList.add(noticePageBody);
        page1.setRecords(noticePageBodyList);
        //Page<NoticePageBody> page = noticeService.selectNoticePage(userId,state,pageNo,pageSize);
        return success(page1);
    }

    /**
     * 根据公告id查询公告详情接口
     * @param id             公告id
     * @return
     * @throws BaseException
     */
    @PostMapping("/selectNoticeDetail")
    public ResponseEntityWrapper selectNoticeDetail(@RequestParam Long id) throws BaseException {
        NoticeDetailBody noticeDetailBody = new NoticeDetailBody();
        noticeDetailBody.setAuthor("网络技术");
        noticeDetailBody.setContent("新华社北京3月21日电根据《中共中央政治局关于加强和维护党中央集中统一领导的若干规定》，中央政治局同志每年向党中央和习近平总书记书面述职一次。这是党的十九大以来加强和维护党中央集中统一领导的重要制度安排。近期，中央政治局同志首次向党中央和习近平总书记书面述职。习近平认真审阅中央政治局同志的述职报告，并就中央政治局同志履行职责、做好工作、改进作风提出重要要求。习近平强调，十九届中央政治局受全党全国各族人民重托，担负着重大领导责任。每位中央政治局同志都必须不忘初心、牢记使命，胸怀大局、执政为民，勇于开拓、敢于担当，克己奉公、廉洁自律，发挥示范带头作用，以实际行动团结带领各级干部和广大人民群众，万众一心为实现“两个一百年”奋斗目标而努力奋斗。");
        noticeDetailBody.setCover("http://image.baidu.com/search/detail?ct=503316480&z=0&ipn=false&word=%E7%BE%8E%E5%A5%B3%E5%9B%BE%E7%89%87&step_word=&hs=0&pn=1&spn=0&di=159971652720&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=2&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=-1&cs=2238220304%2C765636600&os=3024519604%2C1637386069&simid=4052488503%2C585765833&adpicid=0&lpn=0&ln=3948&fr=&fmq=1521623325758_R&fm=result&ic=0&s=undefined&se=&sme=&tab=0&width=&height=&face=undefined&ist=&jit=&cg=girl&bdtype=0&oriquery=&objurl=http%3A%2F%2Fwww.zhlzw.com%2FUploadFiles%2FArticle_UploadFiles%2F201204%2F20120412123915795.jpg&fromurl=ippr_z2C%24qAzdH3FAzdH3Fooo_z%26e3Bziszo_z%26e3Bv54AzdH3Ff3AzdH3F5s44AzdH3F0088d0_z%26e3Bip4s&gsm=0&rpstart=0&rpnum=0");
        noticeDetailBody.setCreateTime(1521623384L);
        noticeDetailBody.setId(1521256645L);
        noticeDetailBody.setNoticeH5Address("http://tool.chinaz.com/Tools/unixtime.aspx");
        noticeDetailBody.setIssueUserName("李");
        noticeDetailBody.setNotReadNumber(12);
        noticeDetailBody.setReadNumber(35);
        noticeDetailBody.setTitle("中共中央政治局关于加强和维护党中央集中统一领导的若干规定");
        noticeDetailBody.setSecrecyState(0);
        //NoticeDetailBody noticeDetailBody = noticeService.selectNoticeDetail(id);
        return success(noticeDetailBody);
    }

    /**
     * 根据公告id查询已读未读用户接口
     * @param id     公告id
     * @param state  是否阅读 0为已读 1为未读
     * @return
     * @throws BaseException
     */
    @PostMapping("/selectNoticeUser")
    public ResponseEntityWrapper selectNoticeUser(@RequestParam Long id,@RequestParam Integer state) throws BaseException {
        NoticeUserBody noticeUserBody = new NoticeUserBody();
        noticeUserBody.setImg("http://image.baidu.com/search/detail?ct=503316480&z=0&ipn=false&word=%E7%BE%8E%E5%A5%B3%E5%9B%BE%E7%89%87&step_word=&hs=0&pn=3&spn=0&di=132752591070&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=2&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=-1&cs=989341999%2C236255160&os=2976076010%2C1629577786&simid=4242652037%2C972070137&adpicid=0&lpn=0&ln=3948&fr=&fmq=1521623325758_R&fm=result&ic=0&s=undefined&se=&sme=&tab=0&width=&height=&face=undefined&ist=&jit=&cg=girl&bdtype=0&oriquery=&objurl=http%3A%2F%2Fwww.zhlzw.com%2FUploadFiles%2FArticle_UploadFiles%2F201204%2F20120412123912727.jpg&fromurl=ippr_z2C%24qAzdH3FAzdH3Fooo_z%26e3Bziszo_z%26e3Bv54AzdH3Ff3AzdH3F5s44AzdH3F0088d0_z%26e3Bip4s&gsm=0&rpstart=0&rpnum=0");
        noticeUserBody.setUserName("春天");
        List<NoticeUserBody> list1 = new ArrayList<>();
        list1.add(noticeUserBody);
        //List<UserInfoBody> list = noticeService.selectNoticeUser(id,state);
        return success(list1);
    }
}
