package com.xaqinren.healthyelders.moduleZhiBo.popupWindow;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.LayoutPopZbjGoodsListBinding;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.GoodsListAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.ZBGoodsListAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GoodsBean;
import com.xaqinren.healthyelders.utils.AnimUtils;

import java.util.ArrayList;
import java.util.List;

import razerdp.basepopup.BasePopupWindow;

/**
 * Created by Lee. on 2021/3/30.
 * 直播间商品列表
 */
public class ZBGoodsListPop extends BasePopupWindow {

    private ZBGoodsListAdapter mAdapter;
    private RecyclerView rvList;
    private TextView tvTitle;

    public ZBGoodsListPop(Context context) {
        super(context);
        //去掉背景
        setBackground(R.color.transparent);
        setShowAnimation(AnimUtils.PopAnimBottom2Enter(context));
        setDismissAnimation(AnimUtils.PopAnimBottom2Exit(context));
        initView();
    }


    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        rvList = findViewById(R.id.rv_list);
        mAdapter = new ZBGoodsListAdapter(R.layout.item_zb_goods);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.setAdapter(mAdapter);

        List<GoodsBean> goodsBeans = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            GoodsBean goodsBean = new GoodsBean();
            goodsBean.title = "商品" + i;
            goodsBeans.add(goodsBean);
        }

        mAdapter.setNewInstance(goodsBeans);

        initEvent();
    }

    private void initEvent() {

    }


    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.layout_pop_zbj_goods_list);
    }

    @Override
    public void onDismiss() {
        super.onDismiss();
    }
}
