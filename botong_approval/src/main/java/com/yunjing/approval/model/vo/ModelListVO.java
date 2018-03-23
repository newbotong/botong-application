package com.yunjing.approval.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 刘小鹏
 * @date 2018/03/21
 */
@Data
public class ModelListVO {

    private Long categoryId;

    private String categroyName;

    private Integer sort;

    private Long updateTime;

    private List<ModelVO> modelVOList;

    private Integer modelCount;

}
