package com.yunjing.approval.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.yunjing.approval.excel.BaseExModel;
import com.yunjing.approval.service.IApprovalService;
import com.yunjing.approval.service.IExportLogService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * @author liuxiaopeng
 * @date 2018/01/15
 */
@RestController
@RequestMapping("/approval/data")
public class ExportController extends BaseController {

    @Autowired
    private IExportLogService exportLogService;

    @Autowired
    private IApprovalService approvalService;
    /**
     * 获取导出记录
     *
     * @param page 分页对象
     * @param oid  企业主键
     * @return
     */
    @PostMapping("/log")
    public ResponseEntityWrapper getExportLog(@ModelAttribute(value = "page") Page page, @RequestParam("oid") Long oid) {

        return success(exportLogService.findExportLogPage(page, oid));
    }

    /**
     * 审批数据导出
     *
     * @param oid             企业主键
     * @param uid             用户主键
     * @param mid             模型主键, 审批类型, 可空(全部)
     * @param state           审批状态  0:审批中 1:审批完成 2:已撤回, 可空(全部)
     * @param title           审批标题
     * @param createTimeStart 发起时间_开始
     * @param createTimeEnd   发起时间_结束
     * @param finishTimeStart 完成时间_开始
     * @param finishTimeEnd   完成时间_结束
     * @return
     * @throws Exception
     */
    @GetMapping("/export")
    public ResponseEntityWrapper exportData(@RequestParam("oid") Long oid, @RequestParam("uid") Long uid, Long mid, Integer state, String title, String createTimeStart,
                             String createTimeEnd, String finishTimeStart, String finishTimeEnd, HttpServletResponse response) throws Exception {
        boolean exportFlag = false;
        BaseExModel excel = approvalService.createApprovalExcel(oid,uid, mid, state, title, createTimeStart, createTimeEnd, finishTimeStart, finishTimeEnd);
        String fileName =  excel.getFileName();
        //设置响应类型，告知浏览器输出的是图片
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes("UTF-8"), "ISO8859-1"));
//        设置响应头信息，告诉浏览器不要缓存此内容
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        //设置HttpOnly属性,防止Xss攻击
        response.setHeader("Set-Cookie", "name=value; HttpOnly");
        //生成图片并通过response输出
        response.setDateHeader("Expire", 0);
        return success(excel,exportFlag,response);
    }

    private ResponseEntityWrapper success(BaseExModel excel, boolean resultFlag,HttpServletResponse response) throws Exception {
        try {
            OutputStream out = response.getOutputStream();
            excel.createWorkbook().write(out);
            resultFlag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                response.getOutputStream().flush();
                response.getOutputStream().close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return success(resultFlag);
        }
    }
}
