package com.yunjing.approval.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author 刘小鹏
 * @date 2018/03/23
 */
@Data
public class InputDetailDTO {

    private String inputName;

    private String inputValue;

    private Integer dataType;

    private String unit;

    private List<InputDetailDTO> inputDetailDTOS;
}
