package com.yunjing.info.controller;

import com.yunjing.info.common.ValidationUtil;
import com.yunjing.info.config.InfoConstants;
import com.yunjing.info.service.InfoCatalogService;
import com.yunjing.info.service.InfoCatalogServiceV2;
import com.yunjing.info.service.InfoContentService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.validate.BeanFieldValidator;
import com.yunjing.mommon.validate.ValidateUtils;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 资讯的Web端接口
 *
 * @author 李双喜
 * @date 2018/3/30 15:24
 */
@RestController
@RequestMapping("/infos")
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
    @PostMapping("/insert-catalog")
    public ResponseEntityWrapper insert(@RequestParam Long orgId, @RequestParam Long parentId,@RequestParam String name) throws BaseException{
        //校验分类名称，如果包含中文字符 算两个，总计不超过12个字符
        if(ValidationUtil.length(ValidationUtil.trim(name))>12){
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
    @PostMapping("/update-parent")
    public ResponseEntityWrapper modify(@RequestParam Long orgId, @RequestParam Long parentId,@RequestParam Long catalogId,@RequestParam String name) throws BaseException{
        //校验分类名称，如果包含中文字符 算两个，总计不超过12个字符
        if(ValidationUtil.length(ValidationUtil.trim(name))>12){
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
    @PostMapping("/delete-parent")
    public ResponseEntityWrapper deleteInfoCategory(@RequestParam Long orgId,@RequestParam Long parentId,@RequestParam Long catalogId) throws BaseException{
        return result(infoContentService.deleteInfoCategory(orgId,parentId,catalogId));
    }

    @PostMapping("/delete-info")
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
    @PostMapping("/update-catalog-show")
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
    @PostMapping("/update-info-show")
    public ResponseEntityWrapper displayInfoContent(@RequestParam Long orgId,@RequestParam Long id,@RequestParam Integer whetherShow) throws BaseException{
        return result(infoContentService.displayInfoContent(orgId,id,whetherShow));
    }

    /**
     * 获取一级下的所有分类
     * @param orgId
     * @return
     */
    @GetMapping("/catelog-list")
    public ResponseEntityWrapper getInfoCatalogList(@RequestParam Long orgId)  throws BaseException {
        return success(infoContentService.getInfoCatalogList(orgId));
    }



    private ResponseEntityWrapper result(InfoConstants.StateCode stateCode) {
        return (new ResponseEntityWrapper.Builder()).statusCode(stateCode.getCode()).statusMessage(stateCode.getMessage()).build();
    }




}
