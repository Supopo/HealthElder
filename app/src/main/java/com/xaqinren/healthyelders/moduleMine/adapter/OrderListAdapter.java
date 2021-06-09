package com.xaqinren.healthyelders.moduleMine.adapter;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ItemOrderListBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleLogin.activity.SelectLoginActivity;
import com.xaqinren.healthyelders.moduleMine.bean.OrderListBean;
import com.xaqinren.healthyelders.moduleMine.bean.OrderListItemBean;
import com.xaqinren.healthyelders.uniApp.UniService;
import com.xaqinren.healthyelders.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

public class OrderListAdapter extends BaseQuickAdapter<OrderListBean, BaseViewHolder> implements LoadMoreModule {
    //    NOT_PAID("未支付"),
    //    PAID("买家已付款"),
    //    PART_PAID("买家部分付款"),
    //    CONFIRMED("等待商家发货"),
    //    DELIVERING("已出库"),
    //    RECEIPT("已签收"),
    //    CANCELED("交易关闭"),
    //    FINISHED("交易成功"),
    //    DELIVER_FAILED("递送失败"),
    //    WAITING_REFUNDED("待退款"),
    //    PART_PAID_RETURN("部分退款"),
    //    RETURN("已退款");
    private int uniSubKey;
    public OrderListAdapter() {
        super(R.layout.item_order_list);
        addChildClickViewIds(
                R.id.store_name,
                R.id.xgdz,
                R.id.qxdd,
                R.id.ycsh,
                R.id.ckwl,
                R.id.scdd,
                R.id.sqsh,
                R.id.jxfk,
                R.id.qrsh,
                R.id.pj
        );
    }

    public void setUniSubKey(int uniSubKey) {
        this.uniSubKey = uniSubKey;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, OrderListBean orderListBean) {
        ItemOrderListBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setData(orderListBean);
        OrderListItemAdapter adapter = new OrderListItemAdapter();
        binding.itemsList.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.itemsList.setAdapter(adapter);
        binding.itemsList.setNestedScrollingEnabled(false);
        adapter.setList(orderListBean.getItems());

        adapter.setOnItemClickListener((adapter1, view, position) -> {
            if (UserInfoMgr.getInstance().getAccessToken() == null) {
                Intent intent = new Intent(getContext(),SelectLoginActivity.class);
                getContext().startActivity(intent);
                return;
            }
            UniService.startService(getContext(), Constant.JKZL_MINI_APP_ID, uniSubKey, orderListBean.getJumpUrl());
        });
        GlideUtil.intoImageView(getContext(),orderListBean.getStoreLogo(),binding.storeLogo, (int) getContext().getResources().getDimension(R.dimen.dp_2));
        String status = orderListBean.getQueryStatus();
        int childCount = binding.orderStatusLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = binding.orderStatusLayout.getChildAt(i);
            childAt.setVisibility(View.GONE);
        }
        binding.orderStatusLayout.setVisibility(View.GONE);
        if (status.equals("NOT_PAID")) {
            //待支付
            binding.orderStatusLayout.setVisibility(View.VISIBLE);
            binding.xgdz.setVisibility(View.VISIBLE);
            binding.qxdd.setVisibility(View.VISIBLE);
            binding.jxfk.setVisibility(View.VISIBLE);

        } else if (status.equals("PAID")) {
            //等待商家发货
            binding.orderStatusLayout.setVisibility(View.GONE);
        } else if (status.equals("DELIVERING")) {
            //已出库
            binding.orderStatusLayout.setVisibility(View.VISIBLE);
//            binding.ycsh.setVisibility(View.VISIBLE);
            binding.ckwl.setVisibility(View.VISIBLE);
            binding.qrsh.setVisibility(View.VISIBLE);
        } else if (status.equals("RECEIPT") ||  status.equals("FINISHED")) {
            //交易成功,待评价
            binding.orderStatusLayout.setVisibility(View.VISIBLE);
            binding.scdd.setVisibility(View.VISIBLE);
            binding.sqsh.setVisibility(View.GONE);
            binding.pj.setVisibility(View.VISIBLE);
        } else if (status.equals("CANCELED")) {
            //交易关闭
            //交易成功,待评价
            binding.orderStatusLayout.setVisibility(View.VISIBLE);
            binding.scdd.setVisibility(View.VISIBLE);
        } else if (status.equals("WAITING_REFUNDED")) {
            //待退款
        } else if (status.equals("RETURN")) {
            //已退款
        }
    }
}
