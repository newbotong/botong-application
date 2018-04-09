package com.yunjing.info.mapper;

import com.baomidou.mybatisplus.plugins.Page;
import com.common.mybatis.mapper.IBaseMapper;
import com.yunjing.info.dto.InfoDto;
import com.yunjing.info.model.InfoCatalog;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * <p> 资讯目录Mapper
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/23.
 */
@Repository
public interface InfoCatalogMapper extends IBaseMapper<InfoCatalog> {
    /**
     * 根据目录id查询企业分页内容
     *
     * @param map  map入参
     * @param page 分页
     * @return
     */
    List<InfoDto> selectInfoCatalog(Map<String, Object> map, Page<InfoDto> page);
}
