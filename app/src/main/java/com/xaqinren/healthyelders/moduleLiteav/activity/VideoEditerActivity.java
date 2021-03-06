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
import android.util.Log;
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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.tencent.bugly.proguard.A;
import com.tencent.liteav.audio.TXCUGCBGMPlayer;
import com.tencent.liteav.basic.log.TXCLog;
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
import com.tencent.qcloud.ugckit.module.cut.IVideoCutLayout;
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
import com.tencent.qcloud.ugckit.module.effect.utils.Edit;
import com.tencent.qcloud.ugckit.module.record.AudioFocusManager;
import com.tencent.qcloud.ugckit.module.record.MusicInfo;
import com.tencent.qcloud.ugckit.module.record.RecordMusicManager;
import com.tencent.qcloud.ugckit.module.record.UGCKitRecordConfig;
import com.tencent.qcloud.ugckit.module.record.VideoRecordSDK;
import com.tencent.qcloud.ugckit.utils.BackgroundTasks;
import com.tencent.qcloud.ugckit.utils.DialogUtil;
import com.tencent.qcloud.ugckit.utils.TelephonyUtil;
import com.tencent.qcloud.ugckit.utils.ToastUtil;
import com.tencent.qcloud.xiaoshipin.videoeditor.TCVideoEffectActivity;
import com.tencent.rtmp.TXLog;
import com.tencent.ugc.TXUGCRecord;
import com.tencent.ugc.TXVideoEditConstants;
import com.tencent.ugc.TXVideoEditer;
import com.tencent.ugc.TXVideoInfoReader;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.MainActivity;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityVideoEditerBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleLiteav.Constant;
import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicItemBean;
import com.xaqinren.healthyelders.moduleLiteav.dialog.MusicSelDialog;
import com.xaqinren.healthyelders.moduleLiteav.liteAv.LiteAvConstant;
import com.xaqinren.healthyelders.moduleLiteav.liteAv.MusicRecode;
import com.xaqinren.healthyelders.moduleLiteav.viewModel.VideoEditerViewModel;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.utils.MScreenUtil;

import java.util.List;

import me.goldze.mvvmhabit.base.AppManager;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.Utils;

/**
 * ???????????????
 */
public class VideoEditerActivity extends BaseActivity<ActivityVideoEditerBinding, VideoEditerViewModel> implements View.OnClickListener, VideoProgressController.VideoProgressSeekListener, Edit.OnCutChangeListener {
    private static final String TAG = "TCVideoEditerActivity";
    /**
     * ????????????
     */
    private String mVideoPath;
    private UGCKitVideoEdit mUGCKitVideoEdit;
    // ?????????
    private LinearLayout mTvBgm;
    // ????????????
    private LinearLayout mTvMotion;
    // ????????????
    private LinearLayout mTvSpeed;
    // ????????????
    private LinearLayout mTvFilter;
    // ??????
    private LinearLayout mTvPaster;
    // ??????
    private LinearLayout mTvSubtitle;
    // ?????????
    private ImageView backIv;
    private boolean              mIsPublish;
    public static int publish_code = 1001;
    private int REQUEST_MUSIC = 10003;
    private int margin220;
    private int margin66;
    private boolean mComplete;
    /**
     * ???????????????????????????
     */
    private int effVideoHeight;
    private int screenVideoHeight;

    private long cutStartTime, cutEndTime;
    TXVideoEditConstants.TXVideoInfo info;

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
    private boolean isStartMusicActivity;
    //?????????????????????
    private boolean hasMusic;
    private int type;


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
        effVideoHeight = MScreenUtil.getScreenHeight(this) - margin220 - margin66;
        screenVideoHeight = MScreenUtil.getScreenHeight(this);
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
        config.isSaveToDCIM = false;

        mUGCKitVideoEdit.setConfig(config);
        mVideoPath = getIntent().getStringExtra(UGCKitConstants.VIDEO_PATH);
        hasMusic = getIntent().getBooleanExtra(UGCKitConstants.VIDEO_HAS_MUSIC,false);
        type = getIntent().getIntExtra("type",0);//1????????????????????????????????????
        if (!TextUtils.isEmpty(mVideoPath)) {
            mUGCKitVideoEdit.setVideoPath(mVideoPath);
        }
        // ??????????????????
        mUGCKitVideoEdit.initPlayer();
        mTvBgm.setOnClickListener(this);
        mTvMotion.setOnClickListener(this);
        mTvSpeed.setOnClickListener(this);
        mTvFilter.setOnClickListener(this);
        mTvPaster.setOnClickListener(this);
        mTvSubtitle.setOnClickListener(this);
        binding.jianjiLayout.setOnClickListener(this);

        binding.videoEditView.setCutChangeListener(this);
        binding.videoEdit.getTitleBar().setVisibility(View.GONE);

        backIv.setOnClickListener(view ->  showCancelAlert());

        binding.publish.setOnClickListener(view -> {
//            mUGCKitVideoEdit.showPublishDialog();
            VideoEditerSDK.getInstance().setPublishFlag(true);
            mUGCKitVideoEdit.startGenerate();
        });
        info = TXVideoInfoReader.getInstance(UGCKit.getAppContext()).getVideoFileInfo(mVideoPath);
        loadVideoInfo(info);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CodeTable.MUSIC_BACK) {
            onMusicSelActivityBack();
            return;
        }
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
        rePlay();
    }

    @Override
    protected void onPause() {
        //??????mic
        mUGCKitVideoEdit.stop();
        mUGCKitVideoEdit.setOnVideoEditListener(null);
        RecordMusicManager.getInstance().stopPreviewMusic();
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        rePlay();
    }

    private void rePlay() {
        //?????????activity????????????
        Activity peek = AppManager.getActivityStack().peek();
        if (peek.getClass() != this.getClass()) {
            return;
        }
        //?????????????????????????????????
        RecordMusicManager.getInstance().stopPreviewMusic();
        //??????????????????????????????????????????
        if (MusicRecode.getInstance().getUseMusicItem() != null) {
            MusicInfo musicInfo = RecordMusicManager.getInstance().getMusicInfo();
            MMusicItemBean bean = MusicRecode.getInstance().getUseMusicItem();
            resetMusicBGM(bean.name, bean.localPath);
            VideoEditerSDK.getInstance().getEditer().setVideoVolume(0);
            VideoEditerSDK.getInstance().getEditer().setBGMVolume(1);
            PlayerManagerKit.getInstance().restartPlay();
            binding.musicName.setText(bean.name);
        }else{
            videoVolume();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean music = intent.getBooleanExtra("music", false);
        if (music) {
            onMusicSelActivityBack();
        }
    }

    private void onMusicSelActivityBack() {
        if (!isInitEffect)
            initEffect();
        isInitEffect = true;
        showMusicPanel();
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
        binding.timelineView.setVisibility(View.VISIBLE);
        binding.videoEditView.setVisibility(View.GONE);
        if (id == R.id.sel_music) {
            if (!isInitEffect)
                initEffect();
            isInitEffect = true;
            showMusicPanel();
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
        } else if (id == R.id.jianji_layout) {
            binding.timelineView.setVisibility(View.GONE);
            binding.videoEditView.setVisibility(View.VISIBLE);
            hideFragment();
            startEffectActivity(UGCKitConstants.TYPE_EDITER_CUT);
        }
    }

    /**
     * ?????????????????????????????????
     *
     * @param effectType {@link UGCKitConstants#TYPE_EDITER_BGM} ???????????????</p>
     *                   {@link UGCKitConstants#TYPE_EDITER_MOTION} ??????????????????</p>
     *                   {@link UGCKitConstants#TYPE_EDITER_SPEED} ??????????????????</p>
     *                   {@link UGCKitConstants#TYPE_EDITER_FILTER} ??????????????????</p>
     *                   {@link UGCKitConstants#TYPE_EDITER_PASTER} ????????????</p>
     *                   {@link UGCKitConstants#TYPE_EDITER_SUBTITLE} ????????????</p>
     */
    private void startEffectActivity(int effectType) {
        showEffect();
        if (!isInitEffect)
            initEffect();
        isInitEffect = true;
        showEffect(effectType);
    }
    private void showMusicPanel() {
        binding.iconLayout.setVisibility(View.GONE);
        binding.effectLayout.setVisibility(View.GONE);
        showMusicPop();
    }
    private void hideMusicPanel() {
        binding.iconLayout.setVisibility(View.VISIBLE);
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
     * ???????????? begin
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
    boolean isInitEffect = false;
    private void initEffect() {

        mTimeFragment = new TCTimeFragment();
        mTimeFragment.setOnTimeLineListener(mOnTimeLineListener);

        mStaticFilterFragment = new TCStaticFilterFragment();
        mMotionFragment = new TCMotionFragment();
        mPasterFragment = new TCPasterFragment(info);
        mBubbleFragment = new TCBubbleSubtitleFragment(info);
        mMusicFragment = new TCMusicSettingFragment();

        binding.timelineView.setOnTimeChangeListener((type, time) -> {
            if (mTimeFragment != null) {
                mTimeFragment.onTimeChange(type, time);
            }
        });

        // ?????????????????????????????????????????????????????????????????????????????????
        VideoEditerSDK.getInstance().resetDuration();
        // ??????????????????
        ConfigureLoader.getInstance().loadConfigToDraft();

        ConfigureLoader.getInstance().setMusicVolume(1);
        ConfigureLoader.getInstance().setVideoVolume(1);

        ConfigureLoader.getInstance().saveConfigFromDraft();

        TelephonyUtil.getInstance().initPhoneListener();

        initTitlebar();

        preivewVideo();

        List<Bitmap> thumbnailList = VideoEditerSDK.getInstance().getAllThumbnails();
        for (int i = 0; i < thumbnailList.size(); i++) {
            binding.videoEditView.addBitmap(i, thumbnailList.get(i));
        }
        binding.videoEditView.setCount(thumbnailList.size());
    }

    private void loadVideoInfo( TXVideoEditConstants.TXVideoInfo info) {
        // ??????????????????
        if (mComplete == true) {
            return;
        }
        mComplete = false;

        if (info == null) {

        } else {
            cutStartTime = 0;
            cutEndTime = info.duration ;
            setVideoInfo(info);
            TXCLog.i(TAG,"[UGCKit][VideoCut]load thunmail");
            // ????????????
        }
    }

    /**
     * ??????,??????
     */
    private void initTitlebar() {
        binding.cancel.setOnClickListener(v -> {
            // ??????"??????",?????????????????????????????????
            DraftEditer.getInstance().clear();
            // ?????????????????????SDK?????????
            VideoEditerSDK.getInstance().restore();
            hideEffect();
            VideoEditerSDK.getInstance().setCutterStartTime(cutStartTime, cutEndTime);
            PlayerManagerKit.getInstance().restartPlay();

        });
        binding.save.setOnClickListener(v -> {
            // ??????"??????"????????????????????????????????????
            ConfigureLoader.getInstance().saveConfigFromDraft();
            hideEffect();
            cutStartTime = tempCutStartTime;
            cutEndTime = tempCutEndTime;
            VideoEditerSDK.getInstance().setCutterStartTime(cutStartTime, cutEndTime);
            PlayerManagerKit.getInstance().restartPlay();
        });
    }

    private void preivewVideo() {
        // ????????????????????????
        binding.timelineView.initVideoProgressLayout();
        // ??????????????????
        initPlayerLayout();
        // ????????????
        PlayerManagerKit.getInstance().startPlay();
    }

    public void initPlayerLayout() {

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
        if (fragment == mCurrentFragment && !mCurrentFragment.isHidden()) return;
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

    private void hideFragment() {
        if (mCurrentFragment==null)return;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(mCurrentFragment);
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

    @Override
    public void onCutClick() {

    }

    @Override
    public void onCutChangeKeyDown() {
        PlayerManagerKit.getInstance().stopPlay();
    }
    private long tempCutStartTime,tempCutEndTime;
    @Override
    public void onCutChangeKeyUp(long startTime, long endTime, int type) {
        long duration = (endTime - startTime) / 1000;
        tempCutStartTime = startTime;
        tempCutEndTime = endTime;
//        String str = getResources().getString(com.tencent.qcloud.ugckit.R.string.ugckit_video_cutter_activity_load_video_success_already_picked) + duration + "s";
//        mTextDuration.setText(str);

        VideoEditerSDK.getInstance().setCutterStartTime(startTime, endTime);
        PlayerManagerKit.getInstance().startPlay();

        TXLog.d(TAG, "startTime:" + startTime + ",endTime:" + endTime + ",duration:" + duration);
    }

    public void setVideoInfo(@NonNull TXVideoEditConstants.TXVideoInfo videoInfo) {

        int durationS = (int) (videoInfo.duration / 1000);
//        int thumbCount = durationS / 3;

        TXCLog.i(TAG, "[UGCKit][VideoCut]init cut time, start:" + 0 + ", end:" + durationS * 1000);
        VideoEditerSDK.getInstance().setCutterStartTime(0, videoInfo.duration);

        binding.videoEditView.setMediaFileInfo(videoInfo);


    }

    private MusicSelDialog mMusicPop;
    private void showMusicPop() {
        if (mMusicPop == null) {
            mMusicPop = new MusicSelDialog(this);
            mMusicPop.setEnableVolumeEdit(true,!hasMusic);
        }
        mMusicPop.setType(type);//?????? ???????????????????????????
        mMusicPop.show();

        Window window = mMusicPop.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            mMusicPop.getWindow().setDimAmount(0.f);
            mMusicPop.getWindow().setAttributes(lp);
        }

        mMusicPop.setOnClickListener(new MusicSelDialog.OnClickListener() {
            @Override
            public void onMusicPlay() {
                if (hasMusic)
                    muteVideo();
                PlayerManagerKit.getInstance().restartPlay();
            }

            @Override
            public void onMoreClick() {
                mMusicPop.dismiss();
                isStartMusicActivity = true;
                //????????????
                ConfigureLoader.getInstance().setMusicVolume(0f);
                ConfigureLoader.getInstance().setVideoVolume(1f);
                ConfigureLoader.getInstance().saveConfigFromDraft();

                MusicRecode.CURRENT_BOURN = com.xaqinren.healthyelders.moduleLiteav.Constant.BOURN_EDIT;
                Intent intent = new Intent(getActivity(),ChooseMusicActivity.class);
                startActivityForResult(intent, CodeTable.MUSIC_BACK);
                overridePendingTransition(R.anim.activity_bottom_2enter,R.anim.activity_push_none);
            }

            @Override
            public void onItemPlay(MMusicItemBean bean) {
                binding.musicName.setText(bean.name);
                resetMusicBGM(bean.name , bean.localPath);
                if (hasMusic)
                    muteVideo();
                PlayerManagerKit.getInstance().restartPlay();
            }

            @Override
            public void onVolumeChange(float oVolume, float bgmVolume) {
                ConfigureLoader.getInstance().setMusicVolume(bgmVolume);
                if (hasMusic) {
                    ConfigureLoader.getInstance().setVideoVolume(0);
                }else{
                    ConfigureLoader.getInstance().setVideoVolume(oVolume);
                }

                ConfigureLoader.getInstance().saveConfigFromDraft();


                VideoEditerSDK.getInstance().getEditer().setBGMVolume(0);
                if (hasMusic) {
                    VideoEditerSDK.getInstance().getEditer().setVideoVolume(0);
                }else{
                    VideoEditerSDK.getInstance().getEditer().setVideoVolume(oVolume);
                }
//                muteVideo();
            }

            @Override
            public void onJianJiClick(MMusicItemBean bean) {
                //??????
            }

            @Override
            public void onCollClick(MMusicItemBean bean) {
                //??????
            }

            @Override
            public void onDismiss() {
                hideMusicPanel();
                videoVolume();

                VideoEditerSDK.getInstance().getEditer().setBGMStartTime(0, 10 * 60 * 1000);
                VideoEditerSDK.getInstance().getEditer().setBGMLoop(true);
                ConfigureLoader.getInstance().saveConfigFromDraft();

                PlayerManagerKit.getInstance().restartPlay();
            }

            @Override
            public void onStopPlay() {
                binding.musicName.setText("????????????");
                //????????????
                ConfigureLoader.getInstance().setMusicVolume(0f);
                ConfigureLoader.getInstance().setVideoVolume(1f);
                ConfigureLoader.getInstance().saveConfigFromDraft();
            }
        });
    }

    private void resetMusicBGM(String name , String path) {
        LogUtils.e(TAG, "resetMusicBGM -> " + name + "\t" + path);
        VideoEditerSDK.getInstance().getEditer().setBGM(path);
        VideoEditerSDK.getInstance().getEditer().setBGMStartTime(0, 10 * 60 * 1000);
        ConfigureLoader.getInstance().setMusicPath(name, path, 1);
        ConfigureLoader.getInstance().saveConfigFromDraft();
    }

    /**
     * ????????????
     */
    private void muteVideo() {
        VideoEditerSDK.getInstance().getEditer().setVideoVolume(0);
        VideoEditerSDK.getInstance().getEditer().setBGMVolume(0);
    }

    /**
     * ????????????????????????
     */
    private void videoVolume() {
        if (VideoEditerSDK.getInstance() != null) {
            if (VideoEditerSDK.getInstance().getEditer() != null) {
                //?????????????????????
                float volume = ConfigureLoader.getInstance().getMusicVolume();
                VideoEditerSDK.getInstance().getEditer().setBGMVolume(volume);
                //??????????????????
                float vVolume = ConfigureLoader.getInstance().getVideoVolume();
                VideoEditerSDK.getInstance().getEditer().setVideoVolume(vVolume);
            }else{
                //????????????????????????
                VideoEditerSDK.getInstance().getEditer().setBGMVolume(0);
                VideoEditerSDK.getInstance().getEditer().setVideoVolume(1);
            }
        }
    }


    /** ???????????? end */



}
