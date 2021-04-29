package com.xaqinren.healthyelders.moduleLiteav.liteAv;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;

import com.tencent.liteav.demo.beauty.BeautyParams;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.tencent.qcloud.ugckit.UGCKitConstants;
import com.tencent.qcloud.ugckit.module.effect.bgm.TCMusicActivity;
import com.tencent.qcloud.ugckit.module.record.AudioFocusManager;
import com.tencent.qcloud.ugckit.module.record.MusicInfo;
import com.tencent.qcloud.ugckit.module.record.RecordMusicManager;
import com.tencent.qcloud.ugckit.module.record.UGCKitRecordConfig;
import com.tencent.qcloud.ugckit.module.record.VideoRecordSDK;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.ugc.TXRecordCommon;
import com.tencent.ugc.TXUGCRecord;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.moduleLiteav.fragment.StartLiteAVFragment;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.lang.ref.SoftReference;

import me.goldze.mvvmhabit.utils.ToastUtils;
import retrofit2.http.PUT;

import static com.tencent.liteav.demo.beauty.utils.ResourceUtils.getResources;

public class LiteAvRecode implements VideoRecordSDK.OnVideoRecordListener {
    private static LiteAvRecode liteAvRecode;
    private PopupWindow scalePop;

    private LiteAvRecode(){}
    public static LiteAvRecode getInstance(){
        if (liteAvRecode == null) {
            liteAvRecode = new LiteAvRecode();
        }
        return liteAvRecode;
    }

    private boolean cameraSwitch = true;        //是否前置摄像头UI判断
    private boolean mIsTorchOpenFlag;           // 是否打开闪光灯UI判断
    private int minRecordTime = 100;
    private int maxRecordTime = 10 * 1000;
    private int currentAsp = TXRecordCommon.VIDEO_ASPECT_RATIO_9_16;  //屏幕比

    private final int STATUS_IDLE = 0;
    private final int STATUS_RECODE = 1;
    private final int STATUS_PAUSE = 2;
    private int currentStatus = STATUS_IDLE;
    private long currentRecodeTime = 0;


    private RecodeLiteListener recodeLiteListener;
    private SoftReference<Context> context ;


    public void init(Context context) {
        this.context = new SoftReference<>(context);
        VideoRecordSDK.getInstance().initSDK();
        VideoRecordSDK.getInstance().initRecordDraft(this.context.get());
        VideoRecordSDK.getInstance().setOnRestoreDraftListener(new VideoRecordSDK.OnRestoreDraftListener() {
            @Override
            public void onDraftProgress(long duration) {
//                getRecordBottomLayout().updateProgress((int) duration);
//                getRecordBottomLayout().getRecordProgressView().clipComplete();
            }

            @Override
            public void onDraftTotal(long duration) {
//                getRecordRightLayout().setMusicIconEnable(false);
//                getRecordRightLayout().setAspectIconEnable(false);

                float second = duration / 1000f;
                boolean enable = second >= UGCKitRecordConfig.getInstance().mMinDuration / 1000;
//                getTitleBar().setVisible(enable, ITitleBarLayout.POSITION.RIGHT);
            }
        });
        UGCKitRecordConfig config = getRecodeConfig();
        VideoRecordSDK.getInstance().setVideoRecordListener(this);
        VideoRecordSDK.getInstance().setConfig(config);
        VideoRecordSDK.getInstance().updateBeautyParam(config.mBeautyParams);
    }

    public UGCKitRecordConfig getRecodeConfig(){
        UGCKitRecordConfig config = UGCKitRecordConfig.getInstance();
        config.mMinDuration = minRecordTime;
        config.mMaxDuration = maxRecordTime;
        config.mQuality = TXRecordCommon.VIDEO_QUALITY_HIGH;
        config.mFrontCamera = true;
        config.mTouchFocus = false;
        config.mAspectRatio = TXRecordCommon.VIDEO_ASPECT_RATIO_9_16;
        config.mBeautyParams = new BeautyParams();
        config.mBeautyParams.mBeautyStyle = 0;
        config.mBeautyParams.mBeautyLevel = 4;
        config.mBeautyParams.mWhiteLevel = 1;
        return config;
    }

    public void startPreview(TXCloudVideoView videoView) {
        VideoRecordSDK.getInstance().startCameraPreview(videoView);
    }

    public void switchCamera() {
        cameraSwitch = !cameraSwitch;
        LogUtils.i(StartLiteAVFragment.class.getSimpleName(), "cameraSwitch="+cameraSwitch);
        recodeLiteListener.isCameraFront(cameraSwitch);
        /*if (!cameraSwitch){
            binding.lightLayout.setVisibility(View.VISIBLE);
        }else{
            binding.lightLayout.setVisibility(View.GONE);
        }*/
        VideoRecordSDK.getInstance().switchCamera(cameraSwitch);
    }

    public void setFrontCamera() {
        cameraSwitch = true;
        recodeLiteListener.isCameraFront(cameraSwitch);
        VideoRecordSDK.getInstance().switchCamera(cameraSwitch);
    }

    public void setBackCamera() {
        cameraSwitch = false;
        recodeLiteListener.isCameraFront(cameraSwitch);
        VideoRecordSDK.getInstance().switchCamera(cameraSwitch);
    }

    public void setRecodeSpeed(int speed) {
        VideoRecordSDK.getInstance().setRecordSpeed(speed);
    }

    /**
     * 闪光灯
     */
    public void toggleTorch() {
        mIsTorchOpenFlag = !mIsTorchOpenFlag;
        if (mIsTorchOpenFlag) {
            recodeLiteListener.lightOpen(true);
            TXUGCRecord record = VideoRecordSDK.getInstance().getRecorder();
            if (record != null) {
                record.toggleTorch(true);
            }
        } else {
            TXUGCRecord record = VideoRecordSDK.getInstance().getRecorder();
            recodeLiteListener.lightOpen(false);
            if (record != null) {
                record.toggleTorch(false);
            }
        }
    }


    public void setRecodeLiteListener(RecodeLiteListener recodeLiteListener) {
        this.recodeLiteListener = recodeLiteListener;
    }

    public int getCurrentAsp() {
        return currentAsp;
    }


    /**
     * 切换屏幕比例
     * @param currentAsp
     */
    public void setCurrentAsp(int currentAsp) {
        this.currentAsp = currentAsp;
        UGCKitRecordConfig.getInstance().mAspectRatio = this.currentAsp;
        VideoRecordSDK.getInstance().updateAspectRatio();
    }


    /**
     * 选择音乐
     */
    public void setMusicInfo(MusicInfo info) {
        setRecordMusicInfo(info);
    }

    public void setRecordMusicInfo(@NonNull MusicInfo musicInfo) {
        if (musicInfo != null) {
            LogUtils.d(getClass().getSimpleName(), "music name:" + musicInfo.name + ", path:" + musicInfo.path);
        }
        TXUGCRecord record = VideoRecordSDK.getInstance().getRecorder();
        if (record != null) {
            long duration = record.setBGM(musicInfo.path);
            musicInfo.duration = duration;
            LogUtils.d(getClass().getSimpleName(), "music duration:" + musicInfo.duration);
        }
        // 设置音乐信息
        RecordMusicManager.getInstance().setRecordMusicInfo(musicInfo);
        // 更新音乐Pannel
        recodeLiteListener.onSetMusicInfoSuccess(musicInfo);
        // 音乐试听
        RecordMusicManager.getInstance().startPreviewMusic();
    }

    /**
     * 显示放弃录制对话框
     */
    public void showGiveupRecordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context.get());
        AlertDialog alertDialog = builder.setTitle(context.get().getResources().getString(com.tencent.qcloud.ugckit.R.string.ugckit_cancel_record)).setCancelable(false).setMessage(com.tencent.qcloud.ugckit.R.string.ugckit_confirm_cancel_record_content)
                .setPositiveButton(com.tencent.qcloud.ugckit.R.string.ugckit_give_up, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        dialog.dismiss();
                        VideoRecordSDK.getInstance().deleteAllParts();
                        return;
                    }
                })
                .setNegativeButton(context.get().getResources().getString(com.tencent.qcloud.ugckit.R.string.ugckit_wrong_click), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        alertDialog.show();
    }

    @Override
    public void onRecordProgress(long milliSecond) {
        LogUtils.i(getClass().getSimpleName(), "onRecordProgress\tl=");
        int s = (int) (milliSecond / 1000);
        if (s < 10) {
            recodeLiteListener.onRecodeProgress("00:0" + s, milliSecond);
        }else{
            int min = s / 60;
            int sec = s % 60;
            if (min < 0) {
                recodeLiteListener.onRecodeProgress("00:" + sec, milliSecond);
            }else
                recodeLiteListener.onRecodeProgress("0" + min + ":" + (sec < 10 ? "0"+sec : sec) , milliSecond);
        }
        currentRecodeTime = milliSecond;
    }

    public long getCurrentRecodeTime() {
        return currentRecodeTime;
    }

    public boolean isCanEdit() {
        return getCurrentRecodeTime() > minRecordTime;
    }

    @Override
    public void onRecordEvent() {

    }

    @Override
    public void onRecordComplete(TXRecordCommon.TXRecordResult result) {
        if (result.retCode >= 0) {
            // 录制成功， 视频文件在 result.videoPath 中
            boolean editFlag = UGCKitRecordConfig.getInstance().mIsNeedEdit;
            /*if (editFlag) {
                // 录制后需要进行编辑，预处理产生视频缩略图
                startPreprocess(result.videoPath);
            }else{

            }*/
            VideoRecordSDK.getInstance().getRecorder().stopBGM();
            currentStatus = STATUS_IDLE;
            if (currentRecodeTime < minRecordTime) {
                ToastUtil.toastLongMessage("录制最少"+(minRecordTime/1000)+"秒");
                return;
            }
            recodeLiteListener.onRecodeComplete();
        } else {
            // 错误处理，错误码定义请参见 TXRecordCommon 中“录制结果回调错误码定义”
            LogUtils.e("LiteAVRecode", result.descMsg);
        }
    }

    public void setVolum(float volume) {
        TXUGCRecord record = VideoRecordSDK.getInstance().getRecorder();
        if (record != null) {
            record.setBGMVolume(volume);
        }
    }

    public void musicTimeChanged(long startTime, long endTime) {
        MusicInfo musicInfo = RecordMusicManager.getInstance().getMusicInfo();
        musicInfo.startTime = startTime;
        musicInfo.endTime = endTime;

        RecordMusicManager.getInstance().startPreviewMusic();
    }

    public void onMusicReplace() {
        recodeLiteListener.onMusicReplace(UGCKitRecordConfig.getInstance().musicInfo.position);
    }

    public void onMusicDelete() {
        showDeleteMusicDialog();
    }

    public void onMusicSelect() {
        // 停止音乐试听
        RecordMusicManager.getInstance().stopPreviewMusic();
    }


    private void showDeleteMusicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context.get());
        AlertDialog alertDialog = builder.setTitle(getResources().getString(com.tencent.qcloud.ugckit.R.string.ugckit_tips)).setCancelable(false).setMessage(com.tencent.qcloud.ugckit.R.string.ugckit_delete_bgm_or_not)
                .setPositiveButton(com.tencent.qcloud.ugckit.R.string.ugckit_confirm_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        dialog.dismiss();

                        RecordMusicManager.getInstance().deleteMusic();
                        // 录制添加BGM后是录制不了人声的，而音效是针对人声有效的
                        //getRecordRightLayout().setSoundEffectIconEnable(true);

//                        getRecordMusicPannel().setMusicName("");
                        //getRecordMusicPannel().setVisibility(View.GONE);
                    }
                })
                .setNegativeButton(getResources().getString(com.tencent.qcloud.ugckit.R.string.ugckit_btn_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        alertDialog.show();
    }

    public void startRecode() {
        //清理掉草稿箱
        if (currentStatus == STATUS_IDLE) {
            VideoRecordSDK.getInstance().deleteAllParts();
            recode();
        } else if (currentStatus == STATUS_PAUSE) {
            VideoRecordSDK.getInstance().getRecorder().resumeBGM();
            VideoRecordSDK.getInstance().resumeRecord();
            LogUtils.e("LiteAVRecode","已恢复录制");
            recodeLiteListener.onRecodeSuccess();
            currentStatus = STATUS_RECODE;
        } else if (currentStatus == STATUS_RECODE) {
            VideoRecordSDK.getInstance().getRecorder().stopBGM();
            VideoRecordSDK.getInstance().stopRecord();
        } else {
            recode();
        }
//        VideoRecordSDK.getInstance().deleteAllParts();
//        recode();

    }
    private void recode() {
        int result = VideoRecordSDK.getInstance().startRecord();
        if (result != TXRecordCommon.START_RECORD_OK) {
            if (result == -4) {
                //画面还没出来
            } else if (result == -3) {//版本太低

            }
            else if (result == -5) {// licence 验证失败] }
            } else {
                // 启动成功}
                // 结束录
                //VideoRecordSDK.getInstance().stopRecord();
                // 录制完成回调
            }
            currentStatus = STATUS_IDLE;
        }else{
            recodeLiteListener.onRecodeSuccess();
            currentStatus = STATUS_RECODE;
        }
    }

    /**
     * 长按录制停止
     */
    public void shortPauseRecode() {
        VideoRecordSDK.getInstance().getRecorder().stopBGM();
        if (isIsRecodeStatus()) {
            VideoRecordSDK.getInstance().pauseRecord();
            currentStatus = STATUS_PAUSE;
            LogUtils.e("LiteAVRecode","已暂停录制");
        }
    }
    public void pauseRecode() {
        VideoRecordSDK.getInstance().stopCameraPreview();
        VideoRecordSDK.getInstance().getRecorder().stopBGM();
        if (isIsRecodeStatus()) {
            VideoRecordSDK.getInstance().pauseRecord();
            currentStatus = STATUS_PAUSE;
            LogUtils.e("LiteAVRecode","已暂停录制");
        }
    }

    /**
     * 停止录像，去编辑
     */
    public void stopRecode() {
        VideoRecordSDK.getInstance().getRecorder().stopBGM();
        if (isIsRecodeStatus()) {
            currentStatus = STATUS_IDLE;
            VideoRecordSDK.getInstance().stopRecord();
            LogUtils.e("LiteAVRecode","已停止录像");
        }
    }

    public void releaseRecode() {
        currentStatus = STATUS_IDLE;
        if (VideoRecordSDK.getInstance()!=null){
            VideoRecordSDK.getInstance().releaseRecord();
        }
        LogUtils.i(getClass().getSimpleName(), "onDestroyView\tl=");
    }

    public void restart() {

    }

    public boolean isIsRecodeStatus() {
        return currentStatus == STATUS_RECODE;
    }

    public interface RecodeLiteListener{
        void isCameraFront(boolean front);
        void lightOpen(boolean open);
        void onSetMusicInfoSuccess(MusicInfo musicInfo);
        void onMusicReplace(int position);
        void onRecodeProgress(String time,long timeInt);
        void onRecodeComplete();
        void onRecodeSuccess();
    }
}
