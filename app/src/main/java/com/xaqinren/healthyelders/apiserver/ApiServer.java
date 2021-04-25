package com.xaqinren.healthyelders.apiserver;

import com.xaqinren.healthyelders.moduleHome.bean.GirlsBean;
import com.xaqinren.healthyelders.moduleLogin.bean.LoginTokenBean;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleLogin.bean.WeChatUserInfoBean;

import java.util.List;

import io.reactivex.Observable;
import me.goldze.mvvmhabit.http.BaseResponse;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiServer {

    @GET("data/category/Girl/type/Girl/page/{page}/count/{count}")
    Observable<BaseResponse<List<GirlsBean>>> getGirls(@Path("page") Integer page, @Path("count") Integer count);

    /**
     * 获取微信信息
     *
     * @param authorization
     * @param mid
     * @param code
     * @return
     */
    @GET("merchant/oauth/weChatUserInfo")
    Observable<MBaseResponse<WeChatUserInfoBean>> getWxChatInfo(
            @Header("Authorization") String authorization,
            @Header("mid") String mid,
            @Query("code") String code);


    /**
     * 微信登录（真）
     *
     * @param body
     * @return
     */
    @Headers(
            {"content-type:application/json"}
    )
    @POST("user/open/weChatLogin")
    Observable<MBaseResponse<LoginTokenBean>> toWxChatRealLogin(@Header("Authorization") String authorization,
                                                                @Header("mid") String mid,
                                                                @Body RequestBody body);

    /**
     * 手机号登录注册
     *
     * @param authorization
     * @param mid
     * @param body
     * @return
     */
    @Headers(
            {"content-type:application/json"}
    )
    @POST("user/open/mobileLogin")
    Observable<MBaseResponse<LoginTokenBean>> phoneLogin(@Header("Authorization") String authorization,
                                                         @Header("mid") String mid,
                                                         @Body RequestBody body);

    /**
     * 获取用户信息
     *
     * @param authorization
     * @return
     */
    @GET("user/findUserBaseInfo")
    Observable<MBaseResponse<UserInfoBean>> getUserInfo(@Header("Authorization") String authorization);

}
