package com.yunjing.info.mapper;

import com.baomidou.mybatisplus.plugins.Page;
import com.common.mybatis.mapper.IBaseMapper;
import com.yunjing.info.dto.InfoContentDto;
import com.yunjing.info.dto.InfoDto;
import com.yunjing.info.model.InfoContent;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * <p> 资讯内容Mapper
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/23.
 */
@Repository
public interface InfoContentMapper extends IBaseMapper<InfoContent> {
    /**
     * 搜索分页查询
     *
     * @param map  入参
     * @param page 分页
     * @return
     */
    List<InfoDto> searchPage(Map<String, Object> map, Page<InfoDto> page);

    /**
     * web端资讯分页模糊查询
     *
     * @param orgId
     * @param catalogId
     * @param title
     * @param pageNo
     * @param pageSize
     * @return
     */
    List<InfoContentDto> selectParentPage(@Param("orgId") String orgId, @Param("catalogId") String catalogId, @Param("title") String title, @Param("pageNo") Integer pageNo, @Param("pageSize") Integer pageSize);

    ;

}
