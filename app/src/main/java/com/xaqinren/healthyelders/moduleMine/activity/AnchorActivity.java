package com.xaqinren.healthyelders.moduleMine.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.tabs.TabLayout;
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
    private String phone = "13812345678";

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
        binding.llWdkf.setOnClickListener(v -> {
            callService();
        });
        binding.llWallet.setOnClickListener(v -> {
            startActivity(WalletActivity.class);
        });

    }

    private String[] titles = {"今日", "前7天", "前30天"};

    private void callService() {
        List<ListPopMenuBean> menus = new ArrayList<>();
        menus.add(new ListPopMenuBean("拨打电话：" + phone));
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
