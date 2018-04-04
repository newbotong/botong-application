package com.yunjing.approval.service;


import com.common.mybatis.service.IBaseService;
import com.yunjing.approval.model.entity.Copy;
import com.yunjing.approval.model.vo.UserVO;

import java.util.List;

/**
 * @author roc
 * @date 2017/12/21
 */
public interface ICopyService extends IBaseService<Copy> {

    /**
     * 获取抄送人
     *
     * @param modelId 模型编号
     * @return List<UserVO>
     * @throws Exception 抛异常
     */
    List<UserVO> get(Long modelId) throws Exception;

    /**
     * 保存抄送人
     *
     * @param modelId 模型编号
     * @param userIds 用户集合
     * @return boolean
     * @throws Exception 抛异常
     */
    boolean save(Long modelId, String userIds) throws Exception;

    /**
     * 删除审批抄送人
     *
     * @param oid 模型主键
     * @param uid 用户主键
     * @return boolean
     */
    boolean deleteCopyUser(Long oid, Long uid);
}
