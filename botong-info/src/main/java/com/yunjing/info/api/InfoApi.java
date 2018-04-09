package com.yunjing.info.api;

import com.baomidou.mybatisplus.plugins.Page;
import com.yunjing.info.dto.InfoContentDetailDto;
import com.yunjing.info.dto.InfoDto;
import com.yunjing.info.param.InfoCategoryParam;
import com.yunjing.info.service.InfoCatalogService;
import com.yunjing.info.service.InfoContentService;
import com.yunjing.mommon.base.BaseController;
import com.yunjing.mommon.global.exception.BaseException;
import com.yunjing.mommon.validate.BeanFieldValidator;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

/**
 * 资讯Api
 *
 * @author 李双喜
 * @date 2018/3/20 17:57
 */
@RestController
@RequestMapping("/info")
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
     * @throws BaseException
     */
    @PostMapping("/parent")
    public ResponseEntityWrapper selectParent(@RequestParam String orgId, @RequestParam String userId) throws BaseException {
        Map<String, Object> map = infoCatalogService.selectParent(orgId, userId);
        return success(map);
    }

    /**
     * 查询资讯父级目录下分页列表
     *
     * @param orgId     企业id
     * @param catalogId 目录id
     * @param userId    用户id
     * @param pageNo    当前页码
     * @param pageSize  每页显示条数
     * @return
     * @throws BaseException
     * @throws IOException
     */
    @PostMapping("/parent-all")
    public ResponseEntityWrapper selectParentAll(@RequestParam String orgId, @RequestParam String catalogId, @RequestParam String userId, @RequestParam Integer pageNo, @RequestParam Integer pageSize) throws BaseException, IOException {
        Map<String, Object> map = infoCatalogService.selectParentAll(orgId, catalogId, userId, pageNo, pageSize);
        return success(map);
    }


    /**
     * 查询资讯详情接口
     *
     * @param id     资讯id
     * @param userId 用户id
     * @return
     * @throws BaseException
     * @throws IOException
     */
    @PostMapping("/detail")
    public ResponseEntityWrapper selectDetail(@RequestParam String id, @RequestParam String userId) throws BaseException,IOException  {
        InfoContentDetailDto infoContentDetailDto = infoContentService.selectDetail(id, userId);
        return success(infoContentDetailDto);
    }


    /**
     * 更新阅读数量接口
     *
     * @param id 资讯id
     * @return
     */
    @PostMapping("/update")
    public ResponseEntityWrapper updateNumber(@RequestParam String id) throws BaseException {
        infoContentService.updateNumber(id);
        return success();
    }


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
    public ResponseEntityWrapper searchPage(@RequestParam String orgId, String title, @RequestParam Integer pageNo, @RequestParam Integer pageSize) {
        Page<InfoDto> page = infoContentService.searchPage(orgId, title, pageNo, pageSize);
        return success(page);
    }
}
