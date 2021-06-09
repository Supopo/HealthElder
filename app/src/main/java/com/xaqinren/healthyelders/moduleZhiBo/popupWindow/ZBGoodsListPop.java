package com.xaqinren.healthyelders.moduleZhiBo.popupWindow;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.apiserver.LiveRepository;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.LayoutPopZbjGoodsListBinding;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.GoodsListAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.ZBGoodsListAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GoodsBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.LiveConstants;
import com.xaqinren.healthyelders.utils.AnimUtils;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.bus.RxBus;
import razerdp.basepopup.BasePopupWindow;

/**
 * Created by Lee. on 2021/3/30.
 * 直播间商品列表
 */
public class ZBGoodsListPop extends BasePopupWindow {

    private ZBGoodsListAdapter mAdapter;
    private SwipeRefreshLayout srl;
    private RecyclerView rvList;
    private TextView tvTitle;
    private String liveRoomId;
    private View emptyView;
    private int type;

    public ZBGoodsListPop(Context context, String liveRoomId, int type) {
        super(context);
        //去掉背景
        setBackground(R.color.transparent);
        setShowAnimation(AnimUtils.PopAnimBottom2Enter(context));
        setDismissAnimation(AnimUtils.PopAnimBottom2Exit(context));
        this.liveRoomId = liveRoomId;
        this.type = type;

        initView();
    }


    private void initView() {
        srl = findViewById(R.id.srl);
        tvTitle = findViewById(R.id.tv_title);
        rvList = findViewById(R.id.rv_list);
        mAdapter = new ZBGoodsListAdapter(R.layout.item_zb_goods);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.setAdapter(mAdapter);

        emptyView = View.inflate(getContext(), R.layout.empty_zb_goods, null);
        TextView tvAdd = emptyView.findViewById(R.id.tv_add);
        tvAdd.setOnClickListener(lis -> {
            ToastUtil.toastShortMessage("ADD");
        });
        getGoodsList();
        initEvent();

        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                srl.setRefreshing(false);
                getGoodsList();
            }
        });

        mAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                nowPos = position;
                if (type == 1) {
                    //设置讲解商品
                    setGoodsShow(mAdapter.getData().get(position).id);
                }

            }
        });
    }

    private int nowPos;
    private void getGoodsList() {
        LiveRepository.getInstance().getZbGoodsLis(goodsList, liveRoomId);
    }

    private void setGoodsShow(String commodityId) {
        LiveRepository.getInstance().setZBGoodsShow(dismissDialog, setSuccess, liveRoomId, commodityId, true);
    }

    private MutableLiveData<Boolean> setSuccess = new MutableLiveData<>();
    private MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();
    private MutableLiveData<List<GoodsBean>> goodsList = new MutableLiveData<>();

    private void initEvent() {
        setSuccess.observe((LifecycleOwner) getContext(), setSuccess ->{
            if (setSuccess != null) {
                //刷新列表
                if (mAdapter.getData().get(nowPos).canExplain) {
                    //主播群发消息通知大家获取直播中带货信息
                    RxBus.getDefault().post(new EventBean(LiveConstants.IMCMD_SHOW_GOODS,0));
                }else {
                    //主播群发消息通知大家取消带货信息
                    RxBus.getDefault().post(new EventBean(LiveConstants.IMCMD_SHOW_GOODS_CANCEL,0));
                }

            }
        });

        goodsList.observe((LifecycleOwner) getContext(), goodsBeans -> {
            if (goodsBeans != null) {
                if (goodsBeans.size() == 0) {
                    mAdapter.setEmptyView(emptyView);
                } else {
                    for (GoodsBean goodsBean : goodsBeans) {
                        goodsBean.type = type;
                    }

                    mAdapter.setNewInstance(goodsBeans);
                }
            }
        });
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
