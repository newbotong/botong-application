package com.yunjing.approval.service;


import com.common.mybatis.service.IBaseService;
import com.yunjing.approval.model.entity.SetsProcess;
import com.yunjing.approval.model.vo.UserVO;

import java.util.List;

/**
 * @author 刘小鹏
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
    boolean delete(String modelId, String conditions) throws Exception;

    /**
     * 获取审批流程
     *
     * @param modelId     模型主键
     * @param conditionId 条件主键
     * @return
     * @throws Exception
     */
    List<UserVO> getProcess(String modelId, String conditionId) throws Exception;

    /**
     * 设置审批流程信息
     *
     * @param modelId     模型主键
     * @param conditionId 条件主键
     * @param userArray   用户集合
     * @return
     * @throws Exception
     */
    boolean updateProcess(String modelId, String conditionId, String userArray) throws Exception;

    /**
     * 删除审批流程人
     *
     * @param companyId 公司id
     * @param memberId  成员id
     */
    void deleteProcessUser(String companyId, String memberId);

}
