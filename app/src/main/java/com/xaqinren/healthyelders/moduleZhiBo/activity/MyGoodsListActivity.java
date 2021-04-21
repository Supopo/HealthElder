package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityGoodsListBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.GoodsListAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GoodsBean;
import com.xaqinren.healthyelders.moduleZhiBo.viewModel.GoodsListViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.goldze.mvvmhabit.base.BaseActivity;


//注意ActivityBaseBinding换成自己activity_layout对应的名字 ActivityXxxBinding
public class MyGoodsListActivity extends BaseActivity<ActivityGoodsListBinding, GoodsListViewModel> {

    private GoodsListAdapter mAdapter;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_goods_list;

    }

    @Override
    public int initVariableId() {
        return com.xaqinren.healthyelders.BR.viewModel;
    }


    //页面数据初始化方法
    @Override
    public void initData() {
        tvTitle.setText("我的商品");
        tvRight.setText("商品列表");
        initAdapter();
        viewModel.getDataList();
    }


    //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
    @Override
    public void initViewObservable() {
        //下拉刷新
        binding.srlContent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.getDataList();
                //刷新完立即关闭刷新效果是因为本身请求就带有Dialog
                //如果此处不关闭的话应该在网络请求结束后关闭
                binding.srlContent.setRefreshing(false);
            }
        });

        //往adapter里面加载数据
        viewModel.dataList.observe(this, dataList -> {
            if (dataList != null) {
                mAdapter.setList(dataList);
                if (dataList.size() == 0) {
                    //创建适配器.空布局，没有数据时候默认展示的
                    mAdapter.setEmptyView(R.layout.list_empty);
                }
            }
        });


    }


    private Map<Integer, GoodsBean> selectMap = new HashMap<>();

    private void initAdapter() {
        mAdapter = new GoodsListAdapter(R.layout.item_goods_bean);

        binding.rvContent.setLayoutManager(new LinearLayoutManager(this));
        binding.rvContent.setAdapter(mAdapter);

        viewModel.getDataList();


        //Item点击事件
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            selectEvent(position);
        });


        mAdapter.addChildClickViewIds(R.id.iv_select);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            selectEvent(position);
        });

        binding.tvRemove.setOnClickListener(lis -> {
            removeSelect();
        });

    }

    //移除所选商品
    private void removeSelect() {
        List<GoodsBean> temp = new ArrayList<>();
        if (selectMap.size() > 0) {
            for (Integer integer : selectMap.keySet()) {
                temp.add(selectMap.get(integer));
            }
            mAdapter.getData().removeAll(temp);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void selectEvent(int position) {
        mAdapter.getData().get(position).isSelect = !mAdapter.getData().get(position).isSelect;
        mAdapter.notifyItemChanged(position, CodeTable.RESH_VIEW_CODE);

        if (mAdapter.getData().get(position).isSelect) {
            selectMap.put(mAdapter.getData().get(position).id, mAdapter.getData().get(position));
        } else {
            if (selectMap.get(mAdapter.getData().get(position).id) != null) {
                selectMap.remove(mAdapter.getData().get(position).id);
            }
        }

        binding.tvSelect.setText("选中商品：" + (selectMap.size() == 0 ? "" : selectMap.size()));
    }

}
