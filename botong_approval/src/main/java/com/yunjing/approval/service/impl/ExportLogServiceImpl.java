package com.yunjing.approval.service.impl;

import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.plugins.Page;
import com.common.mybatis.service.impl.BaseServiceImpl;
import com.yunjing.approval.dao.cache.UserRedisService;
import com.yunjing.approval.dao.mapper.ExportLogMapper;
import com.yunjing.approval.model.entity.ExportLog;
import com.yunjing.approval.model.vo.ExportLogVO;
import com.yunjing.approval.model.vo.LogPageVO;
import com.yunjing.approval.model.vo.ModelVO;
import com.yunjing.approval.model.vo.UserVO;
import com.yunjing.approval.service.IExportLogService;
import com.yunjing.approval.service.IModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 导出记录--业务实现类
 *
 * @author liuxiaopeng
 * @date 2018/01/15
 */
@Service
public class ExportLogServiceImpl extends BaseServiceImpl<ExportLogMapper, ExportLog> implements IExportLogService {

    @Autowired
    private UserRedisService userRedisService;
    @Autowired
    private IModelService modelService;

    @Override
    public LogPageVO findExportLogPage(Page page, Long orgId) {
        LogPageVO logPage = new LogPageVO();
        Page<ExportLog> page1 = this.selectPage(page, Condition.create().where("org_id={0}", orgId).orderBy("create_time", false));
        List<ExportLog> records = page1.getRecords();
        List<ExportLogVO> list = new ArrayList<>();
        List<ModelVO> modelListByOrgId = modelService.findModelListByOrgId(orgId);
        String mName;
        Set<ModelVO> modelList = new HashSet<>();
        for (ExportLog exportLog : records) {
            ExportLogVO exportLogVO = new ExportLogVO();
            exportLogVO.setFileName(exportLog.getFileName());
            exportLogVO.setCreateTime(exportLog.getCreateTime());
            UserVO user = userRedisService.getByUserId(exportLog.getUserId().toString());
            exportLogVO.setName(user.getUserNick());
            if (exportLog.getModelId().contains(",")) {
                mName = "";
                // 多个审批类型导出记录的处理
                List<String> modelIds = Arrays.asList(exportLog.getModelId().split(","));
                modelIds.stream().forEach(modelId -> {
                    Set<ModelVO> modelVOS = modelListByOrgId.stream().filter(modelVO -> modelVO.getModelId().equals(modelId)).collect(Collectors.toSet());
                    modelList.addAll(modelVOS);
                });
                for (ModelVO m : modelList) {
                    mName += m.getModelName() + "|";
                }
                exportLogVO.setApprovalType(mName.substring(0, mName.length() - 1));
            } else {
                // 单个审批类型导出记录的处理
                List<ModelVO> models = modelListByOrgId.stream().filter(modelVO -> modelVO.getModelId().equals(exportLog.getModelId())).collect(Collectors.toList());
                if (models != null && !models.isEmpty()) {
                    exportLogVO.setApprovalType(models.get(0).getModelName());
                }
            }
            list.add(exportLogVO);
        }
        logPage.setCurrent(page.getCurrent());
        logPage.setSize(page.getSize());
        int count = this.selectCount(Condition.create().where("org_id={0}", orgId));
        logPage.setTotal(count);
        logPage.setRecords(list);
        return logPage;
    }
}
