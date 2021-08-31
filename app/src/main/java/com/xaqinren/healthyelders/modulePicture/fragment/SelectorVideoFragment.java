package com.xaqinren.healthyelders.modulePicture.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tencent.liteav.demo.beauty.BeautyParams;
import com.tencent.qcloud.ugckit.UGCKitConstants;
import com.tencent.qcloud.ugckit.component.swipemenu.touch.OnItemMoveListener;
import com.tencent.qcloud.ugckit.module.effect.VideoEditerSDK;
import com.tencent.qcloud.ugckit.module.picker.data.ItemView;
import com.tencent.qcloud.ugckit.module.picker.data.PickerManagerKit;
import com.tencent.qcloud.ugckit.module.picker.data.TCVideoFileInfo;
import com.tencent.qcloud.ugckit.module.record.UGCKitRecordConfig;
import com.tencent.qcloud.ugckit.module.record.VideoRecordSDK;
import com.tencent.qcloud.xiaoshipin.videoeditor.TCVideoCutActivity;
import com.tencent.qcloud.xiaoshipin.videojoiner.TCVideoJoinerActivity;
import com.tencent.ugc.TXRecordCommon;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.FragmentSelectorVideoBinding;
import com.xaqinren.healthyelders.moduleLiteav.activity.VideoEditerActivity;
import com.xaqinren.healthyelders.modulePicture.adapter.MenuAdapter;
import com.xaqinren.healthyelders.modulePicture.adapter.SelPictureAdapter;
import com.xaqinren.healthyelders.widget.GridDividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.module.OnMultiClickListener;
import me.goldze.mvvmhabit.utils.ConvertUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * 拍视频，选择视频
 */
public class SelectorVideoFragment extends BaseFragment<FragmentSelectorVideoBinding, BaseViewModel> implements ItemView.OnDeleteListener, OnItemMoveListener {
    private ArrayList<TCVideoFileInfo> list;
    private ArrayList<TCVideoFileInfo> selList;
    private SelPictureAdapter adapter;
    private MenuAdapter menuAdapter;
    private long maxTime = 3 * 60 * 1000 + 1000;

    @NonNull
    private Handler mHandlder = new Handler();
    private int mMinSelectedItemCount = 1;


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
        selList = new ArrayList<>();
        loadPictureList();

        adapter = new SelPictureAdapter(R.layout.item_sel_picture);
        View footer = View.inflate(getContext(), R.layout.header_empty_148dp, null);
        View viewById = footer.findViewById(R.id.view);
        ViewGroup.LayoutParams layoutParams = viewById.getLayoutParams();
        layoutParams.height = (int) getResources().getDimension(R.dimen.dp_178);
        viewById.setLayoutParams(layoutParams);
        binding.imageList.setLayoutManager(new GridLayoutManager(getContext(), 4));
        binding.imageList.addItemDecoration(new GridDividerItemDecoration(ConvertUtils.dp2px(2), getResources().getColor(R.color.transparent)));
        binding.imageList.setAdapter(adapter);
        adapter.addFooterView(footer);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            TCVideoFileInfo info = list.get(position);

            if (info.isSelected()) {
                //取消
                info.setSelected(!info.isSelected());
                int index = selList.indexOf(info);
                for (int i = index + 1; i < selList.size(); i++) {
                    int currentPosition = selList.get(i).getCurrentPosition();
                    selList.get(i).setCurrentPosition(currentPosition - 1);
                }
                menuAdapter.removeIndex(index);
                setMenuVisible();
            }else {
                //计算添加后是否超过3分钟
                if (checkMaxTime(info.getDuration())) {
                    ToastUtils.showShort("最多支持选择3分钟");
                    return;
                }
                info.setSelected(!info.isSelected());
                //更改序列号
                info.setCurrentPosition(selList.size() + 1);
                menuAdapter.addItem(info);
                setMenuVisible();
            }

            adapter.notifyDataSetChanged();
            if (selList.size() >= mMinSelectedItemCount) {
                binding.btnNext.setEnabled(true);
            }
        });

        menuAdapter = new MenuAdapter(getContext(), selList);
        binding.menuList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        menuAdapter.setOnItemDeleteListener(this);
        binding.menuList.setAdapter(menuAdapter);
        binding.menuList.setLongPressDragEnabled(true);
        binding.menuList.setOnItemMoveListener(this);

        binding.btnNext.setOnClickListener(new OnMultiClickListener(5000) {
            @Override
            public void onMultiClick(View v) {
                if (selList.size() == 1) {
                    Intent intent = new Intent(getContext(), VideoEditerActivity.class);
                    intent.putExtra(UGCKitConstants.VIDEO_PATH, selList.get(0).getFilePath());
                    startActivity(intent);
                } else
                    startVideoJoinActivity(selList);
            }
        });
    }

    private boolean checkMaxTime() {
        long time = 0;
        for (TCVideoFileInfo info : selList) {
            time += info.getDuration();
        }
        if (time > maxTime) {
            return true;
        }
        return false;
    }

    private boolean checkMaxTime(long duration) {
        long time = 0;
        for (TCVideoFileInfo info : selList) {
            Log.v("--Time", info.getFileName() + "-" + info.getDuration());
            time += info.getDuration();
        }
        if (time > maxTime) {
            //当前已经大于3分钟
            return true;
        }
        time += duration;
        if (time > maxTime) {
            //加上当前需要添加的后大于3分钟
            return true;
        }
        return false;
    }

    private void setMenuVisible() {
        if (menuAdapter.getAll().isEmpty()) {
            binding.menuList.setVisibility(View.GONE);
        } else {
            binding.menuList.setVisibility(View.VISIBLE);
        }
        if (menuAdapter.getItemCount() < mMinSelectedItemCount) {
            binding.btnNext.setEnabled(false);
        }
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
                list = PickerManagerKit.getInstance(getContext()).getAllVideo();
                adapter.setList(list);
            });
        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDelete(int position) {
        if (position < 0) {  // 异常情况处理，getAdapterPosition可能返回为 -1
            return;
        }
        TCVideoFileInfo info = selList.get(position);
        info.setSelected(false);
        for (int i = position + 1; i < selList.size(); i++) {
            int currentPosition = selList.get(i).getCurrentPosition();
            selList.get(i).setCurrentPosition(currentPosition - 1);
        }
        menuAdapter.removeIndex(position);
        adapter.notifyDataSetChanged();
        setMenuVisible();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        TCVideoFileInfo info1 = selList.get(toPosition);
        TCVideoFileInfo info2 = selList.get(fromPosition);
        int index1 = list.indexOf(info1);
        int index2 = list.indexOf(info2);
        int po1 = info1.getCurrentPosition();
        int po2 = info2.getCurrentPosition();
        list.get(index1).setCurrentPosition(po2);
        list.get(index2).setCurrentPosition(po1);

        Collections.swap(selList, fromPosition, toPosition);
        menuAdapter.notifyItemMoved(fromPosition, toPosition);

        adapter.notifyItemChanged(index1);
        adapter.notifyItemChanged(index2);

        return true;
    }

    @Override
    public void onItemDismiss(int position) {

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
