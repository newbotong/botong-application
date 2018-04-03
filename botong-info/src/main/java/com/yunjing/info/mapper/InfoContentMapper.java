package com.yunjing.info.mapper;

import com.baomidou.mybatisplus.plugins.Page;
import com.common.mybatis.mapper.IBaseMapper;
import com.yunjing.info.dto.InfoDTO;
import com.yunjing.info.model.InfoContent;
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
    List<InfoDTO> searchPage(Map<String, Object> map, Page<InfoDTO> page);

}
