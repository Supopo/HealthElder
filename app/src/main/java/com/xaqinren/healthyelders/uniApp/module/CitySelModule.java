package com.xaqinren.healthyelders.uniApp.module;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencent.mm.opensdk.utils.Log;
import com.xaqinren.healthyelders.moduleLiteav.bean.LocationBean;
import com.xaqinren.healthyelders.utils.LogUtils;

import io.dcloud.feature.sdk.DCUniMPActivity;
import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;
import me.goldze.mvvmhabit.base.AppManager;

public class CitySelModule extends UniModule {
    private String TAG = "CitySelModule";

    public CitySelModule() {
        super();
    }

    private UniJSCallback callback;

    @UniJSMethod(uiThread = true)
    public void openCity(JSONObject options, UniJSCallback callback) {
        this.callback = callback;
        StringBuffer buffer = new StringBuffer();
        buffer.append("jkzl://app_open/location_activity");
        Intent action = new Intent(Intent.ACTION_VIEW);
        StringBuilder builder = new StringBuilder();
        builder.append(buffer.toString());
        action.setData(Uri.parse(builder.toString()));
        Activity context = (Activity) mUniSDKInstance.getContext();//DCUniMPActivity
        LogUtils.e(TAG, context.toString());
        if (context != null) {
            context.startActivityForResult(action, 123);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 123) {
                LocationBean bean = (LocationBean) data.getSerializableExtra("bean");
                callback.invoke(JSON.toJSONString(bean));
            }
        }
    }

    @Override
    public void onActivityCreate() {
        super.onActivityCreate();
    }

    @Override
    public void onActivityStop() {
        super.onActivityStop();
    }
}
