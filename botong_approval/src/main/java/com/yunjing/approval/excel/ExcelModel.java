package com.yunjing.approval.excel;

import com.yunjing.approval.model.vo.ApprovalExcelVO;
import com.yunjing.approval.model.vo.ApprovalTemplVO;
import com.yunjing.approval.model.vo.AttrValueVO;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * @author liuxiaopeng
 * @date 2018/01/23
 */
@Data
public class ExcelModel {

    /**
     * 审批模板列表
     */
    private List<ApprovalTemplVO> tempList;

    /**
     * 审批信息列表
     */
    private List<ApprovalExcelVO> approvalList;


    /**
     * 工作表名称
     */
    private String sheetName;

    /**
     * Excel Model 数据项名称列表
     */
    private List<String> titles;

    /**
     * Excel Model 数据项内容列表
     */
    private List<List<String>> items;

    /**
     * Excel Model 提示信息
     */
    protected String notice;

    /**
     * Excel Model 表头
     */
    protected String tableHeader;

    /**
     * Excel Model 文件名
     */
    private String fileName;

    /**
     * 数据项名称对应所在列索引容器
     * 数据结构 &lt;cKey, titleIndex&gt:
     * 通过title 名称，获取其以及其对应数据所在列
     */
    private Map<String, Integer> titleMap;

    /**
     * 生成 数据区的一行数据
     *
     * @param approvalData 审批数据
     * @return
     */
    private List<String> item(ApprovalExcelVO approvalData) {

        String[] item = new String[titles.size()];
        item[ApprovalExConsts.CELL_NUM_TITLE_0] = approvalData.getTitle();
        item[ApprovalExConsts.CELL_NUM_STATE_1] = approvalData.getState();
        item[ApprovalExConsts.CELL_NUM_RESULT_2] = approvalData.getResult();
//        item[ApprovalExConsts.CELL_NUM_CREATE_TIME_3] = approvalData.getCreateTime();
//        item[ApprovalExConsts.CELL_NUM_FINISH_TIME_4] = approvalData.getFinishTime();
        item[ApprovalExConsts.CELL_NUM_USERNAME_5] = approvalData.getUserName();
        item[ApprovalExConsts.CELL_NUM_DEPT_NAME_6] = approvalData.getDeptName();
        item[ApprovalExConsts.CELL_NUM_APPROVAL_NAME_7] = approvalData.getApprovalName();
        item[ApprovalExConsts.CELL_NUM_TIME_CONSUMING_8] = approvalData.getTimeConsuming();
        if (approvalData.getListValue() != null && approvalData.getListValue().size() > 0) {
            for (AttrValueVO attr : approvalData.getListValue()) {
                if (attr != null && StringUtils.isNotBlank(attr.getAttrVal())) {
                    item[titleMap.get(attr.getCkey())] = attr.getAttrVal();
                }
            }
        }
        return Arrays.asList(item);
    }


    /**
     * 设置 Excel 数据区单元格数据
     */
    private void setItems() {
        setTitleMap();
        items = new ArrayList<>();
        for(ApprovalExcelVO approvalExcelVO : this.approvalList) {
            items.add(item(approvalExcelVO));
        }
    }

    /**
     * 设置数据项名称索引
     */
    private void setTitleMap() {
        titleMap = new HashMap<>();
        for (int i = 0; i < titles.size(); i++) {
            titleMap.put(titles.get(i), i);
        }
    }

    /**
     * 生成Excel 对应数据项名称列表
     */
    public void setTitles(List<ApprovalTemplVO> tempList) {
        List<String> titles = new ArrayList<>();
        titles.add(ApprovalExConsts.CELL_NAME_TITLE);
        titles.add(ApprovalExConsts.CELL_NAME_STATE);
        titles.add(ApprovalExConsts.CELL_NAME_RESULT);
        titles.add(ApprovalExConsts.CELL_NAME_CREATE_TIME);
        titles.add(ApprovalExConsts.CELL_NAME_FINISH_TIME);
        titles.add(ApprovalExConsts.CELL_NAME_USERNAME);
        titles.add(ApprovalExConsts.CELL_NAME_DEPT_NAME);
        titles.add(ApprovalExConsts.CELL_NAME_APPROVAL_NAME);
        titles.add(ApprovalExConsts.CELL_NAME_TIME_CONSUMING);
        for (ApprovalTemplVO t : tempList) {
            titles.add(t.getCKey());
        }
        this.titles = titles;
    }

    public List<ApprovalExcelVO> getApprovalList() {
        return approvalList;
    }

    public void setApprovalList(List<ApprovalExcelVO> approvalList) {
        this.approvalList = approvalList;
        setItems();
    }

    public List<String> getTitles() {
        return titles;
    }

}
