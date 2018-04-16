package com.yunjing.botong.log.excel;

import lombok.Data;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * @author 016
 * @date 2018/01/23
 */
@Data
public class LogExModel extends BaseExModel{

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
    public Workbook createWorkbook() {

        return super.createHSSFWorkbook(excelModelList);
    }



}
