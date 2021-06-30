package com.xaqinren.healthyelders.http;

import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.global.Constant;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by goldze on 2017/5/10.
 * 请求拦截器
 */
public class MBaseInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request()
                .newBuilder();

        String uid = "";
        if (UserInfoMgr.getInstance().getUserInfo() != null) {
            if (UserInfoMgr.getInstance().getUserInfo().getId() != null) {
                uid = UserInfoMgr.getInstance().getUserInfo().getId();
            }
        }

        //开放接口需要传的参数
        if (chain.request().url().toString().contains("/open/") ||chain.request().url().toString().contains("/oauth/") ) {
            builder.addHeader("Authorization", Constant.HEADER_DEF).build();
            builder.addHeader("mid", Constant.APP_MID).build();
            builder.addHeader("uid", uid).build();
        }

        //请求信息
        return chain.proceed(builder.build());
    }
}