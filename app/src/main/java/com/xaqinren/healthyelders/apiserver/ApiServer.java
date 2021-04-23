package com.xaqinren.healthyelders.apiserver;

import com.xaqinren.healthyelders.moduleHome.bean.GirlsBean;

import java.util.List;

import io.reactivex.Observable;
import me.goldze.mvvmhabit.http.BaseResponse;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiServer {

    @GET("data/category/Girl/type/Girl/page/{page}/count/{count}")
    Observable<BaseResponse<List<GirlsBean>>> getGirls(@Path("page") Integer page, @Path("count") Integer count);

    //微信登录
    @GET("merchant/oauth/weChatUserInfo")
    Observable<MBaseResponse<Object>> toWxChatLogin(
            @Header("Authorization") String authorization,
            @Header("mid") String mid,
            @Query("code") String code);
}
