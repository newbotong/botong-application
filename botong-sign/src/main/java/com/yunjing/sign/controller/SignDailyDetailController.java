package com.yunjing.sign.controller;

import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.validate.BeanFieldValidator;
import com.yunjing.mommon.wrapper.PageWrapper;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.sign.beans.model.SignDetailDaily;
import com.yunjing.sign.beans.param.SignConfigParam;
import com.yunjing.sign.beans.param.SignDetailParam;
import com.yunjing.sign.beans.param.UserAndDeptParam;
import com.yunjing.sign.beans.vo.SignConfigVO;
import com.yunjing.sign.beans.vo.UserMonthListVO;
import com.yunjing.sign.dao.mapper.SignDetailMapper;
import com.yunjing.sign.excel.BaseExModel;
import com.yunjing.sign.service.ISignConfigDailyService;
import com.yunjing.sign.service.ISignDetailDailyService;
import com.yunjing.sign.service.ISignDetailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * <p>
 * 签到明细 前端控制器
 * </p>
 *
 * @author jingwj
 * @since 2018-03-26
 */
@RestController
@RequestMapping("/web/sign/daily/")
public class SignDailyDetailController extends BaseController {

    @Autowired
    private ISignDetailDailyService iSignDetailDailyService;

    @Autowired
    private ISignConfigDailyService iSignConfigDailyService;

    @Value("${botong.log.appId}")
    private String appId;


    /**
     * 签到统计
     * @param userAndDeptParam      多部门id和多memberid
     * @return                      按月返回所有member的签到明细
     */
    @PostMapping("/statistics")
    public ResponseEntityWrapper statistics(@RequestBody UserAndDeptParam userAndDeptParam){
        // 基础校验
        BeanFieldValidator.getInstance().validate(userAndDeptParam);
        userAndDeptParam.setAppId(appId);
        PageWrapper<UserMonthListVO> page = iSignDetailDailyService.staticsMonthInfo(userAndDeptParam);
        return success(page);
    }

    /**
     * 签到设置
     * @param signConfigParam       签到设置参数
     * @return                      是否设置成功
     */
    @PostMapping("/setting")
    public ResponseEntityWrapper setting(@RequestBody SignConfigParam signConfigParam){
        // 基础校验
        BeanFieldValidator.getInstance().validate(signConfigParam);
        boolean isAdd = iSignConfigDailyService.setSignConfig(signConfigParam);
        return success(isAdd);
    }


    /**
     * 查看签到设置
     * @param orgId             组织Id
     * @return                  签到设置对象
     */
    @GetMapping("/get-setting")
    public ResponseEntityWrapper getSetting(@RequestParam String orgId){
        // 基础校验
        BeanFieldValidator.getInstance().validate(orgId);
        SignConfigVO vo = iSignConfigDailyService.getSignConfig(orgId);
        return success(vo);
    }



    /**
     * 导出签到数据
     *
     * @param response          reponse
     * @param userIds           多个memberids
     * @param deptIds           多个部门ids
     * @param signDate          统计时间
     * @return                  输出文件流
     * @throws Exception        异常
     */
    @GetMapping("/export")
    public ResponseEntityWrapper export(HttpServletResponse response, String userIds, String deptIds, String signDate, String memberId) throws Exception {
        UserAndDeptParam userAndDeptParamT = new UserAndDeptParam();
        userAndDeptParamT.setDeptIds(deptIds);
        userAndDeptParamT.setUserIds(userIds);
        userAndDeptParamT.setSignDate(signDate);
        userAndDeptParamT.setMemberId(memberId);
        userAndDeptParamT.setAppId(appId);
        boolean exportFlag = false;
        BaseExModel excel = iSignDetailDailyService.createTempExcel(userAndDeptParamT);
        String fileName = excel.getFileName();
        //设置响应类型，告知浏览器输出的是图片
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-disposition",
                "attachment; filename=" + new String(fileName.getBytes("UTF-8"), "ISO8859-1"));
//        设置响应头信息，告诉浏览器不要缓存此内容
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        //设置HttpOnly属性,防止Xss攻击
        response.setHeader("Set-Cookie", "name=value; HttpOnly");
        //生成图片并通过response输出
        response.setDateHeader("Expire", 0);
        return success(response, excel, exportFlag);
    }

    /**
     * 返回到输出流，
     * @param response      输出response
     * @param excel         excel表格对象
     * @param resultFlag    成功与否
     * @return              输出流，null
     * @throws Exception    异常
     */
    private ResponseEntityWrapper success(HttpServletResponse response, BaseExModel excel, boolean resultFlag) throws Exception {
        OutputStream out = response.getOutputStream();
        Workbook workbook = null;
        try {
            workbook = excel.createWorkbook();
            workbook.write(out);
            resultFlag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
                out.flush();
                out.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }

        }
        return null;
    }


    /**
     * 签到明细
     * @param signDetailParam       签到明细对象
     * @return                      返回明细列表
     */
    @PostMapping("/list")
    public ResponseEntityWrapper statistics(@RequestBody SignDetailParam signDetailParam){
        // 基础校验
        BeanFieldValidator.getInstance().validate(signDetailParam);
        List<SignDetailDaily> list = iSignDetailDailyService.queryDetailList(signDetailParam);
        return success(list);
    }

}
