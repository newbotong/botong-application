package com.yunjing.approval.service.impl;


import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.yunjing.approval.dao.mapper.OrgModelMapper;
import com.yunjing.approval.model.entity.OrgModel;
import com.yunjing.approval.service.IOrgModelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 企业模型实现类
 *
 * @author 刘小鹏
 * @date 2017/11/29
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class OrgModelServiceImpl extends ServiceImpl<OrgModelMapper, OrgModel> implements IOrgModelService {

}



