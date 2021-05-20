package com.xaqinren.healthyelders.apiserver;

import com.xaqinren.healthyelders.bean.BaseListRes;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.bean.CommentListBean;
import com.xaqinren.healthyelders.moduleHome.bean.GirlsBean;
import com.xaqinren.healthyelders.moduleHome.bean.HomeRes;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishAtMyBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.TopicBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.VideoCommentBean;
import com.xaqinren.healthyelders.moduleLogin.bean.LoginTokenBean;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleLogin.bean.WeChatUserInfoBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveHeaderInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveOverInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBSettingBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import me.goldze.mvvmhabit.http.BaseResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;


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

    //上传文件
    @POST
    Observable<MBaseResponse<List<String>>> uploadMultiFile(
            @Url String host, @Header("Authorization") String token, @Body MultipartBody requestBody);

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

    //短视频获取热点话题
    @GET("content/findHotTopic")
    Observable<MBaseResponse<List<TopicBean>>> getHotTopicList(@Header("Authorization") String authorization);

    //短视频搜索热点话题
    @GET("content/findPageByName")
    Observable<MBaseResponse<List<TopicBean>>> getSearchTopicList(@Header("Authorization") String authorization, @Query("name") String key);


    //短视频发布
    @Headers({"content-type:application/json"})
    @POST("content/shortVideo/release")
    Observable<MBaseResponse<Object>> postPublishLiteAv(@Header("Authorization") String authorization,
                                                        @Body RequestBody body);

    //图文发布
    @Headers({"content-type:application/json"})
    @POST("content/userDiary/release")
    Observable<MBaseResponse<Object>> postPublishTextPhoto(@Header("Authorization") String authorization,
                                                           @Body RequestBody body);

    //用户，获取好友
    @GET("user/queryFriendsPage")
    Observable<MBaseResponse<BaseListRes<List<LiteAvUserBean>>>> getUserFriend(@Header("Authorization") String authorization,
                                                                               @Query("page") int page, @Query("pageSize") int pageSize, @Query("identity") String identity);

    //用户，获取好友
    @GET("user/querySearchPage")
    Observable<MBaseResponse<BaseListRes<List<LiteAvUserBean>>>> getSearchUserFriend(@Header("Authorization") String authorization,
                                                                                     @Query("page") int page, @Query("pageSize") int pageSize, @Query("key") String name);

    //获取首页视频直播
    @GET("content/open/queryComprehensivePage")
    Observable<MBaseResponse<BaseListRes<List<VideoInfo>>>> getHomeVideoList(@Header("uid") String uid, @Header("Authorization") String authorization, @Header("mid") String mid, @Query("page") Integer page, @Query("pageSize") Integer count, @Query("type") Integer type, @Query("resourceType") String resourceType);

    //获取首页菜单信息
    @GET("content/open/findHomeData")
    Observable<MBaseResponse<HomeRes>> getHomeInfo();

    //首页点赞
    @Headers({"content-type:application/json"})
    @POST("content/shortVideo/favorite")
    Observable<MBaseResponse<HomeRes>> setVideoLike(@Header("Authorization") String authorization, @Body RequestBody body);

    //获取正在直播的好友
    @GET("content/findAttentionUserLive")
    Observable<MBaseResponse<List<VideoInfo>>> getLiveFirends(@Header("Authorization") String authorization);

    //评论
    @Headers({"content-type:application/json"})
    @POST("content/shortVideo/comment")
    Observable<MBaseResponse<CommentListBean>> toComment(@Header("Authorization") String authorization,
                                                @Body RequestBody body);

    //评论列表
    @GET("content/open/shortVideo/comment/findPageByShortVideoId")
    Observable<MBaseResponse<BaseListRes<List<CommentListBean>>>> getCommentList(@Query("page") int page, @Query("pageSize") int pageSize, @Query("shortVideoId") String shortVideoId);

    //回复评论
    @Headers({"content-type:application/json"})
    @POST("content/shortVideo/commentReply")
    Observable<MBaseResponse<CommentListBean>> toCommentReply(@Header("Authorization") String authorization,
                                                @Body RequestBody body);

    //回复列表
    @GET("content/open/shortVideo/commentReply/findPageByCommentId")
    Observable<MBaseResponse<BaseListRes<List<CommentListBean>>>> getCommentReplyList(@Query("page") int page, @Query("pageSize") int pageSize, @Query("commentId") String commentId);

    //评论点赞
    @Headers({"content-type:application/json"})
    @POST("content/shortVideo/commentFavorite")
    Observable<MBaseResponse<Object>> setCommentLike(@Header("Authorization") String authorization, @Body RequestBody body);
}
