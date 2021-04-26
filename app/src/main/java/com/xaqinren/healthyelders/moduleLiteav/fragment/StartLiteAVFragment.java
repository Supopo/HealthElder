package com.xaqinren.healthyelders.moduleLiteav.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Measure;
import android.icu.util.MeasureUnit;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.tencent.liteav.demo.beauty.model.BeautyInfo;
import com.tencent.liteav.demo.beauty.model.ItemInfo;
import com.tencent.liteav.demo.beauty.model.TabInfo;
import com.tencent.liteav.demo.beauty.view.BeautyPanel;
import com.tencent.qcloud.tim.uikit.utils.PopWindowUtil;
import com.tencent.qcloud.ugckit.UGCKitConstants;
import com.tencent.qcloud.ugckit.UGCKitVideoRecord;
import com.tencent.qcloud.ugckit.basic.ITitleBarLayout;
import com.tencent.qcloud.ugckit.basic.UGCKitResult;
import com.tencent.qcloud.ugckit.module.effect.bgm.TCMusicActivity;
import com.tencent.qcloud.ugckit.module.record.AspectView;
import com.tencent.qcloud.ugckit.module.record.AudioFocusManager;
import com.tencent.qcloud.ugckit.module.record.MusicInfo;
import com.tencent.qcloud.ugckit.module.record.RecordMusicManager;
import com.tencent.qcloud.ugckit.module.record.RecordMusicPannel;
import com.tencent.qcloud.ugckit.module.record.RecordSpeedLayout;
import com.tencent.qcloud.ugckit.module.record.UGCKitRecordConfig;
import com.tencent.qcloud.ugckit.module.record.VideoRecordSDK;
import com.tencent.qcloud.ugckit.module.record.interfaces.IRecordMusicPannel;
import com.tencent.qcloud.ugckit.module.record.interfaces.IVideoRecordKit;
import com.tencent.qcloud.ugckit.utils.ToastUtil;
import com.tencent.qcloud.xiaoshipin.videoeditor.TCVideoEditerActivity;
import com.tencent.qcloud.xiaoshipin.videorecord.TCVideoRecordActivity;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.ugc.TXRecordCommon;
import com.tencent.ugc.TXUGCBase;
import com.tencent.ugc.TXUGCRecord;
import com.tencent.ugc.TXVideoEditConstants;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.FragmentStartLiteAvBinding;
import com.xaqinren.healthyelders.moduleLiteav.liteAv.LiteAvRecode;
import com.xaqinren.healthyelders.moduleZhiBo.viewModel.StartLiveUiViewModel;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.widget.BottomDialog;
import com.xaqinren.healthyelders.widget.RecordButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.utils.ConvertUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.goldze.mvvmhabit.utils.Utils;

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
    private int maxRecordTime = 20 * 1000;
    private int minRecordTime = 5 * 1000;
    private StartLiveUiViewModel liveUiViewModel;
    private LiteAvRecode liteAvRecode;
    private OnFragmentStatusListener onFragmentStatusListener;

    public void setOnFragmentStatusListener(OnFragmentStatusListener onFragmentStatusListener) {
        this.onFragmentStatusListener = onFragmentStatusListener;
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_start_lite_av;
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
                if (integer.intValue() == 0) {
                    //释放
                    VideoRecordSDK.getInstance().releaseRecord();
                }else{
                    //重新加载
//                    initCameraRecode();
                }
            }
        });

    }

    @Override
    public void initData() {
        super.initData();
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
        binding.recodeBtn.setOnRecordButtonListener(new RecordButton.OnRecordButtonListener() {
            @Override
            public void onRecordStart() {
                startRecode();
            }

            @Override
            public void onRecordPause() {
                pauseRecode();
            }
        });
        binding.recordSpeedLayout.setOnRecordSpeedListener(speed -> liteAvRecode.setRecodeSpeed(speed));

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
                    break;
                case R.id.three_four:
                    mAspectRatio = TXRecordCommon.VIDEO_ASPECT_RATIO_3_4;
                    break;
                case R.id.four_three:
                    mAspectRatio = TXRecordCommon.VIDEO_ASPECT_RATIO_4_3;
                    break;
                case R.id.six_nine:
                    mAspectRatio = TXRecordCommon.VIDEO_ASPECT_RATIO_16_9;
                    break;
                case R.id.nine_six:
                    mAspectRatio = TXRecordCommon.VIDEO_ASPECT_RATIO_9_16;
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
        onFragmentStatusListener.isRecode(false);
    }

    @Override
    public void onRecodeProgress(String time) {
        binding.recodeTime.setText(time);
    }

    @Override
    public void onRecodeSuccess() {
        showRecodePanel();
        onFragmentStatusListener.isRecode(true);
    }

    @Override
    public void onRecodeComplete() {
        ToastUtils.showShort("录制成功");
        showNormalPanel();
        onFragmentStatusListener.isRecode(false);
        startEditActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (hasPermission()) {

        }
    }

    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(getActivity(), permissions.toArray(new String[0]), 100);
                return false;
            }
        }
        return true;
    }

    private void startEditActivity() {
        Intent intent = new Intent(getContext(), TCVideoEditerActivity.class);
        intent.putExtra(UGCKitConstants.VIDEO_PATH, VideoRecordSDK.getInstance().getRecordVideoPath());
        startActivity(intent);
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
        int size = VideoRecordSDK.getInstance().getPartManager().getPartsPathList().size();
        if (size > 0) {
            liteAvRecode.showGiveupRecordDialog();
            return true;
        }
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
