package com.xaqinren.healthyelders.apiserver;

import com.xaqinren.healthyelders.bean.BaseListRes;
import com.xaqinren.healthyelders.moduleHome.bean.GirlsBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishAtMyBean;
import com.xaqinren.healthyelders.moduleLogin.bean.LoginTokenBean;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleLogin.bean.WeChatUserInfoBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveHeaderInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveOverInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBSettingBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;

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
    @Headers({"content-type:application/json"})
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
    @Headers({"content-type:application/json"})
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


    //主播-创建直播间
    @Headers({"content-type:application/json"})
    @POST("live/start")
    Observable<MBaseResponse<LiveInitInfo>> toStartLive(@Header("Authorization") String authorization,
                                                        @Body RequestBody body);

    //主播-继续直播
    @GET("live/continue")
    Observable<MBaseResponse<LiveInitInfo>> reStartLive(@Header("Authorization") String authorization, @Query("liveRoomRecordId") String liveRoomRecordId);

    //主播-获取直播间权限信息
    @Headers({"content-type:application/json"})
    @GET("live/findLiveHomeInfo")
    Observable<MBaseResponse<LiveInitInfo>> checkLiveInfo(@Header("Authorization") String authorization);

    //用户-进入直播间
    @Headers({"content-type:application/json"})
    @POST("live/inRoom")
    Observable<MBaseResponse<LiveInitInfo>> toJoinLive(@Header("Authorization") String authorization,
                                                       @Body RequestBody body);

    //主播-结束直播
    @Headers({"content-type:application/json"})
    @POST("live/over")
    Observable<MBaseResponse<Object>> toOverLive(@Header("Authorization") String authorization,
                                                 @Body RequestBody body);

    //上传文件
    @POST("merchant/fileUpload")
    Observable<MBaseResponse<String>> uploadFile(@Header("Authorization") String token, @Body RequestBody requestBody);

    //直播间信息刷新接口-点赞数-观众数-榜单
    @GET("live/findLiveRoomRefreshParams")
    Observable<MBaseResponse<LiveHeaderInfo>> refreshLiveRoomInfo(@Header("Authorization") String authorization, @Query("liveRoomRecordId") String liveRoomRecordId);

    //直播间点赞
    @Headers({"content-type:application/json"})
    @POST("live/live/zan")
    Observable<MBaseResponse<Object>> toZanLive(@Header("Authorization") String authorization,
                                                @Body RequestBody body);

    //直播间用户列表
    @GET("live/queryLiveUserPage")
    Observable<MBaseResponse<BaseListRes<List<ZBUserListBean>>>> getZBUserList(@Header("Authorization") String authorization, @Query("liveRoomRecordId") String liveRoomRecordId, @Query("page") int page, @Query("pageSize") int pageSize);

    //直播间禁言
    @Headers({"content-type:application/json"})
    @POST("live/bannedUser")
    Observable<MBaseResponse<Object>> setUserSpeech(@Header("Authorization") String authorization,
                                                    @Body RequestBody body);

    //用户关注
    @Headers({"content-type:application/json"})
    @POST("user/attentionUser")
    Observable<MBaseResponse<Object>> setUserFollow(@Header("Authorization") String authorization,
                                                    @Body RequestBody body);


    //直播间拉黑
    @Headers({"content-type:application/json"})
    @POST("live/shieldingUser")
    Observable<MBaseResponse<Object>> setUserBlack(@Header("Authorization") String authorization,
                                                   @Body RequestBody body);

    //观众-离开直播间
    @GET("live/leaveRoom")
    Observable<MBaseResponse<Object>> leaveLive(@Header("Authorization") String authorization, @Query("liveRoomRecordId") String liveRoomRecordId);

    //直播间结束统计
    @GET("live/overStatistical")
    Observable<MBaseResponse<LiveOverInfo>> liveOverInfo(@Header("Authorization") String authorization, @Query("liveRoomRecordId") String liveRoomRecordId);

    //直播新增粉丝统计
    @GET("live/live/attentionCount")
    Observable<MBaseResponse<Object>> addFansCount(@Header("Authorization") String authorization, @Query("liveRoomRecordId") String liveRoomRecordId);

    //直播间设置
    @Headers({"content-type:application/json"})
    @POST("/live/updateLiveHomeSetting")
    Observable<MBaseResponse<Object>> setZBStatus(@Header("Authorization") String authorization,
                                                  @Body RequestBody body);

    //短视频发布页面->自己好友,关注,粉丝
    @GET("user/queryAttentionUserInfoPage")
    Observable<MBaseResponse<PublishAtMyBean>> getLiteAvPublishMyAtList(@Header("Authorization") String authorization, @Query("page") int page, @Query("pageSize") int pageSize);

    //直播间禁言
    @Headers({"content-type:application/json"})
    @POST("live/voiceMicMute")
    Observable<MBaseResponse<Object>> setVoiceMicMute(@Header("Authorization") String authorization,
                                                @Body RequestBody body);

    //连麦用户列表
    @GET("live/findMicUsers")
    Observable<MBaseResponse<List<ZBUserListBean>>> findMicUsers(@Header("Authorization") String authorization, @Query("liveRoomRecordId") String liveRoomRecordId);

    //直播间加入多人连麦/离开多人连麦
    @Headers({"content-type:application/json"})
    @POST("live/voiceMic")
    Observable<MBaseResponse<Object>> setVoiceMic(@Header("Authorization") String authorization,
                                                      @Body RequestBody body);

    //获取直播间设置状态
    @GET("live/findLiveHomeStatus")
    Observable<MBaseResponse<LiveInitInfo>> getLiveStatus(@Header("Authorization") String authorization, @Query("liveRoomId") String liveRoomId);
}
