package com.yunjing.approval.web;

import com.baomidou.mybatisplus.plugins.Page;
import com.yunjing.approval.excel.BaseExModel;
import com.yunjing.approval.model.vo.ApprovalVO;
import com.yunjing.approval.param.DataParam;
import com.yunjing.approval.service.IApprovalService;
import com.yunjing.approval.service.IExportLogService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.wrapper.PageWrapper;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * @author 刘小鹏
 * @date 2018/04/03
 */
@RestController
@RequestMapping("/web/approval/data")
public class ExportController extends BaseController {

    @Autowired
    private IExportLogService exportLogService;

    @Autowired
    private IApprovalService approvalService;

    /**
     * 获取审批数据列表
     *
     * @param dataParam 设置查询参数
     * @return 分页列表
     */
    @PostMapping("/list")
    public ResponseEntityWrapper page(@RequestBody DataParam dataParam) throws Exception {
        PageWrapper<ApprovalVO> page = approvalService.page(dataParam);
        return success(page);
    }

    /**
     * 删除审批数据
     *
     * @param approvalId 审批主键
     * @return ResponseEntityWrapper
     */
    @PostMapping("/delete")
    public ResponseEntityWrapper deleteApproval(@RequestParam("approvalId") String approvalId) throws Exception {

        return success(approvalService.delete(approvalId));
    }

    /**
     * 获取导出记录
     *
     * @param page      分页对象
     * @param companyId 公司id
     * @return ResponseEntityWrapper
     */
    @PostMapping("/log")
    public ResponseEntityWrapper getExportLog(@ModelAttribute(value = "page") Page page, @RequestParam("companyId") String companyId) {

        return success(exportLogService.findExportLogPage(page, companyId));
    }

    /**
     * 审批数据导出
     *
     * @param dataParam 导出参数
     * @return ResponseEntityWrapper
     * @throws Exception 抛异常
     */
    @GetMapping("/export")
    public ResponseEntityWrapper exportData(@RequestParam("companyId") String companyId, @RequestParam("memberId")String memberId, DataParam dataParam, HttpServletResponse response) throws Exception {
        dataParam.setCompanyId(companyId);
        dataParam.setMemberId(memberId);
        BaseExModel excel = approvalService.createApprovalExcel(dataParam);
        String fileName = excel.getFileName();
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
        return success(excel, response);
    }

    private ResponseEntityWrapper success(BaseExModel excel, HttpServletResponse response) throws Exception {
        try {
            OutputStream out = response.getOutputStream();
            excel.createWorkbook().write(out);
            return success();
        } catch (Exception e) {
            e.printStackTrace();
            return error();
        } finally {
            try {
                response.getOutputStream().flush();
                response.getOutputStream().close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
}
