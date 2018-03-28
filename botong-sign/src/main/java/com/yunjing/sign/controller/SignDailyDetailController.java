package com.yunjing.sign.controller;

import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.validate.BeanFieldValidator;
import com.yunjing.mommon.wrapper.PageWrapper;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.sign.beans.param.UserAndDeptParam;
import com.yunjing.sign.beans.vo.UserMonthListVO;
import com.yunjing.sign.dao.mapper.SignDetailMapper;
import com.yunjing.sign.excel.BaseExModel;
import com.yunjing.sign.service.ISignDetailDailyService;
import com.yunjing.sign.service.ISignDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

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


    /**
     * 签到统计
     * @param userAndDeptParam
     * @return
     */
    @PostMapping("/statistics")
    public ResponseEntityWrapper statistics(@RequestBody UserAndDeptParam userAndDeptParam){
        // 基础校验
        BeanFieldValidator.getInstance().validate(userAndDeptParam);
        PageWrapper<UserMonthListVO> page = iSignDetailDailyService.staticsMonthInfo(userAndDeptParam);
        return success(page);
    }

    /**
     * 导出签到数据
     *
     * @param response
     * @param userIds
     * @param deptIds
     * @param signDate
     * @return
     * @throws Exception
     */
    @GetMapping("/export")
    public ResponseEntityWrapper export(HttpServletResponse response, String userIds, String deptIds, String signDate) throws Exception {
        UserAndDeptParam userAndDeptParam = new UserAndDeptParam();
        userAndDeptParam.setDeptIds(deptIds);
        userAndDeptParam.setUserIds(userIds);
        userAndDeptParam.setSignDate(signDate);
        boolean exportFlag = false;
        BaseExModel excel = iSignDetailDailyService.createTempExcel(userAndDeptParam);
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

    private ResponseEntityWrapper success(HttpServletResponse response, BaseExModel excel, boolean resultFlag) throws Exception {
        OutputStream out = response.getOutputStream();
        try {
            excel.createWorkbook().write(out);
            resultFlag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.flush();
                out.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return success(resultFlag);
        }
    }

}
