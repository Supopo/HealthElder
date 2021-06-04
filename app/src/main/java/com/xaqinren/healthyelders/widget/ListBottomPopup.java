package com.xaqinren.healthyelders.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ListPopMenuBean;
import com.xaqinren.healthyelders.utils.AnimUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import razerdp.basepopup.BasePopupWindow;

/**
 * Created by Lee. on 2021/4/24.
 * 列表形式的底部弹窗
 */
public class ListBottomPopup extends BasePopupWindow {
    public List<ListPopMenuBean> menus;
    private BaseQuickAdapter<ListPopMenuBean, BaseViewHolder> adapter;
    private OnItemClickListener itemClickListener;

    public ListBottomPopup(Context context) {
        super(context);
    }

    public ListBottomPopup(Context context, List<ListPopMenuBean> menus) {
        super(context);
        setBackground(R.color.transparent);
        setShowAnimation(AnimUtils.PopAnimBottom2Enter(context));
        setDismissAnimation(AnimUtils.PopAnimBottom2Exit(context));
        this.menus = menus;
        initView();
    }

    public ListBottomPopup(Context context, List<ListPopMenuBean> menus, OnItemClickListener itemClickListener) {
        super(context);
        setShowAnimation(AnimUtils.PopAnimBottom2Enter(context));
        setDismissAnimation(AnimUtils.PopAnimBottom2Exit(context));
        this.menus = menus;
        this.itemClickListener = itemClickListener;
        initView();
    }

    private void initView() {
        RecyclerView rvPop = findViewById(R.id.rv_pop);
        TextView tvCancel = findViewById(R.id.tv_cancel);
        RelativeLayout rlPop = findViewById(R.id.rl_pop);

        //根据item计Pop的高度，不然是整页
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rlPop.getLayoutParams();
        lp.height = (int) ScreenUtil.dp2px(getContext(), (64 + menus.size() * 57));
        rlPop.setLayoutParams(lp);

        rvPop.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BaseQuickAdapter<ListPopMenuBean, BaseViewHolder>(R.layout.item_pop_list) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, ListPopMenuBean menuBean) {
                TextView tvMenu = holder.getView(R.id.tv_menu);
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tvMenu.getLayoutParams();
                lp.height = (int) ScreenUtil.dp2px(getContext(), 56);
                tvMenu.setLayoutParams(lp);

                holder.setText(R.id.tv_menu, menuBean.menuName);
                if (menuBean.textColor != 0) {
                    tvMenu.setTextColor(menuBean.textColor);
                }
                if (menuBean.textSize != 0) {
                    tvMenu.setTextSize(menuBean.textSize);
                }
                if (menuBean.textStyle == 1) {
                    tvMenu.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                }
            }
        };
        rvPop.setAdapter(adapter);
        if (menus != null) {
            adapter.setNewInstance(menus);
        }

        adapter.setOnItemClickListener(itemClickListener);
        tvCancel.setOnClickListener(lis -> {
            dismiss();
        });

    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener){
        adapter.setOnItemClickListener(itemClickListener);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_bottom_list);
    }

}
