package com.yunjing.approval.service;


import com.common.mybatis.service.IBaseService;
import com.yunjing.approval.model.entity.Copy;
import com.yunjing.approval.model.vo.UserVO;

import java.util.List;

/**
 * @author 刘小鹏
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
    List<UserVO> get(String modelId) throws Exception;

    /**
     * 客户端获取抄送人
     *
     * @param companyId 公司主键
     * @param memberId  成员主键
     * @param modelId   模型编号
     * @return List<UserVO>
     * @throws Exception 抛异常
     */
    List<UserVO> getCopy(String companyId, String memberId, String modelId) throws Exception;

    /**
     * 保存抄送人
     *
     * @param modelId   模型编号
     * @param memberIds 成员集合
     * @return boolean
     * @throws Exception 抛异常
     */
    boolean save(String modelId, String memberIds) throws Exception;

    /**
     * 删除审批抄送人
     *
     * @param companyId 公司id
     * @param memberId  成员id
     * @return boolean
     */
    boolean deleteCopyUser(String companyId, String memberId);
}
