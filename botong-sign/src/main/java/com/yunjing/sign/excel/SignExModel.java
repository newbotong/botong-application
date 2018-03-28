package com.yunjing.sign.excel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * @author 016
 * @date 2018/01/23
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SignExModel extends BaseExModel{

    /**
     * 工作表数据
     */
    private List<ExcelModel> excelModelList;

    /**
     * 创建 Excel 的 Workbook 对象
     *
     * @return
     * @throws Exception
     */
    @Override
    public Workbook createWorkbook() throws Exception {


        return super.createHSSFWorkbook(excelModelList);
    }



}
