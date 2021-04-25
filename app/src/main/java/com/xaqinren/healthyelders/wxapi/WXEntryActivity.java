

package com.xaqinren.healthyelders.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.xaqinren.healthyelders.MainActivity;
import com.xaqinren.healthyelders.apiserver.ApiServer;
import com.xaqinren.healthyelders.apiserver.CustomObserver;
import com.xaqinren.healthyelders.apiserver.MBaseResponse;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.global.InfoCache;
import com.xaqinren.healthyelders.http.RetrofitClient;
import com.xaqinren.healthyelders.moduleLogin.activity.PhoneLoginActivity;
import com.xaqinren.healthyelders.moduleLogin.bean.LoginTokenBean;
import com.xaqinren.healthyelders.moduleLogin.bean.WeChatUserInfoBean;

import java.util.HashMap;

import io.dcloud.share.mm.AbsWXCallbackActivity;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.utils.SPUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class WXEntryActivity extends AbsWXCallbackActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppApplication.mWXapi.handleIntent(getIntent(), this);
    }

    //微信请求相应
    @Override
    public void onReq(BaseReq baseReq) {

    }

    //发送到微信请求的响应结果
    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                Log.i("WXTest", "onResp OK");

                if (resp instanceof SendAuth.Resp) {
                    SendAuth.Resp newResp = (SendAuth.Resp) resp;
                    //获取微信传回的code
                    String code = newResp.code;
                    Log.i("WXTest", "onResp code = " + code);
                    //去通知服务器
                    String a = "Basic MTM3ODYxODg2MDYxMzE0NDU3NjprMnF3c2sxNTMzZm1jMGhqdHJiYWowcjk=";
                    String m = "1378618860613144576";
                    getWxChatInfo(a, m, code);
                }

                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                Log.i("WXTest", "onResp ERR_USER_CANCEL ");
                //发送取消
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                Log.i("WXTest", "onResp ERR_AUTH_DENIED");
                //发送被拒绝
                break;
            default:
                Log.i("WXTest", "onResp default errCode " + resp.errCode);
                //发送返回
                break;
        }
        finish();
    }

    private void getWxChatInfo(String authorization, String mid, String code) {
        RetrofitClient.getInstance().create(ApiServer.class)
                .getWxChatInfo(authorization, mid, code)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MBaseResponse<WeChatUserInfoBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MBaseResponse<WeChatUserInfoBean> baseResponse) {
                        if (baseResponse.isOk()) {
                            SPUtils.getInstance().put(Constant.SP_KEY_WX_INFO,JSON.toJSONString(baseResponse.getData()));
//                            toWxChatRealLogin(baseResponse.getData());
                            RxBus.getDefault().post(new EventBean(CodeTable.WX_LOGIN_SUCCESS,null));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}



