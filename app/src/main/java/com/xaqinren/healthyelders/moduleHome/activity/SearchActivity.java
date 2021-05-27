package com.xaqinren.healthyelders.moduleHome.activity;

import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivitySearchBinding;
import com.xaqinren.healthyelders.moduleHome.adapter.HistoryTagAdapter;
import com.xaqinren.healthyelders.moduleHome.adapter.HotTagAdapter;
import com.xaqinren.healthyelders.moduleHome.viewModel.SearchViewModel;
import com.xaqinren.healthyelders.widget.AutoLineLayoutManager;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * Created by Lee. on 2021/5/27.
 * 首页-搜索页面
 */
public class SearchActivity extends BaseActivity<ActivitySearchBinding, SearchViewModel> {

    private HotTagAdapter hotTagAdapter;
    private HistoryTagAdapter historyTagAdapter;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_search;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setStatusBarTransparentBlack();

        historyTagAdapter = new HistoryTagAdapter(R.layout.item_search_history);
        binding.rvHistory.setLayoutManager(new AutoLineLayoutManager());
        binding.rvHistory.setAdapter(historyTagAdapter);

        hotTagAdapter = new HotTagAdapter(R.layout.item_search_hot);
        binding.rvHot.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvHot.setAdapter(hotTagAdapter);

        viewModel.getHistoryList();
        viewModel.getHotList();

        binding.tvBack.setOnClickListener(lis ->{
            finish();
        });
        binding.tvClean.setOnClickListener(lis ->{
            //清除搜索历史
        });
    }


    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.searchHistoryList.observe(this, datas -> {
            if (datas != null) {
                historyTagAdapter.setNewInstance(datas);
            }
        });
        viewModel.searchHotList.observe(this, datas -> {
            if (datas != null) {
                hotTagAdapter.setNewInstance(datas);
            }
        });
    }
}
