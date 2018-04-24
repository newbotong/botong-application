package com.yunjing.approval.service;

import com.yunjing.approval.model.dto.*;

import java.util.List;

/**
 * 审批数据迁移
 *
 * @author 刘小鹏
 * @date 2018/04/18
 */
public interface IDataTransferService {

    /**
     * 保存botong1.0 approval 表数据到 botong2.0数据库中
     *
     * @param dtoList
     * @return boolean
     */
    boolean addApproval(List<ApprovalDTO> dtoList);

    /**
     * 保存botong1.0 copy 表数据到 botong2.0数据库中
     *
     * @param dtoList
     * @return boolean
     */
    boolean addCopy(List<CopyDTO> dtoList);

    /**
     * 保存botong1.0 model 表数据到 botong2.0数据库中
     *
     * @param dtoList
     * @return boolean
     */
    boolean addModel(List<ModelDTO> dtoList);

    /**
     * 保存botong1.0 model_item 表数据到 botong2.0数据库中
     *
     * @param dtoList
     * @return boolean
     */
    boolean addModelItem(List<ModelItemDTO> dtoList);

    /**
     * 保存botong1.0 org_model 表数据到 botong2.0数据库中
     *
     * @param dtoList
     * @return boolean
     */
    boolean addOrgModel(List<OrgModelDTO> dtoList);

    /**
     * 保存botong1.0 approval_process 表数据到 botong2.0数据库中
     *
     * @param dtoList
     * @return boolean
     */
    boolean addApprovalProcess(List<ApprovalProcessDTO> dtoList);

    /**
     * 保存botong1.0 approval_sets_condition 表数据到 botong2.0数据库中
     *
     * @param dtoList
     * @return boolean
     */
    boolean addSetsCondition(List<SetsConditionDTO> dtoList);

    /**
     * 保存botong1.0 approval_sets_process 表数据到 botong2.0数据库中
     *
     * @param dtoList
     * @return boolean
     */
    boolean addSetsProcess(List<SetsProcessDTO> dtoList);

    /**
     * 保存botong1.0 approval_copys 表数据到 botong2.0数据库中
     *
     * @param dtoList
     * @return boolean
     */
    boolean addCopyS(List<CopySDTO> dtoList);
    /**
     * 保存botong1.0 approval_attr 表数据到 botong2.0数据库中
     *
     * @param dtoList
     * @return boolean
     */
    boolean addApprovalAttr(List<ApprovalAttrDTO> dtoList);



}
