package com.xaqinren.healthyelders.moduleLiteav.activity;

import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.hfopen.sdk.entity.ChannelItem;
import com.hfopen.sdk.entity.ChannelSheet;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityChooseMusicBinding;
import com.xaqinren.healthyelders.moduleLiteav.viewModel.ChooseMusicViewModel;
import com.xaqinren.healthyelders.msuciSdk.MusicSdk;
import com.xaqinren.healthyelders.utils.LogUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.ToastUtils;

public class ChooseMusicActivity extends BaseActivity<ActivityChooseMusicBinding, ChooseMusicViewModel> {

    private final String TAG = "ChooseMusicActivity";
    private ArrayList<ChannelItem> channelItems;
    private HashMap<String, List< com.hfopen.sdk.entity.Record>> recordHashMap;

    private int cSheetPage = 1, cSheetPageSize = 10;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_choose_music;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        channelItems = new ArrayList<>();
        recordHashMap = new HashMap<>();
        getChannelList();
    }
    private void getChannelList() {
        MusicSdk.INSTANCE.channelList(new MusicSdk.OnDataBack<ArrayList<ChannelItem>>() {
            @Override
            public void fail(@NotNull String msg) {
                ToastUtils.showShort(msg);
            }

            @Override
            public void success(ArrayList<ChannelItem> data) {
                LogUtils.e(TAG, JSON.toJSONString(data));
                channelItems = data;
                getChannelSheet(data.get(0).getGroupId(), cSheetPage, cSheetPageSize);
            }
        });
    }
    private void getChannelSheet(String group , int page , int pageSize) {
        MusicSdk.INSTANCE.channelSheet(group, null, null, page, pageSize, new MusicSdk.OnDataBack<ChannelSheet>() {
            @Override
            public void fail(@Nullable String msg) {
                ToastUtils.showShort(msg);
            }

            @Override
            public void success(ChannelSheet data) {
                LogUtils.e(TAG, JSON.toJSONString(data));
                if (recordHashMap.containsKey(group)){
                    cSheetPage++;
                    recordHashMap.get(group).addAll(data.getRecord());
                }else{
                    recordHashMap.put(group, data.getRecord());
                }
            }
        });
    }
}
