package com.yunjing.approval.service.impl;

import com.baomidou.mybatisplus.mapper.Condition;
import com.common.mybatis.page.Page;
import com.yunjing.approval.common.Colors;
import com.yunjing.approval.dao.cache.UserRedisService;
import com.yunjing.approval.dao.mapper.ApprovalMapper;
import com.yunjing.approval.dao.mapper.ApprovalProcessMapper;
import com.yunjing.approval.dao.mapper.CopySMapper;
import com.yunjing.approval.dao.mapper.ModelLMapper;
import com.yunjing.approval.model.dto.ApprovalContentDTO;
import com.yunjing.approval.model.dto.ApprovalDetailDTO;
import com.yunjing.approval.model.dto.InputDetailDTO;
import com.yunjing.approval.model.dto.InternalDetailDTO;
import com.yunjing.approval.model.entity.ApprovalUser;
import com.yunjing.approval.model.entity.ModelL;
import com.yunjing.approval.model.vo.*;
import com.yunjing.approval.service.IApprovalApiService;
import com.yunjing.approval.service.IApprovalUserService;
import com.yunjing.approval.service.IModelService;
import com.yunjing.mommon.global.exception.BaseException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author liuxiaopeng
 * @date 2018/02/28
 */
@Service

public class ApprovalApiServiceImpl implements IApprovalApiService {

    @Autowired
    private IModelService modelService;
    @Autowired
    private IApprovalUserService approvalUserService;
    @Autowired
    private ModelLMapper modelLMapper;
    @Autowired
    private ApprovalProcessMapper approvalProcessMapper;
    @Autowired
    private CopySMapper copysMapper;
    @Autowired
    private ApprovalMapper approvalMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<ClientModelVO> getList(Long orgId) {
        List<ModelVO> modelVOS = modelLMapper.selectModelListByOrgId(orgId);
        List<ClientModelVO> list = new ArrayList<>();
        for (ModelVO modelVO : modelVOS) {
            ClientModelVO modelVO1 = new ClientModelVO(modelVO);
            list.add(modelVO1);
        }
        return list;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Page<ClientApprovalVO> getWaited(Page page, Long orgId, Long userId, String searchKey) {
        boolean flag = false;
        int current = page.getCurrentPage();
        int size = page.getPageSize();
        int index = (current - 1) * size;
        Page<ClientApprovalVO> clientApprovalVOPage = new Page<>(current, size);
        List<ClientApprovalVO> clientApprovalVOS = new ArrayList<>();
        List<ApprovalContentDTO> waitedMeApprovalList = approvalProcessMapper.getWaitedMeApprovalList(index, size, orgId, userId, searchKey, flag);
        convertList(clientApprovalVOS, waitedMeApprovalList);
        clientApprovalVOPage.build(clientApprovalVOS);
        clientApprovalVOPage.setTotalCount(clientApprovalVOS.size());
        return clientApprovalVOPage;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Page<ClientApprovalVO> getCompleted(Page page, Long orgId, Long userId, String searchKey) {
        boolean flag = false;
        int current = page.getCurrentPage();
        int size = page.getPageSize();
        int index = (current - 1) * size;
        Page<ClientApprovalVO> clientApprovalVOPage = new Page<>(current, size);
        List<ClientApprovalVO> clientApprovalVOS = new ArrayList<>();
        List<ApprovalContentDTO> completedMeApprovalList = approvalProcessMapper.getCompletedApprovalList(index, size, orgId, userId, searchKey, flag);
        convertList(clientApprovalVOS, completedMeApprovalList);
        clientApprovalVOPage.build(clientApprovalVOS);
        clientApprovalVOPage.setTotalCount(clientApprovalVOS.size());
        return clientApprovalVOPage;
    }

    @Override
    public Page<ClientApprovalVO> getLaunched(Page page, Long orgId, Long userId, String searchKey) {
        boolean flag = false;
        int current = page.getCurrentPage();
        int size = page.getPageSize();
        int index = (current - 1) * size;
        Page<ClientApprovalVO> clientApprovalVOPage = new Page<>(current, size);
        List<ClientApprovalVO> clientApprovalVOS = new ArrayList<>();
        List<ApprovalContentDTO> launchedMeApprovalList = approvalProcessMapper.getLaunchedApprovalList(index, size, orgId, userId, searchKey, flag);
        List<ApprovalUser> userList = approvalUserService.selectList(Condition.create());
        String message = "";
        int i = 1;
        ApprovalUser user = new ApprovalUser();
        for (ApprovalContentDTO contentVO : launchedMeApprovalList) {
            List<ApprovalUser> users = userList.stream().filter(approvalUser -> approvalUser.getId().equals(contentVO.getUserId())).collect(Collectors.toList());
            for (ApprovalUser approvalUser : users) {
                user = approvalUser;
            }
            if (contentVO.getProcessState() == 0 && contentVO.getState() == 0) {
                int critical = i++;
                String name = StringUtils.isNotBlank(user.getName()) ? user.getName() : "";
                if (name.length() > 2) {
                    message = "等待" + name.substring(1, 3) + "审批";
                } else {
                    message = "等待" + name + "审批";
                }
                if (critical == 1) {
                    contentVO.setMessage(message);
                }
            } else if (contentVO.getProcessState() == 1) {
                message = "审批完成";
                if (contentVO.getResult() != null && contentVO.getResult() == 1) {
                    message += " （同意）";
                } else {
                    message += " （拒绝）";
                }
            } else if (contentVO.getState() == 2) {
                message = "已撤回";
            }
            contentVO.setMessage(message);
        }
        convertList(clientApprovalVOS, launchedMeApprovalList);
        clientApprovalVOPage.build(clientApprovalVOS);
        clientApprovalVOPage.setTotalCount(clientApprovalVOS.size());
        return clientApprovalVOPage;
    }

    @Override
    public Page<ClientApprovalVO> getCopied(Page page, Long orgId, Long userId, String searchKey) {
        boolean flag = false;
        int current = page.getCurrentPage();
        int size = page.getPageSize();
        int index = (current - 1) * size;
        Page<ClientApprovalVO> clientApprovalVOPage = new Page<>(current, size);
        List<ClientApprovalVO> clientApprovalVOS = new ArrayList<>();
        List<ApprovalContentDTO> copyApprovalList = copysMapper.getCopiedApprovalList(index, size, orgId, userId, searchKey, flag);
        convertList(clientApprovalVOS, copyApprovalList);
        clientApprovalVOPage.build(clientApprovalVOS);
        clientApprovalVOPage.setTotalCount(clientApprovalVOS.size());
        return clientApprovalVOPage;
    }

    @Override
    public ClientApprovalDetailVO getApprovalDetail(Long orgId, Long userId, Long approvalId) {
        List<ApprovalDetailDTO> approvalDetail = approvalMapper.getApprovalDetail(approvalId);
        ClientApprovalDetailVO clientApprovalDetailVO = new ClientApprovalDetailVO();
        // 获取审批详情
        List<InputDetailDTO> detail = getDetail(approvalDetail);
        // 注入审批信息详情
        clientApprovalDetailVO.setInputDetailList(detail);

        // 根据审批主键查询审批信息
        ApprovalDetailDTO approvalById = approvalMapper.getApprovalById(approvalId);
        if (approvalById != null) {
            // 审批主体信息
            clientApprovalDetailVO.setName(approvalById.getName());
            ModelL modelL = modelService.selectOne(Condition.create().where("id={0}", approvalById.getModelId()));
            clientApprovalDetailVO.setModelName(modelL.getModelName() != null ? modelL.getModelName() : null);
            clientApprovalDetailVO.setDeptName(approvalById.getDeptName());
            clientApprovalDetailVO.setPosition(approvalById.getPosition());
            if (StringUtils.isNotBlank(approvalById.getAvatar())) {
                clientApprovalDetailVO.setAvatar(approvalById.getAvatar());
            } else {
                clientApprovalDetailVO.setColor(Colors.generateBeautifulColor(approvalById.getMobile(), approvalById.getName()));
                clientApprovalDetailVO.setAvatarName(approvalById.getName().length() <= 2 ? approvalById.getName() : approvalById.getName().substring(1, 3));
            }
            clientApprovalDetailVO.setState(approvalById.getState());
            clientApprovalDetailVO.setResult(approvalById.getResult() != null ? approvalById.getResult() : null);
        }
        // 获取审批人信息
        List<ApprovalUserVO> approvalUserList = approvalProcessMapper.getApprovalUserList(approvalId);
        int index = 1;
        for (ApprovalUserVO approvalUserVO : approvalUserList) {
            approvalUserVO.setColor(Colors.generateBeautifulColor(approvalUserVO.getMobile(), approvalUserVO.getName()));
            if (StringUtils.isBlank(approvalUserVO.getAvatar())) {
                approvalUserVO.setColor(Colors.generateBeautifulColor(approvalUserVO.getMobile(), approvalUserVO.getName()));
                approvalUserVO.setAvatarName(approvalUserVO.getName().length() <= 2 ? approvalUserVO.getName() : approvalUserVO.getName().substring(1, 3));
            }
            if (approvalUserVO.getProcessState() == 0) {
                approvalUserVO.setApprovalTime(null);
                if (approvalUserVO.getUserId().equals(userId)) {
                    //描述提醒用户信息
                    clientApprovalDetailVO.setProcessState(approvalUserVO.getState());
                    clientApprovalDetailVO.setMessage("等待我审批");
                } else {
                    int flag = index++;
                    //描述提醒用户信息
                    if (flag == 1) {
                        clientApprovalDetailVO.setMessage("等待" + approvalUserVO.getName() + "审批");
                    }
                }
            }
        }
        clientApprovalDetailVO.setApprovalUserList(approvalUserList);

        // 获取抄送人信息
        List<CopyUserVO> copyUserList = copysMapper.getCopyUserList(approvalId);
        copyUserList.forEach(copyUserVO -> {
            if (StringUtils.isBlank(copyUserVO.getAvatar())) {
                copyUserVO.setColor(Colors.generateBeautifulColor(copyUserVO.getMobile(), copyUserVO.getName()));
                copyUserVO.setAvatarName(copyUserVO.getName().length() <= 2 ? copyUserVO.getName() : copyUserVO.getName().substring(1, 3));
            }
        });
        // 注入抄送人信息
        clientApprovalDetailVO.setCopyUserList(copyUserList);
        return clientApprovalDetailVO;
    }


    private void convertList(List<ClientApprovalVO> clientApprovalVOS, List<ApprovalContentDTO> approvalList) {
        // 获取所有用户
        List<ApprovalUser> userList = approvalUserService.selectList(Condition.create());
        approvalList.forEach(contentVO -> {
            if (StringUtils.isBlank(contentVO.getUserAvatar())) {
                List<ApprovalUser> users = userList.stream().filter(approvalUser -> approvalUser.getId().equals(contentVO.getUserId())).collect(Collectors.toList());
                for (ApprovalUser user : users) {
                    contentVO.setColor(Colors.generateBeautifulColor(StringUtils.isNotBlank(user.getMobile()) ? user.getMobile() : "", StringUtils.isNotBlank(user.getName()) ? user.getName() : ""));
                }
            }
            ClientApprovalVO clientApprovalVO = new ClientApprovalVO(contentVO);
            clientApprovalVOS.add(clientApprovalVO);
        });
    }

    private List<InputDetailDTO> getDetail(List<ApprovalDetailDTO> approvalDetail) {
        // 审批详情内的明细集合
        List<InternalDetailDTO> internalDetailDTOS = new ArrayList<>();
        List<InputDetailDTO> inputDetailDTOS = new ArrayList<>();
        int count = 0;
        int dataType = 0;
        String itemLabel = "";
        for (ApprovalDetailDTO detailDTO : approvalDetail) {
            InputDetailDTO inputDetailDTO = new InputDetailDTO();
            if (detailDTO.getIsChild() != null && detailDTO.getDataType() != 8) {
                // 处理审批项中有子项的情况
                InternalDetailDTO internalDetailDTO = new InternalDetailDTO();
                internalDetailDTO.setKeyName(detailDTO.getItemLabel());
                internalDetailDTO.setAttrName(detailDTO.getField());
                internalDetailDTO.setAttrValue(detailDTO.getAttrValue());
                internalDetailDTO.setDataType(detailDTO.getDataType());
                internalDetailDTO.setOptValue(detailDTO.getOptValue());
                internalDetailDTOS.add(internalDetailDTO);
            } else if (detailDTO.getDataType() == 5) {
                // 处理数据类型是--日期区间--的情况
                InputDetailDTO inputStart = new InputDetailDTO();
                InputDetailDTO inputEnd = new InputDetailDTO();
                String[] inputTime = detailDTO.getAttrValue().split(",");
                String strName = "开始时间,结束时间";
                if (StringUtils.isNotBlank(detailDTO.getOptValue())) {
                    strName = detailDTO.getOptValue();
                }
                inputStart.setInputName(strName.split(",")[0]);
                inputStart.setInputValue(inputTime[0]);
                inputStart.setDataType(detailDTO.getDataType());
                inputDetailDTOS.add(inputStart);
                inputEnd.setInputName(strName.split(",")[1]);
                inputEnd.setInputValue(inputTime[1]);
                inputEnd.setDataType(detailDTO.getDataType());
                inputDetailDTOS.add(inputEnd);
            } else if (detailDTO.getDataType() == 7) {
                // 数据类型是--明细 的情况
                itemLabel = detailDTO.getItemLabel();
                dataType = detailDTO.getDataType();
            } else {
                inputDetailDTO.setInputName(detailDTO.getItemLabel());
                inputDetailDTO.setDataType(detailDTO.getDataType());
                inputDetailDTO.setUnit(detailDTO.getUnit());
                inputDetailDTO.setInputValue(detailDTO.getAttrValue());
                if (inputDetailDTO.getDataType() != 0) {
                    inputDetailDTOS.add(inputDetailDTO);
                }
            }
            //审批明细数据处理输出
            for (int i = count - 1; i >= 0; i--) {
                InputDetailDTO input = new InputDetailDTO();
                List<InputDetailDTO> list = new ArrayList<InputDetailDTO>();
                int num = count - 1 - i;
                input.setDataType(dataType);
                input.setInputName(itemLabel);
                input.setInputValue(String.valueOf(num));
                for (InternalDetailDTO internalDetailDTO : internalDetailDTOS) {
                    InputDetailDTO input0 = new InputDetailDTO();
                    if (internalDetailDTO.getAttrValue() == null) {
                        continue;
                    }
                    String[] value = internalDetailDTO.getAttrValue().split(",");
                    String str;
                    if (value.length > i) {
                        if (internalDetailDTO.getDataType() == 5) {
                            String strName = "开始时间,结束时间";
                            if (StringUtils.isNotBlank(internalDetailDTO.getOptValue())) {
                                strName = internalDetailDTO.getOptValue();
                            }
                            InputDetailDTO input1 = new InputDetailDTO();
                            InputDetailDTO input2 = new InputDetailDTO();
                            input1.setInputName(strName.split(",")[0]);
                            input2.setInputName(strName.split(",")[1]);
                            list.add(input1);
                            list.add(input2);
                        } else {
                            str = value[i];
                            input0.setInputName(internalDetailDTO.getKeyName());
                            input0.setInputValue(str);
                            list.add(input0);
                        }
                    }
                }
                input.setInputDetailDTOS(list);
                inputDetailDTOS.add(input);
            }
        }
        return inputDetailDTOS;
    }
}
