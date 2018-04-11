package com.yunjing.botong.log.controller;

import com.yunjing.botong.log.excel.BaseExModel;
import com.yunjing.botong.log.params.ReceviedParam;
import com.yunjing.botong.log.params.SearchParam;
import com.yunjing.botong.log.service.ILogSearchService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.utils.DateUtil;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Date;

/**
 * <p>
 * <p> 日志管理
 * </p>
 *
 * @author jingwj
 * @since 2018/3/28.
 */
@RestController
@RequestMapping("/web/log/manage")
@Slf4j
public class LogSearchController extends BaseController {

    @Autowired
    private ILogSearchService iLogSearchService;

    /**
     * 我收到的日志明细列表
     * @param searchParam     接收参数
     * @return
     */
    @PostMapping("/find-page")
    public ResponseEntityWrapper receviedPage(@RequestBody SearchParam searchParam) {
        return success(iLogSearchService.findPage(searchParam));
    }


    /**
     * 删除日志
     * @param searchParam    日志id
     * @return              成功与否
     */
    @PostMapping("/delete")
    public ResponseEntityWrapper delete(@RequestBody SearchParam searchParam) {
        return success(iLogSearchService.batchDelete(searchParam.getLogIds()));
    }

    /**
     * 导出日志数据
     * @param submitType
     * @param startDate
     * @param endDate
     * @param userIds
     * @param deptIds
     * @return
     * @throws Exception
     */
    @GetMapping("/export")
    public ResponseEntityWrapper export(HttpServletResponse response,
                         @RequestParam(value = "submitType", required = false) String submitType,
                         @RequestParam(value = "startDate", required = false) String startDate,
                         @RequestParam(value = "endDate", required = false) String endDate,
                         @RequestParam(value = "orgId") String orgId,
                         @RequestParam(value = "userIds", required = false) String[] userIds,
                         @RequestParam(value = "deptIds", required = false) String[] deptIds) throws Exception {

        boolean exportFlag = false;
        SearchParam searchParam = new SearchParam();
        searchParam.setSubmitType(Integer.valueOf(submitType));
        searchParam.setUserIds(userIds);
        searchParam.setDeptIds(deptIds);
        searchParam.setStartDate(startDate);
        searchParam.setOrgId(orgId);
        searchParam.setEndDate(endDate);
        BaseExModel excel = iLogSearchService.createLogExcel(searchParam);
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

    private ResponseEntityWrapper success(HttpServletResponse response, BaseExModel excel, boolean resultFlag) {
        try {
            OutputStream out = response.getOutputStream();
            Date time = new Date();
            excel.createWorkbook().write(out);
            Date time1 = new Date();;
            log.info("写日志数据到excel表的耗时："+ DateUtil.calculateIntervalSecond(time, time1));
            resultFlag = true;
        } catch (Exception e) {
            log.error("写日志数据到excel表出现异常",e);
        } finally {
            try {
                response.getOutputStream().flush();
                response.getOutputStream().close();

            } catch (Exception e2) {
                log.error("输出流关闭异常",e2);
            }
        }
        return null;
    }
}
