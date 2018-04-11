package com.yunjing.botong.log.excel;

import com.yunjing.botong.log.vo.AttrValueVO;
import com.yunjing.botong.log.vo.LogExcelVO;
import com.yunjing.botong.log.vo.LogTemplVO;
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
     * 签到模板列表
     */
    private List<LogTemplVO> tempList;

    /**
     * 签到信息列表
     */
    private List<LogExcelVO> logList;


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
     * @param logData 签到数据
     * @return
     */
    private List<String> item(LogExcelVO logData) {

        String[] item = new String[titles.size()];
        item[LogExConsts.CELL_NUM_SENDER_0] = logData.getSender();
        item[LogExConsts.CELL_NUM_DEPT_1] = logData.getDeptName();
        item[LogExConsts.CELL_NUM_SENDTIME_2] = logData.getSendTime();

        if (logData.getListValue() != null && !logData.getListValue().isEmpty()) {
            for (AttrValueVO attr : logData.getListValue()) {
                if (attr != null && StringUtils.isNotBlank(attr.getAttrVal())) {
                    if (attr.getEkey() != null) {
                        item[titleMap.get(attr.getEkey())] = attr.getAttrVal();
                    }
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
        for(LogExcelVO logExcelVO : this.logList) {
            items.add(item(logExcelVO));
        }
    }

    /**
     * 设置数据项名称索引
     */
    private void setTitleMap() {
        titleMap = new HashMap<>(8);
        for (int i = 0; i < titles.size(); i++) {
            titleMap.put(tempList.get(i).getEKey(), i);
        }
    }

    /**
     * 生成Excel 对应数据项名称列表
     */
    public void setTitles(List<LogTemplVO> logTemplVOList) {
        List<String> titleList = new ArrayList<>();
        List<LogTemplVO> titleList1 = new ArrayList<>();
        LogTemplVO logTemplVO1 = new LogTemplVO();
        logTemplVO1.setCKey(LogExConsts.CELL_NAME_SENDER);
        logTemplVO1.setEKey(LogExConsts.CELL_NAME_SENDER_EN);

        LogTemplVO logTemplVO2 = new LogTemplVO();
        logTemplVO2.setCKey(LogExConsts.CELL_NAME_DEPT);
        logTemplVO2.setEKey(LogExConsts.CELL_NAME_DEPT_EN);


        LogTemplVO logTemplVO3 = new LogTemplVO();
        logTemplVO3.setCKey(LogExConsts.CELL_NAME_SENDTIME);
        logTemplVO3.setEKey(LogExConsts.CELL_NAME_SENDTIME_EN);
        titleList1.add(logTemplVO1);
        titleList1.add(logTemplVO2);
        titleList1.add(logTemplVO3);
        titleList1.addAll(logTemplVOList);

        for (LogTemplVO t : titleList1) {
            titleList.add(t.getCKey());
        }

        this.tempList = titleList1;
        this.titles = titleList;
    }

    public List<LogExcelVO> getLogList() {
        return logList;
    }

    public void setLogList(List<LogExcelVO> logList) {
        this.logList = logList;
        setItems();
    }
    public List<String> getTitles() {
        return titles;
    }

}
