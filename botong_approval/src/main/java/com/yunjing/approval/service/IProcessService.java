package com.yunjing.approval.service;


import com.common.mybatis.service.IBaseService;
import com.yunjing.approval.model.entity.SetsProcess;
import com.yunjing.approval.model.vo.UserVO;

import java.util.List;

/**
 * @author roc
 * @date 2017/12/21
 */
public interface IProcessService extends IBaseService<SetsProcess> {

    /**
     * 清空审批流程
     *
     * @param modelId    模型编号
     * @param conditions 审批条件编号
     * @return
     * @throws Exception
     */
    boolean delete(Long modelId, Long conditions) throws Exception;

    /**
     * 获取审批流程
     *
     * @param modelId    模型主键
     * @param conditionId 条件主键
     * @return
     * @throws Exception
     */
    List<UserVO> getProcess(Long modelId, Long conditionId) throws Exception;

    /**
     * 设置审批流程信息
     *
     * @param modelId     模型主键
     * @param conditionId 条件主键
     * @param userArray   用户集合
     * @return
     * @throws Exception
     */
    boolean updateProcess(Long modelId, Long conditionId, String userArray) throws Exception;

    /**
     * 删除审批流程人
     *
     * @param oid 企业主键
     * @param uid 用户主键
     */
    void deleteProcessUser(Long oid, Long uid);

}
