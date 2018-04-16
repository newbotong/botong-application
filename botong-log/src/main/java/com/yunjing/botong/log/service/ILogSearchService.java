package com.yunjing.botong.log.service;

import com.baomidou.mybatisplus.service.IService;
import com.common.mongo.util.PageWrapper;
import com.yunjing.botong.log.excel.BaseExModel;
import com.yunjing.botong.log.params.ReceviedParam;
import com.yunjing.botong.log.params.SearchParam;
import com.yunjing.botong.log.vo.LogDetailVO;
import com.yunjing.botong.log.vo.LogExcelVO;

import java.util.List;

/**
 * <p>
 * 签到配置 服务类
 * </p>
 *
 * @author jingwj
 * @since 2018-03-21
 */
public interface ILogSearchService  {


    /**
     * 查询我收到的日志列表
     * @param receviedParam     日志参数对象
     * @return                  日志明细列表
     */
    PageWrapper<LogDetailVO> receivePage(ReceviedParam receviedParam);

    /**
     * 查询我发送的日志列表
     * @param receviedParam     日志参数对象
     * @return                  日志明细列表
     */
    PageWrapper<LogDetailVO> sendPage(ReceviedParam receviedParam);


    /**
     * 未读设置为已读
     * @param logId             日志Id
     * @param userId            用户id
     * @return                  日志明细列表
     */
    boolean read(String logId, String userId);

    /**
     * 未读设置为已读
     * @param receviedParam     日志参数对象
     * @return                  成功与否
     */
    boolean read(ReceviedParam receviedParam);

    /**
     * 删除日志
     * @param logId         日志id
     * @param userId        用户id
     * @return              成功与否
     */
    boolean delete(String logId, String userId);

    /**
     * 查询我收到的日志列表
     * @param searchParam       日志查询参数对象
     * @return                  日志明细列表
     */
    PageWrapper<LogDetailVO> findPage(SearchParam searchParam);


    /**
     * 删除日志
     * @param logIds        日志ids
     * @return              成功与否
     */
    boolean batchDelete(String[] logIds);

    /**
     * 查询所有的日志列表
     * @param searchParam
     * @return
     */
    List<LogExcelVO> findAll(SearchParam searchParam);


    /**
     * 根据条件执行导出日志信息
     *
     * @param searchParam       参数对象
     * @return                  对象
     * @throws Exception        异常
     */
    BaseExModel createLogExcel(SearchParam searchParam) throws Exception;
}
