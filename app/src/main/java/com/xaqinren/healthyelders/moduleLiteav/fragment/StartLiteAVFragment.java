package com.xaqinren.healthyelders.moduleLiteav.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ModuleInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.tencent.liteav.demo.beauty.model.BeautyInfo;
import com.tencent.liteav.demo.beauty.model.ItemInfo;
import com.tencent.liteav.demo.beauty.model.TabInfo;
import com.tencent.liteav.demo.beauty.view.BeautyPanel;
import com.tencent.qcloud.tim.uikit.utils.FileUtil;
import com.tencent.qcloud.ugckit.UGCKitConstants;
import com.tencent.qcloud.ugckit.module.effect.bgm.TCMusicActivity;
import com.tencent.qcloud.ugckit.module.record.MusicInfo;
import com.tencent.qcloud.ugckit.module.record.PhotoSoundPlayer;
import com.tencent.qcloud.ugckit.module.record.RecordModeView;
import com.tencent.qcloud.ugckit.module.record.RecordMusicManager;
import com.tencent.qcloud.ugckit.module.record.RecordMusicPannel;
import com.tencent.qcloud.ugckit.module.record.UGCKitRecordConfig;
import com.tencent.qcloud.ugckit.module.record.VideoRecordSDK;
import com.tencent.qcloud.ugckit.module.record.interfaces.IRecordMusicPannel;
import com.tencent.qcloud.ugckit.utils.BitmapUtils;
import com.tencent.qcloud.xiaoshipin.mainui.TCMainActivity;
import com.tencent.qcloud.xiaoshipin.videochoose.TCPicturePickerActivity;
import com.tencent.ugc.TXRecordCommon;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.FragmentStartLiteAvBinding;
import com.xaqinren.healthyelders.moduleLiteav.activity.VideoEditerActivity;
import com.xaqinren.healthyelders.moduleLiteav.liteAv.LiteAvRecode;
import com.xaqinren.healthyelders.moduleLiteav.service.LocationService;
import com.xaqinren.healthyelders.modulePicture.Constant;
import com.xaqinren.healthyelders.modulePicture.activity.PublishTextPhotoActivity;
import com.xaqinren.healthyelders.moduleZhiBo.activity.StartLiveActivity;
import com.xaqinren.healthyelders.moduleZhiBo.viewModel.StartLiveUiViewModel;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.widget.BottomDialog;
import com.xaqinren.healthyelders.widget.RecordButton;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.utils.PermissionUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

public class StartLiteAVFragment extends BaseFragment<FragmentStartLiteAvBinding, BaseViewModel> implements
        LiteAvRecode.RecodeLiteListener, IRecordMusicPannel.MusicChangeListener {

    private BottomDialog mLvJingPop;            //滤镜弹窗
    private BottomDialog mMeiYanPop;            //美颜弹窗
    private BottomDialog mMusicPop;            //美颜弹窗
    private BeautyPanel mMeiYanControl;         //美颜控制器
    private BeautyPanel mLvJingControl;         //滤镜控制器
    private RecordMusicPannel musicPannel;         //滤镜控制器
    private PopupWindow scalePop;               //屏幕比例弹窗
    private boolean isRecord = false;

    private StartLiveUiViewModel liveUiViewModel;
    private LiteAvRecode liteAvRecode;
    private OnFragmentStatusListener onFragmentStatusListener;
    private int REQUEST_PERMISSION = 100;
    private String photoPath;
    private int currentMode = RecordButton.VIDEO_MODE;

    public void setOnFragmentStatusListener(OnFragmentStatusListener onFragmentStatusListener) {
        this.onFragmentStatusListener = onFragmentStatusListener;
    }



    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_start_lite_av;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        liveUiViewModel.getCurrentPage()
                .observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                LogUtils.i(getClass().getSimpleName(), "liveUiViewModel onChanged\t" + integer.intValue());
                if (integer.intValue() == 0 ) {
                    //释放
                    VideoRecordSDK.getInstance().getRecorder().stopBGM();
                    VideoRecordSDK.getInstance().stopCameraPreview();
                }  else {
                    if (integer.intValue() == 2) {
                        //变为拍照模式
                        changePhotoMode();
                    }else{
                        changeVideoMode();
                    }
                    //重新加载
                    VideoRecordSDK.getInstance().getRecorder().resumeBGM();
                    VideoRecordSDK.getInstance().startCameraPreview(binding.videoView);
                }
            }
        });

    }

    private void changePhotoMode() {
        binding.selMusic.setVisibility(View.GONE);
        binding.speedLayout.setVisibility(View.GONE);
        binding.recodeBtn.setMode(RecordButton.PHOTO_MODE);
    }

    private void changeVideoMode() {
        binding.selMusic.setVisibility(View.VISIBLE);
        binding.speedLayout.setVisibility(View.VISIBLE);
        binding.recodeBtn.setMode(RecordButton.VIDEO_MODE);
    }



    @Override
    public void initData() {
        super.initData();
        Bundle bundle = getArguments();
        if (bundle != null) {
            currentMode = bundle.getInt(com.xaqinren.healthyelders.modulePicture.constant.Constant.RECODE_MODE);
        }
        if (currentMode == RecordButton.PHOTO_MODE) {
            changePhotoMode();
        }
        liveUiViewModel = ViewModelProviders.of(getActivity()).get(StartLiveUiViewModel.class);
        initCameraRecode();
        initView();
    }

    private void initView() {

        //翻转
        binding.turnLayout.setOnClickListener(view -> {
            liteAvRecode.switchCamera();
        });
        //快慢速
        binding.speedLayout.setOnClickListener(view -> {
            int v = binding.recordSpeedLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE;
            binding.recordSpeedLayout.setVisibility(v);
        });
        //滤镜
        binding.filterLayout.setOnClickListener(view -> {
            hidePanel();
            showLJPop();
        });
        //美颜
        binding.beautyLayout.setOnClickListener(view -> {
            hidePanel();
            showMYPop();
        });
        //屏幕比例
        binding.scaleLayout.setOnClickListener(view -> {
            showScalePop();
        });
        //闪光灯
        binding.lightLayout.setOnClickListener(view -> {
            liteAvRecode.toggleTorch();
        });
        //选择音乐
        binding.selMusic.setOnClickListener(view -> {
            showMusic();
        });
        //录制
        /*binding.recodeBtn.setOnClickListener(view -> {
            startRecode();
        });*/
        binding.galleryLayout.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), TCPicturePickerActivity.class);
            startActivity(intent);
        });
        binding.recodeBtn.setOnRecordButtonListener(new RecordButton.OnRecordButtonListener() {
            @Override
            public void onRecordStart() {
                startRecode();
            }

            @Override
            public void onRecordPause() {
                showDialog();
                //手指松开,录制完成
                pauseRecode();
            }

            @Override
            public void onPhotoSuccess() {
                //调用SDK拍照
                PhotoSoundPlayer.playPhotoSound();
                VideoRecordSDK.getInstance().takePhoto(bitmap -> {
                    photoPath = FileUtil.saveBitmap(null, bitmap);
                    binding.photoPreview.rlContainer.setVisibility(View.VISIBLE);
                    binding.photoPreview.photoPreviewIv.setImageBitmap(bitmap);
                    //隐藏activity 底部bar
                    if (onFragmentStatusListener!=null)
                        onFragmentStatusListener.isRecode(true);
                });
            }
        });
        binding.recodeBtn.setRecodeMaxTime(liteAvRecode.getMaxRecordTime());
        binding.recodeBtn.setRecodeProgressBarWidth((int) getResources().getDimension(R.dimen.dp_6));
        binding.recordSpeedLayout.setOnRecordSpeedListener(speed -> liteAvRecode.setRecodeSpeed(speed));
        binding.photoPreview.cancel.setOnClickListener(view -> {
            photoPath = "";
            binding.photoPreview.rlContainer.setVisibility(View.GONE);
            if (onFragmentStatusListener!=null)
                onFragmentStatusListener.isRecode(false);
        });
        binding.photoPreview.save.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), PublishTextPhotoActivity.class);
            intent.putExtra(Constant.PHOTO_PATH, photoPath);
            startActivity(intent);
            if (onFragmentStatusListener!=null)
                onFragmentStatusListener.isRecode(false);
            binding.photoPreview.rlContainer.setVisibility(View.GONE);
        });
    }

    /**
     * 屏幕比例
     */
    private void showScalePop(){
        if (scalePop == null) {
            View filterView = View.inflate(getActivity(), R.layout.pop_scale_control, null);
            ImageView oneOne = filterView.findViewById(R.id.one_one);
            ImageView threeFour = filterView.findViewById(R.id.three_four);
            ImageView fourThree = filterView.findViewById(R.id.four_three);
            ImageView sixNine = filterView.findViewById(R.id.six_nine);
            ImageView nineSix = filterView.findViewById(R.id.nine_six);
            scalePop = new PopupWindow(filterView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            scalePop.setFocusable(false);
            scalePop.setOutsideTouchable(true);
            scalePop.setBackgroundDrawable(new ColorDrawable());
            scalePop.setAnimationStyle(R.style.animationRight);
            filterView.setOnClickListener(view -> scalePop.dismiss());
            oneOne.setOnClickListener(scaleClick);
            threeFour.setOnClickListener(scaleClick);
            fourThree.setOnClickListener(scaleClick);
            sixNine.setOnClickListener(scaleClick);
            nineSix.setOnClickListener(scaleClick);
        }
        if (scalePop.isShowing()){scalePop.dismiss();return;}
        float dp16 = getResources().getDimension(R.dimen.dp_16);
        scalePop.showAsDropDown(binding.beautyLayout, 0, (int) dp16);
        scalePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });

    }

    private View.OnClickListener scaleClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int mAspectRatio = TXRecordCommon.VIDEO_ASPECT_RATIO_9_16;
            switch (view.getId()) {
                case R.id.one_one:
                    mAspectRatio = TXRecordCommon.VIDEO_ASPECT_RATIO_1_1;
                    binding.scaleIv.setImageResource(R.mipmap.icon_xsp_1_1);
                    binding.scaleTv.setText("1:1");
                    break;
                case R.id.three_four:
                    mAspectRatio = TXRecordCommon.VIDEO_ASPECT_RATIO_3_4;
                    binding.scaleIv.setImageResource(R.mipmap.icon_xsp_3_4);
                    binding.scaleTv.setText("3:4");
                    break;
                case R.id.four_three:
                    mAspectRatio = TXRecordCommon.VIDEO_ASPECT_RATIO_4_3;
                    binding.scaleIv.setImageResource(R.mipmap.icon_xsp_4_3);
                    binding.scaleTv.setText("4:3");
                    break;
                case R.id.six_nine:
                    mAspectRatio = TXRecordCommon.VIDEO_ASPECT_RATIO_16_9;
                    binding.scaleIv.setImageResource(R.mipmap.icon_xsp_16_9);
                    binding.scaleTv.setText("16:9");
                    break;
                case R.id.nine_six:
                    mAspectRatio = TXRecordCommon.VIDEO_ASPECT_RATIO_9_16;
                    binding.scaleIv.setImageResource(R.mipmap.icon_xsp_9_16);
                    binding.scaleTv.setText("9:16");
                    break;
            }
            liteAvRecode.setCurrentAsp(mAspectRatio);
        }
    };

    private void showMYPop() {
        if (mMeiYanPop == null) {
            View filterView = View.inflate(getActivity(), R.layout.pop_beauty_control, null);
            mMeiYanControl = filterView.findViewById(R.id.beauty_pannel);
            mMeiYanControl.setPosition(0);
            mMeiYanControl.setBeautyManager(VideoRecordSDK.getInstance().getRecorder().getBeautyManager());
            mMeiYanControl.setPopTitle("美颜");
            mMeiYanPop = new BottomDialog(getActivity(), filterView,
                    null);
        }
        mMeiYanPop.show();
        mMeiYanControl.setMeiYDialogBgDraw(getResources().getDrawable(R.drawable.bg_meiy_dialog));
        Window window = mMeiYanPop.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            mMeiYanPop.getWindow().setDimAmount(0.f);
            mMeiYanPop.getWindow().setAttributes(lp);
        }
        mMeiYanControl.setOnBeautyListener(new BeautyPanel.OnBeautyListener() {
            @Override
            public void onTabChange(TabInfo tabInfo, int position) {
            }

            @Override
            public boolean onClose() {
                return false;
            }

            @Override
            public boolean onClick(TabInfo tabInfo, int tabPosition, ItemInfo itemInfo, int itemPosition) {
                return false;
            }

            @Override
            public boolean onLevelChanged(TabInfo tabInfo, int tabPosition, ItemInfo itemInfo, int itemPosition, int beautyLevel) {
                return false;
            }
        });
        mMeiYanPop.setOnBottomItemClickListener(new BottomDialog.OnBottomItemClickListener() {
            @Override
            public void onBottomItemClick(BottomDialog dialog, View view) {

            }
        });
        mMeiYanPop.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                showNormalPanel();
            }
        });
    }

    private void showLJPop() {
        if (mLvJingPop == null) {
            View filterView = View.inflate(getActivity(), R.layout.pop_beauty_control, null);
            mLvJingControl = filterView.findViewById(R.id.beauty_pannel);
            mLvJingControl.setPosition(1);
            mLvJingControl.setBeautyManager(VideoRecordSDK.getInstance().getRecorder().getBeautyManager());

            mLvJingControl.setPopTitle("滤镜");
            BeautyInfo defaultBeautyInfo = mLvJingControl.getDefaultBeautyInfo();
            mLvJingControl.setBeautyInfo(defaultBeautyInfo);

            mLvJingPop = new BottomDialog(getActivity(), filterView,
                    null);
        }
        mLvJingPop.show();
        mLvJingControl.setMeiYDialogBgDraw(getResources().getDrawable(R.drawable.bg_meiy_dialog));
        Window window = mLvJingPop.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            mLvJingPop.getWindow().setDimAmount(0.f);
            mLvJingPop.getWindow().setAttributes(lp);
        }
        mLvJingControl.setOnBeautyListener(new BeautyPanel.OnBeautyListener() {
            @Override
            public void onTabChange(TabInfo tabInfo, int position) {
            }

            @Override
            public boolean onClose() {
                return false;
            }

            @Override
            public boolean onClick(TabInfo tabInfo, int tabPosition, ItemInfo itemInfo, int itemPosition) {
                return false;
            }

            @Override
            public boolean onLevelChanged(TabInfo tabInfo, int tabPosition, ItemInfo itemInfo, int itemPosition, int beautyLevel) {
                return false;
            }
        });
        mLvJingPop.setOnBottomItemClickListener(new BottomDialog.OnBottomItemClickListener() {
            @Override
            public void onBottomItemClick(BottomDialog dialog, View view) {

            }
        });
        mLvJingPop.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                showNormalPanel();
            }
        });
    }

    private void showMusicPop() {
        if (mMusicPop == null) {
            View filterView = View.inflate(getActivity(), R.layout.pop_music_control, null);
            musicPannel = filterView.findViewById(R.id.record_music_pannel);
            musicPannel.setOnMusicChangeListener(this);
            mMusicPop = new BottomDialog(getActivity(), filterView,
                    null);
        }
        mMusicPop.show();

        Window window = mMusicPop.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            mMusicPop.getWindow().setDimAmount(0.f);
            mMusicPop.getWindow().setAttributes(lp);
        }

        mMusicPop.setOnBottomItemClickListener(new BottomDialog.OnBottomItemClickListener() {
            @Override
            public void onBottomItemClick(BottomDialog dialog, View view) {

            }
        });
        mMusicPop.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                showNormalPanel();
            }
        });
    }

    private void initCameraRecode(){
        liteAvRecode = LiteAvRecode.getInstance();
        liteAvRecode.init(getContext());
        liteAvRecode.setRecodeLiteListener(this);
        liteAvRecode.startPreview(binding.videoView);
    }

    /**
     * 选择音乐
     */
    private void showMusic() {
        boolean isChooseMusicFlag = RecordMusicManager.getInstance().isChooseMusic();
        if (isChooseMusicFlag) {
            //展示音乐面板
            showMusicPanel();
        }else{
            Intent bgmIntent = new Intent(getContext(), TCMusicActivity.class);
            bgmIntent.putExtra(UGCKitConstants.MUSIC_POSITION, UGCKitRecordConfig.getInstance().musicInfo.position);
            startActivityForResult(bgmIntent, UGCKitConstants.ACTIVITY_MUSIC_REQUEST_CODE);
        }
    }


    /**
     * 开启录制
     */
    private void startRecode() {
        liteAvRecode.startRecode();
    }

    private void pauseRecode() {
        showNormalPanel();
        if (liteAvRecode.isCanEdit()) {
            liteAvRecode.stopRecode();
        }else{
            liteAvRecode.shortPauseRecode();
        }
        if (onFragmentStatusListener!=null)
        onFragmentStatusListener.isRecode(false);
    }

    @Override
    public void onRecodeProgress(String time , long timeInt) {
        binding.recodeTime.setText(time);
        binding.recodeBtn.setRecodeProgress(timeInt);
    }

    @Override
    public void onRecodeSuccess(boolean success) {
//        binding.recodeBtn.recodeComplete();
        if (!success){
            //录制开启失败
            dismissDialog();
            return;
        }
        //开始录制成功
        showRecodePanel();
        if (onFragmentStatusListener!=null)
        onFragmentStatusListener.isRecode(true);
    }

    @Override
    public void onRecodeComplete() {
        //录制成功
        ToastUtils.showShort("录制成功");
        showNormalPanel();
        if (onFragmentStatusListener!=null)
        onFragmentStatusListener.isRecode(false);
        binding.recodeTime.setText(null);
        binding.recodeBtn.setRecodeProgress(0);
        binding.recodeBtn.recodeComplete();
        startEditActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!hasPermission()) {
            String[] permissions = {Manifest.permission.CAMERA , Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS};
            requestPermissions(permissions,REQUEST_PERMISSION);
        }
    }

    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {Manifest.permission.CAMERA , Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS};
            return PermissionUtils.checkPermissionAllGranted(getActivity(), permissions);
        }
        return true;
    }

    private void startEditActivity() {
        Intent intent = new Intent(getContext(), VideoEditerActivity.class);
        intent.putExtra(UGCKitConstants.VIDEO_PATH, VideoRecordSDK.getInstance().getRecordVideoPath());
        startActivity(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        dismissDialog();
    }

    @Override
    public void onDestroyView() {
        liteAvRecode.releaseRecode();
        super.onDestroyView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != UGCKitConstants.ACTIVITY_MUSIC_REQUEST_CODE) {
            return;
        }
        if (data == null) {
            return;
        }
        MusicInfo musicInfo = new MusicInfo();
        musicInfo.path = data.getStringExtra(UGCKitConstants.MUSIC_PATH);
        musicInfo.name = data.getStringExtra(UGCKitConstants.MUSIC_NAME);
        musicInfo.position = data.getIntExtra(UGCKitConstants.MUSIC_POSITION, -1);
        liteAvRecode.setMusicInfo(musicInfo);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        }
    }

    private void hidePanel() {

        binding.recordSpeedLayout.setVisibility(View.GONE);
        binding.rightPanel.setVisibility(View.GONE);
        binding.selMusic.setVisibility(View.GONE);
        binding.recodeBtn.setVisibility(View.INVISIBLE);
        binding.galleryLayout.setVisibility(View.INVISIBLE);
        binding.recodeTime.setVisibility(View.INVISIBLE);
    }

    private void showNormalPanel() {
            binding.recordSpeedLayout.setVisibility(View.GONE);
            binding.recodeTime.setVisibility(View.INVISIBLE);
            binding.galleryLayout.setVisibility(View.VISIBLE);
            binding.selMusic.setVisibility(View.VISIBLE);
            binding.recodeBtn.setVisibility(View.VISIBLE);
            binding.rightPanel.setVisibility(View.VISIBLE);
    }

    private void showRecodePanel() {
        hidePanel();
        binding.recodeTime.setVisibility(View.VISIBLE);
        binding.recodeBtn.setVisibility(View.VISIBLE);
    }

    private void showMusicPanel() {
        hidePanel();
        showMusicPop();
    }


    @Override
    public void isCameraFront(boolean front) {
        if (!front){
            binding.lightLayout.setVisibility(View.VISIBLE);
        }else{
            binding.lightLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void lightOpen(boolean open) {
        if (!open) {
            binding.lightIv.setImageResource(R.mipmap.icon_xsp_shgd);
        }else{
            binding.lightIv.setImageResource(R.mipmap.icon_xsp_shgd_k);
        }
    }

    @Override
    public void onMusicReplace(int position) {
        Intent bgmIntent = new Intent(getContext(), TCMusicActivity.class);
        bgmIntent.putExtra(UGCKitConstants.MUSIC_POSITION, position);
        startActivityForResult(bgmIntent, UGCKitConstants.ACTIVITY_MUSIC_REQUEST_CODE);
    }

    @Override
    public void onSetMusicInfoSuccess(MusicInfo musicInfo) {
        showMusicPanel();
        if (musicPannel!=null)
            musicPannel.setMusicInfo(musicInfo);
    }
    /** music 部分 begin */
    @Override
    public void onMusicVolumChanged(float volume) {
        liteAvRecode.setVolum(volume);
    }

    @Override
    public void onMusicTimeChanged(long startTime, long endTime) {
        liteAvRecode.musicTimeChanged(startTime,endTime);
    }

    @Override
    public void onMusicReplace() {
        liteAvRecode.onMusicReplace();
    }

    @Override
    public void onMusicDelete() {
        liteAvRecode.onMusicDelete();
    }

    @Override
    public void onMusicSelect() {
        liteAvRecode.onMusicSelect();
        mMusicPop.dismiss();
        showNormalPanel();
    }
    /** music 部分 end */

    /** 对应 activity 生命周期 */
    public boolean onBackPress() {
        VideoRecordSDK.getInstance().deleteAllParts();
        return false;
    }

    public void onActivityStop(){
        liteAvRecode.pauseRecode();
    }

    public void onActivityRestart(){
        liteAvRecode.restart();
        VideoRecordSDK.getInstance().startCameraPreview(binding.videoView);
    }

    /**
     * 正在录制状态中,用户准备切换页面
     */
    public void onFragmentChange() {

    }

    public interface OnFragmentStatusListener{
        void isRecode(boolean isRecode);
    }
}
