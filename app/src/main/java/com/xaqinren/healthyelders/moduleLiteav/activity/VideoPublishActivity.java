package com.xaqinren.healthyelders.moduleLiteav.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.tencent.qcloud.tim.uikit.utils.FileUtil;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.tencent.qcloud.ugckit.UGCKit;
import com.tencent.qcloud.ugckit.UGCKitConstants;
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK;
import com.tencent.qcloud.ugckit.module.upload.TXUGCPublish;
import com.tencent.qcloud.ugckit.module.upload.TXUGCPublishTypeDef;
import com.tencent.qcloud.ugckit.utils.LogReport;
import com.tencent.qcloud.ugckit.utils.NetworkUtil;
import com.tencent.qcloud.ugckit.utils.Signature;
import com.tencent.qcloud.ugckit.utils.TCUserMgr;
import com.tencent.ugc.TXVideoEditConstants;
import com.tencent.ugc.TXVideoEditer;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.MainActivity;
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
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishSummaryBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.SaveDraftBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.TopicBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.VideoPublishEditBean;
import com.xaqinren.healthyelders.moduleLiteav.liteAv.LiteAvConstant;
import com.xaqinren.healthyelders.moduleLiteav.liteAv.MusicRecode;
import com.xaqinren.healthyelders.moduleLiteav.service.LocationService;
import com.xaqinren.healthyelders.moduleLiteav.viewModel.VideoPublishViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.utils.RetrieverUtils;
import com.xaqinren.healthyelders.widget.YesOrNoDialog;
import com.xaqinren.healthyelders.widget.LiteAvOpenModePopupWindow;
import com.xaqinren.healthyelders.widget.VideoPublishEditTextView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.ImageUtils;
import me.goldze.mvvmhabit.utils.KeyBoardUtils;
import me.goldze.mvvmhabit.utils.PermissionUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * ??????
 */
public class VideoPublishActivity extends BaseActivity<ActivityVideoPublishBinding, VideoPublishViewModel> implements PoiSearch.OnPoiSearchListener,
        TXUGCPublishTypeDef.ITXVideoPublishListener {

    private LiteAvOpenModePopupWindow openModePop;
    //?????????????????????
    private PublishTopicAdapter publishTopicAdapter;
    private List<TopicBean> topicBeans = new ArrayList<>();
    private PublishLocationAdapter publishLocationAdapter;
    private List<LocationBean> locationBeans = new ArrayList<>();
    private String TAG = "VideoPublishActivity";
    //@????????????adapter
    private ChooseUserAdapter userAdapter;
    //@????????????
    private List<LiteAvUserBean> liteAvUserBeans = new ArrayList<>();
    //?????????????????????#?????????
    private ChooseTopicAdapter chooseTopicAdapter;
    //????????????
    private List<TopicBean> listTopicBeans = new ArrayList<>();

    private int album_code = 666;  //??????
    private int location_code = 777;//??????
    private int unlook_code = 888;//??????

    private String mVideoPath = null;
    private String mCoverPath = null;
    private boolean mDisableCache;
    //????????????
    private double lat, lon;
    private String poiName;
    private String cityCode;
    private String cityName;
    //????????????
    LocationBean locationBean;
    private Disposable eventDisposable;
    //??????????????????
    private List<LiteAvUserBean> unLookUserList = new ArrayList<>();
    //?????????Id
    private long publishDraftId;
    //????????????
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
        setTitle("??????");
        eventDisposable = RxBus.getDefault().toObservable(EventBean.class).subscribe(o -> {
            if (o.msgId == CodeTable.LOCATION_SUCCESS) {
                //????????????
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
            //?????????????????????
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
        //????????????
        binding.selCover.setOnClickListener(view -> {
            Intent intent = new Intent(this, ChooseVideoCoverActivity.class);
            intent.putExtra(UGCKitConstants.VIDEO_PATH, mVideoPath);
            if (!TextUtils.isEmpty(mCoverPath)) {
                intent.putExtra(UGCKitConstants.VIDEO_COVERPATH, mCoverPath);
            }
            startActivityForResult(intent, album_code);
        });
        //??????????????????
        binding.desText.setInputMax(55);
        //????????????
        binding.includePublish.locationLayout.setOnClickListener(view -> {
            Intent intent = new Intent(this, ChooseLocationActivity.class);
            startActivityForResult(intent, location_code);
        });
        binding.includePublish.videoOpenModeLayout.setOnClickListener(view -> {
            //????????????
            KeyBoardUtils.hideKeyBoard(this, view.getWindowToken());
            showOpenModeDialog();
        });
        //??????????????????
        binding.includePublish.saveDraftBtn.setOnClickListener(view -> {
            YesOrNoDialog yesOrNoDialog = new YesOrNoDialog(this);
            yesOrNoDialog.setMessageText("??????????????????????????????");
            yesOrNoDialog.showDialog();
            yesOrNoDialog.setRightBtnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    yesOrNoDialog.dismissDialog();
                    createDraftContent();
                }
            });

        });
        binding.includePublish.publishBtn.setOnClickListener(view -> {
            //????????????
            if (TextUtils.isEmpty(binding.desText.getText().toString().trim())) {
                ToastUtil.toastShortMessage("???????????????");
                return;
            }
            publishVideo();
        });
        binding.desText.setOnTextChangeListener(new VideoPublishEditTextView.OnTextChangeListener() {
            @Override
            public void inputTopic(String str) {
                LogUtils.e(TAG, "inputTopic ?????????????????? -> " + str);
                showTopicView(str);
            }

            @Override
            public void inputNoTopic() {
                LogUtils.e(TAG, "inputNoTopic ?????????????????? -> ");
                binding.includeListTopic.layoutPublishAt.setVisibility(View.GONE);
            }

            @Override
            public void inputAt(String str) {
                LogUtils.e(TAG, "inputTopic ??????@?????? -> " + str);
                showAtView(str);
            }

            @Override
            public void inputNoAt() {
                LogUtils.e(TAG, "inputNoTopic ??????@?????? -> ");
                binding.includeListAt.layoutPublishAt.setVisibility(View.GONE);
            }

            @Override
            public void maxInput() {
                LogUtils.e(TAG, "inputNoTopic ????????????????????? -> ");
                ToastUtils.showShort("????????????" + binding.desText.getInputMax() + "?????????");
            }
        });
        publishTopicAdapter.setOnItemClickListener((adapter, view, position) -> {
            TopicBean topicBean = topicBeans.get(position);
            binding.desText.append("#" + topicBean.getName());
            binding.desText.addBlackKey();
        });
        publishLocationAdapter.setOnItemClickListener((adapter, view, position) -> {
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
        //        Glide.with(this).asBitmap().load(mCoverPath).into(binding.coverView);
        initListView();
//        loginUser();
        //????????????
        viewModel.getHotTopic();

        RetrieverUtils.cropBitmap(mCoverPath, new RetrieverUtils.OnBitmapCall() {
            @Override
            public void onBack(String bitmap) {
                mCoverPath = bitmap;
                Glide.with(getActivity()).asBitmap().load(mCoverPath).into(binding.coverView);
            }
        });
    }


    /**
     * ???????????? begin
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
            LogReport.getInstance().uploadLogs(LogReport.ELK_ACTION_VIDEO_SIGN, TCUserMgr.SUCCESS_CODE, "??????????????????");
            startPublish();
        } else {
            LogReport.getInstance().uploadLogs(LogReport.ELK_ACTION_VIDEO_SIGN, 0, "??????????????????");
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
                 * ???????????????????????????
                 */
                mVideoPublish.setListener(VideoPublishActivity.this);

                TXUGCPublishTypeDef.TXPublishParam param = new TXUGCPublishTypeDef.TXPublishParam();
                param.signature = mCosSignature;
                param.videoPath = mVideoPath;
                param.coverPath = mCoverPath;
                int publishCode = mVideoPublish.publishVideo(param);
                binding.publishProgressMark.setVisibility(View.VISIBLE);
                if (publishCode != 0) {
                    //                    mTVPublish.setText("???????????????????????????" + publishCode);
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
         * ELK??????????????????????????????????????????
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
        if (locationBean != null) {
            bean.address = locationBean.desName;
            bean.province = locationBean.province;
            bean.city = locationBean.city;
            bean.district = locationBean.district;
        }
        bean.latitude = lat + "";
        bean.longitude = lon + "";
        bean.creationViewAuth = bean.getMode(publishMode);
        bean.shortVideoName = "?????????";
        bean.shortVideoCover = cover;
        bean.shortVideoUrl = shortVideoUrl;
        bean.shortVideoId = shortVideoId;
        bean.canRecommendFriends = isComment;

        if (MusicRecode.getInstance().getUseMusicItem() != null) {
            bean.musicId = MusicRecode.getInstance().getUseMusicItem().getId();
            bean.musicName = MusicRecode.getInstance().getUseMusicItem().name;
        }

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
            bean.refuseUserIds.add(userBean.getId());
        }
        bean.summary = summaryBean;

        LogUtils.e(TAG, JSON.toJSONString(bean));
        //????????????????????????
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

    private void clearDrafts() {
        if (publishDraftId != 0) {
            //????????????????????????
            String fileName = UserInfoMgr.getInstance().getUserInfo().getId();
            viewModel.delDraftsById(this, fileName, publishDraftId);
        }
    }

    /** ???????????? end */

    /**
     * ??????@???#????????????
     */
    private void initListView() {
        binding.includeListTopic.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.includeListAt.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userAdapter = new ChooseUserAdapter();
        userAdapter.setList(liteAvUserBeans);
        userAdapter.setOnItemClickListener((adapter, view, position) -> {
            String name = liteAvUserBeans.get(position).getName();
            binding.desText.setAtStr("@" + name, liteAvUserBeans.get(position).getId());
        });
        chooseTopicAdapter = new ChooseTopicAdapter(R.layout.item_publish_topic_view_adapter);
        chooseTopicAdapter.setList(listTopicBeans);
        chooseTopicAdapter.setOnItemClickListener((adapter, view, position) -> {
            String title = listTopicBeans.get(position).getName();
            binding.desText.setTopicStr("#" + title, 0);//TODO ?????? topic ID
        });
        binding.includeListAt.recyclerView.setAdapter(userAdapter);
        binding.includeListTopic.recyclerView.setAdapter(chooseTopicAdapter);

        userAdapter.getLoadMoreModule().setEnableLoadMore(true);
        userAdapter.getLoadMoreModule().setAutoLoadMore(true);
        userAdapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //???????????????????????????
                if (singleSearchAt) {
                    //????????????,????????????
                    viewModel.getMyAtList(atPage, atPageSize);
                } else {
                    //????????????,????????????
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
                    //????????????,??????ID,??????USerID,????????????
                    avUserBean.setId(avUserBean.getId());
                }
            }
            atPage++;
            this.liteAvUserBeans.addAll(liteAvUserBean);
            userAdapter.setList(this.liteAvUserBeans);
            if (liteAvUserBean.isEmpty() || liteAvUserBean.size() < atPageSize) {
                userAdapter.getLoadMoreModule().loadMoreEnd(false);
            } else {
                userAdapter.getLoadMoreModule().loadMoreComplete();
            }
        });
        viewModel.loginRoomSuccess.observe(this, aBoolean -> {

        });
        viewModel.publishSuccess.observe(this, aBoolean -> {
            if (aBoolean) {
                LogUtils.e(TAG, "??????????????????");
                ToastUtils.showLong("????????????");
                EventBus.getDefault().post(UGCKitConstants.EVENT_MSG_PUBLISH_DONE);
                NetworkUtil.getInstance(UGCKit.getAppContext()).unregisterNetChangeReceiver();
                MusicRecode.getInstance().setUseMusicItem(null);
                clearDrafts();
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constant.PUBLISH_SUCCESS, true);
                startActivity(MainActivity.class, bundle);
                overridePendingTransition(R.anim.activity_push_none, R.anim.activity_right_2exit);
            } else {
                //????????????
                LogUtils.e(TAG, "??????????????????");
            }
        });
        viewModel.topicList.observe(this, topicBeans -> {
            //????????????
            this.topicBeans.clear();
            this.topicBeans.addAll(topicBeans);
            publishTopicAdapter.setList(this.topicBeans);
        });
        viewModel.topicSearchList.observe(this, topicBeans -> {
            //????????????
            this.listTopicBeans.clear();
            this.listTopicBeans.addAll(topicBeans);
            if (this.listTopicBeans.isEmpty()) {
                this.listTopicBeans.add(new TopicBean(currentTopicStr));
            }
            chooseTopicAdapter.setList(this.listTopicBeans);
        });
        viewModel.fileUpload.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                publish(s);
            }
        });
    }

    private String currentTopicStr = "";

    /**
     * ????????????
     */
    private void showTopicView(String str) {
        currentTopicStr = str.replace("#", "");
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
        } else {
            //??????
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
        if (resultCode == Activity.RESULT_OK) {
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
                if (openModePop != null && openModePop.isShowing()) {
                    openModePop.dismiss();
                }
            }
        }
    }

    /**
     * ????????????location????????????????????????
     *
     * @param bean
     */
    private void equalsLocation(LocationBean bean) {
        if (locationBeans.isEmpty())
            return;
        for (LocationBean locationBean : locationBeans) {
            if (locationBean.desName.equals(bean.desName)) {
                //????????????
                locationBean.isSelLocation = true;
                this.locationBean = locationBean;
            } else {
                locationBean.isSelLocation = false;
            }
        }
        publishLocationAdapter.notifyDataSetChanged();
    }

    /**
     * ????????????
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
                            startActivityForResult(intent, unlook_code);
                            overridePendingTransition(R.anim.activity_bottom_2enter, R.anim.activity_push_none);
                        }
                        break;
                    }
                    binding.includePublish.openModeTv.setText(publishModeGetName(publishMode));
                    switch (publishMode) {
                        case LiteAvOpenModePopupWindow.OPEN_MODE:
                            binding.includePublish.ivOpen.setBackground(getResources().getDrawable(R.mipmap.icon_quanx_gk));
                            break;
                        case LiteAvOpenModePopupWindow.FRIEND_MODE:
                            binding.includePublish.ivOpen.setBackground(getResources().getDrawable(R.mipmap.icon_quanx_peny));
                            break;
                        case LiteAvOpenModePopupWindow.PRIVATE_MODE:
                            binding.includePublish.ivOpen.setBackground(getResources().getDrawable(R.mipmap.icon_quanx_sim));
                            break;
                    }
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
                return "?????? ?? ???????????????";
            case LiteAvOpenModePopupWindow.FRIEND_MODE:
                return "?????? ?? ??????????????????";
            case LiteAvOpenModePopupWindow.PRIVATE_MODE:
                return "?????? ?? ???????????????";
            case LiteAvOpenModePopupWindow.HIDE_MODE: {
                if (unLookUserList.isEmpty()) {
                    return null;
                }
                return "????????????: " + (unLookUserList.size() > 1 ? unLookUserList.get(0).getName() + "..???" + unLookUserList.size() + "???" : unLookUserList.get(0).getName());
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
        } else {
            ToastUtils.showShort("??????????????????");
        }
    }


    /**
     * ???????????? begin
     */
    private void checkPermission() {
        boolean check = PermissionUtils.checkPermission(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
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
     * ??????????????????????????????????????????
     */
    private void getAddressList() {
        PoiSearch.Query query = new PoiSearch.Query(poiName, "", cityCode);
        query.setPageSize(20);// ?????????????????????????????????poiitem
        query.setPageNum(1);//??????????????????
        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(lat, lon), 1000));//??????????????????????????????????????????
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        locationBeans.clear();
        int index = 0;
        for (PoiItem item : poiResult.getPois()) {
            if (index == 6) {
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
        //??????????????????
        LocationBean bean = new LocationBean();
        bean.isLookMore = true;
        bean.desName = "????????????";
        bean.address = "????????????";
        locationBeans.add(bean);
        publishLocationAdapter.setList(locationBeans);
        if (this.locationBean != null)
            equalsLocation(locationBean);
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    /** ???????????? end*/

    /** ??????????????? begin*/
    /**
     * ????????????????????????
     */
    private void getDraftContent() {
        String fileName = UserInfoMgr.getInstance().getUserInfo().getId();
        SaveDraftBean bean = viewModel.getDraftsById(this, fileName, publishDraftId);
        if (bean == null) {
            return;
        }
        //??????
        PublishDesBean publishDesBean = new PublishDesBean();
        publishDesBean.content = bean.getContent();
        publishDesBean.publishFocusItemBeans = bean.getPublishFocusItemBeans();
        binding.desText.initDesStr(publishDesBean);
        //??????
        mVideoPath = bean.getVideoPath();
        //??????
        mCoverPath = bean.getCoverPath();
        //??????
        lon = bean.getLon();
        lat = bean.getLat();
        locationBean = new LocationBean();
        locationBean.desName = bean.getAddress();
        locationBean.lon = lon;
        locationBean.lat = lat;
        binding.includePublish.myLocation.setText(bean.getAddress());
        unLookUserList = bean.getUnLookUser();
        //??????
        publishMode = bean.getOpenMode();
        //??????
        isComment = bean.isComment();
    }

    /**
     * ?????????????????????
     */
    private void createDraftContent() {
        //??????id????????????????????????path?????????path???????????????????????????????????????????????????????????????????????????TODO ?????????
        String fileName = UserInfoMgr.getInstance().getUserInfo().getId();

        PublishDesBean publishDesBean = binding.desText.getDesStr();
        SaveDraftBean saveDraftBean;
        if (publishDraftId > 0) {
            saveDraftBean = viewModel.getDraftsById(this, fileName, publishDraftId);
        } else {
            saveDraftBean = new SaveDraftBean();
            saveDraftBean.setId(System.currentTimeMillis() / 1000);
        }

        saveDraftBean.setContent(publishDesBean.content);
        saveDraftBean.setPublishFocusItemBeans(publishDesBean.publishFocusItemBeans);

        saveDraftBean.setCoverPath(mCoverPath);
        saveDraftBean.setVideoPath(mVideoPath);
        if (locationBean != null) {
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
        if (MusicRecode.getInstance().getUseMusicItem() != null) {
            saveDraftBean.setMusicId(MusicRecode.getInstance().getUseMusicItem().getId());
            saveDraftBean.setMusicName(MusicRecode.getInstance().getUseMusicItem().name);
        }

        saveDraftBean.setSaveTime(System.currentTimeMillis());

        viewModel.saveDraftsById(this, fileName, saveDraftBean);
        List<LiteAvUserBean> unLookUser = saveDraftBean.getUnLookUser();
        if (unLookUser == null) {
            unLookUser = new ArrayList<>();
        }
        unLookUser.addAll(this.unLookUserList);
        saveDraftBean.setUnLookUser(unLookUser);

        LogUtils.e(TAG, "?????????????????????ID -> " + saveDraftBean.getId());
        ToastUtils.showShort("????????????");
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constant.PUBLISH_SUCCESS, true);
        startActivity(MainActivity.class, bundle);
        overridePendingTransition(R.anim.activity_push_none, R.anim.activity_right_2exit);
    }


    /** ??????????????? end*/

}
