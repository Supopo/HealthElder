package com.xaqinren.healthyelders.moduleLiteav.activity;

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
import com.xaqinren.healthyelders.databinding.ActivityVideoPublishBinding;
import com.xaqinren.healthyelders.moduleLiteav.adapter.ChooseTopicAdapter;
import com.xaqinren.healthyelders.moduleLiteav.adapter.ChooseUserAdapter;
import com.xaqinren.healthyelders.moduleLiteav.adapter.PublishLocationAdapter;
import com.xaqinren.healthyelders.moduleLiteav.adapter.PublishTopicAdapter;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.LocationBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.TopicBean;
import com.xaqinren.healthyelders.moduleLiteav.viewModel.VideoPublishViewModel;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.widget.LiteAvOpenModePopupWindow;
import com.xaqinren.healthyelders.widget.SpeacesItemDecoration;
import com.xaqinren.healthyelders.widget.VideoPublishEditTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * 发布
 */
public class VideoPublishActivity extends BaseActivity<ActivityVideoPublishBinding, VideoPublishViewModel> {
    private String mVideoPath = null;
    private String mCoverPath = null;
    private boolean mDisableCache;
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
        testData();
        mVideoPath = getIntent().getStringExtra(UGCKitConstants.VIDEO_PATH);
        mCoverPath = getIntent().getStringExtra(UGCKitConstants.VIDEO_COVERPATH);
        mDisableCache = getIntent().getBooleanExtra(UGCKitConstants.VIDEO_RECORD_NO_CACHE, false);
        publishTopicAdapter = new PublishTopicAdapter(R.layout.item_publish_topic_adapter);
        LinearLayoutManager topManager = new LinearLayoutManager(this);
        topManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.topicList.setLayoutManager(topManager);
        binding.topicList.setAdapter(publishTopicAdapter);
        publishTopicAdapter.addData(topicBeans);

        publishLocationAdapter = new PublishLocationAdapter(R.layout.item_publish_location);
        LinearLayoutManager locManager = new LinearLayoutManager(this);
        locManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.includePublish.locationList.setLayoutManager(locManager);
        binding.includePublish.locationList.setAdapter(publishLocationAdapter);
        publishLocationAdapter.addData(locationBeans);
        //选择封面
        Random random = new Random();
        binding.selCover.setOnClickListener(view -> {
            Intent intent = new Intent(this, ChooseVideoCoverActivity.class);
            intent.putExtra(UGCKitConstants.VIDEO_PATH, mVideoPath);
            if (!TextUtils.isEmpty(mCoverPath)) {
                intent.putExtra(UGCKitConstants.VIDEO_COVERPATH, mCoverPath);
            }
            startActivityForResult(intent, 666);

//            binding.desText.setAtStr("@你好" + random.nextInt(1000));
//            binding.desText.addBlackKey();

        });
        //设置最大输入
        binding.desText.setInputMax(55);
        //选择地址
        binding.includePublish.locationLayout.setOnClickListener(view -> {
            Intent intent = new Intent(this, ChooseLocationActivity.class);
            startActivityForResult(intent, 777);
        });
        binding.includePublish.videoOpenModeLayout.setOnClickListener(view -> {
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
        binding.includeListTopic.recyclerView.getAdapter().notifyDataSetChanged();
        binding.includeListTopic.layoutPublishAt.setVisibility(View.VISIBLE);
    }
    private void showAtView(String str) {
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

        locationBeans.add(new LocationBean("收到了方式砥砺奋斗"));
        locationBeans.add(new LocationBean("收到了方式砥砺奋斗"));
        locationBeans.add(new LocationBean("收到了方式砥砺奋斗"));
        locationBeans.add(new LocationBean("收到了方式砥砺奋斗"));

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
        if (resultCode == Activity.RESULT_OK && requestCode == 666) {
            String path = data.getStringExtra("path");
            Glide.with(this).asBitmap().load(path).into(binding.coverView);
            mCoverPath = path;
        }
    }
    private LiteAvOpenModePopupWindow openModePop;
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


}
