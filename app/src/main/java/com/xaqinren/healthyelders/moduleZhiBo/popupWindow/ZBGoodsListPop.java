package com.xaqinren.healthyelders.moduleZhiBo.popupWindow;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
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
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBGoodsBean;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.LiveConstants;
import com.xaqinren.healthyelders.utils.AnimUtils;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.widget.LoadingDialog;
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
    private TextView tvGl;
    private String liveRoomId;
    private View emptyView;
    private int type;
    private LoadingDialog loadingDialog;
    private ImageView ivBack;

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
        loadingDialog = new LoadingDialog(getContext());

        srl = findViewById(R.id.srl);
        ivBack = findViewById(R.id.iv_back);
        tvGl = findViewById(R.id.tv_gl);
        tvTitle = findViewById(R.id.tv_title);
        rvList = findViewById(R.id.rv_list);
        mAdapter = new ZBGoodsListAdapter(R.layout.item_zb_goods);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.setAdapter(mAdapter);

        if (type == 2) {
            tvGl.setVisibility(View.INVISIBLE);
        }

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

        ivBack.setOnClickListener(lis -> {
            dismiss();
        });
        mAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                nowPos = position;
                if (type == 1) {
                    //设置讲解商品
                    setGoodsShow(String.valueOf(mAdapter.getData().get(position).id));
                }
            }
        });
    }

    private int nowPos;

    private void getGoodsList() {
        LiveRepository.getInstance().getZbGoodsLis(goodsList, liveRoomId);
    }

    private void setGoodsShow(String commodityId) {
        loadingDialog.show();
        LiveRepository.getInstance().setZBGoodsShow(dismissDialog, setSuccess, liveRoomId, commodityId, !mAdapter.getData().get(nowPos).getCanExplain());
    }

    private MutableLiveData<Boolean> setSuccess = new MutableLiveData<>();
    private MutableLiveData<Boolean> dismissDialog = new MutableLiveData<>();
    private MutableLiveData<List<ZBGoodsBean>> goodsList = new MutableLiveData<>();

    private void initEvent() {
        setSuccess.observe((LifecycleOwner) getContext(), setSuccess -> {
            if (setSuccess != null && setSuccess) {
                mAdapter.getData().get(nowPos).setCanExplain(!mAdapter.getData().get(nowPos).getCanExplain());
                String jsonString = JSON.toJSONString(mAdapter.getData().get(nowPos));
                LogUtils.v("--", jsonString);
                if (mAdapter.getData().get(nowPos).getCanExplain()) {
                    //主播群发消息通知大家获取直播中带货信息
                    RxBus.getDefault().post(new EventBean(LiveConstants.IMCMD_SHOW_GOODS, jsonString));
                } else {
                    //主播群发消息通知大家取消带货信息
                    RxBus.getDefault().post(new EventBean(LiveConstants.IMCMD_SHOW_GOODS_CANCEL, ""));
                }
                getGoodsList();

            }
        });
        dismissDialog.observe((LifecycleOwner) getContext(), dismissDialog -> {
            if (dismissDialog != null) {
                if (dismissDialog) {
                    loadingDialog.dismiss();
                }
            }
        });

        goodsList.observe((LifecycleOwner) getContext(), goodsBeans -> {
            if (goodsBeans != null) {
                if (goodsBeans.size() == 0) {
                    mAdapter.setEmptyView(emptyView);
                } else {
                    for (ZBGoodsBean goodsBean : goodsBeans) {
                        goodsBean.type = type;
                    }
                    tvTitle.setText("直播商品(" + goodsBeans.size() + ")");
                    if (mAdapter.getData().size() > 0) {
                        mAdapter.setList(goodsBeans);
                    } else {
                        mAdapter.setNewInstance(goodsBeans);
                    }
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
