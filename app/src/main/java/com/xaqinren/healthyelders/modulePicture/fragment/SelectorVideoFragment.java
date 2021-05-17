package com.xaqinren.healthyelders.modulePicture.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.tencent.qcloud.ugckit.UGCKitConstants;
import com.tencent.qcloud.ugckit.module.picker.data.PickerManagerKit;
import com.tencent.qcloud.ugckit.module.picker.data.TCVideoFileInfo;
import com.tencent.qcloud.xiaoshipin.videoeditor.TCVideoCutActivity;
import com.tencent.qcloud.xiaoshipin.videojoiner.TCVideoJoinerActivity;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.FragmentSelectorVideoBinding;

import java.util.ArrayList;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.base.BaseViewModel;
/**
 * 拍视频，选择视频
 */
public class SelectorVideoFragment extends BaseFragment<FragmentSelectorVideoBinding, BaseViewModel> {
    //全部视频
    ArrayList<TCVideoFileInfo> list;
    @NonNull
    private Handler mHandlder = new Handler();

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_selector_video;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        loadPictureList();
    }

    /**
     * 功能：加载本地所有图片</p>
     * 包含：<p/>
     * 1、已授权读取手机SD卡权限，则通过{@link PickerManagerKit#getAllPictrue()} 读取本地相册扫描出的所有图片
     * 2、未授权读取手机SD卡权限，在Android6.0需要动态获取权限
     */
    private void loadPictureList() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            mHandlder.post(() -> {
                //TODO 获取到图库,绑定到listview
                list = PickerManagerKit.getInstance(getContext()).getAllVideo();
            });
        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    /**
     * 视频选择后,裁剪页
     */
    public void startVideoCutActivity(TCVideoFileInfo fileInfo) {
        Intent intent = new Intent(getContext(), TCVideoCutActivity.class);
        intent.putExtra(UGCKitConstants.VIDEO_PATH, fileInfo.getFilePath());
        startActivity(intent);
    }
    /**
     * 视频选择后,合成【多个视频】
     */
    public void startVideoJoinActivity(ArrayList<TCVideoFileInfo> videoPathList) {
        Intent intent = new Intent(getContext(), TCVideoJoinerActivity.class);
        intent.putExtra(UGCKitConstants.INTENT_KEY_MULTI_CHOOSE, videoPathList);
        startActivity(intent);
    }



}
