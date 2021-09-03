package com.xaqinren.healthyelders.moduleMsg.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.tencent.qcloud.ugckit.utils.ToastUtil;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityEmptyBinding;
import com.xaqinren.healthyelders.moduleMsg.viewModel.EmptyViewModel;
import com.xaqinren.healthyelders.widget.InputPwdDialog;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * Created by Lee. on 2021/9/3.
 * 用于消息页面中转进入直播间弹框
 */
public class EmptyActivity extends BaseActivity<ActivityEmptyBinding, EmptyViewModel> {

    private InputPwdDialog pwdDialog;
    private String liveRoomId;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_empty;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initParam() {
        super.initParam();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            liveRoomId = extras.getString("liveRoomId");
        }
    }

    @Override
    public void initData() {
        super.initData();
        rlTitle.setVisibility(View.GONE);
        pwdDialog = new InputPwdDialog(getActivity());
        pwdDialog.setRightBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(pwdDialog.code)) {
                    ToastUtil.toastShortMessage("请输入密码");
                    return;
                }
                showDialog();
                viewModel.joinLive(liveRoomId, pwdDialog.code);
                pwdDialog.dismissDialog();
            }
        });
        pwdDialog.setLeftBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwdDialog.dismissDialog();
                finish();
            }
        });
        pwdDialog.showDialog();
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.dismissDialog.observe(this, dis -> {
            if (dis != null) {
                if (dis) {
                    dismissDialog();
                    finish();
                }
            }
        });
    }
}
