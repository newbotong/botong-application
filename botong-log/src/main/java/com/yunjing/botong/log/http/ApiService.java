package com.yunjing.botong.log.http;

import com.yunjing.botong.log.processor.feign.param.DangParam;
import com.yunjing.mommon.base.PushParam;
import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * <p>
 * <p> 应用中心 api 接口
 * </p>
 *
 * @author tao.zeng.
 * @since 2018/4/3.
 */
public interface ApiService {

    /**
     * 服务器地址，必须以 / 结尾，别问我为啥，因为没有 / 会报错
     */
    String BASE_URL = "http://192.168.10.234:17220/";

    /**
     * 推送
     *
     * @param param
     * @return
     */
    @POST("/api/microapps/appcenter/push/push-target-user")
    // 设置请求头，post body方式提交
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ResponseEntityWrapper> push(@Body PushParam param);


    /**
     * 表单信息提交
     *
     * @return
     */
    @POST("/xxx")
    // post 表单形式提交
    @FormUrlEncoded
    Call<ResponseBody> test();


    /**
     * dang
     *
     * @param param
     * @return
     */
    @POST("/api/microapps/appcenter/dang/send-dang")
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    Call<ResponseEntityWrapper> dang(@Body DangParam param);
}
