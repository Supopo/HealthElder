package com.xaqinren.healthyelders.modulePicture.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.tencent.bugly.proguard.A;
import com.tencent.qcloud.ugckit.UGCKitConstants;
import com.tencent.qcloud.ugckit.UGCKitPicturePicker;
import com.tencent.qcloud.ugckit.component.swipemenu.touch.OnItemMoveListener;
import com.tencent.qcloud.ugckit.module.picker.data.ItemView;
import com.tencent.qcloud.ugckit.module.picker.data.PickerManagerKit;
import com.tencent.qcloud.ugckit.module.picker.data.TCVideoFileInfo;
import com.tencent.qcloud.ugckit.module.picker.view.IPickerLayout;
import com.tencent.qcloud.ugckit.utils.ToastUtil;
import com.tencent.qcloud.xiaoshipin.mainui.list.DividerGridItemDecoration;
import com.tencent.qcloud.xiaoshipin.videochoose.TCPicturePickerActivity;
import com.tencent.qcloud.xiaoshipin.videoeditor.TCVideoCutActivity;
import com.tencent.qcloud.xiaoshipin.videojoiner.TCPictureJoinActivity;
import com.tencent.qcloud.xiaoshipin.videojoiner.TCVideoJoinerActivity;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.FragmentSelectorImageBinding;
import com.xaqinren.healthyelders.modulePicture.adapter.MenuAdapter;
import com.xaqinren.healthyelders.modulePicture.adapter.SelPictureAdapter;
import com.xaqinren.healthyelders.widget.GridDividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.module.OnMultiClickListener;
import me.goldze.mvvmhabit.utils.ConvertUtils;

/**
 * ????????????????????????
 */
public class SelectorImageFragment extends BaseFragment <FragmentSelectorImageBinding, BaseViewModel> implements ItemView.OnDeleteListener, OnItemMoveListener {
    private ArrayList<TCVideoFileInfo> list;
    private ArrayList<TCVideoFileInfo> selList;
    private SelPictureAdapter adapter;

    private MenuAdapter menuAdapter;


    @NonNull
    private Handler mHandlder = new Handler();
    private int mMinSelectedItemCount = 3;
    private int mMaxSelectedItemCount = 35;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_selector_image;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        // ????????????
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
            if (selList.size() >= mMaxSelectedItemCount) {
                ToastUtil.toastShortMessage("??????????????????????????????");
                return;
            }

            TCVideoFileInfo info = list.get(position);
            info.setSelected(!info.isSelected());
            if (info.isSelected()) {
                //???????????????
                info.setCurrentPosition(selList.size() + 1);
                menuAdapter.addItem(info);
                setMenuVisible();
            }else{
                int index = selList.indexOf(info);
                for (int i = index + 1; i < selList.size(); i++) {
                    int currentPosition = selList.get(i).getCurrentPosition();
                    selList.get(i).setCurrentPosition(currentPosition - 1);
                }
                menuAdapter.removeIndex(index);
                setMenuVisible();
            }
            adapter.notifyDataSetChanged();
            if (selList.size() >= mMinSelectedItemCount) {
                binding.btnNext.setEnabled(true);
            }
        });

        menuAdapter = new MenuAdapter(getContext(), selList);
        binding.menuList.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false));
        menuAdapter.setOnItemDeleteListener(this);
        binding.menuList.setAdapter(menuAdapter);
        binding.menuList.setLongPressDragEnabled(true);
        binding.menuList.setOnItemMoveListener(this);
        binding.btnNext.setOnClickListener(new OnMultiClickListener(5000) {
            @Override
            public void onMultiClick(View v) {
                startPictureJoinActivity(selList);
            }
        });
    }

    private void setMenuVisible() {
        if (menuAdapter.getAll().isEmpty()) {
            binding.menuList.setVisibility(View.GONE);
        }else{
            binding.menuList.setVisibility(View.VISIBLE);
        }
        if (menuAdapter.getItemCount() < mMinSelectedItemCount) {
            binding.btnNext.setEnabled(false);
        }
    }

    /**
     * ???????????????,????????????????????????
     */
    /**
     * ??????????????????????????????????????????????????????????????????????????????????????????
     */
    private void startPictureJoinActivity(ArrayList<TCVideoFileInfo> fileInfoList) {
        ArrayList<String> picturePathList = new ArrayList<String>();
        for (TCVideoFileInfo info : fileInfoList) {
            if (Build.VERSION.SDK_INT >= 29) {
                picturePathList.add(info.getFileUri().toString());
            } else {
                picturePathList.add(info.getFilePath());
            }
        }
        Intent intent = new Intent(getContext(), TCPictureJoinActivity.class);
        intent.putExtra(UGCKitConstants.INTENT_KEY_MULTI_PIC_LIST, picturePathList);
        startActivity(intent);
    }


    /**
     * ?????????????????????????????????</p>
     * ?????????<p/>
     * 1????????????????????????SD?????????????????????{@link PickerManagerKit#getAllPictrue()} ??????????????????????????????????????????
     * 2????????????????????????SD???????????????Android6.0????????????????????????
     */
    private void loadPictureList() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            mHandlder.post(() -> {
                list = PickerManagerKit.getInstance(getContext()).getAllPictrue();
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
        if (position < 0) {  // ?????????????????????getAdapterPosition??????????????? -1
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
}
