package com.yunjing.approval.excel;

import com.yunjing.approval.model.vo.ApprovalTemplVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author liuxiaopeng
 * @date 2018/03/05
 */
@Data
public class ApprovalExData implements Serializable{

    /**
     * 上传时间戳
     */
    private long createStamp;

    /**
     * 模板项列表
     */
    private List<ApprovalTemplVO> approvalTempVOList;

    private Map<String, List<ApprovalTemplVO>> temMap;

    public ApprovalExData() {}

    /**
     * 审批Excel数据解析器构造函数
     * @param approvalTempVOList 审批模板数据
     */
    public ApprovalExData(List<ApprovalTemplVO> approvalTempVOList) {
        this.approvalTempVOList = approvalTempVOList;
        this.createStamp = System.currentTimeMillis();
    }
    /**
     * 审批Excel数据解析器构造函数
     * @param temMap 审批模板数据
     */
    public ApprovalExData(Map<String, List<ApprovalTemplVO>> temMap) {
        this.temMap = temMap;
        this.createStamp = System.currentTimeMillis();
    }

}
