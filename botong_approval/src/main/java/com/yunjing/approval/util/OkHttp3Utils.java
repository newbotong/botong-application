//package com.yunjing.approval.util;
//
//import com.alibaba.fastjson.JSONObject;
//import com.yunjing.mommon.global.exception.BaseException;
//import okhttp3.Cache;
//import okhttp3.OkHttpClient;
//import org.apache.http.Consts;
//import org.apache.http.HttpEntity;
//import org.apache.http.NameValuePair;
//import org.apache.http.ParseException;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.util.EntityUtils;
//import org.apache.log4j.LogManager;
//import org.apache.log4j.Logger;
//import org.springframework.util.StringUtils;
//
//import javax.net.ssl.HostnameVerifier;
//import javax.net.ssl.SSLSession;
//import java.io.File;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
///**
// * Http 请求外部接口
// *
// * @version 1.0.0
// * @author: 刘小鹏
// * @date 2018/03/30
// * @description
// **/
//public class OkHttp3Utils {
//
//
//    /**
//     * @Description 初始化OkHttp
//     */
//    private void initOkHttp() {
//        File cache = getExternalCacheDir();
//        int cacheSize = 10 * 1024 * 1024;
//
//        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(mContext));
//        Https.SSLParams sslParams = Https.getSslSocketFactory(null, null, null);
//
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(15, TimeUnit.SECONDS)//连接超时(单位:秒)
//                .writeTimeout(20, TimeUnit.SECONDS)//写入超时(单位:秒)
//                .readTimeout(20, TimeUnit.SECONDS)//读取超时(单位:秒)
//                .pingInterval(20, TimeUnit.SECONDS) //websocket轮训间隔(单位:秒)
//                .cache(new Cache(cache.getAbsoluteFile(), cacheSize))//设置缓存
//                .cookieJar(cookieJar)//Cookies持久化
//                .hostnameVerifier(new HostnameVerifier() {
//                    @Override
//                    public boolean verify(String hostname, SSLSession session) {
//                        return true;
//                    }
//                })
//                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)//https配置
//                .build();
//
//        OkHttpUtils.initClient(okHttpClient);
//    }
//
//}
