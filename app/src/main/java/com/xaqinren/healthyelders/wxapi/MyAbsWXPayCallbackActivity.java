package com.xaqinren.healthyelders.wxapi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelmsg.ShowMessageFromWX;
import com.tencent.mm.opensdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xaqinren.healthyelders.utils.LogUtils;

import io.dcloud.ProcessMediator;
import io.dcloud.common.DHInterface.FeatureMessageDispatcher;
import io.dcloud.common.DHInterface.IReflectAble;
import io.dcloud.common.adapter.util.AndroidResources;

public class MyAbsWXPayCallbackActivity extends Activity implements IWXAPIEventHandler, IReflectAble {

    boolean isMultiProcess = false;
    private String TAG = getClass().getSimpleName();

    public MyAbsWXPayCallbackActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String wx_appid = AndroidResources.getMetaValue("WX_APPID");
        String MultiProcessCount = AndroidResources.getMetaValue("MultiProcessCount");
        this.isMultiProcess = !TextUtils.isEmpty(MultiProcessCount);

        try {
            IWXAPI api = WXAPIFactory.createWXAPI(this, wx_appid, false);
            api.handleIntent(this.getIntent(), this);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    public void onReq(BaseReq req) {
        FeatureMessageDispatcher.dispatchMessage(req);
        if (req.getType() == 4) {
            this.goToMsg(req);
        }

        if (this.isMultiProcess) {
            Intent n = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("style", "BaseReq");
            req.toBundle(bundle);
            n.putExtra("result", bundle);
            ProcessMediator.setResult(n);
        }

        this.finish();
    }

    @SuppressLint("WrongConstant")
    private void goToMsg(BaseReq req) {
        ShowMessageFromWX.Req showReq = (ShowMessageFromWX.Req)req;
        WXMediaMessage wxMsg = showReq.message;
        WXAppExtendObject obj = (WXAppExtendObject)wxMsg.mediaObject;
        PackageManager packageManager = this.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(this.getPackageName());
        intent.putExtra("WX_SHOW_MESSAGE", obj.extInfo);
        intent.putExtra("__launcher__", "miniProgram");
        intent.addFlags(524288);
        this.startActivity(intent);
    }

    @SuppressLint("WrongConstant")
    private void goToMsg(BaseResp resp) {
        WXLaunchMiniProgram.Resp launchMiniProResp = (WXLaunchMiniProgram.Resp)resp;
        String extraData = launchMiniProResp.extMsg;
        LogUtils.e(TAG, "extraData -> " + launchMiniProResp.extMsg) ;
        LogUtils.e(TAG, "extraData -> " + launchMiniProResp.errStr) ;
        LogUtils.e(TAG, "extraData -> " + launchMiniProResp.openId) ;
        if (!TextUtils.isEmpty(extraData)) {
            PackageManager packageManager = this.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(this.getPackageName());
            intent.putExtra("WX_SHOW_MESSAGE", extraData);
            intent.putExtra("__launcher__", "miniProgram");
            intent.addFlags(524288);
            this.startActivity(intent);
        }
    }

    public void onResp(BaseResp resp) {
        FeatureMessageDispatcher.dispatchMessage(resp);
        LogUtils.e(TAG, "onResp -> " + resp.getType());
        if (resp.getType() == 19) {
            this.goToMsg(resp);
        }

        if (this.isMultiProcess) {
            Intent n = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("style", "BaseResp");
            resp.toBundle(bundle);
            n.putExtra("result", bundle);
            ProcessMediator.setResult(n);
        }

        this.overridePendingTransition(0, 0);
        this.finish();
    }

}
