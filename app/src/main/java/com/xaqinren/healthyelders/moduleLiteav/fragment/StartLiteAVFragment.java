package com.xaqinren.healthyelders.moduleLiteav.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ModuleInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.luck.picture.lib.PictureSelectionModel;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.tencent.liteav.demo.beauty.model.BeautyInfo;
import com.tencent.liteav.demo.beauty.model.ItemInfo;
import com.tencent.liteav.demo.beauty.model.TabInfo;
import com.tencent.liteav.demo.beauty.view.BeautyPanel;
import com.tencent.qcloud.tim.uikit.utils.FileUtil;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.tencent.qcloud.ugckit.UGCKitConstants;
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK;
import com.tencent.qcloud.ugckit.module.effect.bgm.TCMusicActivity;
import com.tencent.qcloud.ugckit.module.record.MusicInfo;
import com.tencent.qcloud.ugckit.module.record.PhotoSoundPlayer;
import com.tencent.qcloud.ugckit.module.record.RecordModeView;
import com.tencent.qcloud.ugckit.module.record.RecordMusicManager;
import com.tencent.qcloud.ugckit.module.record.RecordMusicPannel;
import com.tencent.qcloud.ugckit.module.record.UGCKitRecordConfig;
import com.tencent.qcloud.ugckit.module.record.VideoRecordSDK;
import com.tencent.qcloud.ugckit.module.record.interfaces.IRecordMusicPannel;
import com.tencent.qcloud.xiaoshipin.videochoose.TCPicturePickerActivity;
import com.tencent.ugc.TXRecordCommon;
import com.tencent.ugc.TXUGCRecord;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.FragmentStartLiteAvBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleLiteav.activity.ChooseMusicActivity;
import com.xaqinren.healthyelders.moduleLiteav.activity.VideoEditerActivity;
import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicItemBean;
import com.xaqinren.healthyelders.moduleLiteav.dialog.MusicSelDialog;
import com.xaqinren.healthyelders.moduleLiteav.liteAv.LiteAvRecode;
import com.xaqinren.healthyelders.moduleLiteav.liteAv.MusicRecode;
import com.xaqinren.healthyelders.moduleLiteav.service.LocationService;
import com.xaqinren.healthyelders.modulePicture.Constant;
import com.xaqinren.healthyelders.modulePicture.activity.PublishTextPhotoActivity;
import com.xaqinren.healthyelders.modulePicture.activity.SelectorMediaActivity;
import com.xaqinren.healthyelders.modulePicture.activity.TackPictureActivity;
import com.xaqinren.healthyelders.moduleZhiBo.activity.StartLiveActivity;
import com.xaqinren.healthyelders.moduleZhiBo.fragment.StartLiveFragment;
import com.xaqinren.healthyelders.moduleZhiBo.viewModel.StartLiveUiViewModel;
import com.xaqinren.healthyelders.utils.GlideEngine;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.widget.BottomDialog;
import com.xaqinren.healthyelders.widget.RecordButton;
import com.xaqinren.healthyelders.widget.comment.CommentPublishDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.utils.PermissionUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

public class StartLiteAVFragment extends BaseFragment<FragmentStartLiteAvBinding, BaseViewModel> implements
        LiteAvRecode.RecodeLiteListener, IRecordMusicPannel.MusicChangeListener {

    private BottomDialog mLvJingPop;            //????????????
    private BottomDialog mMeiYanPop;            //????????????
    //    private BottomDialog mMusicPop;            //????????????
    private MusicSelDialog mMusicPop;            //????????????
    public BeautyPanel mMeiYanControl;         //???????????????
    public BeautyPanel mLvJingControl;         //???????????????
    private RecordMusicPannel musicPannel;         //???????????????
    private PopupWindow scalePop;               //??????????????????
    private boolean isRecord = false;

    private StartLiveUiViewModel liveUiViewModel;
    public LiteAvRecode liteAvRecode;
    private OnFragmentStatusListener onFragmentStatusListener;
    private int REQUEST_PERMISSION = 100;
    private int REQUEST_MUSIC = 10003;
    private String photoPath;
    private int currentMode = RecordButton.VIDEO_MODE;
    private String TAG = getClass().getSimpleName();
    /**
     * ???????????????,????????????,????????????????????????????????????
     */
    private boolean holderScreen = false;
    private boolean isStartMusicActivity;
    private StartLiveFragment startLiveFragment;
    private int type;//type = 1 ??????????????????????????????


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
                        if (integer.intValue() == 0) {
                            if (liteAvRecode != null) {
                                liteAvRecode.setCurrentAsp(TXRecordCommon.VIDEO_ASPECT_RATIO_9_16);
                            }

                            binding.rlLive.setVisibility(View.VISIBLE);
                            binding.rlVideo.setVisibility(View.GONE);

                        } else {
                            if (liteAvRecode != null) {
                                liteAvRecode.setCurrentAsp(mAspectRatio);
                            }

                            binding.rlLive.setVisibility(View.GONE);
                            binding.rlVideo.setVisibility(View.VISIBLE);

                            VideoRecordSDK.getInstance().initSDK();
                            if (integer.intValue() == 2) {
                                //??????????????????
                                currentMode = RecordButton.PHOTO_MODE;
                                changePhotoMode();
                            } else {
                                currentMode = RecordButton.VIDEO_MODE;
                                VideoRecordSDK.getInstance().getRecorder().resumeBGM();
                                changeVideoMode();
                            }
                            //????????????
                            VideoRecordSDK.getInstance().startCameraPreview(binding.videoView);
                        }
                    }
                });

    }

    private void changePhotoMode() {
        //???????????????????????????????????????
        //??????????????????????????????????????????????????????
        if (onFragmentStatusListener != null)
            onFragmentStatusListener.isRecode(false);
        binding.recodeTime.setText(null);
        binding.recodeBtn.setRecodeProgress(0);
        if (liteAvRecode != null && type != 1) {
            liteAvRecode.restart();
        }
        binding.selMusic.setVisibility(View.GONE);
        binding.speedLayout.setVisibility(View.GONE);
        binding.recodeBtn.setMode(RecordButton.PHOTO_MODE);
        if (type == 1) {
            binding.galleryLayout.setVisibility(View.GONE);
        }
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
            type = bundle.getInt("type");
            currentMode = bundle.getInt(com.xaqinren.healthyelders.modulePicture.constant.Constant.RECODE_MODE);
        }
        if (currentMode == RecordButton.PHOTO_MODE) {
            changePhotoMode();
        }
        liveUiViewModel = ViewModelProviders.of(getActivity()).get(StartLiveUiViewModel.class);
        initCameraRecode();
        initView();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        startLiveFragment = new StartLiveFragment();
        fragmentManager.beginTransaction().add(R.id.fl_live, startLiveFragment).commit();
    }

    private void initView() {
        binding.ivBack.setOnClickListener(lis -> {
            getActivity().finish();
        });
        //??????
        binding.turnLayout.setOnClickListener(view -> {
            liteAvRecode.switchCamera();
            startLiveFragment.isBackCamera = !startLiveFragment.isBackCamera;
        });
        //?????????
        binding.speedLayout.setOnClickListener(view -> {
            int v = binding.recordSpeedLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE;
            binding.recordSpeedLayout.setVisibility(v);
        });
        //??????
        binding.filterLayout.setOnClickListener(view -> {
            hidePanel();
            showLJPop();
        });
        //??????
        binding.beautyLayout.setOnClickListener(view -> {
            hidePanel();
            showMYPop();
        });
        //????????????
        binding.scaleLayout.setOnClickListener(view -> showScalePop());
        //?????????
        binding.lightLayout.setOnClickListener(view -> liteAvRecode.toggleTorch());
        //????????????
        binding.selMusic.setOnClickListener(view -> {
            showMusic();
            //            Intent intent = new Intent(getContext(), ChooseMusicActivity.class);
            //            startActivityForResult(intent , REQUEST_MUSIC);
        });
        //??????
        /*binding.recodeBtn.setOnClickListener(view -> {
            startRecode();
        });*/
        binding.galleryLayout.setOnClickListener(view -> {
            if (currentMode == RecordButton.PHOTO_MODE) {
                //?????????????????????
                PictureSelector.create(this)
                        .openGallery(PictureMimeType.ofImage())
                        .loadImageEngine(GlideEngine.createGlideEngine()) // ?????????Demo GlideEngine.java
                        .maxSelectNum(9)// ????????????????????????
                        .isCamera(false)// ????????????????????????
                        .previewEggs(true)//??????????????? ????????????????????????????????????(???????????????????????????????????????????????????)
                        .previewImage(true)// ?????????????????????
                        .enableCrop(false)// ???????????? true or false
                        .compress(true)// ?????????????????? ????????????Luban??????
                        .isAndroidQTransform(false)//???????????? ??????????????????????????????????????????
                        .forResult(PictureConfig.CHOOSE_REQUEST);//????????????onActivityResult code
            } else {
                //?????????????????????
                Intent intent = new Intent(getActivity(), SelectorMediaActivity.class);
                startActivity(intent);
            }
        });
        binding.recodeBtn.setOnRecordButtonListener(new RecordButton.OnRecordButtonListener() {
            @Override
            public void onRecordStart() {
                startRecode();
            }

            @Override
            public void onRecordPause() {
                LogUtils.e(TAG, "onRecordPause");
                if (liteAvRecode.getCurrentRecodeTime() < liteAvRecode.getMinRecordTime()) {
                    ToastUtils.showShort("??????????????????");
                    pauseRecode();
                    return;
                }
                //????????????,????????????
                showDialog();
                pauseRecode();
            }

            @Override
            public void onPhotoSuccess() {
                //??????SDK??????
                PhotoSoundPlayer.playPhotoSound();
                VideoRecordSDK.getInstance().takePhoto(bitmap -> {
                    File directory_pictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    photoPath = FileUtil.saveBitmap(directory_pictures.getAbsolutePath(), bitmap);
                    binding.photoPreview.rlContainer.setVisibility(View.VISIBLE);
                    binding.photoPreview.photoPreviewIv.setImageBitmap(bitmap);
                    holderScreen = true;
                    //??????activity ??????bar
                    if (onFragmentStatusListener != null)
                        onFragmentStatusListener.isRecode(true);
                });
            }
        });
        binding.recodeBtn.setRecodeMaxTime(liteAvRecode.getMaxRecordTime());
        binding.recodeBtn.setRecodeProgressBarWidth((int) getResources().getDimension(R.dimen.dp_6));
        binding.recordSpeedLayout.setOnRecordSpeedListener(speed -> liteAvRecode.setRecodeSpeed(speed));
        binding.photoPreview.cancel.setOnClickListener(view -> {
            File file = new File(photoPath);
            file.deleteOnExit();
            photoPath = "";
            holderScreen = false;
            binding.photoPreview.rlContainer.setVisibility(View.GONE);
            if (onFragmentStatusListener != null)
                onFragmentStatusListener.isRecode(false);
        });
        binding.photoPreview.save.setOnClickListener(view -> {
            holderScreen = false;
            Intent intent = new Intent(getContext(), PublishTextPhotoActivity.class);
            ArrayList<String> paths = new ArrayList<>();
            paths.add(photoPath);
            intent.putExtra(Constant.PHOTO_PATH, paths);
            if (onFragmentStatusListener != null)
                onFragmentStatusListener.isRecode(false);
            binding.photoPreview.rlContainer.setVisibility(View.GONE);
            if (getActivity() instanceof TackPictureActivity) {
                getActivity().setResult(Activity.RESULT_OK, intent);
            } else {
                startActivity(intent);
            }
            //??????????????????????????????????????????????????????????????????????????????????????????
            getActivity().finish();
        });
    }


    /**
     * ????????????
     */
    private void showScalePop() {
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
        if (scalePop.isShowing()) {
            scalePop.dismiss();
            return;
        }
        float dp16 = getResources().getDimension(R.dimen.dp_16);
        scalePop.showAsDropDown(binding.beautyLayout, 0, (int) dp16);
        scalePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });

    }

    private int mAspectRatio = TXRecordCommon.VIDEO_ASPECT_RATIO_9_16;

    private View.OnClickListener scaleClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
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

    public void showMYPop() {
        if (mMeiYanPop == null) {
            View filterView = View.inflate(getActivity(), R.layout.pop_beauty_control, null);
            mMeiYanControl = filterView.findViewById(R.id.beauty_pannel);
            mMeiYanControl.setPosition(0);
            mMeiYanControl.setBeautyManager(VideoRecordSDK.getInstance().getRecorder().getBeautyManager());

            mMeiYanControl.setPopTitle("??????");
            BeautyInfo defaultBeautyInfo = mMeiYanControl.getDefaultBeautyInfo();
            mMeiYanControl.setBeautyInfo(defaultBeautyInfo);
            mMeiYanPop = new BottomDialog(getActivity(), filterView,
                    null);
        }
        mMeiYanPop.show();
        mMeiYanControl.setMeiYDialogBgDraw(getResources().getDrawable(R.drawable.bg_meiy_dialog));
        Window window = mMeiYanPop.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            mMeiYanPop.getWindow().setDimAmount(0f);
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
                if (itemPosition < 3) {
                    startLiveFragment.mBeautyStyle = itemPosition;
                }
                startLiveFragment.mBeautypos = itemPosition;
                return false;
            }

            @Override
            public boolean onLevelChanged(TabInfo tabInfo, int tabPosition, ItemInfo itemInfo, int itemPosition, int beautyLevel) {
                if (itemPosition < 3) {
                    startLiveFragment.mBeautyStyle = itemPosition;
                    startLiveFragment.mBeautyLevel = beautyLevel;
                } else if (itemPosition == 3) {
                    startLiveFragment.mWhitenessLevel = beautyLevel;
                } else {
                    startLiveFragment.mRuddinessLevel = beautyLevel;
                }
                startLiveFragment.mBeautypos = itemPosition;
                startLiveFragment.mAllBeautyLevel = beautyLevel;
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

    public void showLJPop() {
        if (mLvJingPop == null) {
            View filterView = View.inflate(getActivity(), R.layout.pop_beauty_control, null);
            mLvJingControl = filterView.findViewById(R.id.beauty_pannel);
            mLvJingControl.setPosition(1);
            mLvJingControl.setBeautyManager(VideoRecordSDK.getInstance().getRecorder().getBeautyManager());

            mLvJingControl.setPopTitle("??????");
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
                if (itemInfo != null) {
                    itemInfo.itemPos = itemPosition;
                    startLiveFragment.mFilterStyle = itemInfo;
                }
                return false;
            }

            @Override
            public boolean onLevelChanged(TabInfo tabInfo, int tabPosition, ItemInfo itemInfo, int itemPosition, int beautyLevel) {
                if (itemInfo != null) {
                    itemInfo.itemPos = itemPosition;
                    itemInfo.itemLevel = beautyLevel;
                    startLiveFragment.mFilterStyle = itemInfo;
                }
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
            mMusicPop = new MusicSelDialog(getActivity());
            mMusicPop.setEnableVolumeEdit(false, false);
        }
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

            }

            @Override
            public void onMoreClick() {
                mMusicPop.dismiss();
                isStartMusicActivity = true;
                MusicRecode.CURRENT_BOURN = com.xaqinren.healthyelders.moduleLiteav.Constant.BOURN_RECODE;
                Intent intent = new Intent(getActivity(), ChooseMusicActivity.class);
                getActivity().startActivityForResult(intent, CodeTable.MUSIC_BACK);
                getActivity().overridePendingTransition(R.anim.activity_bottom_2enter, R.anim.activity_push_none);
            }

            @Override
            public void onItemPlay(MMusicItemBean bean) {
                binding.musicName.setText(bean.name);
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
                showNormalPanel();
            }

            @Override
            public void onVolumeChange(float oVolume, float bgmVolume) {

            }

            @Override
            public void onStopPlay() {
                binding.musicName.setText("????????????");
            }
        });
    }

    private void initCameraRecode() {
        liteAvRecode = LiteAvRecode.getInstance();
        liteAvRecode.init(getContext());
        liteAvRecode.setRecodeLiteListener(this);
        liteAvRecode.startPreview(binding.videoView);
    }

    /**
     * ????????????
     */
    private void showMusic() {
        showMusicPanel();
    }


    /**
     * ????????????
     */
    private void startRecode() {
        liteAvRecode.startRecode();
    }

    private void pauseRecode() {
        showNormalPanel();
        if (liteAvRecode.isCanEdit()) {
            liteAvRecode.stopRecode();
        } else {
            liteAvRecode.shortPauseRecode();
        }
        if (onFragmentStatusListener != null)
            onFragmentStatusListener.isRecode(false);
    }

    @Override
    public void onRecodeProgress(String time, long timeInt) {
        binding.recodeTime.setText(time);
        binding.recodeBtn.setRecodeProgress(timeInt);
    }

    @Override
    public void onRecodeSuccess(boolean success) {
        //        binding.recodeBtn.recodeComplete();
        if (!success) {
            //??????????????????
            dismissDialog();
            showNormalPanel();
            return;
        }
        //??????????????????
        showRecodePanel();
        if (onFragmentStatusListener != null)
            onFragmentStatusListener.isRecode(true);
    }

    @Override
    public void onRecodeComplete() {
        //????????????
        ToastUtils.showShort("????????????");
        showNormalPanel();
        if (onFragmentStatusListener != null)
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
            String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS};
            requestPermissions(permissions, REQUEST_PERMISSION);
        }
    }

    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS};
            return PermissionUtils.checkPermissionAllGranted(getActivity(), permissions);
        }
        return true;
    }

    private void startEditActivity() {
        MMusicItemBean useMusicItem = MusicRecode.getInstance().getUseMusicItem();
        Intent intent = new Intent(getContext(), VideoEditerActivity.class);
        intent.putExtra(UGCKitConstants.VIDEO_PATH, VideoRecordSDK.getInstance().getRecordVideoPath());
        intent.putExtra(UGCKitConstants.VIDEO_HAS_MUSIC, useMusicItem != null);
        startActivity(intent);
        //todo ????????????????????????????????????????????????????????????
        //        RxBus.getDefault().post(new EventBean(CodeTable.CODE_SUCCESS,"overLive-zb"));
    }

    @Override
    public void onStop() {
        super.onStop();
        dismissDialog();
    }

    @Override
    public void onDestroyView() {
        if (liteAvRecode != null) {
            liteAvRecode.releaseRecode();
        }
        super.onDestroyView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                //????????????
                List<LocalMedia> result = PictureSelector.obtainMultipleResult(data);
                ArrayList<String> paths = new ArrayList<>();
                for (LocalMedia media : result) {
                    String path = media.getRealPath();
                    paths.add(path);
                }
                //??????????????????
                Intent intent = new Intent(getContext(), PublishTextPhotoActivity.class);
                intent.putStringArrayListExtra(Constant.PHOTO_PATH, paths);
                startActivity(intent);
            }
        } else if (resultCode == UGCKitConstants.ACTIVITY_MUSIC_REQUEST_CODE) {
            if (data == null) {
                return;
            }
            MusicInfo musicInfo = new MusicInfo();
            musicInfo.path = data.getStringExtra(UGCKitConstants.MUSIC_PATH);
            musicInfo.name = data.getStringExtra(UGCKitConstants.MUSIC_NAME);
            musicInfo.position = data.getIntExtra(UGCKitConstants.MUSIC_POSITION, -1);
            liteAvRecode.setMusicInfo(musicInfo);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        }
    }

    public void hidePanel() {

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
        if (currentMode == RecordButton.VIDEO_MODE)
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
        if (!front) {
            binding.lightLayout.setVisibility(View.VISIBLE);
        } else {
            binding.lightLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void lightOpen(boolean open) {
        if (!open) {
            binding.lightIv.setImageResource(R.mipmap.icon_xsp_shgd);
        } else {
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
        if (musicPannel != null)
            musicPannel.setMusicInfo(musicInfo);
    }

    /**
     * music ?????? begin
     */
    @Override
    public void onMusicVolumChanged(float volume) {
        liteAvRecode.setVolum(volume);
    }

    @Override
    public void onMusicTimeChanged(long startTime, long endTime) {
        liteAvRecode.musicTimeChanged(startTime, endTime);
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
    /** music ?????? end */

    /**
     * ?????? activity ????????????
     */
    public boolean onBackPress() {
        if (holderScreen) {
            holderScreen = false;
            binding.photoPreview.rlContainer.setVisibility(View.GONE);
            if (onFragmentStatusListener != null)
                onFragmentStatusListener.isRecode(false);
            return true;
        }
        VideoRecordSDK.getInstance().deleteAllParts();
        return false;
    }

    public void onActivityStop() {
        if (isStartMusicActivity) {
            return;
        }
        if (liteAvRecode != null) {
            liteAvRecode.pauseRecode();
        }
    }

    public void onMusicSelActivityBack() {
        showMusic();
    }

    public void onActivityRestart() {
        isStartMusicActivity = false;
        //        liteAvRecode.restart();
        VideoRecordSDK instance = VideoRecordSDK.getInstance();
        instance.initSDK();
        VideoRecordSDK.getInstance().startCameraPreview(binding.videoView);
        RecordMusicManager.getInstance().deleteMusic();
        if (MusicRecode.getInstance().getUseMusicItem() != null) {
            MMusicItemBean bean = MusicRecode.getInstance().getUseMusicItem();
            MusicInfo musicInfo = new MusicInfo();
            musicInfo.path = bean.musicUrl;
            musicInfo.name = bean.name;
            if (bean.localPath != null) {
                musicInfo.path = bean.localPath;
            }

            RecordMusicManager.getInstance().setRecordMusicInfo(musicInfo);

            TXUGCRecord record = VideoRecordSDK.getInstance().getRecorder();
            if (record != null) {
                long duration = record.setBGM(musicInfo.path);
                record.setBGMVolume(1);
                musicInfo.duration = duration;
            }
            // ??????????????????
            binding.musicName.setText(bean.name);
        }

    }

    /**
     * ?????????????????????,????????????????????????
     */
    public void onFragmentChange() {

    }

    public interface OnFragmentStatusListener {
        void isRecode(boolean isRecode);
    }
}
