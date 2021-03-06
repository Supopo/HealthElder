package com.xaqinren.healthyelders.apiserver;

import com.xaqinren.healthyelders.BuildConfig;
import com.xaqinren.healthyelders.bean.AppConfigBean;
import com.xaqinren.healthyelders.bean.BaseListRes;
import com.xaqinren.healthyelders.bean.SlideBarBean;
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
import com.xaqinren.healthyelders.moduleMsg.bean.GroupIconBean;
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
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
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
     * ??????????????????
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
     * ?????????????????????
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
     * ?????????????????????
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
     * ??????????????????
     *
     * @param authorization
     * @return
     */
    @GET("jkzl/findUserBaseInfo")
    Observable<MBaseResponse<UserInfoBean>> getUserInfo(@Header("Authorization") String authorization);


    //??????-???????????????
    @Headers({"content-type:application/json"})
    @POST("live/start")
    Observable<MBaseResponse<LiveInitInfo>> toStartLive(@Header("Authorization") String authorization,
                                                        @Body RequestBody body);

    //??????-????????????
    @GET("live/continue")
    Observable<MBaseResponse<LiveInitInfo>> reStartLive(@Header("Authorization") String authorization, @Query("liveRoomRecordId") String liveRoomRecordId);

    //??????-???????????????????????????
    @Headers({"content-type:application/json"})
    @GET("live/findLiveHomeInfo")
    Observable<MBaseResponse<LiveInitInfo>> checkLiveInfo(@Header("Authorization") String authorization);

    //??????-???????????????
    @Headers({"content-type:application/json"})
    @POST("live/inRoom")
    Observable<MBaseResponse<LiveInitInfo>> toJoinLive(@Header("Authorization") String authorization,
                                                       @Body RequestBody body);

    //??????-????????????
    @Headers({"content-type:application/json"})
    @POST("live/over")
    Observable<MBaseResponse<Object>> toOverLive(@Header("Authorization") String authorization,
                                                 @Body RequestBody body);

    //????????????
    @POST("merchant/fileUpload")
    Observable<MBaseResponse<String>> uploadFile(@Header("Authorization") String token, @Body RequestBody requestBody);

    //????????????
    @POST("content/filesUpload")
    Observable<MBaseResponse<List<String>>> uploadMultiFile(
            /*@Url String host,*/ @Header("Authorization") String token, @Body MultipartBody requestBody);

    //???????????????????????????-?????????-?????????-??????
    @GET("live/findLiveRoomRefreshParams")
    Observable<MBaseResponse<LiveHeaderInfo>> refreshLiveRoomInfo(@Header("Authorization") String authorization, @Query("liveRoomRecordId") String liveRoomRecordId);

    //???????????????
    @Headers({"content-type:application/json"})
    @POST("live/live/zan")
    Observable<MBaseResponse<Object>> toZanLive(@Header("Authorization") String authorization,
                                                @Body RequestBody body);

    //?????????????????????
    @GET("live/queryLiveUserPage")
    Observable<MBaseResponse<BaseListRes<List<ZBUserListBean>>>> getZBUserList(@Header("Authorization") String authorization, @Query("liveRoomRecordId") String liveRoomRecordId, @Query("page") int page, @Query("pageSize") int pageSize);

    //???????????????
    @Headers({"content-type:application/json"})
    @POST("live/bannedUser")
    Observable<MBaseResponse<Object>> setUserSpeech(@Header("Authorization") String authorization,
                                                    @Body RequestBody body);

    //????????????
    @Headers({"content-type:application/json"})
    @POST("user/attentionUser")
    Observable<MBaseResponse<Object>> setUserFollow(@Header("Authorization") String authorization,
                                                    @Body RequestBody body);


    //???????????????
    @Headers({"content-type:application/json"})
    @POST("live/shieldingUser")
    Observable<MBaseResponse<Object>> setUserBlack(@Header("Authorization") String authorization,
                                                   @Body RequestBody body);

    //??????-???????????????
    @GET("live/leaveRoom")
    Observable<MBaseResponse<Object>> leaveLive(@Header("Authorization") String authorization, @Query("liveRoomRecordId") String liveRoomRecordId);

    //?????????????????????
    @GET("live/overStatistical")
    Observable<MBaseResponse<LiveOverInfo>> liveOverInfo(@Header("Authorization") String authorization, @Query("liveRoomRecordId") String liveRoomRecordId);

    //????????????????????????
    @GET("live/live/attentionCount")
    Observable<MBaseResponse<Object>> addFansCount(@Header("Authorization") String authorization, @Query("liveRoomRecordId") String liveRoomRecordId);

    //???????????????
    @Headers({"content-type:application/json"})
    @POST("/live/updateLiveHomeSetting")
    Observable<MBaseResponse<Object>> setZBStatus(@Header("Authorization") String authorization,
                                                  @Body RequestBody body);

    //?????????????????????->????????????,??????,??????
    @GET("user/queryAttentionUserInfoPage")
    Observable<MBaseResponse<PublishAtMyBean>> getLiteAvPublishMyAtList(@Header("Authorization") String authorization, @Query("page") int page, @Query("pageSize") int pageSize);

    //???????????????
    @Headers({"content-type:application/json"})
    @POST("live/voiceMicMute")
    Observable<MBaseResponse<Object>> setVoiceMicMute(@Header("Authorization") String authorization,
                                                      @Body RequestBody body);

    //??????????????????
    @GET("live/findMicUsers")
    Observable<MBaseResponse<List<ZBUserListBean>>> findMicUsers(@Header("Authorization") String authorization, @Query("liveRoomRecordId") String liveRoomRecordId);

    //???????????????????????????/??????????????????
    @Headers({"content-type:application/json"})
    @POST("live/voiceMic")
    Observable<MBaseResponse<Object>> setVoiceMic(@Header("Authorization") String authorization,
                                                  @Body RequestBody body);

    //???????????????????????????
    @GET("live/findLiveHomeStatus")
    Observable<MBaseResponse<LiveInitInfo>> getLiveStatus(@Header("Authorization") String authorization, @Query("liveRoomId") String liveRoomId);

    //???????????????????????????
    @GET("content/findHotTopic")
    Observable<MBaseResponse<List<TopicBean>>> getHotTopicList(@Header("Authorization") String authorization);

    //???????????????????????????
    @GET("content/findPageByName")
    Observable<MBaseResponse<List<TopicBean>>> getSearchTopicList(@Header("Authorization") String authorization, @Query("name") String key);


    //???????????????
    @Headers({"content-type:application/json"})
    @POST("content/shortVideo/release")
    Observable<MBaseResponse<Object>> postPublishLiteAv(@Header("Authorization") String authorization,
                                                        @Body RequestBody body);

    //????????????
    @Headers({"content-type:application/json"})
    @POST("content/userDiary/release")
    Observable<MBaseResponse<Object>> postPublishTextPhoto(@Header("Authorization") String authorization,
                                                           @Body RequestBody body);

    //?????????????????????
    @GET("user/queryFriendsPage")
    Observable<MBaseResponse<BaseListRes<List<LiteAvUserBean>>>> getUserFriend(@Header("Authorization") String authorization,
                                                                               @Query("page") int page, @Query("pageSize") int pageSize, @Query("identity") String identity,
                                                                               @Query("targetId") String tagetId);

    //?????????????????????
    @GET("user/querySearchPage")
    Observable<MBaseResponse<BaseListRes<List<LiteAvUserBean>>>> getSearchUserFriend(@Header("Authorization") String authorization,
                                                                                     @Query("page") int page, @Query("pageSize") int pageSize, @Query("key") String name);

    //???????????????????????? type 0 - ?????? 1 ?????? 2 ??????
    @GET("jkzl/open/queryComprehensivePage")
    Observable<MBaseResponse<BaseListRes<List<VideoInfo>>>> getHomeVideoList(@Header("uid") String uid, @Header("Authorization") String authorization, @Header("mid") String mid, @Query("page") Integer page,
                                                                             @Query("pageSize") Integer count, @Query("type") Integer type, @Query("resourceType") String resourceType, @Query("tags") String tags, @Query("key") String key);

    //????????????????????????
    @GET("jkzl/open/findHomeData")
    Observable<MBaseResponse<HomeMenuRes>> getHomeInfo();

    //????????????
    @Headers({"content-type:application/json"})
    @POST("content/shortVideo/favorite")
    Observable<MBaseResponse<HomeMenuRes>> setVideoLike(@Header("Authorization") String authorization, @Body RequestBody body);

    //???????????????????????????
    @GET("jkzl/findAttentionUserLive")
    Observable<MBaseResponse<List<VideoInfo>>> getLiveFirends(@Header("Authorization") String authorization);

    //??????
    @Headers({"content-type:application/json"})
    @POST("content/shortVideo/comment")
    Observable<MBaseResponse<CommentListBean>> toComment(@Header("Authorization") String authorization,
                                                         @Body RequestBody body);

    //????????????
    @GET("content/open/shortVideo/comment/findPageByShortVideoId")
    Observable<MBaseResponse<BaseListRes<List<CommentListBean>>>> getCommentList(@Query("page") int page, @Query("pageSize") int pageSize, @Query("shortVideoId") String shortVideoId);

    //????????????
    @Headers({"content-type:application/json"})
    @POST("content/shortVideo/commentReply")
    Observable<MBaseResponse<CommentListBean>> toCommentReply(@Header("Authorization") String authorization,
                                                              @Body RequestBody body);

    //????????????
    @GET("content/open/shortVideo/commentReply/findPageByCommentId")
    Observable<MBaseResponse<BaseListRes<List<CommentListBean>>>> getCommentReplyList(@Query("page") int page, @Query("pageSize") int pageSize, @Query("commentId") String commentId);

    //????????????
    @Headers({"content-type:application/json"})
    @POST("content/shortVideo/commentFavorite")
    Observable<MBaseResponse<Object>> setCommentLike(@Header("Authorization") String authorization, @Body RequestBody body);

    //???????????????
    @Headers({"content-type:application/json"})
    @POST("content/userDiary/favorite")
    Observable<MBaseResponse<HomeMenuRes>> setUserDiaryLike(@Header("Authorization") String authorization, @Body RequestBody body);


    //??????????????????
    @Headers({"content-type:application/json"})
    @POST("user/saveUserIdCardInfo")
    Observable<MBaseResponse<Object>> saveUserIdCardInfo(@Header("Authorization") String authorization,
                                                         @Body RequestBody body);

    //??????????????????
    @GET("content/queryMusicChannelSheetPage")
    Observable<MBaseResponse<BaseListRes<List<MusicClassBean>>>> getMusicClass(@Header("Authorization") String authorization);

    //????????????
    @GET("content/findRecommendMusicChannelSheet")
    Observable<MBaseResponse<List<MMusicBean>>> getChannelSheet(@Header("Authorization") String authorization);

    //????????????
    @GET("content/queryMusicPage")
    Observable<MBaseResponse<BaseListRes<List<MMusicItemBean>>>> getMusicList(@Header("Authorization") String authorization,
                                                                              @Query("page") int page, @Query("pageSize") int pageSize, @Query("name") String name, @Query("sheetId") String sheetId);

    //?????? ????????????
    @GET("content/queryMusicRecommend")
    Observable<MBaseResponse<List<MMusicItemBean>>> getMusicRecommend(@Header("Authorization") String authorization);

    //?????? ????????????
    @GET("content/queryMusicFavoritePage")
    Observable<MBaseResponse<BaseListRes<List<MMusicItemBean>>>> getMusicFavorite(@Header("Authorization") String authorization, @Query("page") int page, @Query("pageSize") int pageSize);

    //?????? ????????????
    @Headers({"content-type:application/json"})
    @POST("content/musicFavorite")
    Observable<MBaseResponse<Object>> musicFavorite(@Header("Authorization") String authorization, @Body RequestBody body);

    //????????????
    @GET("user/queryAttentionUserInfoPage")
    Observable<MBaseResponse<BaseListRes<List<ZBUserListBean>>>> getFriendsList(@Header("Authorization") String authorization, @Query("page") int page, @Query("pageSize") int pageSize);

    //????????????IMSig
    @GET("live/findUserSig")
    Observable<MBaseResponse<Object>> getUserSig(@Header("Authorization") String authorization);

    //??????????????????
    @GET("jkzl/queryUserCreationPage")
    Observable<MBaseResponse<BaseListRes<List<VideoInfo>>>> getMyVideoList(@Header("Authorization") String authorization, @Query("page") int page, @Query("pageSize") int pageSize, @Query("creationViewAuth") String creationViewAuth, @Query("targetId") String id);

    //????????????????????????
    @GET("jkzl/queryUserFavoriteCreationPage")
    Observable<MBaseResponse<BaseListRes<List<DZVideoInfo>>>> getMyLikeVideoList(@Header("Authorization") String authorization, @Query("page") int page, @Query("pageSize") int pageSize, @Query("targetId") String id);


    //????????????
    @GET("content/open/userDiary/info")
    Observable<MBaseResponse<DiaryInfoBean>> getDiaryInfo(@Header("Authorization") String authorization, @Query("id") String id);

    //????????????
    @Headers({"content-type:application/json"})
    @POST("content/userDiary/comment")
    Observable<MBaseResponse<CommentListBean>> toDiaryComment(@Header("Authorization") String authorization,
                                                              @Body RequestBody body);

    //??????????????????
    @GET("content/open/userDiary/comment/findPageByUserDiaryId")
    Observable<MBaseResponse<BaseListRes<List<CommentListBean>>>> getDiaryCommentList(@Query("page") int page, @Query("pageSize") int pageSize, @Query("userDiaryId") String shortVideoId);

    //??????????????????
    @Headers({"content-type:application/json"})
    @POST("content/userDiary/commentReply")
    Observable<MBaseResponse<CommentListBean>> toDiaryCommentReply(@Header("Authorization") String authorization,
                                                                   @Body RequestBody body);

    //??????????????????
    @GET("content/open/userDiary/commentReply/findPageByCommentId")
    Observable<MBaseResponse<BaseListRes<List<CommentListBean>>>> getDiaryCommentReplyList(@Query("page") int page, @Query("pageSize") int pageSize, @Query("commentId") String commentId);

    //??????????????????
    @Headers({"content-type:application/json"})
    @POST("content/userDiary/commentFavorite")
    Observable<MBaseResponse<Object>> setDiaryCommentLike(@Header("Authorization") String authorization, @Body RequestBody body);

    //????????????
    @Headers({"content-type:application/json"})
    @POST("content/userDiary/favorite")
    Observable<MBaseResponse<Object>> setDiaryFavorite(@Header("Authorization") String authorization, @Body RequestBody body);

    //????????????cid
    @GET("content/bindAlias")
    Observable<MBaseResponse<Object>> bindAlias(@Header("Authorization") String authorization, @Query("cid") String cid);

    //????????????????????????
    @GET("jkzl/open/findMallHomeMenu")
    Observable<MBaseResponse<MallMenuRes>> getMallMenu();

    //????????????????????????
    @GET("jkzl/open/findMallHomeCategory")
    Observable<MBaseResponse<List<MenuBean>>> getMallTypeMenu();

    //????????????
    @GET("jkzl/open/queryCommodityPage")
    Observable<MBaseResponse<BaseListRes<List<GoodsBean>>>> getMallGoodsList(@Query("page") int page, @Query("pageSize") int pageSize, @Query("category") String category);

    //????????????
    @GET("jkzl/open/queryCommodityPage")
    Observable<MBaseResponse<BaseListRes<List<GoodsBean>>>> getMallGoodsList(@Query("page") int page,
                                                                             @Query("pageSize") int pageSize,
                                                                             @Query("title") String title,
                                                                             @Query("sortBy") String sortBy,
                                                                             @Query("orderBy") String orderBy,
                                                                             @Query("category") String category);

    //?????????????????????????????????
    @GET("jkzl/open/queryHomeHotSearch")
    Observable<MBaseResponse<SlideBarBean>> getHotWords();

    //?????? - ????????????
    @GET("jkzl/open/queryCommodityHotSearch")
    Observable<MBaseResponse<SlideBarBean>> getGoodsHotWords();

    //????????????
    @GET("content/queryMessagePage")
    Observable<MBaseResponse<BaseListRes<List<InteractiveBean>>>> getMessageData(@Header("Authorization") String authorization, @Query("page") int page, @Query("pageSize") int pageSize, @Query("messageGroup") String messageGroup, @Query("messageType") String messageType);

    //???????????????
    @GET("jkzl/shortVideo/info")
    Observable<MBaseResponse<VideoInfo>> getVideoInfo(@Header("Authorization") String authorization, @Query("id") String id);

    //???????????????
    @GET("jkzl/open/queryUserPage")
    Observable<MBaseResponse<BaseListRes<List<VideoInfo>>>> getSearchUser(@Query("page") int page, @Query("pageSize") int pageSize, @Query("tags") String tag);

    //???????????????
    @GET("merchant/open/queryAppletInfoPage")
    Observable<MBaseResponse<BaseListRes<List<UniBean>>>> getUniList();

    //????????????
    @GET("live/open/gift/list")
    Observable<MBaseResponse<List<GiftBean>>> getGiftList(@Header("Authorization") String authorization);

    //???????????????
    @Headers({"content-type:application/json"})
    @POST("user/saveUserContactList")
    Observable<MBaseResponse<Object>> postUserContact(@Header("Authorization") String authorization, @Body RequestBody body);

    //???????????????
    @GET("jkzl/findRecommendContactFriends")
    Observable<MBaseResponse<List<FriendBean>>> getRecommendContactF(@Header("Authorization") String authorization);

    //????????????
    @GET("user/findRecommendUserFriends")
    Observable<MBaseResponse<List<FriendBean>>> getRecommendF(@Header("Authorization") String authorization);

    //?????? - ????????????
    @GET("content/open/queryGroupIcon")
    Observable<MBaseResponse<GroupIconBean>> getGroupIcon();

    //????????????
    @Headers({"content-type:application/json"})
    @POST("live/gift/give")
    Observable<MBaseResponse<Object>> sendGift(@Header("Authorization") String authorization, @Body RequestBody body);

    //??????????????????
    @GET("user/dissolveRelationship")
    Observable<MBaseResponse<Object>> delFans(@Header("Authorization") String authorization, @Query("targetId") String id);

    //????????????
    @GET("user/findPointRechargeConfig")
    //user/getRechargeList
    Observable<MBaseResponse<ChongZhiListRes>> getChongZhiList(@Header("Authorization") String authorization);

    //????????????
    @POST("merchant/unifiedPay")
    Observable<MBaseResponse<String>> toPay(
            /*@Url String host,*/ @Header("Authorization") String token, @Body RequestBody requestBody);

    //????????????????????? - ?????????
    @GET("live/overAudienceStatistical")
    Observable<MBaseResponse<LiveOverInfo>> liveOverInfoGZ(@Header("Authorization") String authorization, @Query("liveRoomRecordId") String liveRoomRecordId);

    //????????????
    @GET("jkzl/queryLikePage")
    Observable<MBaseResponse<BaseListRes<List<VideoInfo>>>> getSomeLikeVideoList(@Header("Authorization") String authorization, @Query("page") Integer page,
                                                                                 @Query("pageSize") Integer count, @Query("resourceType") String resourceType, @Query("excludeId") String excludeId);

    //??????????????????
    @POST("user/updateUserInfo")
    Observable<MBaseResponse<Object>> updateUserInfo(@Header("Authorization") String authorization, @Body RequestBody requestBody);

    //??????????????????-?????????????????????
    @GET("live/findUserProfile")
    Observable<MBaseResponse<UserInfoBean>> getLiveUserInfo(@Header("Authorization") String authorization, @Query("targetId") String targetId, @Query("liveRoomRecordId") String liveRoomRecordId);

    //????????????
    @GET("merchant/open/findUpdateAppVersion?appId=" + BuildConfig.APPLICATION_ID)
    Observable<MBaseResponse<VersionBean>> checkVersion();

    //??????????????????
    @GET("user/findAccountInfo")
    Observable<MBaseResponse<WalletBean>> getWalletInfo(@Header("Authorization") String authorization);

    //????????????
    @GET("user/findUserWalletAccountRecordPage")
    Observable<MBaseResponse<BillRecodeBean>> getBillInfo(@Header("Authorization") String authorization, @Query("date") String key);

    //??????????????????
    @GET("user/findUserWalletAccountRecordInfo")
    Observable<MBaseResponse<BillDetailBean>> getBillInfoDetail(@Header("Authorization") String authorization, @Query("id") String key);

    //??????????????????
    @POST("user/setPayPassword")
    Observable<MBaseResponse<Object>> setPassWord(@Header("Authorization") String authorization, @Body RequestBody requestBody);

    //????????????????????????
    @GET("live/findLiveRoomBlacklist")
    Observable<MBaseResponse<BaseListRes<List<ZBUserListBean>>>> getBlackList(@Header("Authorization") String authorization, @Query("page") Integer page,
                                                                              @Query("pageSize") Integer count, @Query("liveRoomId") String liveRoomId);


    //???????????????????????????
    @GET("live/findLiveRoomMuteList")
    Observable<MBaseResponse<BaseListRes<List<ZBUserListBean>>>> getJinYanList(@Header("Authorization") String authorization, @Query("page") Integer page,
                                                                               @Query("pageSize") Integer count, @Query("liveRoomRecordId") String liveRoomRecordId);

    //????????????
    @GET("commodity/queryUserCommodityOrderPage")
    Observable<MBaseResponse<BaseListRes<List<OrderListBean>>>> getOrderList(@Header("Authorization") String authorization, @Query("statusNumber") int type, @Query("page") int page);

    //????????????
    @POST("commodity/cancelCommodityOrder")
    Observable<MBaseResponse<Object>> cancelOrder(@Header("Authorization") String authorization, @Body RequestBody requestBody);

    //????????????
    @POST("commodity/deleteCommodityOrder")
    Observable<MBaseResponse<Object>> delOrder(@Header("Authorization") String authorization, @Body RequestBody requestBody);

    //????????????
    @POST("commodity/confirmCommodityReceipt")
    Observable<MBaseResponse<Object>> receiptOrder(@Header("Authorization") String authorization, @Body RequestBody requestBody);

    //????????????
    @POST("user/withdrawalApplication")
    Observable<MBaseResponse<Object>> withdrawal(@Header("Authorization") String authorization, @Body RequestBody requestBody);

    //??????????????????
    @GET("jkzl/findUserProfile")
    Observable<MBaseResponse<UserInfoBean>> getOtherUserInfo(@Header("Authorization") String authorization, @Query("targetId") String targetId);

    //??????APP??????
    @GET("jkzl/open/findAppConfigInfo")
    Observable<MBaseResponse<AppConfigBean>> getAppConfig();

    //?????????????????????
    @GET("live/findCommodityByLiveRoomId")
    Observable<MBaseResponse<ZBGoodsListRes>> getZbGoodsList(@Header("Authorization") String authorization, @Query("liveRoomId") String liveRoomId);

    //?????????????????????/????????????
    @POST("live/liveCommodityExplain")
    Observable<MBaseResponse<Object>> setZBGoodsShow(@Header("Authorization") String authorization, @Body RequestBody requestBody);

    //??????????????????
    @GET("content/open/sendRegLoginMsg")
    Observable<MBaseResponse<Object>> senLoginSMS(@Query("mobile") String liveRoomId);

    //???????????????????????????
    @GET("jkzl/open/findUserSidebar")
    Observable<MBaseResponse<SlideBarBean>> getSlideBar();

    //??????????????????-???????????????
    @GET("jkzl/open/queryTypeByName")
    Observable<MBaseResponse<SlideBarBean>> getMenuAllWords(@Query("type") String type);

    //????????? - ??????
    @GET("content/shortVideo/delete")
    Observable<MBaseResponse<Object>> delLiteAvVideo(@Header("Authorization") String authorization, @Query("id") String id);

    //?????? - ??????
    @GET("content/userDiary/delete")
    Observable<MBaseResponse<Object>> delDiary(@Header("Authorization") String authorization, @Query("id") String id);

    //???????????????-?????????
    @Headers({"content-type:application/json"})
    @POST("live/updateBlockWord")
    Observable<MBaseResponse<Object>> setBlockWord(@Header("Authorization") String authorization,
                                                   @Body RequestBody body);

    //??????token
    @Headers({"content-type:application/json"})
    @POST("merchant/oauth/refresh/token")
    Observable<MBaseResponse<LoginTokenBean>> refreshToken(@Body RequestBody body);

    //?????????????????? ?????? userDiary
    @POST("content/shortVideo/viewAuthUpdate")
    Observable<MBaseResponse<Object>> setVideoStatus(@Header("Authorization") String authorization, @Body RequestBody requestBody);

    //????????????????????????
    @POST("content/feedbackSave")
    Observable<MBaseResponse<Object>> feedbackSave(@Header("Authorization") String authorization, @Body RequestBody requestBody);

    //??????????????????
    @GET("user/bindRecommendRelationship")
    Observable<MBaseResponse<Object>> bindUserRelationship(@Header("Authorization") String authorization, @Query("recommendCode") String id);
}

