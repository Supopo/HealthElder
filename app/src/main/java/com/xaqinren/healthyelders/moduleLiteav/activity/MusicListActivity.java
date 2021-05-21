package com.xaqinren.healthyelders.moduleLiteav.activity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityMusicListBinding;
import com.xaqinren.healthyelders.moduleLiteav.adapter.MusicItemAdapter;
import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicItemBean;
import com.xaqinren.healthyelders.moduleLiteav.viewModel.MusicListViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.goldze.mvvmhabit.base.BaseActivity;

public class MusicListActivity extends BaseActivity<ActivityMusicListBinding, MusicListViewModel> {
    private List<MMusicItemBean> mMusicItemBeans;
    private MusicItemAdapter adapter;
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_music_list;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setTitle("热门");
        mMusicItemBeans = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            mMusicItemBeans.add(new MMusicItemBean("" + random.nextInt(99999999)));
        }
        //抖音一页3个
        adapter = new MusicItemAdapter(R.layout.item_music_item);
        binding.content.setLayoutManager(new LinearLayoutManager(this));
        binding.content.setAdapter(adapter);
        adapter.setList(mMusicItemBeans);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
    }

}
