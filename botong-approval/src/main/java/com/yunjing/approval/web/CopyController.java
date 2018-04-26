package com.yunjing.approval.web;

import com.yunjing.approval.service.ICopyService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 刘小鹏
 * @date 2017/12/21
 */
@RestController
@RequestMapping("/web/approval/copy")
public class CopyController extends BaseController {

    @Autowired
    private ICopyService copyService;

    /**
     * 获取抄送人
     *
     * @param modelId 模型主键
     * @return ResponseEntityWrapper
     * @throws Exception 抛异常
     */
    @PostMapping("/get")
    public ResponseEntityWrapper get(@RequestParam String modelId) throws Exception {
        return success(copyService.get(modelId));
    }

    /**
     * @param modelId 模型主键
     * @param userIds 用户主键集合
     * @return ResponseEntityWrapper
     * @throws Exception 抛异常
     */
    @PostMapping("/save")
    public ResponseEntityWrapper save(@RequestParam String modelId, String userIds) throws Exception {
        return success(copyService.save(modelId, userIds));
    }

    /**
     * 删除抄送人
     *
     * @param companyId 公司id
     * @param memberId  成员id
     */
    @PostMapping("/delete")
    public ResponseEntityWrapper deleteCopyUser(@RequestParam String companyId, @RequestParam String memberId) {
        return success(copyService.deleteCopyUser(companyId, memberId));
    }
}
