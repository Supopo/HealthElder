package com.xaqinren.healthyelders.moduleLiteav.fragment;

import android.Manifest;
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

import com.tencent.liteav.demo.beauty.model.BeautyInfo;
import com.tencent.liteav.demo.beauty.model.ItemInfo;
import com.tencent.liteav.demo.beauty.model.TabInfo;
import com.tencent.liteav.demo.beauty.view.BeautyPanel;
import com.tencent.qcloud.tim.uikit.utils.PopWindowUtil;
import com.tencent.qcloud.ugckit.UGCKitConstants;
import com.tencent.qcloud.ugckit.UGCKitVideoRecord;
import com.tencent.qcloud.ugckit.basic.UGCKitResult;
import com.tencent.qcloud.ugckit.module.effect.bgm.TCMusicActivity;
import com.tencent.qcloud.ugckit.module.record.AspectView;
import com.tencent.qcloud.ugckit.module.record.MusicInfo;
import com.tencent.qcloud.ugckit.module.record.RecordSpeedLayout;
import com.tencent.qcloud.ugckit.module.record.UGCKitRecordConfig;
import com.tencent.qcloud.ugckit.module.record.VideoRecordSDK;
import com.tencent.qcloud.ugckit.module.record.interfaces.IVideoRecordKit;
import com.tencent.qcloud.ugckit.utils.ToastUtil;
import com.tencent.qcloud.xiaoshipin.videoeditor.TCVideoEditerActivity;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.ugc.TXRecordCommon;
import com.tencent.ugc.TXUGCBase;
import com.tencent.ugc.TXUGCRecord;
import com.tencent.ugc.TXVideoEditConstants;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.FragmentStartLiteAvBinding;
import com.xaqinren.healthyelders.widget.BottomDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.utils.ConvertUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.goldze.mvvmhabit.utils.Utils;

public class StartLiteAVFragment extends BaseFragment<FragmentStartLiteAvBinding, BaseViewModel> implements TXRecordCommon.ITXVideoRecordListener {

    private TXCloudVideoView videoView;
    private TXUGCRecord mTXCameraRecord;
    private boolean cameraSwitch = true;        //是否前置摄像头UI判断
    private boolean mIsTorchOpenFlag;           // 是否打开闪光灯UI判断
    private BottomDialog mLvJingPop;            //滤镜弹窗
    private BottomDialog mMeiYanPop;            //美颜弹窗
    private BeautyPanel mMeiYanControl;         //美颜控制器
    private BeautyPanel mLvJingControl;         //滤镜控制器
    private PopupWindow scalePop;               //屏幕比例弹窗
    private AspectView aspectView;              //屏幕比例控件
    private boolean isRecord = false;
    private int maxRecordTime = 20 * 1000;
    private int minRecordTime = 5 * 1000;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_start_lite_av;
    }


    @Override
    public int initVariableId() {
        return BR.viewModel;
    }


    @Override
    public void initData() {
        super.initData();
        initCameraRecode();
        initView();
    }

    private void initView() {
        //翻转
        binding.turnLayout.setOnClickListener(view -> {
            //true:切换前置摄像头 false:切换后置摄像头
            cameraSwitch = !cameraSwitch;
            Log.i(StartLiteAVFragment.class.getSimpleName(), "cameraSwitch="+cameraSwitch);
            if (!cameraSwitch){
                binding.lightLayout.setVisibility(View.VISIBLE);
            }else{
                binding.lightLayout.setVisibility(View.GONE);
            }
            mTXCameraRecord.switchCamera(cameraSwitch);
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
            toggleTorch();
        });
        //选择音乐
        binding.selMusic.setOnClickListener(view -> {

        });
        //录制
        binding.recodeBtn.setOnClickListener(view -> {
            startRecode();
        });

        binding.recordSpeedLayout.setOnRecordSpeedListener(new RecordSpeedLayout.OnRecordSpeedListener() {
            @Override
            public void onSpeedSelect(int speed) {
                mTXCameraRecord.setRecordSpeed(speed);
            }
        });
//
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
            mTXCameraRecord.setAspectRatio(mAspectRatio);
        }
    };

    private void showMYPop() {
        if (mMeiYanPop == null) {
            View filterView = View.inflate(getActivity(), R.layout.pop_beauty_control, null);
            mMeiYanControl = filterView.findViewById(R.id.beauty_pannel);
            mMeiYanControl.setPosition(0);
            mMeiYanControl.setBeautyManager(mTXCameraRecord.getBeautyManager());
            mMeiYanControl.setPopTitle("美颜");
            mMeiYanPop = new BottomDialog(getActivity(), filterView,
                    null);
        }
        mMeiYanPop.show();
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
            mLvJingControl.setBeautyManager(mTXCameraRecord.getBeautyManager());

            mLvJingControl.setPopTitle("滤镜");
            BeautyInfo defaultBeautyInfo = mLvJingControl.getDefaultBeautyInfo();
            mLvJingControl.setBeautyInfo(defaultBeautyInfo);

            mLvJingPop = new BottomDialog(getActivity(), filterView,
                    null);
        }
        mLvJingPop.show();
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

    /**
     * 切换闪光灯开/关
     */
    private void toggleTorch() {
        mIsTorchOpenFlag = !mIsTorchOpenFlag;
        if (mIsTorchOpenFlag) {
            binding.lightIv.setImageResource(R.mipmap.icon_xsp_shgd_k);
            TXUGCRecord record = mTXCameraRecord;
            if (record != null) {
                record.toggleTorch(true);
            }
        } else {
            binding.lightIv.setImageResource(R.mipmap.icon_xsp_shgd);
            TXUGCRecord record = mTXCameraRecord;
            if (record != null) {
                record.toggleTorch(false);
            }
        }
    }

    private void initCameraRecode(){
        mTXCameraRecord = TXUGCRecord.getInstance(getActivity().getApplicationContext());
        mTXCameraRecord.setVideoRecordListener(this);                    // 设置录制回调
        videoView = binding.videoView; // 准备一个预览摄像头画面的
        TXRecordCommon.TXUGCSimpleConfig param = new TXRecordCommon.TXUGCSimpleConfig();
        //param.videoQuality = TXRecordCommon.VIDEO_QUALITY_LOW;        // 360p
        //param.videoQuality = TXRecordCommon.VIDEO_QUALITY_MEDIUM;        // 540p
        param.videoQuality = TXRecordCommon.VIDEO_QUALITY_HIGH;        // 720p
        param.isFront = true;           // 是否使用前置摄像头
        param.minDuration = minRecordTime;    // 视频录制的最小时长 ms
        param.maxDuration = maxRecordTime;    // 视频录制的最大时长 ms
        param.touchFocus = false; // false 为自动聚焦；true 为手动聚焦
        mTXCameraRecord.setAspectRatio(TXRecordCommon.VIDEO_ASPECT_RATIO_9_16);
        mTXCameraRecord.startCameraSimplePreview(param,videoView);
        // 结束画面预览
//        mTXCameraRecord.stopCameraPreview();
    }

    /**
     * 录制效果添加水印
     */
    private void addCameraWater() {
// 3、设置录制效果，这里以添加水印为例
        TXVideoEditConstants.TXRect rect = new TXVideoEditConstants.TXRect();
        rect.x = 0.5f;
        rect.y = 0.5f;
        rect.width = 0.5f;
//        添加水印
        //mTXCameraRecord.setWatermark(BitmapFactory.decodeResource(getResources(), .drawable.water), rect);
// 4、开始录制
        int result = mTXCameraRecord.startRecord();
        if (result != TXRecordCommon.START_RECORD_OK) {
            if (result == -4) {
                //画面还没出来
            } else if (result == -3) {//版本太低

            }
            else if (result == -5) {// licence 验证失败] }
                } else {// 启动成功}
                // 结束录
                mTXCameraRecord.stopRecord();
                // 录制完成回调
                }
        }

    }

    /**
     * 开启录制
     */
    private void startRecode() {
        String info = TXUGCBase.getInstance().getLicenceInfo(getContext());
        Log.i(this.getClass().getSimpleName(), info);
        if (isRecord) {
            restartRecode();
            isRecord = false;
            return;
        }
        int result = mTXCameraRecord.startRecord();
        if (result != TXRecordCommon.START_RECORD_OK) {
            if (result == -4) {
                //画面还没出来
            } else if (result == -3) {//版本太低

            }
            else if (result == -5) {// licence 验证失败] }
            } else {
                // 启动成功}
                // 结束录
                mTXCameraRecord.stopRecord();
                // 录制完成回调
            }
        }else{
            showRecodePanel();
            isRecord = true;
        }
    }
    private void clearRecode() {
        VideoRecordSDK.getInstance().deleteAllParts();
    }
    private void restartRecode(){
        clearRecode();
        int size = VideoRecordSDK.getInstance().getPartManager().getPartsPathList().size();
        if (size == 0) {
            //重录
            startRecode();
        }
    }


    @Override
    public void onRecordEvent(int i, Bundle bundle) {
        Log.i(getClass().getSimpleName(), "onRecordEvent\ti=" + i );

    }

    @Override
    public void onRecordProgress(long l) {
        Log.i(getClass().getSimpleName(), "onRecordProgress\tl="+  l );
        int s = (int) (l / 1000);
        binding.recodeTime.setText("00:" + s);
    }

    public void onRecordComplete (TXRecordCommon.TXRecordResult result){
        if (result.retCode >= 0) {
            // 录制成功， 视频文件在 result.videoPath 中
            ToastUtils.showShort("录制成功");
            isRecord = false;
            clearRecode();
        } else {
            // 错误处理，错误码定义请参见 TXRecordCommon 中“录制结果回调错误码定义”
        }

        showNormalPanel();
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

    private void initWindowParam() {
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void startEditActivity() {
        Intent intent = new Intent(getContext(), TCVideoEditerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mTXCameraRecord!=null){
            mTXCameraRecord.stopCameraPreview();
            mTXCameraRecord.stopRecord();
            mTXCameraRecord.stopBGM();
            mTXCameraRecord.release();
        }

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

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        }
    }

    private void hidePanel() {
        binding.rightPanel.setVisibility(View.INVISIBLE);
        binding.recordSpeedLayout.setVisibility(View.GONE);
        binding.selMusic.setVisibility(View.INVISIBLE);
        binding.recodeBtn.setVisibility(View.INVISIBLE);
        binding.galleryLayout.setVisibility(View.INVISIBLE);
        binding.recodeTime.setVisibility(View.INVISIBLE);
    }

    private void showNormalPanel() {
        binding.rightPanel.setVisibility(View.VISIBLE);
        binding.recordSpeedLayout.setVisibility(View.GONE);
        binding.selMusic.setVisibility(View.VISIBLE);
        binding.recodeBtn.setVisibility(View.VISIBLE);
        binding.galleryLayout.setVisibility(View.VISIBLE);
        binding.recodeTime.setVisibility(View.INVISIBLE);
    }

    private void showRecodePanel() {
        hidePanel();
        binding.recodeTime.setVisibility(View.VISIBLE);
        binding.recodeBtn.setVisibility(View.VISIBLE);
    }

}
