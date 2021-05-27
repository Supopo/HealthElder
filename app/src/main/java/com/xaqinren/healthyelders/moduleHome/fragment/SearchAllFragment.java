package com.xaqinren.healthyelders.moduleHome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.FragmentAllSearchBinding;
import com.xaqinren.healthyelders.moduleHome.adapter.AllSearchAdapter;
import com.xaqinren.healthyelders.moduleHome.bean.VideoInfo;
import com.xaqinren.healthyelders.moduleHome.viewModel.SearchAllViewModel;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseFragment;

/**
 * Created by Lee. on 2021/5/27.
 */
public class SearchAllFragment extends BaseFragment<FragmentAllSearchBinding, SearchAllViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_all_search;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();

        AllSearchAdapter adapter = new AllSearchAdapter();
        binding.rvContent.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvContent.setAdapter(adapter);

        List<VideoInfo> datas = new ArrayList<>();
        VideoInfo videoInfo1 = new VideoInfo();
        videoInfo1.viewType = 0;
        VideoInfo videoInfo2 = new VideoInfo();
        videoInfo2.viewType = 1;
        VideoInfo videoInfo3 = new VideoInfo();
        videoInfo3.viewType = 2;
        VideoInfo videoInfo4 = new VideoInfo();
        videoInfo4.viewType = 3;
        VideoInfo videoInfo5 = new VideoInfo();
        videoInfo5.viewType = 4;
        datas.add(videoInfo1);
        datas.add(videoInfo2);
        datas.add(videoInfo3);
        datas.add(videoInfo4);
        datas.add(videoInfo5);

        adapter.setNewInstance(datas);
    }
}
