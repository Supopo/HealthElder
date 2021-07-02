package com.xaqinren.healthyelders.moduleZhiBo.activity;

import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.ActivityStartRenzheng2Binding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleZhiBo.viewModel.StartRenZheng2ViewModel;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.RxBus;

/**
 * Created by Lee. on 2021/4/24.
 * 身份证认证页面2
 */
public class StartRenZheng2Activity extends BaseActivity<ActivityStartRenzheng2Binding, StartRenZheng2ViewModel> {

    private Bundle extras;
    private Map<String, String> map = new HashMap<>();
    private Disposable subscribe;

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
        map.put("address", extras.getString("address"));
        map.put("idNumber", extras.getString("idNumber"));
        map.put("birthday", extras.getString("birthday"));
        map.put("name", extras.getString("name"));
        map.put("gender", extras.getString("gender"));
        map.put("ethnic", extras.getString("ethnic"));
        map.put("issueAuthority", extras.getString("issueAuthority"));
        map.put("signDate", extras.getString("signDate"));
        map.put("expiryDate", extras.getString("expiryDate"));
    }

    @Override
    public void initData() {
        super.initData();
        if (map != null) {
            binding.etCid.setText((String) map.get("idNumber"));
            binding.etName.setText((String) map.get("name"));
        }

        tvTitle.setText("实名认证");
        binding.btnNext.setOnClickListener(lis -> {
            viewModel.toRenZheng(map);
        });
        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(eventBean -> {
            if (eventBean != null) {
                if (eventBean.msgId == CodeTable.FINISH_ACT && eventBean.content.equals("rz-success")) {
                    finish();
                }
            }
        });
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.renZhengSuccess.observe(this, isSuccess -> {
            int key = getIntent().getIntExtra(Constant.REN_ZHENG_TYPE, 0);
            extras.putInt(Constant.REN_ZHENG_TYPE, key);
            startActivity(RenZhengSuccessActivity.class, extras);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscribe != null) {
            subscribe.dispose();
        }
    }
}
