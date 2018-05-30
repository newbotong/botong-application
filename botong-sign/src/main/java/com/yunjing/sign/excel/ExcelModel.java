package com.yunjing.sign.excel;

import com.yunjing.mommon.enums.DateStyle;
import com.yunjing.mommon.utils.DateUtil;
import com.yunjing.sign.beans.vo.AttrValueVO;
import com.yunjing.sign.beans.vo.SignExcelVO;
import com.yunjing.sign.beans.vo.SignTemplVO;
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
    private List<SignTemplVO> tempList;

    /**
     * 签到信息列表
     */
    private List<SignExcelVO> signList;


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
     * @param signData 签到数据
     * @return
     */
    private List<String> item(SignExcelVO signData) {

        String[] item = new String[titles.size()];
        item[SignExConsts.CELL_NUM_NICK_0] = signData.getUserName();
        item[SignExConsts.CELL_NUM_DEPT_1] = signData.getDeptName();
        item[SignExConsts.CELL_NUM_DEPT_PATH_2] = signData.getPath();
        item[SignExConsts.CELL_NUM_POSITION_3] = signData.getPosition();
        item[SignExConsts.CELL_NUM_DATE_4] = signData.getSignTime();
        item[SignExConsts.CELL_NUM_TIME_5] = DateUtil.DateToString(DateUtil.convertLongToDate(signData.getSignDate()), DateStyle.HH_MM_SS);
        item[SignExConsts.CELL_NUM_LONGITUDE_6] = signData.getLongitude();
        item[SignExConsts.CELL_NUM_LATITUDE_7] = signData.getLatitude();
        item[SignExConsts.CELL_NUM_ADDRESSTITL_8] = signData.getAddressTitle();
        item[SignExConsts.CELL_NUM_ADDRESS_9] = signData.getAddress();
        item[SignExConsts.CELL_NUM_REMARK_10] = signData.getRemark();
        item[SignExConsts.CELL_NUM_DEVICE_11] = signData.getDevice();
        if (signData.getListValue() != null && signData.getListValue().size() > 0) {
            for (AttrValueVO attr : signData.getListValue()) {
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
        for(SignExcelVO signExcelVO : this.signList) {
            items.add(item(signExcelVO));
        }
    }

    /**
     * 设置数据项名称索引
     */
    private void setTitleMap() {
        titleMap = new HashMap<>(8);
        for (int i = 0; i < titles.size(); i++) {
            titleMap.put(titles.get(i), i);
        }
    }

    /**
     * 生成Excel 对应数据项名称列表
     */
    public void setTitles(List<SignTemplVO> tempList) {
        List<String> titles = new ArrayList<>();
        titles.add(SignExConsts.CELL_NAME_NICK);
        titles.add(SignExConsts.CELL_NAME_DEPT);
        titles.add(SignExConsts.CELL_NAME_DEPT_PATH);
        titles.add(SignExConsts.CELL_NAME_POSITION);
        titles.add(SignExConsts.CELL_NAME_SIGN_DATE);
        titles.add(SignExConsts.CELL_NAME_SIGN_TIME);
        titles.add(SignExConsts.CELL_NAME_LONGITUDE);
        titles.add(SignExConsts.CELL_NAME_LATITUDE);
        titles.add(SignExConsts.CELL_NAME_ADDRESSTITL);
        titles.add(SignExConsts.CELL_NAME_ADDRESS);
        titles.add(SignExConsts.CELL_NAME_REMARK);
        titles.add(SignExConsts.CELL_NAME_DEVICE);
        for (SignTemplVO t : tempList) {
            titles.add(t.getCKey());
        }
        this.titles = titles;
    }

    public List<SignExcelVO> getSignList() {
        return signList;
    }

    public void setSignList(List<SignExcelVO> signList) {
        this.signList = signList;
        setItems();
    }
    public List<String> getTitles() {
        return titles;
    }

}
