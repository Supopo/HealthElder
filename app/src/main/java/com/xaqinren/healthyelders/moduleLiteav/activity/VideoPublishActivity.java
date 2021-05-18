package com.xaqinren.healthyelders.moduleLiteav.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.google.gson.Gson;
import com.nostra13.dcloudimageloader.utils.L;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.tencent.bugly.proguard.B;
import com.tencent.qcloud.ugckit.UGCKit;
import com.tencent.qcloud.ugckit.UGCKitConstants;
import com.tencent.qcloud.ugckit.UGCKitVideoPublish;
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK;
import com.tencent.qcloud.ugckit.module.record.VideoRecordSDK;
import com.tencent.qcloud.ugckit.module.upload.TXUGCPublish;
import com.tencent.qcloud.ugckit.module.upload.TXUGCPublishTypeDef;
import com.tencent.qcloud.ugckit.utils.BackgroundTasks;
import com.tencent.qcloud.ugckit.utils.LogReport;
import com.tencent.qcloud.ugckit.utils.NetworkUtil;
import com.tencent.qcloud.ugckit.utils.Signature;
import com.tencent.qcloud.ugckit.utils.TCUserMgr;
import com.tencent.qcloud.ugckit.utils.ToastUtil;
import com.tencent.ugc.TXVideoEditer;
import com.tencent.weibo.sdk.android.component.PublishActivity;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivityVideoPublishBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleLiteav.adapter.ChooseTopicAdapter;
import com.xaqinren.healthyelders.moduleLiteav.adapter.ChooseUserAdapter;
import com.xaqinren.healthyelders.moduleLiteav.adapter.PublishLocationAdapter;
import com.xaqinren.healthyelders.moduleLiteav.adapter.PublishTopicAdapter;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.LocationBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishAtBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishDesBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishFocusItemBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishSummaryBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.SaveDraftBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.TopicBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.VideoPublishEditBean;
import com.xaqinren.healthyelders.moduleLiteav.liteAv.LiteAvConstant;
import com.xaqinren.healthyelders.moduleLiteav.service.LocationService;
import com.xaqinren.healthyelders.moduleLiteav.viewModel.VideoPublishViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.utils.ACache;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.widget.CenterDialog;
import com.xaqinren.healthyelders.widget.ConciseDialog;
import com.xaqinren.healthyelders.widget.LiteAvOpenModePopupWindow;
import com.xaqinren.healthyelders.widget.SpeacesItemDecoration;
import com.xaqinren.healthyelders.widget.VideoPublishEditTextView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.ImageUtils;
import me.goldze.mvvmhabit.utils.KeyBoardUtils;
import me.goldze.mvvmhabit.utils.PermissionUtils;
import me.goldze.mvvmhabit.utils.StringUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.goldze.mvvmhabit.utils.Utils;
import okhttp3.internal.cache.DiskLruCache;

/**
 * 发布
 */
public class VideoPublishActivity extends BaseActivity<ActivityVideoPublishBinding, VideoPublishViewModel> implements PoiSearch.OnPoiSearchListener,
        TXUGCPublishTypeDef.ITXVideoPublishListener{

    private LiteAvOpenModePopupWindow openModePop;
    //热点列表，横向
    private PublishTopicAdapter publishTopicAdapter;
    private List<TopicBean> topicBeans = new ArrayList<>();
    private PublishLocationAdapter publishLocationAdapter;
    private List<LocationBean> locationBeans = new ArrayList<>();
    private String TAG = "VideoPublishActivity";
    //@用户列表adapter
    private ChooseUserAdapter userAdapter;
    //@用户列表
    private List<LiteAvUserBean> liteAvUserBeans = new ArrayList<>();
    //热点列表，出现#输入后
    private ChooseTopicAdapter chooseTopicAdapter;
    //热点列表
    private List<TopicBean> listTopicBeans = new ArrayList<>();

    private int album_code = 666;  //封面
    private int location_code = 777;//定位
    private int unlook_code = 888;//屏蔽

    private String mVideoPath = null;
    private String mCoverPath = null;
    private boolean mDisableCache;
    //定位信息
    private double lat, lon;
    private String poiName;
    private String cityCode;
    private String cityName;
    //当前定位
    LocationBean locationBean;
    private Disposable eventDisposable;
    //屏蔽用户列表
    private List<LiteAvUserBean> unLookUserList = new ArrayList<>();
    //草稿箱Id
    private long publishDraftId;
    //发布权限
    private int publishMode = LiteAvOpenModePopupWindow.OPEN_MODE;
    public boolean isComment;

    private int atPage = 1;
    private int atPageSize = 20;
    private String mCosSignature;

    private Handler mHandler = new Handler();
    private TXUGCPublish mVideoPublish;
    private String shortVideoUrl;
    private String shortVideoId;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_video_publish;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.e("VideoPublishActivity", "onEditCompleted");
    }


    @Override
    public void initData() {
        super.initData();
        setTitle("发布");
        eventDisposable = RxBus.getDefault().toObservable(EventBean.class).subscribe(o -> {
            if (o.msgId == CodeTable.LOCATION_SUCCESS) {
                //定位成功
                LocationBean locationBean = (LocationBean) o.data;
                lat = locationBean.lat;
                lon = locationBean.lon;
                cityCode = locationBean.cityCode;
                cityName = locationBean.cityName;
                poiName = locationBean.desName;
                getAddressList();

            }
        });
        RxSubscriptions.add(eventDisposable);
        checkPermission();
        mVideoPath = getIntent().getStringExtra(UGCKitConstants.VIDEO_PATH);
        mCoverPath = getIntent().getStringExtra(UGCKitConstants.VIDEO_COVERPATH);
        mDisableCache = getIntent().getBooleanExtra(UGCKitConstants.VIDEO_RECORD_NO_CACHE, false);
        publishDraftId = getIntent().getLongExtra(Constant.DraftId, 0L);
        if (publishDraftId > 0) {
            //装在草稿箱内容
            getDraftContent();
        }

        publishTopicAdapter = new PublishTopicAdapter(R.layout.item_publish_topic_adapter);
        LinearLayoutManager topManager = new LinearLayoutManager(this);
        topManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.topicList.setLayoutManager(topManager);
        binding.topicList.setAdapter(publishTopicAdapter);
        publishTopicAdapter.setList(topicBeans);

        publishLocationAdapter = new PublishLocationAdapter(R.layout.item_publish_location);
        LinearLayoutManager locManager = new LinearLayoutManager(this);
        locManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.includePublish.locationList.setLayoutManager(locManager);
        binding.includePublish.locationList.setAdapter(publishLocationAdapter);
        publishLocationAdapter.setList(locationBeans);
        //选择封面
        binding.selCover.setOnClickListener(view -> {
            Intent intent = new Intent(this, ChooseVideoCoverActivity.class);
            intent.putExtra(UGCKitConstants.VIDEO_PATH, mVideoPath);
            if (!TextUtils.isEmpty(mCoverPath)) {
                intent.putExtra(UGCKitConstants.VIDEO_COVERPATH, mCoverPath);
            }
            startActivityForResult(intent, album_code);
        });
        //设置最大输入
        binding.desText.setInputMax(55);
        //选择地址
        binding.includePublish.locationLayout.setOnClickListener(view -> {
            Intent intent = new Intent(this, ChooseLocationActivity.class);
            startActivityForResult(intent, location_code);
        });
        binding.includePublish.videoOpenModeLayout.setOnClickListener(view -> {
            //隐藏键盘
            KeyBoardUtils.hideKeyBoard(this,view.getWindowToken());
            showOpenModeDialog();
        });
        //保存到草稿箱
        binding.includePublish.saveDraftBtn.setOnClickListener(view -> {
            CenterDialog centerDialog = new CenterDialog(this);
            centerDialog.setMessageText("确定保存至草稿箱吗？");
            centerDialog.showDialog();
            centerDialog.setRightBtnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    centerDialog.dismissDialog();
                    createDraftContent();
                }
            });

        });
        binding.includePublish.publishBtn.setOnClickListener(view -> {
            //TODO 发布
            publishVideo();
        });
        binding.desText.setOnTextChangeListener(new VideoPublishEditTextView.OnTextChangeListener() {
            @Override
            public void inputTopic(String str) {
                LogUtils.e(TAG, "inputTopic 提出话题弹窗 -> " + str);
                showTopicView(str);
            }

            @Override
            public void inputNoTopic() {
                LogUtils.e(TAG, "inputNoTopic 隐藏话题弹窗 -> " );
                binding.includeListTopic.layoutPublishAt.setVisibility(View.GONE);
            }

            @Override
            public void inputAt(String str) {
                LogUtils.e(TAG, "inputTopic 提出@弹窗 -> " + str);
                showAtView(str);
            }

            @Override
            public void inputNoAt() {
                LogUtils.e(TAG, "inputNoTopic 隐藏@弹窗 -> " );
                binding.includeListAt.layoutPublishAt.setVisibility(View.GONE);
            }

            @Override
            public void maxInput() {
                LogUtils.e(TAG, "inputNoTopic 已到最大输入值 -> " );
                ToastUtils.showShort("最多输入"+binding.desText.getInputMax()+"个文字");
            }
        });
        publishTopicAdapter.setOnItemClickListener((adapter, view, position) -> {
            TopicBean topicBean = topicBeans.get(position);
            binding.desText.append("#" + topicBean.getName());
            binding.desText.addBlackKey();
        });
        publishLocationAdapter.setOnItemClickListener((adapter,view,position)->{
            LocationBean bean = locationBeans.get(position);
            if (bean.isLookMore) {
                Intent intent = new Intent(this, ChooseLocationActivity.class);
                startActivityForResult(intent, location_code);
                return;
            }
            binding.includePublish.myLocation.setText(bean.desName);
            equalsLocation(bean);
        });

        binding.addTopic.setOnClickListener(view -> binding.desText.append("#"));
        binding.addFriend.setOnClickListener(view -> binding.desText.append("@"));
        binding.includePublish.openModeTv.setText(publishModeGetName(publishMode));
        Glide.with(this).asBitmap().load(mCoverPath).into(binding.coverView);
        initListView();
        loginUser();
        //热门话题
        viewModel.getHotTopic();
    }

    /**
     * 视频发布 begin
     */
    private void loginUser() {
        MLVBLiveRoom mLiveRoom = MLVBLiveRoom.sharedInstance(getApplication());
        viewModel.toLoginRoom(mLiveRoom);
    }

    private void publishVideo() {
        if (!NetworkUtil.isNetworkAvailable(this)) {
            ToastUtils.showShort(com.tencent.qcloud.ugckit.R.string.ugckit_video_publisher_activity_no_network_connection);
            return;
        }
        fetchSignature();
    }

    private void fetchSignature() {
        mCosSignature = Signature.createSing();
        if (mCosSignature != null) {
            LogReport.getInstance().uploadLogs(LogReport.ELK_ACTION_VIDEO_SIGN, TCUserMgr.SUCCESS_CODE, "获取签名成功");
            startPublish();
        }else{
            LogReport.getInstance().uploadLogs(LogReport.ELK_ACTION_VIDEO_SIGN, 0, "获取签名失败");
        }
    }

    private void startPublish() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mVideoPublish == null) {
                    mVideoPublish = new TXUGCPublish(UGCKit.getAppContext(), TCUserMgr.getInstance().getUserId());
                }
                /**
                 * 设置视频发布监听器
                 */
                mVideoPublish.setListener(VideoPublishActivity.this);

                TXUGCPublishTypeDef.TXPublishParam param = new TXUGCPublishTypeDef.TXPublishParam();
                param.signature = mCosSignature;
                param.videoPath = mVideoPath;
                param.coverPath = mCoverPath;
                int publishCode = mVideoPublish.publishVideo(param);
                binding.publishProgressMark.setVisibility(View.VISIBLE);
                if (publishCode != 0) {
//                    mTVPublish.setText("发布失败，错误码：" + publishCode);
                }
                NetworkUtil.getInstance(UGCKit.getAppContext()).setNetchangeListener(new NetworkUtil.NetchangeListener() {
                    @Override
                    public void onNetworkAvailable() {
                        binding.tvProgress.setText(getResources().getString(com.tencent.qcloud.ugckit.R.string.ugckit_video_publisher_activity_network_connection_is_disconnected_video_upload_failed));
                    }
                });
                NetworkUtil.getInstance(UGCKit.getAppContext()).registerNetChangeReceiver();
            }
        });
    }

    @Override
    public void onPublishProgress(long uploadBytes, long totalBytes) {
        int progress = (int) (uploadBytes * 100 / totalBytes);
        Log.d(TAG, "onPublishProgress:" + progress);
        binding.progressbar.setProgress(progress);
        binding.tvProgress.setText(getResources().getString(com.tencent.qcloud.ugckit.R.string.ugckit_video_publisher_activity_is_uploading) + progress + "%");
    }

    @Override
    public void onPublishComplete(TXUGCPublishTypeDef.TXPublishResult publishResult) {
        Log.d(TAG, "onPublishComplete:" + publishResult.retCode);

        /**
         * ELK数据上报：视频发布到点播系统
         */
        LogReport.getInstance().reportPublishVideo(publishResult);

        binding.publishProgressMark.setVisibility(View.GONE);
        if (publishResult.retCode == TXUGCPublishTypeDef.PUBLISH_RESULT_OK) {
            shortVideoUrl = publishResult.videoURL;
            shortVideoId = publishResult.videoId;
            showDialog();
            uploadCover();
        } else {
            if (publishResult.descMsg.contains("java.net.UnknownHostException") || publishResult.descMsg.contains("java.net.ConnectException")) {
                binding.tvProgress.setText(getResources().getString(com.tencent.qcloud.ugckit.R.string.ugckit_video_publisher_activity_network_connection_is_disconnected_video_upload_failed));
            } else {
                binding.tvProgress.setText(publishResult.descMsg);
            }
            Log.e(TAG, publishResult.descMsg);
        }
        binding.publishProgressMark.setVisibility(View.GONE);
    }

    public void publish(String cover) {

        PublishBean bean = new PublishBean();
        if (locationBean!=null) {
            bean.address = locationBean.desName;
            bean.province = locationBean.province;
            bean.city = locationBean.city;
            bean.district = locationBean.district;
        }
        bean.latitude = lat + "";
        bean.longitude = lon + "";
        bean.shortVideoAuth = bean.getMode(publishMode);
        bean.shortVideoName = "小视频";
        bean.shortVideoCover = cover;
        bean.shortVideoUrl = shortVideoUrl;
        bean.shortVideoId = shortVideoId;
        bean.canRecommendFriends = isComment;

        PublishSummaryBean summaryBean = new PublishSummaryBean();

        PublishDesBean desBean = binding.desText.getDesStr();
        summaryBean.content = desBean.content;
        summaryBean.publishFocusItemBeans = desBean.publishFocusItemBeans;

        for (VideoPublishEditBean editBean : binding.desText.getAtList()) {
            PublishAtBean atBean = new PublishAtBean();
            atBean.name = editBean.getContent().replace("@", "");
            atBean.uid = editBean.getId() + "";
            summaryBean.atList.add(atBean);
        }
        for (VideoPublishEditBean editBean : binding.desText.getTopicList()) {
            summaryBean.topicList.add(editBean.getContent().replace("#", ""));
        }

        for (LiteAvUserBean userBean : unLookUserList) {
            bean.refuseUserIds.add(userBean.userId+"");
        }
        bean.summary = JSON.toJSONString(summaryBean);

        LogUtils.e(TAG, JSON.toJSONString(bean));
        //发布到自己服务器
        viewModel.UploadUGCVideo(bean);
    }


    public void uploadCover() {
        ImageUtils.compressWithRx(mCoverPath, new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                if (o == null) {
                    dismissDialog();
                    return;
                }
                viewModel.uploadFile(o.toString());
            }
        });
    }

    private void clearDrafts(){
        if (publishDraftId != 0) {
            //删除对应的草稿箱
            String fileName = UserInfoMgr.getInstance().getUserInfo().getId();
            viewModel.delDraftsById(this,fileName,publishDraftId);
        }
    }

    /** 视频发布 end */

    /**
     * 选中@或#后的弹窗
     */
    private void initListView() {
        binding.includeListTopic.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.includeListAt.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userAdapter = new ChooseUserAdapter();
        userAdapter.setList(liteAvUserBeans);
        userAdapter.setOnItemClickListener((adapter, view, position) -> {
            String name = liteAvUserBeans.get(position).nickname;
            binding.desText.setAtStr("@"+name,liteAvUserBeans.get(position).userId);
        });
        chooseTopicAdapter = new ChooseTopicAdapter(R.layout.item_publish_topic_view_adapter);
        chooseTopicAdapter.setList(listTopicBeans);
        chooseTopicAdapter.setOnItemClickListener((adapter, view, position) -> {
            String title = listTopicBeans.get(position).getName();
            binding.desText.setTopicStr("#"+title , 0 );//TODO 增加 topic ID
        });
        binding.includeListAt.recyclerView.setAdapter(userAdapter);
        binding.includeListTopic.recyclerView.setAdapter(chooseTopicAdapter);

        userAdapter.getLoadMoreModule().setEnableLoadMore(true);
        userAdapter.getLoadMoreModule().setAutoLoadMore(true);
        userAdapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //目前还没数据做分页
                if (singleSearchAt) {
                    //搜索好友,加载更多
                    viewModel.getMyAtList(atPage, atPageSize);
                }else{
                    //搜索用户,加载更多
                    viewModel.searchUserList(atPage, atPageSize, currentAt);
                }
            }
        });
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.requestSuccess.observe(this, aBoolean -> dismissDialog());
        viewModel.liteAvUserList.observe(this, liteAvUserBean -> {
            for (LiteAvUserBean avUserBean : liteAvUserBean) {
                avUserBean.readOnly = true;
                if (!singleSearchAt) {
                    //查询用户,只有ID,没有USerID,手动设置
                    avUserBean.userId = avUserBean.id;
                }
            }
            atPage++;
            this.liteAvUserBeans.addAll(liteAvUserBean);
            userAdapter.setList(this.liteAvUserBeans);
            if (liteAvUserBean.isEmpty() || liteAvUserBean.size() < atPageSize) {
                userAdapter.getLoadMoreModule().loadMoreEnd(false);
            }else{
                userAdapter.getLoadMoreModule().loadMoreComplete();
            }
        });
        viewModel.loginRoomSuccess.observe(this, aBoolean -> {

        });
        viewModel.publishSuccess.observe(this, aBoolean -> {
            if (aBoolean) {
                LogUtils.e(TAG,"发布视频成功");
                ToastUtils.showLong("发布成功");
                EventBus.getDefault().post(UGCKitConstants.EVENT_MSG_PUBLISH_DONE);
                NetworkUtil.getInstance(UGCKit.getAppContext()).unregisterNetChangeReceiver();
                clearDrafts();
                finish();
            }else{
                //发布失败
                LogUtils.e(TAG,"发布视频失败");
            }
        });
        viewModel.topicList.observe(this, topicBeans -> {
            //热点话题
            this.topicBeans.clear();
            this.topicBeans.addAll(topicBeans);
            publishTopicAdapter.setList(this.topicBeans);
        });
        viewModel.topicSearchList.observe(this, topicBeans -> {
            //热点话题
            this.listTopicBeans.clear();
            this.listTopicBeans.addAll(topicBeans);
            chooseTopicAdapter.setList(this.listTopicBeans);
        });
        viewModel.fileUpload.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                publish(s);
            }
        });
    }

    /**
     * 搜索话题
     */
    private void showTopicView(String str) {
        viewModel.getSearchTopic(str.replace("#", ""));
        binding.includeListTopic.recyclerView.getAdapter().notifyDataSetChanged();
        binding.includeListTopic.layoutPublishAt.setVisibility(View.VISIBLE);
    }

    private boolean singleSearchAt = false;
    private String currentAt;
    private void showAtView(String str) {
        atPage = 1;
        this.liteAvUserBeans.clear();
        if (str.equals("@")) {
            singleSearchAt = true;
            viewModel.getMyAtList(atPage, atPageSize);
        }
        else{
            //搜索
            singleSearchAt = false;
            currentAt = str.replace("@", "");
            viewModel.searchUserList(atPage, atPageSize, currentAt);
        }
        binding.includeListAt.layoutPublishAt.setVisibility(View.VISIBLE);
    }


    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK ) {
            if (requestCode == album_code) {
                String path = data.getStringExtra("path");
                Glide.with(this).asBitmap().load(path).into(binding.coverView);
                mCoverPath = path;
            } else if (requestCode == location_code) {
                LocationBean bean = (LocationBean) data.getSerializableExtra("bean");
                this.locationBean = bean;
                binding.includePublish.myLocation.setText(bean.desName);
                equalsLocation(bean);
            } else if (requestCode == unlook_code) {
                this.unLookUserList.clear();
                List<LiteAvUserBean> unLookUserList = (List<LiteAvUserBean>) data.getSerializableExtra("list");
                this.unLookUserList.addAll(unLookUserList);
                openModePop.refreshUnLook();
                binding.includePublish.openModeTv.setText(publishModeGetName(publishMode));
            }
        }
    }

    /**
     * 判断当前location地址有相同的地址
     * @param bean
     */
    private void equalsLocation(LocationBean bean) {
        if (locationBeans.isEmpty())return;
        for (LocationBean locationBean : locationBeans) {
            if (locationBean.desName.equals(bean.desName)) {
                //同一地址
                locationBean.isSelLocation = true;
                this.locationBean = locationBean;
            } else {
                locationBean.isSelLocation = false;
            }
        }
        publishLocationAdapter.notifyDataSetChanged();
    }

    /**
     * 开放权限
     */
    private void showOpenModeDialog() {
        if (openModePop == null) {
            openModePop = new LiteAvOpenModePopupWindow(this);
            openModePop.setComment(isComment);
            openModePop.setMode(publishMode);
            openModePop.setOnItemSelListener(new LiteAvOpenModePopupWindow.OnItemSelListener() {
                @Override
                public void onItemSel(int mode) {
                    publishMode = mode;
                    switch (mode) {
                        case LiteAvOpenModePopupWindow.HIDE_MODE: {
                            Intent intent = new Intent(VideoPublishActivity.this, ChooseUnLookActivity.class);
                            intent.putExtra(LiteAvConstant.UnLookList, (Serializable) unLookUserList);
                            startActivityForResult(intent,unlook_code);
                        }break;
                    }
                    binding.includePublish.openModeTv.setText(publishModeGetName(publishMode));
                }

                @Override
                public void onSwitchChange(boolean comment) {
                    isComment = comment;
                }
            });

        }
        openModePop.setUnLookUserList(this.unLookUserList);
        openModePop.showPopupWindow();
    }

    public String publishModeGetName(int mode) {
        switch (mode) {
            case LiteAvOpenModePopupWindow.OPEN_MODE:
                return "粉丝可见·已开启私密账号";
            case LiteAvOpenModePopupWindow.FRIEND_MODE:
                return "朋友可见";
            case LiteAvOpenModePopupWindow.PRIVATE_MODE:
                return "仅自己可见";
            case LiteAvOpenModePopupWindow.HIDE_MODE:{
                if (unLookUserList.isEmpty()) {
                    return null;
                }
                return "不给谁看:" + unLookUserList.get(0).nickname + (unLookUserList.size() > 1 ? "等" + unLookUserList.size() + "人" : "");
            }
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean check = PermissionUtils.checkPermissionAllGranted(this, permissions);
        if (check) {
            LocationService.startService(this);
        }else{
            ToastUtils.showShort("定位权限缺失");
        }
    }



    /** 定位部分 begin*/
    private void checkPermission() {
        boolean check = PermissionUtils.checkPermission(this,  new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION ,
        });
        if (check) {
            LocationService.startService(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventDisposable.dispose();

    }


    /**
     * 定位成功后，获取周边地址列表
     */
    private void getAddressList() {
        PoiSearch.Query query = new PoiSearch.Query(poiName, "", cityCode);
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(1);//设置查询页码
        PoiSearch poiSearch = new PoiSearch(this,query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(lat, lon), 1000));//设置周边搜索的中心点以及半径
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        locationBeans.clear();
        int index = 0;
        for (PoiItem item : poiResult.getPois()) {
            if (index == 6){
                break;
            }
            LocationBean bean = new LocationBean();
            bean.address = item.getSnippet();
            bean.desName = item.getTitle();
            bean.distance = item.getDistance() + "";
            bean.province = item.getProvinceName();
            bean.city = item.getCityName();
            bean.district = item.getAdName();
            if (item.getLatLonPoint() != null) {
                bean.lat = item.getLatLonPoint().getLatitude();
                bean.lon = item.getLatLonPoint().getLongitude();
            }
            locationBeans.add(bean);
            index++;
        }
        //添加查看更多
        LocationBean bean = new LocationBean();
        bean.isLookMore = true;
        bean.desName = "查看更多";
        bean.address = "查看更多";
        locationBeans.add(bean);
        publishLocationAdapter.setList(locationBeans);
        if (this.locationBean!=null)
            equalsLocation(locationBean);
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    /** 定位部分 end*/

    /** 保存草稿箱 begin*/
    /**
     * 从草稿箱获取内容
     */
    private void getDraftContent() {
        String fileName = UserInfoMgr.getInstance().getUserInfo().getId();
        SaveDraftBean bean = viewModel.getDraftsById(this,fileName,publishDraftId);
        if (bean == null) {
            return;
        }
        //文本
        PublishDesBean publishDesBean = new PublishDesBean();
        publishDesBean.content = bean.getContent();
        publishDesBean.publishFocusItemBeans = bean.getPublishFocusItemBeans();
        binding.desText.initDesStr(publishDesBean);
        //视频
        mVideoPath = bean.getVideoPath();
        //封面
        mCoverPath = bean.getCoverPath();
        //定位
        lon = bean.getLon();
        lat = bean.getLat();
        locationBean = new LocationBean();
        locationBean.desName = bean.getAddress();
        locationBean.lon = lon;
        locationBean.lat = lat;
        binding.includePublish.myLocation.setText(bean.getAddress());

        //权限
        publishMode = bean.getOpenMode();
        //推荐
        isComment = bean.isComment();
    }

    /**
     * 创建草稿箱内容
     */
    private void createDraftContent() {
        //生成id，发布内容，视频path，封面path，定位，权限，禁止查看列表，草稿箱时间，是否推荐，TODO 音乐，
        String fileName = UserInfoMgr.getInstance().getUserInfo().getId();

        PublishDesBean publishDesBean = binding.desText.getDesStr();
        SaveDraftBean saveDraftBean;
        if (publishDraftId > 0) {
            saveDraftBean = viewModel.getDraftsById(this,fileName,publishDraftId);
        }else{
            saveDraftBean = new SaveDraftBean();
            saveDraftBean.setId(System.currentTimeMillis() / 1000);
        }

        saveDraftBean.setContent(publishDesBean.content);
        saveDraftBean.setPublishFocusItemBeans(publishDesBean.publishFocusItemBeans);

        saveDraftBean.setCoverPath(mCoverPath);
        saveDraftBean.setVideoPath(mVideoPath);
        if (locationBean!=null) {
            saveDraftBean.setAddress(locationBean.desName);
            saveDraftBean.setLon(lon);
            saveDraftBean.setLat(lat);
            saveDraftBean.setProvince(locationBean.province);
            saveDraftBean.setCity(locationBean.city);
            saveDraftBean.setDistrict(locationBean.district);
        }
        saveDraftBean.setOpenMode(publishMode);
        saveDraftBean.setUnLookUser(unLookUserList);
        saveDraftBean.setComment(isComment);

        saveDraftBean.setSaveTime(System.currentTimeMillis());

        viewModel.saveDraftsById(this, fileName, saveDraftBean);

        LogUtils.e(TAG, "保存到草稿箱的ID -> " + saveDraftBean.getId());
        finish();
    }



    /** 保存草稿箱 end*/

}
