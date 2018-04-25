package com.yunjing.notice.processor.okhttp.impl;

import com.yunjing.mommon.wrapper.ResponseEntityWrapper;
import com.yunjing.notice.body.Member;
import com.yunjing.notice.processor.okhttp.OrgStructureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.List;

/**
 * okhttp调用组织机构
 *
 * @author tandk
 * @date 2018/4/3 15:58
 */
@Slf4j
@Service
public class OrgStructureServiceImpl implements OrgStructureService {

    @Value("${okhttp.botong.zuul}")
    String baseUrl;

    private OrgStructureService service;

    private void initRetrofit() {
        // 构建 Retrofit 对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                // 添加json转换器，这里使用的是gson，也可以使用fastjson的转换工厂[FastJsonConverterFactory.create()]
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // 构建请求对象
        service = retrofit.create(OrgStructureService.class);
    }

    /**
     * 获取部门下的成员列表
     *
     * @param deptIds   部门id集合,逗号隔开
     * @param memberIds 成员id集合,逗号隔开
     * @return Call<ResponseEntityWrapper>
     */
    @Override
    public Call<ResponseEntityWrapper<List<Member>>> findSubLists(String deptIds, String memberIds, Integer simplify) {
        Call<ResponseEntityWrapper<List<Member>>> call;

        if (service == null) {
            initRetrofit();
        }
        call = service.findSubLists(deptIds,memberIds,simplify);
        return call;
    }
}
