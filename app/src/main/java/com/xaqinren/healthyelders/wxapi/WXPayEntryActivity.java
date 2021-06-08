

package com.xaqinren.healthyelders.wxapi;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.uniApp.UniUtil;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import io.dcloud.feature.payment.weixin.AbsWXPayCallbackActivity;
import io.dcloud.feature.sdk.DCUniMPAloneTaskActivity;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.utils.SPUtils;

public class WXPayEntryActivity extends MyAbsWXPayCallbackActivity {
    //private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //api = WXAPIFactory.createWXAPI(this, "wx4083c9a2be58173b");
        //api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //setIntent(intent);
        //api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        super.onReq(req);
    }

    @Override
    public void onResp(BaseResp resp) {
        super.onResp(resp);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (resp.errCode == 0) {
                // 成功
                if (resp instanceof PayResp) {
                    //充值成功
                    Toast.makeText(WXPayEntryActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    RxBus.getDefault().post(new EventBean(CodeTable.WX_PAY_CODE, 1));
                }
            } else {
                RxBus.getDefault().post(new EventBean(CodeTable.WX_PAY_CODE, resp.errStr, 2));
                Toast.makeText(WXPayEntryActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
            }
            String string = SPUtils.getInstance().getString(Constant.PAY_WAY, "");
            if (!string.equals("app")) {
                //则是从小程序打开的
                try {
                    Class cls = Class.forName("io.dcloud.feature.sdk.DCUniMPAloneTaskActivity");
                    Intent intent = new Intent(this, cls);
                    startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            SPUtils.getInstance().put(Constant.PAY_WAY, "");
            finish();
        }
    }
}



