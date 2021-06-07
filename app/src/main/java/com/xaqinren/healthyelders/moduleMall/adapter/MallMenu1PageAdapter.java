package com.xaqinren.healthyelders.moduleMall.adapter;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ItemMallRvBinding;
import com.xaqinren.healthyelders.moduleHome.bean.MenuBean;
import com.xaqinren.healthyelders.moduleLogin.activity.SelectLoginActivity;
import com.xaqinren.healthyelders.uniApp.UniService;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Lee. on 2021/5/25.
 */
public class MallMenu1PageAdapter extends BaseQuickAdapter<MenuBean, BaseViewHolder> {

    public MallMenu1PageAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder helper, MenuBean menuBean) {
        //注意 ItemBinding 改为自己item_layout的名字 ItemXxxBinding
        ItemMallRvBinding binding = DataBindingUtil.bind(helper.itemView);
        binding.setViewModel(menuBean);
        binding.executePendingBindings();

        MallMenu1Adapter mallMenu1Adapter = new MallMenu1Adapter(R.layout.item_mall_menu1);
        binding.rvContent.setLayoutManager(new GridLayoutManager(getContext(), 5));
        binding.rvContent.setAdapter(mallMenu1Adapter);
        mallMenu1Adapter.setList(menuBean.menuBeans);

        mallMenu1Adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (UserInfoMgr.getInstance().getAccessToken() == null) {
                    Intent intent = new Intent(getContext(),SelectLoginActivity.class);
                    getContext().startActivity(intent);
                    return;
                }
                MenuBean menuBean1 = mallMenu1Adapter.getData().get(position);
                String appId = menuBean1.appId;
                String jumpUrl = menuBean1.jumpUrl;
                UniService.startService(getContext(), appId, 0x10001, jumpUrl);
            }
        });
    }
}
