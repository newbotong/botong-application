package com.yunjing.approval.api;

import com.common.mybatis.page.Page;
import com.yunjing.approval.common.DateUtil;
import com.yunjing.approval.model.vo.*;
import com.yunjing.approval.service.IApprovalApiService;
import com.yunjing.approval.service.IModelItemService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.utils.IDUtils;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * 客户端-审批服务接口
 *
 * @author 刘小鹏
 * @date 2018/03/22
 */
@RestController
@RequestMapping("/api/approval")
public class ClientApprovalController extends BaseController {

    @Autowired
    private IApprovalApiService approvalApiService;

    @Autowired
    private IModelItemService modelItemService;

    /**
     * 获取审批列表
     *
     * @param oid 企业主键
     * @return
     */
    @GetMapping("/index")
    public ResponseEntityWrapper index(@RequestParam("oid") Long oid) throws Exception {
        List<ClientModelVO> list = approvalApiService.getList(oid);
        return success(list);
    }

    /**
     * 获取审批模型详情
     *
     * @param modelId 模型主键
     * @return
     * @throws Exception
     */
    @GetMapping("/model_item_detail")
    public ResponseEntityWrapper getItem(@RequestParam("modelId") Long modelId) throws Exception {
        return success(modelItemService.getModelItem(modelId));
    }


    /**
     * 待我审批列表
     *
     * @param page      @param page 分页对象  current 当前页码, size 页大小
     * @param oid       企业主键
     * @param uid       用户主键
     * @param searchKey 搜索标题
     * @return
     */
    @GetMapping("/waited_approval")
    public ResponseEntityWrapper waitedApproval(@ModelAttribute(value = "page") Page page,
                                                @RequestParam("oid") Long oid,
                                                @RequestParam("uid") Long uid,
                                                String searchKey) {

        return success(approvalApiService.getWaited(page, oid, uid, searchKey));
    }

    /**
     * 已审批列表
     *
     * @param page      @param page 分页对象  current 当前页码, size 页大小
     * @param oid       企业主键
     * @param uid       用户主键
     * @param searchKey 搜索标题
     * @return
     */
    @GetMapping("/completed_approval")
    public ResponseEntityWrapper completedApproval(@ModelAttribute(value = "page") Page page,
                                                   @RequestParam("oid") Long oid,
                                                   @RequestParam("uid") Long uid,
                                                   String searchKey) {
        return success(approvalApiService.getCompleted(page, oid, uid, searchKey));
    }

    /**
     * 我发起的--审批列表
     *
     * @param page      @param page 分页对象  current 当前页码, size 页大小
     * @param oid       企业主键
     * @param uid       用户主键
     * @param searchKey 搜索标题
     * @return
     */
    @GetMapping("/launched_approval")
    public ResponseEntityWrapper launchedApproval(@ModelAttribute(value = "page") Page page,
                                                  @RequestParam("oid") Long oid,
                                                  @RequestParam("uid") Long uid,
                                                  String searchKey) {

        return success(approvalApiService.getLaunched(page, oid, uid, searchKey));
    }

    /**
     * 抄送我的--审批列表
     *
     * @param page      @param page 分页对象  current 当前页码, size 页大小
     * @param oid       企业主键
     * @param uid       用户主键
     * @param state     审批状态
     * @param searchKey 搜索标题
     * @return
     */
    @GetMapping("/copied_approval")
    public ResponseEntityWrapper copiedApproval(@ModelAttribute(value = "page") Page page,
                                                @RequestParam("oid") Long oid,
                                                @RequestParam("uid") Long uid,
                                                @RequestParam(value = "state", defaultValue = "0") Integer state, String searchKey) {

        return success(approvalApiService.getCopied(page, oid, uid, searchKey));
    }

    /**
     * 审批详情
     *
     * @param oid        企业主键
     * @param uid        用户主键
     * @param approvalId 审批主键
     * @return
     */
    @GetMapping("/approval_detail")
    public ResponseEntityWrapper approvalDetail(@RequestParam("oid") Long oid,
                                                @RequestParam("uid") Long uid,
                                                @RequestParam("approvalId") Long approvalId) {
        ClientApprovalDetailVO detailVO = new ClientApprovalDetailVO();
        detailVO.setName("刘小鹏");
        detailVO.setAvatar("https://image.botong.tech/tech/temp/7adf9998e93b4096ae537c4ea60678f7.jpg");
        detailVO.setDeptName("开发部");
        detailVO.setPosition("java工程师");
        detailVO.setState(0);
        detailVO.setModelName("请假");
        List<InputDetailVO> inputDetailVOS = new ArrayList<>();
        InputDetailVO input = new InputDetailVO();
        InputDetailVO input2 = new InputDetailVO();
        InputDetailVO input3 = new InputDetailVO();
        InputDetailVO input4 = new InputDetailVO();
        input.setInputName("请假类型");
        input2.setInputName("请假天数（天）");
        input3.setInputName("请假事由");
        input4.setInputName("结束时间");
        input.setInputValue("调休");
        input2.setInputValue("1");
        input3.setInputValue("回家有事情要处理");
        input4.setInputValue("1521716404522");
        inputDetailVOS.add(input);
        inputDetailVOS.add(input2);
        inputDetailVOS.add(input3);
        inputDetailVOS.add(input4);
        detailVO.setInputDetailList(inputDetailVOS);
        List<ApprovalUserVO> approvalUserVOS = new ArrayList<>();
        ApprovalUserVO approvalUserVO = new ApprovalUserVO();
        approvalUserVO.setName("刘小鹏");
        approvalUserVO.setColor("#774718");
        approvalUserVO.setAvatar("");
        approvalUserVO.setAvatarName("小鹏");
        approvalUserVO.setResult(1);
        approvalUserVO.setState(1);
        approvalUserVO.setApprovalTime(1521716404522L);
        ApprovalUserVO approvalUserVO2 = new ApprovalUserVO();
        approvalUserVO2.setName("李朋军");
        approvalUserVO2.setColor("");
        approvalUserVO2.setAvatar("https://image.botong.tech/tech/temp/7adf9998e93b4096ae537c4ea60678f7.jpg");
        approvalUserVO2.setAvatarName("");
        approvalUserVO2.setResult(1);
        approvalUserVO2.setState(1);
        approvalUserVO2.setApprovalTime(1521716404522L);
        approvalUserVOS.add(approvalUserVO);
        approvalUserVOS.add(approvalUserVO2);
        detailVO.setApprovalUserList(approvalUserVOS);
        List<CopyUserVO> copyUserVOS = new ArrayList<>();
        CopyUserVO copyUserVO = new CopyUserVO();
        copyUserVO.setName("魏一恒");
        copyUserVO.setColor("#774718");
        copyUserVO.setAvatar("");
        copyUserVO.setAvatarName("一恒");
        CopyUserVO copyUserVO2 = new CopyUserVO();
        copyUserVO2.setName("刘小鹏");
        copyUserVO2.setColor("");
        copyUserVO2.setAvatar("https://image.botong.tech/tech/temp/7adf9998e93b4096ae537c4ea60678f7.jpg");
        copyUserVO2.setAvatarName("");
        copyUserVOS.add(copyUserVO);
        copyUserVOS.add(copyUserVO2);
        detailVO.setCopyUserList(copyUserVOS);
        return success(detailVO);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 50; i++) {

            System.out.println(IDUtils.getID());
        }
    }

    /**
     * TODO 造假数据，测试完删除
     */
    private Page<ClientApprovalVO> createApprovalData(Page page, int key) {
        ClientApprovalVO clientApprovalVO = new ClientApprovalVO();
        ClientApprovalVO clientApprovalVO2 = new ClientApprovalVO();
        ClientApprovalVO clientApprovalVO3 = new ClientApprovalVO();
        ClientApprovalVO clientApprovalVO4 = new ClientApprovalVO();

        clientApprovalVO.setCreateTime(DateUtil.getCurrentTime().getTime());
        clientApprovalVO2.setCreateTime(DateUtil.getCurrentTime().getTime());
        clientApprovalVO3.setCreateTime(DateUtil.getCurrentTime().getTime());
        clientApprovalVO4.setCreateTime(DateUtil.getCurrentTime().getTime());
        if (key == 1) {
            clientApprovalVO.setApprovalId(6382507570833133568L);
            clientApprovalVO2.setApprovalId(6382507570833133568L);
            clientApprovalVO3.setApprovalId(6382507570833133568L);
            clientApprovalVO4.setApprovalId(6382532593782362125L);
            clientApprovalVO.setModelName("请假");
            clientApprovalVO2.setModelName("出差");
            clientApprovalVO3.setModelName("外出");
            clientApprovalVO4.setModelName("物品领用");
            clientApprovalVO.setMessage("审批完成(同意)");
            clientApprovalVO2.setMessage("审批完成(拒绝)");
            clientApprovalVO3.setMessage("审批完成(已撤销)");
            clientApprovalVO4.setMessage("审批完成(已撤销)");
            clientApprovalVO.setState(1);
            clientApprovalVO2.setState(1);
            clientApprovalVO3.setState(2);
            clientApprovalVO4.setState(2);
            clientApprovalVO.setResult(1);
            clientApprovalVO2.setResult(2);
            clientApprovalVO3.setResult(4);
            clientApprovalVO4.setResult(4);
            clientApprovalVO.setUserAvatar("https://image.botong.tech/tech/temp/7adf9998e93b4096ae537c4ea60678f7.jpg");
            clientApprovalVO.setUserId(6382507570837327872L);
            clientApprovalVO.setUserNick("刘小鹏");
            clientApprovalVO2.setUserAvatar("https://image.botong.tech/tech/temp/7adf9998e93b4096ae537c4ea60678f7.jpg");
            clientApprovalVO2.setUserId(6382507570837327872L);
            clientApprovalVO2.setUserNick("魏一恒");
            clientApprovalVO3.setUserAvatar("https://image.botong.tech/tech/temp/7adf9998e93b4096ae537c4ea60678f7.jpg");
            clientApprovalVO3.setUserId(6382507570837327872L);
            clientApprovalVO3.setUserNick("李朋军");
            clientApprovalVO4.setUserAvatar("https://image.botong.tech/tech/temp/7adf9998e93b4096ae537c4ea60678f7.jpg");
            clientApprovalVO4.setUserId(6382507570837327872L);
            clientApprovalVO4.setUserNick("李朋军");
        } else if (key == 2) {
            clientApprovalVO.setTitle("刘小鹏的请假审批");
            clientApprovalVO2.setTitle("刘小鹏的出差审批");
            clientApprovalVO3.setTitle("刘小鹏的外出审批");
            clientApprovalVO4.setTitle("刘小鹏的物品领用审批");
            clientApprovalVO.setLogo("https://web.botong.tech/resource/img/approval_logo_card.png");
            clientApprovalVO2.setLogo("https://web.botong.tech/resource/img/approval_logo_card.png");
            clientApprovalVO3.setLogo("https://web.botong.tech/resource/img/approval_logo_card.png");
            clientApprovalVO4.setLogo("https://web.botong.tech/resource/img/approval_logo_card.png");
            clientApprovalVO.setState(0);
            clientApprovalVO2.setState(1);
            clientApprovalVO3.setState(2);
            clientApprovalVO4.setState(1);
            clientApprovalVO2.setResult(2);
            clientApprovalVO3.setResult(4);
            clientApprovalVO4.setResult(1);
            clientApprovalVO.setMessage("等待李朋军审批");
            clientApprovalVO2.setMessage("审批完成(拒绝)");
            clientApprovalVO3.setMessage("审批完成(已撤销)");
            clientApprovalVO4.setMessage("审批完成(同意)");
            clientApprovalVO.setUserAvatar("");
            clientApprovalVO.setUserId(6382507570837327872L);
            clientApprovalVO.setUserNick("李朋军");
        } else {
            clientApprovalVO.setApprovalId(6382507570833133568L);
            clientApprovalVO2.setApprovalId(6382507570833133568L);
            clientApprovalVO3.setApprovalId(6382507570833133568L);
            clientApprovalVO4.setApprovalId(6382532593782362125L);
            clientApprovalVO.setModelName("请假");
            clientApprovalVO2.setModelName("出差");
            clientApprovalVO3.setModelName("外出");
            clientApprovalVO4.setModelName("物品领用");
            clientApprovalVO.setState(0);
            clientApprovalVO2.setState(0);
            clientApprovalVO3.setState(0);
            clientApprovalVO4.setState(0);
            clientApprovalVO.setMessage("家中有事，回去处理");
            clientApprovalVO2.setMessage("出差开拓市场");
            clientApprovalVO3.setMessage("外出办理公积金");
            clientApprovalVO4.setMessage("领用个笔记本电脑");
            clientApprovalVO.setUserAvatar("https://image.botong.tech/tech/temp/7adf9998e93b4096ae537c4ea60678f7.jpg");
            clientApprovalVO.setUserId(6382507570837327872L);
            clientApprovalVO.setUserNick("刘小鹏");
            clientApprovalVO2.setUserAvatar("https://image.botong.tech/tech/temp/7adf9998e93b4096ae537c4ea60678f7.jpg");
            clientApprovalVO2.setUserId(6382507570837327872L);
            clientApprovalVO2.setUserNick("魏一恒");
            clientApprovalVO3.setUserAvatar("https://image.botong.tech/tech/temp/7adf9998e93b4096ae537c4ea60678f7.jpg");
            clientApprovalVO3.setUserId(6382507570837327872L);
            clientApprovalVO3.setUserNick("李朋军");
            clientApprovalVO4.setUserAvatar("https://image.botong.tech/tech/temp/7adf9998e93b4096ae537c4ea60678f7.jpg");
            clientApprovalVO4.setUserId(6382507570837327872L);
            clientApprovalVO4.setUserNick("李朋军");
        }
        List<ClientApprovalVO> list = new ArrayList<>();
        list.add(clientApprovalVO);
        list.add(clientApprovalVO2);
        list.add(clientApprovalVO3);
        list.add(clientApprovalVO4);
        Page<ClientApprovalVO> clientApprovalVOPage = new Page<>(page.getCurrentPage(), page.getPageSize());
        clientApprovalVOPage.build(list);
        clientApprovalVOPage.setTotalCount(list.size());
        return clientApprovalVOPage;
    }
}
