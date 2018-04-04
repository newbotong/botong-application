package com.yunjing.approval.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 刘小鹏
 * @date 2018/04/04
 */
@Data
public class ApproveRowVO {
	
	private Integer num;
	private List<ApproveAttrVO> attrs;
	
	public ApproveRowVO(Integer num, List<ApproveAttrVO> attrs) {
		this.num = num;
		this.attrs = attrs;
	}

}
