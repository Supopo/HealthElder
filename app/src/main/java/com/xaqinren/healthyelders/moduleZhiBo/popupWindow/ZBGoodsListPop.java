package com.xaqinren.healthyelders.moduleZhiBo.popupWindow;

import android.content.Context;
import android.text.TextUtils;
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
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.GoodsListAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.adapter.ZBGoodsListAdapter;
import com.xaqinren.healthyelders.moduleZhiBo.bean.GoodsBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.LiveInitInfo;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBGoodsBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZBGoodsListRes;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.LiveConstants;
import com.xaqinren.healthyelders.uniApp.UniService;
import com.xaqinren.healthyelders.uniApp.UniUtil;
import com.xaqinren.healthyelders.uniApp.bean.UniEventBean;
import com.xaqinren.healthyelders.utils.AnimUtils;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.widget.LoadingDialog;
import razerdp.basepopup.BasePopupWindow;

/**
 * Created by Lee. on 2021/3/30.
 * ?????????????????????
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
    private Disposable uniSubscribe;

    //??????????????????????????????
    private String uniUrl = Constant.LIVE_ADD_GOODS;
    private String uniAPPId = Constant.JKZL_MINI_APP_ID;

    public ZBGoodsListPop(Context context, String liveRoomId, int type) {
        super(context);
        //????????????
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



        emptyView = View.inflate(getContext(), R.layout.empty_zb_goods, null);
        TextView tvAdd = emptyView.findViewById(R.id.tv_add);

        if (type == 2) {
            tvGl.setVisibility(View.INVISIBLE);
            tvAdd.setVisibility(View.INVISIBLE);
        }

        tvAdd.setOnClickListener(lis -> {
            if (!TextUtils.isEmpty(uniAPPId) && !TextUtils.isEmpty(uniUrl)) {
                UniService.startService(getContext(), uniAPPId, 99, uniUrl);
            }
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
                    //??????????????????
                    setGoodsShow(String.valueOf(mAdapter.getData().get(position).id));
                } else if (type == 2) {
                    if (!TextUtils.isEmpty(mAdapter.getData().get(position).appId) && !TextUtils.isEmpty(mAdapter.getData().get(position).jumpUrl)) {
                        UniService.startService(getContext(), mAdapter.getData().get(position).appId, 99, mAdapter.getData().get(position).jumpUrl);
                    }
                }
            }
        });

        mAdapter.setOnItemClickListener(((adapter, view, position) -> {
            if (!TextUtils.isEmpty(mAdapter.getData().get(position).appId) && !TextUtils.isEmpty(mAdapter.getData().get(position).jumpUrl)) {
                UniService.startService(getContext(), mAdapter.getData().get(position).appId, 99, mAdapter.getData().get(position).jumpUrl);
            }
        }));

        tvGl.setOnClickListener(lis -> {
            if (!TextUtils.isEmpty(uniAPPId) && !TextUtils.isEmpty(uniUrl)) {
                UniService.startService(getContext(), uniAPPId, 99, uniUrl);
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
    private MutableLiveData<ZBGoodsListRes> goodsList = new MutableLiveData<>();

    private void initEvent() {
        setSuccess.observe((LifecycleOwner) getContext(), setSuccess -> {
            if (setSuccess != null && setSuccess) {
                mAdapter.getData().get(nowPos).setCanExplain(!mAdapter.getData().get(nowPos).getCanExplain());
                String jsonString = JSON.toJSONString(mAdapter.getData().get(nowPos));
                if (mAdapter.getData().get(nowPos).getCanExplain()) {
                    //?????????????????????????????????????????????????????????
                    RxBus.getDefault().post(new EventBean(LiveConstants.IMCMD_SHOW_GOODS, jsonString));
                } else {
                    //????????????????????????????????????????????????
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
                if (goodsBeans.commodityList != null) {
                    if (goodsBeans.commodityList.size() == 0) {
                        mAdapter.setEmptyView(emptyView);
                    } else {
                        for (ZBGoodsBean goodsBean : goodsBeans.commodityList) {
                            goodsBean.type = type;
                        }
                        tvTitle.setText("????????????(" + goodsBeans.commodityList.size() + ")");
                        if (mAdapter.getData().size() > 0) {
                            mAdapter.setList(goodsBeans.commodityList);
                        } else {
                            mAdapter.setNewInstance(goodsBeans.commodityList);
                        }
                    }
                }

            }
        });

        uniSubscribe = RxBus.getDefault().toObservable(UniEventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == CodeTable.UNI_RELEASE) {
                    if (event.taskId == 99) {
                        toUniApp = true;
                        UniUtil.openUniApp(getContext(), event.appId, event.jumpUrl, null, event.isSelfUni);
                    }
                } else if (event.msgId == CodeTable.UNI_RELEASE_FAIL) {
                    //ToastUtils.showShort("?????????????????????");
                }
            }
        });
    }

    public boolean toUniApp;

    public void refreshData() {
        toUniApp = false;
        getGoodsList();
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.layout_pop_zbj_goods_list);
    }


    @Override
    public void onDismiss() {
        super.onDismiss();
        if (uniSubscribe != null) {
            uniSubscribe.dispose();
        }
    }
}
