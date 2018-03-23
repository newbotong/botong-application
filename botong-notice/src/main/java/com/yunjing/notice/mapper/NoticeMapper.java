package com.yunjing.notice.mapper;

import com.baomidou.mybatisplus.plugins.Page;
import com.yunjing.notice.body.NoticePageBody;
import com.yunjing.notice.entity.NoticeEntity;
import com.yunjing.notice.entity.NoticeUserEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 公告
 *
 * @author 李双喜
 * @since 2018/03/20/.
 */
@Repository
public interface NoticeMapper extends BaseMapper<NoticeEntity> {
    /**
     * 分页查询（客户端）
     * @param map  map入参
     * @param page 分页
     * @return
     */
    List<NoticePageBody> selectNoticePage(Map<String,Object>map, Page<NoticePageBody> page);

}
