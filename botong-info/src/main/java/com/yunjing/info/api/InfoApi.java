package com.yunjing.info.api;

import com.baomidou.mybatisplus.plugins.Page;
import com.yunjing.info.dto.InfoContentDetailDTO;
import com.yunjing.info.dto.InfoDTO;
import com.yunjing.info.param.InfoCategoryParam;
import com.yunjing.info.service.InfoCatalogService;
import com.yunjing.info.service.InfoContentService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.validate.BeanFieldValidator;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 资讯Api
 *
 * @author 李双喜
 * @date 2018/3/20 17:57
 */
@RestController
@RequestMapping("/info/app")
public class InfoApi extends BaseController {

    @Autowired
    private InfoCatalogService infoCatalogService;
    @Autowired
    private InfoContentService infoContentService;

    /**
     * 查询资讯父级目录
     *
     * @param orgId  企业id
     * @param userId 用户id
     * @return
     */
    @PostMapping("/select-parent")
    public ResponseEntityWrapper selectParent(@RequestParam Long orgId, @RequestParam Long userId) throws BaseException {
        Map<String, Object> map = infoCatalogService.selectParent(orgId, userId);
        return success(map);
    }

    /**
     * 查询资讯父级目录下分页列表
     *
     * @param orgId     企业id
     * @param catalogId 目录id
     * @param userId    用户id
     * @return
     * @throws BaseException
     */
    @PostMapping("/select-parent-all")
    public ResponseEntityWrapper selectParentAll(@RequestParam Long orgId, @RequestParam Long catalogId, @RequestParam Long userId) throws BaseException {
        Map<String, Object> map = infoCatalogService.selectParentAll(orgId, catalogId, userId);
        return success(map);
    }


    /**
     * 查询资讯详情接口
     *
     * @param id     资讯id
     * @param userId 用户id
     * @return
     */
    @PostMapping("/select-detail")
    public ResponseEntityWrapper selectDetail(@RequestParam Long id, @RequestParam Long userId) throws BaseException {
        InfoContentDetailDTO infoContentDetailDTO = infoContentService.selectDetail(id, userId);
        return success(infoContentDetailDTO);
    }


    /**
     * 更新阅读数量接口
     *
     * @param id 资讯id
     * @return
     */
    @PostMapping("/update-number")
    public ResponseEntityWrapper updateNumber(@RequestParam Long id) throws BaseException {
        infoContentService.updateNumber(id);
        return success();
    }


    /**
     * 新增资讯接口
     *
     * @param infoCategoryParam 入参对象
     * @return
     */
    @PostMapping("/insert-info")
    public ResponseEntityWrapper insertInfo(@RequestBody InfoCategoryParam infoCategoryParam) throws BaseException {
        BeanFieldValidator.getInstance().ignore().validate(infoCategoryParam);
        infoContentService.insertInfo(infoCategoryParam);
        return success();
    }


    /**
     * 模糊查询资讯
     *
     * @param orgId    企业
     * @param title    标题
     * @param pageNo   当前页码
     * @param pageSize 每页显示条数
     * @return
     * @throws BaseException
     */
    @PostMapping("/search-page")
    public ResponseEntityWrapper searchPage(@RequestParam Long orgId, String title, @RequestParam Integer pageNo, @RequestParam Integer pageSize) throws BaseException {
        Page<InfoDTO> page = infoContentService.searchPage(orgId, title, pageNo, pageSize);
        return success(page);
    }
}
