package com.yunjing.info.controller;

import com.yunjing.info.common.ValidationUtil;
import com.yunjing.info.config.InfoConstants;
import com.yunjing.info.dto.InfoContentDto;
import com.yunjing.info.service.InfoCatalogServiceV2;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.wrapper.PageWrapper;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 资讯的Web端接口
 *
 * @author 李双喜
 * @date 2018/3/30 15:24
 */
@RestController
@RequestMapping("/web/info")
public class InfoControllerV2 extends BaseController {
    @Autowired
    private InfoCatalogServiceV2 infoContentService;

    /**
     * 新增分类
     * @param orgId 公司ID
     * @param parentId 父ID
     * @param name 分类名称
     * @return
     * @throws BaseException
     */
    @PostMapping("/category/add")
    public ResponseEntityWrapper insert(@RequestParam Long orgId, @RequestParam Long parentId,@RequestParam String name) throws BaseException{
        //校验分类名称，如果包含中文字符 算两个，总计不超过12个字符
        if((ValidationUtil.trim(name).length())>InfoConstants.INFO_NAME_MAX){
            return result(InfoConstants.StateCode.CODE_604);
        }
        return result(infoContentService.insertInfoCategory(orgId,parentId,name));
    }


    /**
     * 修改分类名称
     * @param orgId
     * @param parentId
     * @param name
     * @return
     * @throws BaseException
     */
    @PostMapping("/category/update")
    public ResponseEntityWrapper modify(@RequestParam Long orgId, @RequestParam Long parentId,@RequestParam Long catalogId,@RequestParam String name) throws BaseException{
        //校验分类名称，如果包含中文字符 算两个，总计不超过12个字符
        if((ValidationUtil.trim(name)).length()>InfoConstants.INFO_NAME_MAX){
            return result(InfoConstants.StateCode.CODE_604);
        }
        return result(infoContentService.modifyInfoCategory(orgId,parentId,catalogId,name));
    }


    /**
     * - 删除类目
     * @param orgId
     * @param parentId
     * @param catalogId
     * @return
     * @throws BaseException
     */
    @PostMapping("/category/delete")
    public ResponseEntityWrapper deleteInfoCategory(@RequestParam Long orgId,@RequestParam Long parentId,@RequestParam Long catalogId) throws BaseException{
        return result(infoContentService.deleteInfoCategory(orgId,parentId,catalogId));
    }

    /**
     * 删除资讯
     * @param orgId
     * @param id
     * @return
     * @throws BaseException
     */
    @PostMapping("/delete")
    public ResponseEntityWrapper deleteInfoContent(@RequestParam Long orgId,@RequestParam Long id) throws BaseException{
        return result(infoContentService.deleteInfoContent(orgId,id));
    }

    /**
     * 类目隐藏显示接口
     * @param orgId
     * @param parentId
     * @param catalogId
     * @param whetherShow
     * @return
     * @throws BaseException
     */
    @PostMapping("/category/show-hide")
    public ResponseEntityWrapper displayInfoCategory(@RequestParam Long orgId,@RequestParam Long parentId,@RequestParam Long catalogId,@RequestParam Integer whetherShow) throws BaseException{
        return result(infoContentService.displayInfoCategory(orgId,parentId,catalogId,whetherShow));
    }

    /**
     * - 资讯隐藏显示
     * @param orgId
     * @param id
     * @param whetherShow
     * @return
     * @throws BaseException
     */
    @PostMapping("/show-hide")
    public ResponseEntityWrapper displayInfoContent(@RequestParam Long orgId,@RequestParam Long id,@RequestParam Integer whetherShow) throws BaseException{
        return result(infoContentService.displayInfoContent(orgId,id,whetherShow));
    }

    /**
     * 获取一级下的所有分类
     * @param orgId
     * @return
     */
    @GetMapping("/menu/list")
    public ResponseEntityWrapper getInfoCatalogList(@RequestParam Long orgId)  throws BaseException {
        return success(infoContentService.getInfoCatalogList(orgId));
    }


    /**
     * 类目排序
      * @param orgId
     * @param parentId
     * @param catalogId1
     * @param catalogId2
     * @return
     * @throws BaseException
     */
    @PostMapping("/category/sort")
    public ResponseEntityWrapper updateCatalogSort(@RequestParam Long orgId,@RequestParam Long parentId,@RequestParam Long catalogId1,@RequestParam Long catalogId2) throws BaseException{
        return result(infoContentService.updateCatalogSort(orgId,parentId,catalogId1,catalogId2));
    }


    /**
     * 资讯排序
     * @param orgId
     * @param id1
     * @param id2
     * @return
     * @throws BaseException
     */
    @PostMapping("/sort")
    public ResponseEntityWrapper updateInfoSort(@RequestParam Long orgId,@RequestParam Long id1,@RequestParam Long id2) throws BaseException{
        return result(infoContentService.updateInfoSort(orgId,id1,id2));
    }

    /**
     * 查询资讯父级目录下分页列表
     * @param orgId
     * @param catalogId
     * @param title
     * @param pageNo
     * @param pageSize
     * @return
     * @throws BaseException
     */
    @GetMapping("/list")
    public ResponseEntityWrapper selectParentPage(@RequestParam Long orgId,
                                       @RequestParam Long catalogId,
                                       @RequestParam(required = false,defaultValue = "") String title,
                                       @RequestParam(required = false, defaultValue = "1") Integer pageNo,
                                       @RequestParam(required = false, defaultValue = "20") Integer pageSize) throws BaseException{
        PageWrapper<InfoContentDto> page =infoContentService.selectParentPage(orgId, catalogId, title,pageNo, pageSize);
        return success(page);
    }


    private ResponseEntityWrapper result(InfoConstants.StateCode stateCode) {
        return (new ResponseEntityWrapper.Builder()).statusCode(stateCode.getCode()).statusMessage(stateCode.getMessage()).build();
    }




}
