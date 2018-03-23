package com.yunjing.info.processor.rpc.servcie;

import com.yunjing.mommon.wrapper.ResponseEntityWrapper;

/**
 * <p>
 * <p> 收藏服务
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/23.
 */
public interface FavouriteService {


    /**
     * 查询某个内容是否被某个用户收藏
     *
     * @param userId   用户id
     * @param originId 原始内容id
     * @return
     */
    ResponseEntityWrapper verification(Long userId, Long originId);

    /**
     * 资讯修改，同步更新收藏内容
     *
     * @return
     */
    ResponseEntityWrapper update(String favouriteInfo);
}
