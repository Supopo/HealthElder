package com.xaqinren.healthyelders.moduleLiteav.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.tencent.bugly.proguard.A;
import com.tencent.qcloud.tim.uikit.utils.PopWindowUtil;
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.tencent.qcloud.ugckit.UGCKit;
import com.tencent.qcloud.ugckit.UGCKitConstants;
import com.tencent.qcloud.ugckit.UGCKitVideoEdit;
import com.tencent.qcloud.ugckit.basic.UGCKitResult;
import com.tencent.qcloud.ugckit.component.dialog.ActionSheetDialog;
import com.tencent.qcloud.ugckit.component.dialogfragment.ProgressFragmentUtil;
import com.tencent.qcloud.ugckit.component.timeline.VideoProgressController;
import com.tencent.qcloud.ugckit.module.PlayerManagerKit;
import com.tencent.qcloud.ugckit.module.VideoGenerateKit;
import com.tencent.qcloud.ugckit.module.editer.IVideoEditKit;
import com.tencent.qcloud.ugckit.module.editer.UGCKitEditConfig;
import com.tencent.qcloud.ugckit.module.effect.ConfigureLoader;
import com.tencent.qcloud.ugckit.module.effect.TimeLineView;
import com.tencent.qcloud.ugckit.module.effect.TimelineViewUtil;
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK;
import com.tencent.qcloud.ugckit.module.effect.bgm.TCMusicSettingFragment;
import com.tencent.qcloud.ugckit.module.effect.bubble.TCBubbleSubtitleFragment;
import com.tencent.qcloud.ugckit.module.effect.filter.TCStaticFilterFragment;
import com.tencent.qcloud.ugckit.module.effect.motion.TCMotionFragment;
import com.tencent.qcloud.ugckit.module.effect.paster.TCPasterFragment;
import com.tencent.qcloud.ugckit.module.effect.time.TCTimeFragment;
import com.tencent.qcloud.ugckit.module.effect.utils.DraftEditer;
import com.tencent.qcloud.ugckit.utils.TelephonyUtil;
import com.tencent.qcloud.ugckit.utils.ToastUtil;
import com.tencent.qcloud.xiaoshipin.videoeditor.TCVideoEffectActivity;
import com.tencent.ugc.TXVideoEditConstants;
import com.tencent.ugc.TXVideoEditer;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.MainActivity;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityVideoEditerBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleLiteav.liteAv.LiteAvConstant;
import com.xaqinren.healthyelders.moduleLiteav.viewModel.VideoEditerViewModel;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.Utils;

/**
 * 录制完编辑
 */
public class VideoEditerActivity extends BaseActivity<ActivityVideoEditerBinding, VideoEditerViewModel> implements View.OnClickListener , VideoProgressController.VideoProgressSeekListener{
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
    public static int publish_code = 1001;
    private int margin220;
    private int margin66;
    /**
     * 执行编辑的时候高度
     */
    private int effVideoHeight;
    private int screenVideoHeight;


    private IVideoEditKit.OnEditListener mOnVideoEditListener = new IVideoEditKit.OnEditListener() {
        @Override
        public void onEditCompleted(UGCKitResult ugcKitResult) {
            LogUtils.e(TAG, "onEditCompleted");
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
        setTheme(com.hjyy.liteav.R.style.EditerActivityTheme);
        super.onCreate(savedInstanceState);
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
        margin220 = (int) getResources().getDimension(R.dimen.dp_220);
        margin66 = (int) getResources().getDimension(R.dimen.dp_66);
        effVideoHeight = ScreenUtil.getScreenHeight(this) - margin220 - margin66;
        screenVideoHeight = ScreenUtil.getScreenHeight(this);
        setStatusBarTransparent();
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

        backIv.setOnClickListener(view ->  showCancelAlert());

        binding.publish.setOnClickListener(view -> {
//            mUGCKitVideoEdit.showPublishDialog();
            VideoEditerSDK.getInstance().setPublishFlag(true);
            mUGCKitVideoEdit.startGenerate();
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == publish_code) {
            startOnRestart();
        }else
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
        mUGCKitVideoEdit.stop();
        mUGCKitVideoEdit.setOnVideoEditListener(null);
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void startOnRestart() {
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                VideoEditerSDK.getInstance().resetDuration();
                mUGCKitVideoEdit.setVideoPath(mVideoPath);
                return objects;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showDialog();
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                dismissDialog();
                mUGCKitVideoEdit.initPlayer();
                mUGCKitVideoEdit.start();
            }

        };
        asyncTask.execute("");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        mUGCKitVideoEdit.release();
        super.onDestroy();
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
            intent.putExtra(LiteAvConstant.RequestCode, publish_code);
            startActivityForResult(intent, publish_code);
        } else {
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        showCancelAlert();
    }

    private void showCancelAlert() {
        View view = View.inflate(this, R.layout.dialog_video_edit_cancel, null);
        PopupWindow popupWindow = new PopupWindow(view,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.DialogBottomAnimation);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(binding.rlContainer, Gravity.BOTTOM, 0, 0);
        TextView confirmCancel = view.findViewById(R.id.confirm_cancel);
        TextView cancel = view.findViewById(R.id.cancel);
        confirmCancel.setOnClickListener(view1 -> {
            popupWindow.dismiss();
            mUGCKitVideoEdit.backPressedNoAlert();
        });
        cancel.setOnClickListener(view1 -> popupWindow.dismiss());
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
//        TCVideoEffectActivity
//        Intent intent = new Intent(this, TCVideoEffectActivity.class);
//        intent.putExtra(UGCKitConstants.KEY_FRAGMENT, effectType);
//        startActivityForResult(intent, UGCKitConstants.ACTIVITY_OTHER_REQUEST_CODE);
        showEffect();
        initEffect();
        showEffect(effectType);
    }

    private void showEffect() {
        binding.iconLayout.setVisibility(View.GONE);
        binding.effectLayout.setVisibility(View.VISIBLE);
    }
    private void hideEffect() {
        binding.iconLayout.setVisibility(View.VISIBLE);
        binding.effectLayout.setVisibility(View.GONE);
    }

    /**
     * 编辑部分 begin
     */
    private TCVideoEffectActivity tcVideoEffectActivity;
    private TCTimeFragment           mTimeFragment;
    private TCStaticFilterFragment   mStaticFilterFragment;
    private TCMotionFragment         mMotionFragment;
    private TCPasterFragment         mPasterFragment;
    private TCBubbleSubtitleFragment mBubbleFragment;
    private TCMusicSettingFragment   mMusicFragment;

    private Fragment mCurrentFragment;

    private TimeLineView.OnTimeLineListener mOnTimeLineListener = new TimeLineView.OnTimeLineListener() {
        @Override
        public void onAddSlider(int type, long startEffectTime) {
            if (binding.timelineView != null) {
                binding.timelineView.onAddSlider(type, startEffectTime);
            }
        }

        @Override
        public void onRemoveSlider(int type) {
            if (binding.timelineView != null) {
                binding.timelineView.onRemoveSlider(type);
            }
        }

        @Override
        public long getCurrentTime() {
            if (binding.timelineView != null) {
                return binding.timelineView.getCurrentTime();
            }
            return 0;
        }

        @Override
        public void setCurrentTime(long time) {
            if (binding.timelineView != null) {
                binding.timelineView.setCurrentTime(time);
            }
        }
    };

    private void initEffect() {
        mTimeFragment = new TCTimeFragment();
        mTimeFragment.setOnTimeLineListener(mOnTimeLineListener);

        mStaticFilterFragment = new TCStaticFilterFragment();
        mMotionFragment = new TCMotionFragment();
        mPasterFragment = new TCPasterFragment();
        mBubbleFragment = new TCBubbleSubtitleFragment();
        mMusicFragment = new TCMusicSettingFragment();

        binding.timelineView.setOnTimeChangeListener((type, time) -> {
            if (mTimeFragment != null) {
                mTimeFragment.onTimeChange(type, time);
            }
        });

        // 当上个界面为裁剪界面，此时重置裁剪的开始时间和结束时间
        VideoEditerSDK.getInstance().resetDuration();
        // 加载草稿配置
        ConfigureLoader.getInstance().loadConfigToDraft();

        TelephonyUtil.getInstance().initPhoneListener();

        initTitlebar();

        preivewVideo();
    }

    private void initTitlebar() {
        binding.cancel.setOnClickListener(v -> {
            // 点击"返回",清除当前设置的视频特效
            DraftEditer.getInstance().clear();
            // 还原已经设置给SDK的特效
            VideoEditerSDK.getInstance().restore();
            hideEffect();
            PlayerManagerKit.getInstance().restartPlay();
        });
        binding.save.setOnClickListener(v -> {
            // 点击"完成"，应用当前设置的视频特效
            ConfigureLoader.getInstance().saveConfigFromDraft();
            hideEffect();
            PlayerManagerKit.getInstance().restartPlay();
        });
    }

    private void preivewVideo() {
        // 初始化图片时间轴
        binding.timelineView.initVideoProgressLayout();
        // 初始化播放器
        initPlayerLayout();
        // 开始播放
        PlayerManagerKit.getInstance().startPlay();
    }

    public void initPlayerLayout() {
        /*TXVideoEditConstants.TXPreviewParam param = new TXVideoEditConstants.TXPreviewParam();
        param.videoView = mLayoutPlayer;
        param.renderMode = TXVideoEditConstants.PREVIEW_RENDER_MODE_FILL_EDGE;
        TXVideoEditer videoEditer = VideoEditerSDK.getInstance().getEditer();
        if (videoEditer != null) {
            videoEditer.initWithPreview(param);
        }*/
    }

    private void showEffect(int type) {
        TimelineViewUtil.getInstance().setTimelineView(binding.timelineView);
        binding.playControlLayout.updateUIByFragment(type);
        binding.timelineView.updateUIByFragment(type);
        showFragmentByType(type);
    }

    public void showFragmentByType(int type) {
        switch (type) {
            case UGCKitConstants.TYPE_EDITER_BGM:
                showFragment(mMusicFragment, "bgm_setting_fragment");
                break;
            case UGCKitConstants.TYPE_EDITER_MOTION:
                showFragment(mMotionFragment, "motion_fragment");
                break;
            case UGCKitConstants.TYPE_EDITER_SPEED:
                showFragment(mTimeFragment, "time_fragment");
                break;
            case UGCKitConstants.TYPE_EDITER_FILTER:
                showFragment(mStaticFilterFragment, "static_filter_fragment");
                break;
            case UGCKitConstants.TYPE_EDITER_PASTER:
                showFragment(mPasterFragment, "paster_fragment");
                break;
            case UGCKitConstants.TYPE_EDITER_SUBTITLE:
                showFragment(mBubbleFragment, "bubble_fragment");
                break;
        }
    }

    private void showFragment(@NonNull Fragment fragment, String tag) {
        if (fragment == mCurrentFragment) return;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mCurrentFragment != null) {
            transaction.hide(mCurrentFragment);
        }
        if (!fragment.isAdded()) {
            transaction.add(com.tencent.qcloud.ugckit.R.id.fragment_layout, fragment, tag);
        } else {
            transaction.show(fragment);
        }
        mCurrentFragment = fragment;
        transaction.commit();
    }

    public void start() {
        KeyguardManager manager = (KeyguardManager) UGCKit.getAppContext().getSystemService(Context.KEYGUARD_SERVICE);
        if (!manager.inKeyguardRestrictedInputMode()) {
            PlayerManagerKit.getInstance().restartPlay();
        }
    }

    public void stop() {
        PlayerManagerKit.getInstance().stopPlay();
    }

    public void release() {
        PlayerManagerKit.getInstance().removeAllPreviewListener();
        PlayerManagerKit.getInstance().removeAllPlayStateListener();
        TelephonyUtil.getInstance().uninitPhoneListener();
    }

    @Override
    public void onVideoProgressSeek(long currentTimeMs) {
        PlayerManagerKit.getInstance().previewAtTime(currentTimeMs);
    }

    @Override
    public void onVideoProgressSeekFinish(long currentTimeMs) {
        PlayerManagerKit.getInstance().previewAtTime(currentTimeMs);
    }
    /** 编辑部分 end */

}
