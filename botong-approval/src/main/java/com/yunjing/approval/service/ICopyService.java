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
    List<UserVO> get(String modelId);

    /**
     * 客户端获取抄送人
     *
     * @param companyId 公司id
     * @param memberId  成员id
     * @param modelId   模型id
     * @param deptId    部门id
     * @return List<UserVO>
     */
    List<UserVO> getCopy(String companyId, String memberId, String modelId, String deptId);

    /**
     * 保存抄送人
     *
     * @param modelId   模型编号
     * @param memberIds 成员集合
     * @return boolean
     * @throws Exception 抛异常
     */
    boolean save(String modelId, String memberIds);

    /**
     * 删除审批抄送人
     *
     * @param companyId 公司id
     * @param memberId  成员id
     * @return boolean
     */
    boolean deleteCopyUser(String companyId, String memberId);
}
