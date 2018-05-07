package com.yunjing.info.controller;

import com.yunjing.info.common.InfoConstant;
import com.yunjing.info.common.ValidationUtil;
import com.yunjing.info.dto.CompanyRedisCatalogDto;
import com.yunjing.info.dto.InfoContentDto;
import com.yunjing.info.dto.InfoContentWebDto;
import com.yunjing.info.model.InfoContent;
import com.yunjing.info.param.InfoCategoryEditParam;
import com.yunjing.info.param.InfoCategoryParam;
import com.yunjing.info.service.InfoCatalogService;
import com.yunjing.info.service.InfoContentService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.validate.BeanFieldValidator;
import com.yunjing.mommon.wrapper.PageWrapper;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资讯的Web端接口
 *
 * @author 李双喜
 * @date 2018/3/30 15:24
 */
@RestController
@RequestMapping("/web/info")
public class InfoController extends BaseController {
    @Autowired
    private InfoContentService infoContentService;

    @Autowired
    private InfoCatalogService infoCatalogService;

    /**
     * 新增资讯接口
     *
     * @param infoCategoryParam 入参对象
     * @return
     */
    @PostMapping("/insert")
    public ResponseEntityWrapper insertInfo(@RequestBody InfoCategoryParam infoCategoryParam) throws BaseException {
        BeanFieldValidator.getInstance().ignore().validate(infoCategoryParam);
        infoContentService.insertInfo(infoCategoryParam);
        return success();
    }

    /**
     * 查询企业类目
     *
     * @param orgId 企业类目id
     * @return
     */
    @PostMapping("/parent")
    public ResponseEntityWrapper selectParent(@RequestParam String orgId) {
        List<CompanyRedisCatalogDto> list = infoCatalogService.selectParentCatalog(orgId);
        return success(list);
    }

    /**
     * 修改资讯信息
     *
     * @param infoCategoryParam 实体入参
     * @return
     * @throws BaseException
     */
    @PostMapping("/edit")
    public ResponseEntityWrapper infoEdit(@RequestBody InfoCategoryEditParam infoCategoryParam) throws BaseException {
        BeanFieldValidator.getInstance().ignore().validate(infoCategoryParam);
        infoContentService.infoEdit(infoCategoryParam);
        return success();
    }

    /**
     * 新增分类
     *
     * @param orgId    公司ID
     * @param parentId 父ID
     * @param name     分类名称
     * @return
     * @throws BaseException
     */
    @PostMapping("/category/add")
    public ResponseEntityWrapper insert(@RequestParam String orgId, @RequestParam String parentId, @RequestParam String name) throws BaseException {
        //校验分类名称，如果包含中文字符 算两个，总计不超过12个字符
        if ((ValidationUtil.trim(name).length()) > InfoConstant.INFO_NAME_MAX) {
            return result(InfoConstant.StateCode.CODE_604);
        }
        return result(infoCatalogService.insertInfoCategory(orgId, parentId, name));
    }


    /**
     * 修改分类名称
     *
     * @param orgId    企业id
     * @param parentId 一级id
     * @param name     名称
     * @return
     * @throws BaseException
     */
    @PostMapping("/category/update")
    public ResponseEntityWrapper modify(@RequestParam String orgId, @RequestParam String parentId, @RequestParam String catalogId, @RequestParam String name) throws BaseException {
        //校验分类名称，如果包含中文字符 算两个，总计不超过12个字符
        if ((ValidationUtil.trim(name)).length() > InfoConstant.INFO_NAME_MAX) {
            return result(InfoConstant.StateCode.CODE_604);
        }
        return result(infoCatalogService.modifyInfoCategory(orgId, parentId, catalogId, name));
    }


    /**
     * - 删除类目
     *
     * @param orgId     企业id
     * @param parentId  父id
     * @param catalogId 二级id
     * @return
     * @throws BaseException
     */
    @PostMapping("/category/delete")
    public ResponseEntityWrapper deleteInfoCategory(@RequestParam String orgId, @RequestParam String parentId, @RequestParam String catalogId) throws BaseException {
        return result(infoCatalogService.deleteInfoCategory(orgId, parentId, catalogId));
    }

    /**
     * 删除资讯
     *
     * @param orgId 企业id
     * @param id    资讯id
     * @return
     * @throws BaseException
     */
    @PostMapping("/delete")
    public ResponseEntityWrapper deleteInfoContent(@RequestParam String orgId, @RequestParam String id) throws BaseException {
        return result(infoCatalogService.deleteInfoContent(orgId, id));
    }

    /**
     * 类目隐藏显示接口
     *
     * @param orgId       企业id
     * @param parentId    一级级类目id
     * @param catalogId   目录id 存在二级传二级目录id,不存在传一级目录id
     * @param whetherShow 是否显示0：否，1：是
     * @return
     * @throws BaseException
     */
    @PostMapping("/category/show-hide")
    public ResponseEntityWrapper displayInfoCategory(@RequestParam String orgId, @RequestParam String parentId, @RequestParam String catalogId, @RequestParam Integer whetherShow) throws BaseException {
        return result(infoCatalogService.displayInfoCategory(orgId, parentId, catalogId, whetherShow));
    }

    /**
     * - 资讯隐藏显示
     *
     * @param orgId       企业id
     * @param id          资讯id
     * @param whetherShow 是否显示0：否，1：是
     * @return
     * @throws BaseException
     */
    @PostMapping("/show-hide")
    public ResponseEntityWrapper displayInfoContent(@RequestParam String orgId, @RequestParam String id, @RequestParam Integer whetherShow) throws BaseException {
        return result(infoCatalogService.displayInfoContent(orgId, id, whetherShow));
    }


    /**
     * 类目排序
     *
     * @param orgId      企业id
     * @param parentId   一级级类目id
     * @param catalogId1 分类id,第一个分类ID
     * @param catalogId2 分类id,第二个分类ID
     * @return
     * @throws BaseException
     */
    @PostMapping("/category/sort")
    public ResponseEntityWrapper updateCatalogSort(@RequestParam String orgId, @RequestParam String parentId, @RequestParam String catalogId1, @RequestParam String catalogId2) throws BaseException {
        return result(infoCatalogService.updateCatalogSort(orgId, parentId, catalogId1, catalogId2));
    }


    /**
     * 资讯排序
     *
     * @param orgId 企业id
     * @param id1   资讯id,第一个资讯ID
     * @param id2   资讯id,第二个资讯ID
     * @return
     * @throws BaseException
     */
    @PostMapping("/sort")
    public ResponseEntityWrapper updateInfoSort(@RequestParam String orgId, @RequestParam String id1, @RequestParam String id2) throws BaseException {
        return result(infoCatalogService.updateInfoSort(orgId, id1, id2));
    }

    /**
     * 查询资讯父级目录下分页列表
     *
     * @param orgId     企业id
     * @param parentId  一级目录
     * @param catalogId 第二级目录id
     * @param title     标题
     * @param pageNo    页码 ，默认1
     * @param pageSize  页大小 ，默认20
     * @return
     * @throws BaseException
     */
    @GetMapping("/list")
    public ResponseEntityWrapper selectParentPage(@RequestParam String orgId,
                                                  @RequestParam(required = false) String parentId,
                                                  @RequestParam(required = false) String catalogId,
                                                  @RequestParam(required = false, defaultValue = "") String title,
                                                  @RequestParam(required = false, defaultValue = "1") Integer pageNo,
                                                  @RequestParam(required = false, defaultValue = "20") Integer pageSize) throws BaseException {
        PageWrapper<InfoContentDto> page = infoCatalogService.selectParentPage(orgId, parentId, catalogId, title, pageNo, pageSize);
        return success(page);
    }


    private ResponseEntityWrapper result(InfoConstant.StateCode stateCode) {
        return (new ResponseEntityWrapper.Builder()).statusCode(stateCode.getCode()).statusMessage(stateCode.getMessage()).build();
    }

    /**
     * 根据id查询详情接口
     *
     * @param id 资讯id
     * @return
     * @throws BaseException
     */
    @PostMapping("/detail")
    public ResponseEntityWrapper selectWebDetail(@RequestParam String id) throws BaseException {
        InfoContentWebDto infoContent = infoContentService.selectWebDetail(id);
        return success(infoContent);
    }


    @GetMapping("/intoV1DataTransfer")
    public void intoV1DataTransfer() {
        infoCatalogService.intoV1DataTransfer();
    }


    @PostMapping("/initOrg")
    public ResponseEntityWrapper initOrg() {
        infoCatalogService.initOrg();
        return success();
    }
}
