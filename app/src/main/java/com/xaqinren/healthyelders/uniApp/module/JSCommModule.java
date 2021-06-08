package com.xaqinren.healthyelders.uniApp.module;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleLiteav.bean.LocationBean;
import com.xaqinren.healthyelders.uniApp.module.nativeDialog.NativeDialog;
import com.xaqinren.healthyelders.utils.LogUtils;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;
import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.ToastUtils;

public class JSCommModule extends UniModule {
    private String TAG = "JSCommModule";
    private Disposable subscribe;

    public JSCommModule() {
        super();
    }
    private UniJSCallback callback;
    private UniJSCallback callbackSuccess,callbackFail;

    @UniJSMethod(uiThread = true)
    public void openPay(JSONObject options, UniJSCallback callbackSuccess, UniJSCallback callbackFail) {
        this.callbackSuccess = callbackSuccess;
        this.callbackFail = callbackFail;
        Activity context = (Activity) mUniSDKInstance.getContext();//DCUniMPActivity
        LogUtils.e(TAG, context.toString());
        StringBuffer buffer = new StringBuffer();

        //TODO 小程序调用支付页面
        buffer.append("jkzl://app_open/pay_activity");
        Intent action = new Intent(Intent.ACTION_VIEW);
        StringBuilder builder = new StringBuilder();
        builder.append(buffer.toString());

        Bundle bundle = new Bundle();

        bundle.putDouble("czNum", options.getDoubleValue("paymentAmount"));//支付金额
        bundle.putDouble("yeNum", 0.d);//余额
        bundle.putString("orderNo", options.getString("orderNo"));//订单号
        bundle.putString("orderType", options.getString("orderType"));
        bundle.putInt("theme", R.style.EditDialogStyle);
        bundle.putString("payWay", "uni");

        action.putExtras(bundle);
        action.setData(Uri.parse(builder.toString()));

        if (context != null) {
            context.startActivityForResult(action, 456);
        }
    }

    @UniJSMethod(uiThread = true)
    public void openCity(UniJSCallback callback) {
        this.callback = callback;
        Activity context = (Activity) mUniSDKInstance.getContext();//DCUniMPActivity
        LogUtils.e(TAG, context.toString());
        StringBuffer buffer = new StringBuffer();
        buffer.append("jkzl://app_open/location_activity");
        Intent action = new Intent(Intent.ACTION_VIEW);
        StringBuilder builder = new StringBuilder();
        builder.append(buffer.toString());
        action.setData(Uri.parse(builder.toString()));

        if (context != null) {
            context.startActivityForResult(action, 123);
        }
    }

    @Override
    public void onActivityCreate() {
        super.onActivityCreate();

    }

    @Override
    public void onActivityDestroy() {
        super.onActivityDestroy();
    }

    @Override
    public void onActivityResume() {
        super.onActivityResume();
    }

    @Override
    public void onActivityStop() {
        super.onActivityStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 123) {
                LocationBean bean = (LocationBean) data.getSerializableExtra("bean");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("longitude", bean.lon);
                jsonObject.put("latitude", bean.lat);
                jsonObject.put("region", bean.province+bean.city+bean.district);
                jsonObject.put("addreName", bean.addressInfo);
                jsonObject.put("addreInfo", bean.address);
                LogUtils.e(TAG, jsonObject.toJSONString());
                callback.invoke(jsonObject);
            } else if (requestCode == 456) {
                //支付回调
                boolean status = data.getBooleanExtra("status", false);
                LogUtils.e(TAG, "支付回调" + status);
                if (status) {
                    callbackSuccess.invoke(true);
                }else{
                    String msg = data.getStringExtra("msg");
                    callbackFail.invoke(msg);
                }
            }
        }
    }

    NativeDialog nativeDialog;
    @UniJSMethod(uiThread = true)
    public void show(JSONObject options, UniJSCallback jsCallback, UniJSCallback cancelCallback) {

        if (nativeDialog == null) {
            nativeDialog = new NativeDialog(mUniSDKInstance.getContext());
        }
        String title = options.getString("title");
        String titleColor = options.getString("titleColor");
        nativeDialog.setTitleText(title);
        nativeDialog.setColorTitle(titleColor);

        String con = options.getString("con");
        String conColor = options.getString("conColor");
        nativeDialog.setMessageText(con);
        nativeDialog.setColorMessage(conColor);

        String okTitle = options.getString("okTitle");
        String okTextColor = options.getString("okTextColor");
        nativeDialog.setRightBtnText(okTitle);
        nativeDialog.setColorConfirm(okTextColor);

        String cancleTitle = options.getString("cancleTitle");
        String cancleTextColor = options.getString("cancleTextColor");
        nativeDialog.setLeftBtnText(cancleTitle);
        nativeDialog.setColorCancel(cancleTextColor);

        String textAlign = options.getString("textAlign");
        nativeDialog.setTextAlign(textAlign);

        String bgColor = options.getString("bgColor");
        nativeDialog.setBackGround(bgColor);
        boolean singer = options.getBoolean("singer");
        nativeDialog.setSingleConfirm(singer);

        nativeDialog.showDialog();
        nativeDialog.setLeftBtnClickListener(v -> {
            cancelCallback.invoke(false);
            nativeDialog.dismissDialog();
        });
        nativeDialog.setRightBtnClickListener(v -> {
            jsCallback.invoke(true);
            nativeDialog.dismissDialog();
        });
    }
}
