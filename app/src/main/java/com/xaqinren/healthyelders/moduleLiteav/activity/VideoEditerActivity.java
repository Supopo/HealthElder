package com.xaqinren.healthyelders.moduleLiteav.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.tencent.qcloud.ugckit.UGCKitConstants;
import com.tencent.qcloud.ugckit.UGCKitVideoEdit;
import com.tencent.qcloud.ugckit.basic.UGCKitResult;
import com.tencent.qcloud.ugckit.component.dialog.ActionSheetDialog;
import com.tencent.qcloud.ugckit.component.dialogfragment.ProgressFragmentUtil;
import com.tencent.qcloud.ugckit.module.PlayerManagerKit;
import com.tencent.qcloud.ugckit.module.VideoGenerateKit;
import com.tencent.qcloud.ugckit.module.editer.IVideoEditKit;
import com.tencent.qcloud.ugckit.module.editer.UGCKitEditConfig;
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK;
import com.tencent.qcloud.ugckit.utils.ToastUtil;
import com.tencent.qcloud.xiaoshipin.mainui.TCMainActivity;
import com.tencent.qcloud.xiaoshipin.videoeditor.TCVideoEffectActivity;
import com.tencent.qcloud.xiaoshipin.videopublish.TCVideoPublisherActivity;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.MainActivity;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityVideoEditerBinding;
import com.xaqinren.healthyelders.moduleLiteav.viewModel.VideoEditerViewModel;

import me.goldze.mvvmhabit.base.BaseActivity;

public class VideoEditerActivity extends BaseActivity<ActivityVideoEditerBinding, VideoEditerViewModel> implements View.OnClickListener {
    private static final String TAG = "TCVideoEditerActivity";
    /**
     * 视频路径
     */
    private String mVideoPath;
    private UGCKitVideoEdit mUGCKitVideoEdit;
    // 背景音
    private LinearLayout mTvBgm;
    // 动态滤镜
    private LinearLayout mTvMotion;
    // 时间特效
    private LinearLayout mTvSpeed;
    // 静态滤镜
    private LinearLayout mTvFilter;
    // 贴纸
    private LinearLayout mTvPaster;
    // 字幕
    private LinearLayout mTvSubtitle;
    // 返回键
    private ImageView backIv;
    private boolean              mIsPublish;


    private IVideoEditKit.OnEditListener mOnVideoEditListener = new IVideoEditKit.OnEditListener() {
        @Override
        public void onEditCompleted(UGCKitResult ugcKitResult) {
            if (ugcKitResult.errorCode == 0) {
                startPreviewActivity(ugcKitResult);
            } else {
                ToastUtil.toastShortMessage("edit video failed. error code:" + ugcKitResult.errorCode + ",desc msg:" + ugcKitResult.descMsg);
            }
        }

        @Override
        public void onEditCanceled() {
            finish();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initWindowParam();
        setTheme(com.hjyy.liteav.R.style.EditerActivityTheme);
        super.onCreate(savedInstanceState);
        setStatusBarTransparent();
    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_video_editer;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        initView();
    }

    private void initView() {
        mUGCKitVideoEdit = binding.videoEdit;
        mTvBgm = binding.selMusic;
        mTvMotion = binding.actionLayout;
        mTvSpeed = binding.speedLayout;
        mTvFilter = binding.beautyLayout;
        mTvPaster = binding.tiezhiLayout;
        mTvSubtitle = binding.zimuLayout;
        backIv = binding.back;

        UGCKitEditConfig config = new UGCKitEditConfig();
        config.isPublish = true;
        mUGCKitVideoEdit.setConfig(config);
        mVideoPath = getIntent().getStringExtra(UGCKitConstants.VIDEO_PATH);
        if (!TextUtils.isEmpty(mVideoPath)) {
            mUGCKitVideoEdit.setVideoPath(mVideoPath);
        }
        // 初始化播放器
        mUGCKitVideoEdit.initPlayer();
        mTvBgm.setOnClickListener(this);
        mTvMotion.setOnClickListener(this);
        mTvSpeed.setOnClickListener(this);
        mTvFilter.setOnClickListener(this);
        mTvPaster.setOnClickListener(this);
        mTvSubtitle.setOnClickListener(this);

        binding.videoEdit.getTitleBar().setVisibility(View.GONE);

        backIv.setOnClickListener(view -> mUGCKitVideoEdit.backPressed());

        binding.publish.setOnClickListener(view -> {
            mUGCKitVideoEdit.showPublishDialog();
        });

    }

    private void initWindowParam() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mUGCKitVideoEdit.initPlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUGCKitVideoEdit.setOnVideoEditListener(mOnVideoEditListener);
        mUGCKitVideoEdit.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mUGCKitVideoEdit.stop();
        mUGCKitVideoEdit.setOnVideoEditListener(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUGCKitVideoEdit.release();
    }

    private void startPreviewActivity(UGCKitResult ugcKitResult) {
        if (TextUtils.isEmpty(ugcKitResult.outputPath)) {
            return;
        }
        long duration = VideoEditerSDK.getInstance().getVideoDuration();
        Intent intent;
        if (ugcKitResult.isPublish) {
            intent = new Intent(getApplicationContext(), VideoPublishActivity.class);
            intent.putExtra(UGCKitConstants.VIDEO_PATH, ugcKitResult.outputPath);
            if (!TextUtils.isEmpty(ugcKitResult.coverPath)) {
                intent.putExtra(UGCKitConstants.VIDEO_COVERPATH, ugcKitResult.coverPath);
            }
            intent.putExtra(UGCKitConstants.VIDEO_RECORD_DURATION, duration);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        mUGCKitVideoEdit.backPressed();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.sel_music) {
            startEffectActivity(UGCKitConstants.TYPE_EDITER_BGM);
        } else if (id == R.id.action_layout) {
            startEffectActivity(UGCKitConstants.TYPE_EDITER_MOTION);
        } else if (id == R.id.speed_layout) {
            startEffectActivity(UGCKitConstants.TYPE_EDITER_SPEED);
        } else if (id == R.id.beauty_layout) {
            startEffectActivity(UGCKitConstants.TYPE_EDITER_FILTER);
        } else if (id == R.id.tiezhi_layout) {
            startEffectActivity(UGCKitConstants.TYPE_EDITER_PASTER);
        } else if (id == R.id.zimu_layout) {
            startEffectActivity(UGCKitConstants.TYPE_EDITER_SUBTITLE);
        }
    }

    /**
     * 跳转到视频特效编辑界面
     *
     * @param effectType {@link UGCKitConstants#TYPE_EDITER_BGM} 添加背景音</p>
     *                   {@link UGCKitConstants#TYPE_EDITER_MOTION} 添加动态滤镜</p>
     *                   {@link UGCKitConstants#TYPE_EDITER_SPEED} 添加时间特效</p>
     *                   {@link UGCKitConstants#TYPE_EDITER_FILTER} 添加静态滤镜</p>
     *                   {@link UGCKitConstants#TYPE_EDITER_PASTER} 添加贴纸</p>
     *                   {@link UGCKitConstants#TYPE_EDITER_SUBTITLE} 添加字幕</p>
     */
    private void startEffectActivity(int effectType) {
        Intent intent = new Intent(this, TCVideoEffectActivity.class);
        intent.putExtra(UGCKitConstants.KEY_FRAGMENT, effectType);
        startActivityForResult(intent, UGCKitConstants.ACTIVITY_OTHER_REQUEST_CODE);
    }
}