package com.yunjing.info.processor.rpc.handler;

import com.yunjing.info.processor.rpc.fallback.FavouriteFallback;
import com.yunjing.info.processor.rpc.servcie.FavouriteService;
import com.yunjing.info.processor.rpc.vo.InformationVo;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * <p>
 * <p> 收藏rpc
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/23.
 */
@FeignClient(value = "botong-favourite", fallback = FavouriteFallback.class)
public interface FavouriteRpc extends FavouriteService {

    /**
     * 查询某个内容是否被某个用户收藏
     *
     * @param userId   用户id
     * @param originId 原始内容id
     * @return
     */
    @Override
    ResponseEntityWrapper verification(@RequestParam("userId") Long userId, @RequestParam("originId") Long originId);

    /**
     * 资讯修改，同步更新收藏内容
     * <p>
     * 参数为 InformationVo 序列化json对象
     *
     * @param information
     * @return
     */
    @Override
    ResponseEntityWrapper update(@RequestBody InformationVo information);
}
