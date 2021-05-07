package com.xaqinren.healthyelders.moduleLiteav.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.gson.Gson;
import com.nostra13.dcloudimageloader.utils.L;
import com.tencent.bugly.proguard.B;
import com.tencent.qcloud.ugckit.UGCKitConstants;
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK;
import com.tencent.ugc.TXVideoEditer;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.ActivityVideoPublishBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleLiteav.adapter.ChooseTopicAdapter;
import com.xaqinren.healthyelders.moduleLiteav.adapter.ChooseUserAdapter;
import com.xaqinren.healthyelders.moduleLiteav.adapter.PublishLocationAdapter;
import com.xaqinren.healthyelders.moduleLiteav.adapter.PublishTopicAdapter;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.LocationBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.TopicBean;
import com.xaqinren.healthyelders.moduleLiteav.service.LocationService;
import com.xaqinren.healthyelders.moduleLiteav.viewModel.VideoPublishViewModel;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.widget.LiteAvOpenModePopupWindow;
import com.xaqinren.healthyelders.widget.SpeacesItemDecoration;
import com.xaqinren.healthyelders.widget.VideoPublishEditTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.KeyBoardUtils;
import me.goldze.mvvmhabit.utils.PermissionUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * 发布
 */
public class VideoPublishActivity extends BaseActivity<ActivityVideoPublishBinding, VideoPublishViewModel> implements PoiSearch.OnPoiSearchListener {
    private String mVideoPath = null;
    private String mCoverPath = null;
    private boolean mDisableCache;
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
    private int album_code = 666;
    private int location_code = 777;

    //定位信息
    private double lat, lon;
    private String poiName;
    private String cityCode;
    private String cityName;

    //当前定位
    LocationBean locationBean;
    private Disposable eventDisposable;

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
        testData();
        checkPermission();
        mVideoPath = getIntent().getStringExtra(UGCKitConstants.VIDEO_PATH);
        mCoverPath = getIntent().getStringExtra(UGCKitConstants.VIDEO_COVERPATH);
        mDisableCache = getIntent().getBooleanExtra(UGCKitConstants.VIDEO_RECORD_NO_CACHE, false);
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
        Random random = new Random();
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
            binding.desText.setTopicStr("#"+topicBean.topicTitle);
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
        binding.addTopic.setOnClickListener(view -> {
            binding.desText.append("#");
        });
        binding.addFriend.setOnClickListener(view -> {
            binding.desText.append("@");
        });

        initListView();
    }

    /**
     * 选中@或#后的弹窗
     */
    private void initListView() {
        binding.includeListTopic.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.includeListAt.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userAdapter = new ChooseUserAdapter();
        userAdapter.setList(liteAvUserBeans);
        userAdapter.setOnItemClickListener((adapter, view, position) -> {
            String name = liteAvUserBeans.get(position).name;
            binding.desText.setAtStr("@"+name);
            binding.desText.addBlackKey();
        });
        chooseTopicAdapter = new ChooseTopicAdapter(R.layout.item_publish_topic_view_adapter);
        chooseTopicAdapter.setList(listTopicBeans);
        chooseTopicAdapter.setOnItemClickListener((adapter, view, position) -> {
            String title = listTopicBeans.get(position).topicTitle;
            binding.desText.setTopicStr("#"+title);
            binding.desText.addBlackKey();
        });
        binding.includeListAt.recyclerView.setAdapter(userAdapter);
        binding.includeListTopic.recyclerView.setAdapter(chooseTopicAdapter);
    }

    private void showTopicView(String str) {
        //TODO 调用接口
        binding.includeListTopic.recyclerView.getAdapter().notifyDataSetChanged();
        binding.includeListTopic.layoutPublishAt.setVisibility(View.VISIBLE);
    }
    private void showAtView(String str) {
        //TODO 调用接口
        binding.includeListAt.recyclerView.getAdapter().notifyDataSetChanged();
        binding.includeListAt.layoutPublishAt.setVisibility(View.VISIBLE);
    }


    @Override
    public void onBackPressed() {
        TXVideoEditer editer = VideoEditerSDK.getInstance().getEditer();
        if (editer == null) {
            VideoEditerSDK.getInstance().initSDK();
        }
        super.onBackPressed();
    }

    private void testData() {
        topicBeans.add(new TopicBean("#你好呀"));
        topicBeans.add(new TopicBean("#你好呀"));
        topicBeans.add(new TopicBean("#你好呀"));

        //testData
        for (int i = 0; i < 10; i++) {
            LiteAvUserBean userBean = new LiteAvUserBean("你好" + i, "avatar", i);
            userBean.viewType = 1;
            userBean.readOnly = true;
            liteAvUserBeans.add(userBean);
            TopicBean topicBean = new TopicBean("这是一个热点"+i);
            listTopicBeans.add(topicBean);
        }
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
                binding.includePublish.myLocation.setText(bean.desName);
                equalsLocation(bean);
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
            if (locationBean.lat == bean.lat && locationBean.lon == bean.lon) {
                //同一地址
                locationBean.isSelLocation = true;
                this.locationBean = locationBean;
            }else {
                locationBean.isSelLocation = false;
            }
        }
        publishLocationAdapter.notifyDataSetChanged();
    }

    /**
     * 展示开放权限弹窗
     */
    private void showOpenModeDialog() {
        if (openModePop == null) {
            openModePop = new LiteAvOpenModePopupWindow(this);
            openModePop.setOnItemSelListener(new LiteAvOpenModePopupWindow.OnItemSelListener() {
                @Override
                public void onItemSel(int mode) {
                    switch (mode) {
                        case LiteAvOpenModePopupWindow.HIDE_MODE: {
                            Intent intent = new Intent(VideoPublishActivity.this, ChooseUnLookActivity.class);
                            startActivity(intent);
                        }break;
                    }
                }
            });

        }
        openModePop.showPopupWindow();
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
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    /** 定位部分 end*/

}
