package com.yunjing.notice.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.notice.body.NoticeBody;
import com.yunjing.notice.body.NoticeDetailBody;
import com.yunjing.notice.body.NoticeDetailCBody;
import com.yunjing.notice.body.NoticePageBody;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 李双喜
 * @date 2018/3/21 17:40
 */
@RestController
@RequestMapping("/notice")
public class NoticeController extends BaseController {
    /**
     * 新增公告接口
     *
     * @param body
     * @return
     */
    @PostMapping("/insertNotice")
    public ResponseEntityWrapper insertNotice(@RequestBody NoticeBody body) throws BaseException {
        //noticeService.insertNotice(body);
        return success();
    }

    /**
     * 删除公告接口
     */
    @PostMapping("/deleteNotice")
    public ResponseEntityWrapper deleteNotice(@RequestParam String id) {

        return success();
    }

    @PostMapping("/selectNoticePage")
    public ResponseEntityWrapper selectNoticePage(@RequestParam Long userId,@RequestParam Integer pageNo,@RequestParam Integer pageSie) {
        Page<NoticePageBody> page = new Page<>(pageNo,pageSie);
        NoticePageBody noticePageBody = new NoticePageBody();
        noticePageBody.setAuthor("夏天");
        noticePageBody.setCover("http://image.baidu.com/search/detail?ct=503316480&z=0&ipn=false&word=%E7%BE%8E%E5%A5%B3%E5%9B%BE%E7%89%87&step_word=&hs=0&pn=0&spn=0&di=196450544840&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=2&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=-1&cs=1485834719%2C2425074308&os=180212775%2C1626379634&simid=4251467861%2C760086419&adpicid=0&lpn=0&ln=3948&fr=&fmq=1521623325758_R&fm=result&ic=0&s=undefined&se=&sme=&tab=0&width=&height=&face=undefined&ist=&jit=&cg=girl&bdtype=0&oriquery=&objurl=http%3A%2F%2Fpic.qiantucdn.com%2F58pic%2F22%2F06%2F55%2F57b2d98e109c6_1024.jpg&fromurl=ippr_z2C%24qAzdH3FAzdH3Fooo_z%26e3Bcbrtv_z%26e3Bv54AzdH3FprfvAzdH3Fddamccad_z%26e3Bip4s&gsm=0&rpstart=0&rpnum=0");
        noticePageBody.setCreateTime(1521623384L);
        noticePageBody.setId(1521256645L);
        noticePageBody.setWhetherDelete(0);
        noticePageBody.setTitle("你的益达，不，是你的益达");
        noticePageBody.setReadNumber(10);
        noticePageBody.setNotReadNumber(12);
        List<NoticePageBody> noticePageBodyList = new ArrayList<>();
        noticePageBodyList.add(noticePageBody);
        page.setRecords(noticePageBodyList);
        return success(page);
    }

    @PostMapping("/selectNoticeDetail")
    public ResponseEntityWrapper selectNoticeDetail(@RequestParam Long id) {
        NoticeDetailCBody noticeDetailBody = new NoticeDetailCBody();
        noticeDetailBody.setAuthor("网络技术");
        noticeDetailBody.setContent("新华社北京3月21日电根据《中共中央政治局关于加强和维护党中央集中统一领导的若干规定》，中央政治局同志每年向党中央和习近平总书记书面述职一次。这是党的十九大以来加强和维护党中央集中统一领导的重要制度安排。近期，中央政治局同志首次向党中央和习近平总书记书面述职。习近平认真审阅中央政治局同志的述职报告，并就中央政治局同志履行职责、做好工作、改进作风提出重要要求。习近平强调，十九届中央政治局受全党全国各族人民重托，担负着重大领导责任。每位中央政治局同志都必须不忘初心、牢记使命，胸怀大局、执政为民，勇于开拓、敢于担当，克己奉公、廉洁自律，发挥示范带头作用，以实际行动团结带领各级干部和广大人民群众，万众一心为实现“两个一百年”奋斗目标而努力奋斗。");
        noticeDetailBody.setCover("http://image.baidu.com/search/detail?ct=503316480&z=0&ipn=false&word=%E7%BE%8E%E5%A5%B3%E5%9B%BE%E7%89%87&step_word=&hs=0&pn=1&spn=0&di=159971652720&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=2&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=-1&cs=2238220304%2C765636600&os=3024519604%2C1637386069&simid=4052488503%2C585765833&adpicid=0&lpn=0&ln=3948&fr=&fmq=1521623325758_R&fm=result&ic=0&s=undefined&se=&sme=&tab=0&width=&height=&face=undefined&ist=&jit=&cg=girl&bdtype=0&oriquery=&objurl=http%3A%2F%2Fwww.zhlzw.com%2FUploadFiles%2FArticle_UploadFiles%2F201204%2F20120412123915795.jpg&fromurl=ippr_z2C%24qAzdH3FAzdH3Fooo_z%26e3Bziszo_z%26e3Bv54AzdH3Ff3AzdH3F5s44AzdH3F0088d0_z%26e3Bip4s&gsm=0&rpstart=0&rpnum=0");
        noticeDetailBody.setCreateTime(1521623384L);
        noticeDetailBody.setId(1521256645L);
        noticeDetailBody.setTitle("中共中央政治局关于加强和维护党中央集中统一领导的若干规定");
        noticeDetailBody.setPicture("http://image.baidu.com/search/detail?ct=503316480&z=0&ipn=false&word=%E7%BE%8E%E5%A5%B3%E5%9B%BE%E7%89%87&step_word=&hs=0&pn=1&spn=0&di=159971652720&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=2&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=-1&cs=2238220304%2C765636600&os=3024519604%2C1637386069&simid=4052488503%2C585765833&adpicid=0&lpn=0&ln=3948&fr=&fmq=1521623325758_R&fm=result&ic=0&s=undefined&se=&sme=&tab=0&width=&height=&face=undefined&ist=&jit=&cg=girl&bdtype=0&oriquery=&objurl=http%3A%2F%2Fwww.zhlzw.com%2FUploadFiles%2FArticle_UploadFiles%2F201204%2F20120412123915795.jpg&fromurl=ippr_z2C%24qAzdH3FAzdH3Fooo_z%26e3Bziszo_z%26e3Bv54AzdH3Ff3AzdH3F5s44AzdH3F0088d0_z%26e3Bip4s&gsm=0&rpstart=0&rpnum=0");
        noticeDetailBody.setPictureName("16546461263.jpg");
        return success(noticeDetailBody);
    }
}
