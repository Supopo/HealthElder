package com.xaqinren.healthyelders.apiserver;

import com.xaqinren.healthyelders.BuildConfig;
import com.xaqinren.healthyelders.bean.AppConfigBean;
import com.xaqinren.healthyelders.bean.BaseListRes;
import com.xaqinren.healthyelders.moduleHome.bean.CommentListBean;
import com.xaqinren.healthyelders.moduleHome.bean.GirlsBean;
import com.xaqinren.healthyelders.moduleHome.bean.HomeMenuRes;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleHome.bean.SearchBean;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicItemBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.MusicClassBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishAtMyBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.TopicBean;
import com.xaqinren.healthyelders.moduleLogin.bean.LoginTokenBean;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleLogin.bean.WeChatUserInfoBean;
import com.xaqinren.healthyelders.moduleMall.bean.MallMenuRes;
import com.xaqinren.healthyelders.moduleMine.bean.BillDetailBean;
import com.xaqinren.healthyelders.moduleMine.bean.BillRecodeBean;
import com.xaqinren.healthyelders.moduleMine.bean.DZVideoInfo;
import com.xaqinren.healthyelders.moduleMine.bean.OrderListBean;
import com.xaqinren.healthyelders.moduleMine.bean.VersionBean;
import com.xaqinren.healthyelders.moduleMine.bean.WalletBean;
import com.xaqinren.healthyelders.moduleMsg.bean.FriendBean;
import com.xaqinren.healthyelders.moduleMsg.bean.InteractiveBean;
import com.xaqinren.healthyelders.modulePicture.bean.DiaryInfoBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ChongZhiListRes;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GiftBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GoodsBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveHeaderInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveOverInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBGoodsListRes;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBUserListBean;
import com.xaqinren.healthyelders.uniApp.bean.UniBean;

import java.util.List;

import io.reactivex.Observable;
import me.goldze.mvvmhabit.http.BaseResponse;
import okhttp3.MultipartBody;
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
    @GET("jkzl/findUserBaseInfo")
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
    @POST("content/filesUpload")
    Observable<MBaseResponse<List<String>>> uploadMultiFile(
            /*@Url String host,*/ @Header("Authorization") String token, @Body MultipartBody requestBody);

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
                                                                               @Query("page") int page, @Query("pageSize") int pageSize, @Query("identity") String identity,
                                                                               @Query("targetId") String tagetId);

    //用户，搜索用户
    @GET("user/querySearchPage")
    Observable<MBaseResponse<BaseListRes<List<LiteAvUserBean>>>> getSearchUserFriend(@Header("Authorization") String authorization,
                                                                                     @Query("page") int page, @Query("pageSize") int pageSize, @Query("key") String name);

    //获取首页视频直播 type 0 - 推荐 1 关注 2 附近
    @GET("jkzl/open/queryComprehensivePage")
    Observable<MBaseResponse<BaseListRes<List<VideoInfo>>>> getHomeVideoList(@Header("uid") String uid, @Header("Authorization") String authorization, @Header("mid") String mid, @Query("page") Integer page,
                                                                             @Query("pageSize") Integer count, @Query("type") Integer type, @Query("resourceType") String resourceType, @Query("tags") String tags);

    //获取首页菜单信息
    @GET("jkzl/open/findHomeData")
    Observable<MBaseResponse<HomeMenuRes>> getHomeInfo();

    //首页点赞
    @Headers({"content-type:application/json"})
    @POST("content/shortVideo/favorite")
    Observable<MBaseResponse<HomeMenuRes>> setVideoLike(@Header("Authorization") String authorization, @Body RequestBody body);

    //获取正在直播的好友
    @GET("jkzl/findAttentionUserLive")
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

    //对本体点赞
    @Headers({"content-type:application/json"})
    @POST("content/userDiary/favorite")
    Observable<MBaseResponse<HomeMenuRes>> setUserDiaryLike(@Header("Authorization") String authorization, @Body RequestBody body);


    //用户实名认证
    @Headers({"content-type:application/json"})
    @POST("user/saveUserIdCardInfo")
    Observable<MBaseResponse<Object>> saveUserIdCardInfo(@Header("Authorization") String authorization,
                                                         @Body RequestBody body);

    //获取音乐分类
    @GET("content/queryMusicChannelSheetPage")
    Observable<MBaseResponse<BaseListRes<List<MusicClassBean>>>> getMusicClass(@Header("Authorization") String authorization);

    //首页歌单
    @GET("content/findRecommendMusicChannelSheet")
    Observable<MBaseResponse<List<MMusicBean>>> getChannelSheet(@Header("Authorization") String authorization);

    //搜索音乐
    @GET("content/queryMusicPage")
    Observable<MBaseResponse<BaseListRes<List<MMusicItemBean>>>> getMusicList(@Header("Authorization") String authorization,
                                                                              @Query("page") int page, @Query("pageSize") int pageSize, @Query("name") String name, @Query("sheetId") String sheetId);

    //音乐 推荐音乐
    @GET("content/queryMusicRecommend")
    Observable<MBaseResponse<List<MMusicItemBean>>> getMusicRecommend(@Header("Authorization") String authorization);

    //音乐 我的收藏
    @GET("content/queryMusicFavoritePage")
    Observable<MBaseResponse<BaseListRes<List<MMusicItemBean>>>> getMusicFavorite(@Header("Authorization") String authorization, @Query("page") int page, @Query("pageSize") int pageSize);

    //音乐 收藏音乐
    @Headers({"content-type:application/json"})
    @POST("content/musicFavorite")
    Observable<MBaseResponse<Object>> musicFavorite(@Header("Authorization") String authorization, @Body RequestBody body);

    //好友列表
    @GET("user/queryAttentionUserInfoPage")
    Observable<MBaseResponse<BaseListRes<List<ZBUserListBean>>>> getFriendsList(@Header("Authorization") String authorization, @Query("page") int page, @Query("pageSize") int pageSize);

    //获取用户IMSig
    @GET("live/findUserSig")
    Observable<MBaseResponse<Object>> getUserSig(@Header("Authorization") String authorization);

    //我的视频作品
    @GET("jkzl/queryUserCreationPage")
    Observable<MBaseResponse<BaseListRes<List<VideoInfo>>>> getMyVideoList(@Header("Authorization") String authorization, @Query("page") int page, @Query("pageSize") int pageSize, @Query("creationViewAuth") String creationViewAuth,@Query("targetId") String id);

    //我喜欢的视频作品
    @GET("jkzl/queryUserFavoriteCreationPage")
    Observable<MBaseResponse<BaseListRes<List<DZVideoInfo>>>> getMyLikeVideoList(@Header("Authorization") String authorization, @Query("page") int page, @Query("pageSize") int pageSize,@Query("targetId") String id);


    //日志详情
    @GET("content/open/userDiary/info")
    Observable<MBaseResponse<DiaryInfoBean>> getDiaryInfo(@Header("Authorization") String authorization, @Query("id") String id);

    //日记评论
    @Headers({"content-type:application/json"})
    @POST("content/userDiary/comment")
    Observable<MBaseResponse<CommentListBean>> toDiaryComment(@Header("Authorization") String authorization,
                                                              @Body RequestBody body);

    //日记评论列表
    @GET("content/open/userDiary/comment/findPageByUserDiaryId")
    Observable<MBaseResponse<BaseListRes<List<CommentListBean>>>> getDiaryCommentList(@Query("page") int page, @Query("pageSize") int pageSize, @Query("userDiaryId") String shortVideoId);

    //日记回复评论
    @Headers({"content-type:application/json"})
    @POST("content/userDiary/commentReply")
    Observable<MBaseResponse<CommentListBean>> toDiaryCommentReply(@Header("Authorization") String authorization,
                                                                   @Body RequestBody body);

    //日记回复列表
    @GET("content/open/userDiary/commentReply/findPageByCommentId")
    Observable<MBaseResponse<BaseListRes<List<CommentListBean>>>> getDiaryCommentReplyList(@Query("page") int page, @Query("pageSize") int pageSize, @Query("commentId") String commentId);

    //日记评论点赞
    @Headers({"content-type:application/json"})
    @POST("content/userDiary/commentFavorite")
    Observable<MBaseResponse<Object>> setDiaryCommentLike(@Header("Authorization") String authorization, @Body RequestBody body);

    //日记评论点赞
    @Headers({"content-type:application/json"})
    @POST("content/userDiary/favorite")
    Observable<MBaseResponse<Object>> setDiaryFavorite(@Header("Authorization") String authorization, @Body RequestBody body);

    //推送绑定cid
    @GET("content/bindAlias")
    Observable<MBaseResponse<Object>> bindAlias(@Header("Authorization") String authorization, @Query("cid") String cid);

    //获取商城菜单信息
    @GET("jkzl/open/findMallHomeMenu")
    Observable<MBaseResponse<MallMenuRes>> getMallMenu();

    //获取商城菜单信息
    @GET("jkzl/open/findMallHomeCategory")
    Observable<MBaseResponse<List<MenuBean>>> getMallTypeMenu();

    //商品列表
    @GET("jkzl/open/queryCommodityPage")
    Observable<MBaseResponse<BaseListRes<List<GoodsBean>>>> getMallGoodsList(@Query("page") int page, @Query("pageSize") int pageSize, @Query("category") String category);

    //获取热门搜索关键词
    @GET("jkzl/open/queryHotSearch")
    Observable<MBaseResponse<List<SearchBean>>> getHotWords();


    //消息查询
    @GET("content/queryMessagePage")
    Observable<MBaseResponse<BaseListRes<List<InteractiveBean>>>> getMessageData(@Header("Authorization") String authorization, @Query("page") int page, @Query("pageSize") int pageSize, @Query("messageGroup") String messageGroup, @Query("messageType") String messageType);

    //短视频详情
    @GET("jkzl/shortVideo/info")
    Observable<MBaseResponse<VideoInfo>> getVideoInfo(@Header("Authorization") String authorization, @Query("id") String id);

    //搜素哦用户
    @GET("jkzl/open/queryUserPage")
    Observable<MBaseResponse<BaseListRes<List<VideoInfo>>>> getSearchUser(@Query("page") int page, @Query("pageSize") int pageSize, @Query("tags") String tag);

    //小程序列表
    @GET("merchant/open/queryAppletInfoPage")
    Observable<MBaseResponse<BaseListRes<List<UniBean>>>> getUniList();

    //礼物列表
    @GET("live/open/gift/list")
    Observable<MBaseResponse<List<GiftBean>>> getGiftList(@Header("Authorization") String authorization);

    //保存通讯录
    @Headers({"content-type:application/json"})
    @POST("user/saveUserContactList")
    Observable<MBaseResponse<Object>> postUserContact(@Header("Authorization") String authorization, @Body RequestBody body);

    //通讯录好友
    @GET("jkzl/findRecommendContactFriends")
    Observable<MBaseResponse<List<FriendBean>>> getRecommendContactF(@Header("Authorization") String authorization);

    //推荐好友
    @GET("user/findRecommendUserFriends")
    Observable<MBaseResponse<List<FriendBean>>> getRecommendF(@Header("Authorization") String authorization);

    //发送礼物
    @Headers({"content-type:application/json"})
    @POST("live/gift/give")
    Observable<MBaseResponse<Object>> sendGift(@Header("Authorization") String authorization, @Body RequestBody body);

    //解除用户关系
    @GET("user/dissolveRelationship")
    Observable<MBaseResponse<Object>> delFans(@Header("Authorization") String authorization, @Query("targetId") String id);

    //充值列表
    @GET("user/getRechargeList")
    Observable<MBaseResponse<ChongZhiListRes>> getChongZhiList(@Header("Authorization") String authorization);

    //支付接口
    @POST("merchant/unifiedPay")
    Observable<MBaseResponse<String>> toPay(
            /*@Url String host,*/ @Header("Authorization") String token, @Body RequestBody requestBody);

    //直播间结束统计 - 观众端
    @GET("live/overAudienceStatistical")
    Observable<MBaseResponse<LiveOverInfo>> liveOverInfoGZ(@Header("Authorization") String authorization, @Query("liveRoomRecordId") String liveRoomRecordId);

    //猜你喜欢
    @GET("jkzl/queryLikePage")
    Observable<MBaseResponse<BaseListRes<List<VideoInfo>>>> getSomeLikeVideoList(@Header("Authorization") String authorization, @Query("page") Integer page,
                                                                                 @Query("pageSize") Integer count, @Query("resourceType") String resourceType, @Query("excludeId") String excludeId);

    //更新个人资料
    @POST("user/updateUserInfo")
    Observable<MBaseResponse<Object>> updateUserInfo(@Header("Authorization") String authorization, @Body RequestBody requestBody);

    //查看用户资料-直播间用户信息
    @GET("live/findUserProfile")
    Observable<MBaseResponse<UserInfoBean>> getLiveUserInfo(@Header("Authorization") String authorization, @Query("targetId") String targetId, @Query("liveRoomRecordId") String liveRoomRecordId);

    //版本更新
    @GET("merchant/open/findUpdateAppVersion?appId="+BuildConfig.APPLICATION_ID)
    Observable<MBaseResponse<VersionBean>> checkVersion();

    //查询账户余额
    @GET("user/findAccountInfo")
    Observable<MBaseResponse<WalletBean>> getWalletInfo(@Header("Authorization") String authorization);

    //查询账单
    @GET("user/findUserWalletAccountRecordPage")
    Observable<MBaseResponse<BillRecodeBean>> getBillInfo(@Header("Authorization") String authorization, @Query("date") String key);

    //查询账单详情
    @GET("user/findUserWalletAccountRecordInfo")
    Observable<MBaseResponse<BillDetailBean>> getBillInfoDetail(@Header("Authorization") String authorization, @Query("id") String key);

    //设置支付密码
    @POST("user/setPayPassword")
    Observable<MBaseResponse<Object>> setPassWord(@Header("Authorization") String authorization, @Body RequestBody requestBody);

    //查询直播间黑名单
    @GET("live/findLiveRoomBlacklist")
    Observable<MBaseResponse<BaseListRes<List<ZBUserListBean>>>> getBlackList(@Header("Authorization") String authorization, @Query("page") Integer page,
                                                                              @Query("pageSize") Integer count, @Query("liveRoomId") String liveRoomId);


    //查询直播间禁言列表
    @GET("live/findLiveRoomMuteList")
    Observable<MBaseResponse<BaseListRes<List<ZBUserListBean>>>> getJinYanList(@Header("Authorization") String authorization, @Query("page") Integer page,
                                                                              @Query("pageSize") Integer count, @Query("liveRoomRecordId") String liveRoomRecordId);

    //订单列表
    @GET("commodity/queryUserCommodityOrderPage")
    Observable<MBaseResponse<BaseListRes<List<OrderListBean>>>> getOrderList(@Header("Authorization") String authorization, @Query("statusNumber") int type, @Query("page") int page);

    //取消订单
    @POST("commodity/cancelCommodityOrder")
    Observable<MBaseResponse<Object>> cancelOrder(@Header("Authorization") String authorization, @Body RequestBody requestBody);

    //删除订单
    @POST("commodity/deleteCommodityOrder")
    Observable<MBaseResponse<Object>> delOrder(@Header("Authorization") String authorization, @Body RequestBody requestBody);

    //订单收货
    @POST("commodity/confirmCommodityReceipt")
    Observable<MBaseResponse<Object>> receiptOrder(@Header("Authorization") String authorization, @Body RequestBody requestBody);

    //提现申请
    @POST("user/withdrawalApplication")
    Observable<MBaseResponse<Object>> withdrawal(@Header("Authorization") String authorization, @Body RequestBody requestBody);

    //查看用户资料
    @GET("jkzl/findUserProfile")
    Observable<MBaseResponse<UserInfoBean>> getOtherUserInfo(@Header("Authorization") String authorization, @Query("targetId") String targetId);

    //查看APP配置
    @GET("jkzl/open/findAppConfigInfo")
    Observable<MBaseResponse<AppConfigBean>> getAppConfig();

    //直播间带货列表
    @GET("live/findCommodityByLiveRoomId")
    Observable<MBaseResponse<ZBGoodsListRes>> getZbGoodsList(@Header("Authorization") String authorization, @Query("liveRoomId") String liveRoomId);

    //直播中商品设置/取消讲解
    @POST("live/liveCommodityExplain")
    Observable<MBaseResponse<Object>> setZBGoodsShow(@Header("Authorization") String authorization, @Body RequestBody requestBody);

    //登录发送短信
    @GET("content/open/sendRegLoginMsg")
    Observable<MBaseResponse<Object>> senLoginSMS(@Query("mobile") String liveRoomId);
}

