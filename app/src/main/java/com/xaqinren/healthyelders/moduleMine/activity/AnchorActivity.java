package com.xaqinren.healthyelders.moduleMine.activity;

import android.os.Bundle;

import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityAnchorBinding;
import com.xaqinren.healthyelders.moduleMine.viewModel.AnchorViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ListPopMenuBean;
import com.xaqinren.healthyelders.utils.IntentUtils;
import com.xaqinren.healthyelders.widget.ListBottomPopup;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;

public class AnchorActivity extends BaseActivity<ActivityAnchorBinding, AnchorViewModel> {
    private String phone = "测试售价好";

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_anchor;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setTitle("主播中心");
        binding.myService.setOnClickListener(v -> {
            callService();
        });
        binding.myWallet.setOnClickListener(v -> {
            startActivity(WalletActivity.class);
        });
    }

    private void callService() {
        List<ListPopMenuBean> menus = new ArrayList<>();
        menus.add(new ListPopMenuBean("拨打电话："+phone));
        ListBottomPopup listBottomPopup = new ListBottomPopup(this, menus);
        listBottomPopup.setOnItemClickListener((adapter, view, position) -> {
            //掉接口
            if (position == 0) {
                IntentUtils.sendPhone(this, phone);
            }
        });
        listBottomPopup.showPopupWindow();
    }
}
