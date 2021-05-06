package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityStartRenzheng2Binding;

import org.json.JSONObject;

import java.util.Map;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * Created by Lee. on 2021/4/24.
 * 身份证认证页面2
 */
public class StartRenZheng2Activity extends BaseActivity<ActivityStartRenzheng2Binding, BaseViewModel> {

    private Bundle extras;
    private Map<String, String> map;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_start_renzheng2;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initParam() {
        super.initParam();
        extras = getIntent().getExtras();
        String json = extras.getString("json");
        map = (Map) JSON.parseObject(json);
    }

    @Override
    public void initData() {
        super.initData();
        if (map != null) {
            binding.etCid.setText((String) map.get("cid"));
            binding.etName.setText((String) map.get("name"));
        }

        tvTitle.setText("实名认证");
        binding.btnNext.setOnClickListener(lis -> {
            startActivity(RenZhengSuccessActivity.class);
        });
    }

}
