package com.yunjing.info.processor.rpc.fallback;

import com.yunjing.info.processor.rpc.servcie.FavouriteService;
import com.yunjing.info.processor.rpc.vo.InformationVo;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import org.springframework.stereotype.Component;

/**
 * <p>
 * <p> rpc 降级处理
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/3/23.
 */
@Component
public class FavouriteFallback implements FavouriteService {


    @Override
    public ResponseEntityWrapper verification(Long userId, Long originId) {
        return null;
    }

    @Override
    public ResponseEntityWrapper update(InformationVo information) {
        return null;
    }
}
