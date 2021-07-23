package com.xaqinren.healthyelders.uniApp.module;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.camera2.params.OisSample;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.BankCardResult;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.executor.GlideExecutor;
import com.facebook.common.util.UriUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.tencent.bugly.proguard.P;
import com.tencent.qcloud.ugckit.utils.ScreenUtils;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleLiteav.bean.LocationBean;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ListPopMenuBean;
import com.xaqinren.healthyelders.uniApp.activity.PhotoActivity;
import com.xaqinren.healthyelders.uniApp.module.nativeDialog.NativeDialog;
import com.xaqinren.healthyelders.utils.GetFilesUtils;
import com.xaqinren.healthyelders.utils.GlideEngine;
import com.xaqinren.healthyelders.utils.GlideUtil;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.utils.MScreenUtil;
import com.xaqinren.healthyelders.utils.OCRUtils;
import com.xaqinren.healthyelders.utils.QRCodeUtils;
import com.xaqinren.healthyelders.widget.ListBottomPopup;

import org.apache.commons.httpclient.util.URIUtil;

import java.util.ArrayList;
import java.util.List;

import io.dcloud.feature.uniapp.UniSDKInstance;
import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniModule;
import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.StringUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import razerdp.basepopup.BasePopupWindow;

public class JSCommModule extends UniModule {
    private String TAG = "JSCommModule";
    private Disposable subscribe;
    private String maskType;

    public JSCommModule() {
        super();
    }
    private UniJSCallback callback;
    private UniJSCallback callbackSuccess,callbackFail;
    private int REQUEST_GALLERY = 666;

    @UniJSMethod(uiThread = true)
    public void openPay(JSONObject options, UniJSCallback callbackSuccess, UniJSCallback callbackFail) {
        this.callbackSuccess = callbackSuccess;
        this.callbackFail = callbackFail;
        Activity context = (Activity) mUniSDKInstance.getContext();//DCUniMPActivity
        LogUtils.e(TAG, context.toString());
        StringBuffer buffer = new StringBuffer();

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
    public void searchLocation(JSONObject options,UniJSCallback callback) {
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
    //银行卡识别
    @UniJSMethod(uiThread = true)
    public void startRecognize(JSONObject options, UniJSCallback callback) {
        this.callback = callback;
        maskType = options.getString("maskType");
        initAccessToken();
        showSelPop();
    }

    /**
     * 以license文件方式初始化
     */
    private void initAccessToken() {
        AccessToken accessToken = OCR.getInstance(mUniSDKInstance.getContext()).getAccessToken();
        if (accessToken == null || accessToken.hasExpired()) {
            OCR.getInstance(mUniSDKInstance.getContext()).initAccessToken(new OnResultListener<AccessToken>() {
                @Override
                public void onResult(AccessToken accessToken) {
                    String token = accessToken.getAccessToken();
                    LogUtils.v(Constant.TAG_LIVE, "licence方式获取token成功 ");
                }

                @Override
                public void onError(OCRError error) {
                    error.printStackTrace();
                    LogUtils.v(Constant.TAG_LIVE, "licence方式获取token失败" + error.getMessage());
                }
            }, mUniSDKInstance.getContext());
        }
    }

    private void showSelPop() {
        List<ListPopMenuBean> menus = new ArrayList<>();
        menus.add(new ListPopMenuBean("拍摄"));
        menus.add(new ListPopMenuBean("从相册中选取"));
        ListBottomPopup listBottomPopup = new ListBottomPopup(mUniSDKInstance.getContext(), menus);
        listBottomPopup.setOnItemClickListener((adapter, view, position) -> {
            //掉接口
            if (position == 0) {
                toCamera((Activity) mUniSDKInstance.getContext());
            } else if (position == 1) {
                toPhoto((Activity) mUniSDKInstance.getContext());
            }
            listBottomPopup.dismiss();
        });
        listBottomPopup.showPopupWindow();
        ScreenUtils.setWindowAlpha(mUniSDKInstance.getContext(), 1.0f, 0.6f, 400);
        listBottomPopup.setOnDismissListener(new BasePopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ScreenUtils.setWindowAlpha(mUniSDKInstance.getContext(), 0.6f, 1.0f, 200);
            }
        });
    }

    private void toPhoto(Activity activity) {
        Intent intent = new Intent(activity, PhotoActivity.class);
        activity.startActivityForResult(intent, 789);
    }

    private void toCamera(Activity activity) {
        PictureSelector.create(activity)
                .openCamera(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
                .maxSelectNum(1)// 最大图片选择数量
                .isEnableCrop(false)
                .isCompress(true)
                .isAndroidQTransform(false)//开启沙盒 高版本必须选择不然拿不到小图
                .forResult(REQUEST_GALLERY);//结果回调onActivityResult code
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
                callback.invoke(jsonObject);
            } else if (requestCode == 456) {
                //支付回调
                boolean status = data.getBooleanExtra("status", false);
                if (status) {
                    callbackSuccess.invoke(true);
                }else{
                    String msg = data.getStringExtra("msg");
                    callbackFail.invoke(msg);
                }
            } else if (requestCode == REQUEST_GALLERY) {
                List<LocalMedia> result = PictureSelector.obtainMultipleResult(data);
                String path = result.get(0).getPath();
                //开始识别
                if (!StringUtils.isEmpty(path)) {
                    if (maskType.equals("BankCard")){
                        startRecognize(path);

                    }
                    else if (maskType.equals("IdCard")) {

                    }
                }
            } else if (requestCode == 789) {
                String realPathFromUri = data.getStringExtra("path");
                if (!StringUtils.isEmpty(realPathFromUri)) {
                    if (maskType.equals("BankCard")){
                        startRecognize(realPathFromUri);
                    }
                    else if (maskType.equals("IdCard")) {

                    }
                }
            }
        }
    }

    private void startRecognize(String path) {
        OCRUtils.ocrBankCard(mUniSDKInstance.getContext(), path, new OnResultListener<BankCardResult>() {
            @Override
            public void onResult(BankCardResult bankCardResult) {
                if (callback != null) {
                    String bankName = bankCardResult.getBankName();
                    String bankCardNumber = bankCardResult.getBankCardNumber();
                    BankCardResult.BankCardType type = bankCardResult.getBankCardType();
                    int bankCardType = 0;
                    if (type == BankCardResult.BankCardType.Debit) {
                        bankCardType = 1;
                    } else if (type == BankCardResult.BankCardType.Credit) {
                        bankCardType = 2;
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("bankName", bankName);
                    jsonObject.put("bankCardNumber", bankCardNumber);
                    jsonObject.put("bankCardType", bankCardType);
                    jsonObject.put("status", true);
                    callback.invoke(jsonObject);
                }
            }

            @Override
            public void onError(OCRError ocrError) {
                if (callback != null) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("status", false);
                    jsonObject.put("code", ocrError.getErrorCode());
                    callback.invoke(jsonObject);
                }
            }
        });
    }

    NativeDialog nativeDialog;
    @UniJSMethod(uiThread = true)
    public void showModal(JSONObject options, UniJSCallback jsCallback) {

        if (nativeDialog == null) {
            nativeDialog = new NativeDialog(mUniSDKInstance.getContext());
        }
        if (options.containsKey("title")) {
            String title = options.getString("title");
            nativeDialog.setTitleText(title);
        }
        if (options.containsKey("titleColor")){
            String titleColor = options.getString("titleColor");
            nativeDialog.setColorTitle(titleColor);
        }

        if (options.containsKey("content")){
            String content = options.getString("content");
            nativeDialog.setMessageText(content);
        }
        if (options.containsKey("contentColor")){
            String contentColor = options.getString("contentColor");
            nativeDialog.setColorMessage(contentColor);
        }

        if (options.containsKey("confirmTitle")){
            String confirmTitle = options.getString("confirmTitle");
            nativeDialog.setRightBtnText(confirmTitle);
        }

        if (options.containsKey("confirmColor")){
            String confirmColor = options.getString("confirmColor");
            nativeDialog.setColorConfirm(confirmColor);
        }

        if (options.containsKey("cancelTitle")){
            String cancelTitle = options.getString("cancelTitle");
            nativeDialog.setLeftBtnText(cancelTitle);
        }

        if (options.containsKey("cancelColor")){
            String cancelColor = options.getString("cancelColor");
            nativeDialog.setColorCancel(cancelColor);
        }


        if (options.containsKey("textAlign")){
            String textAlign = options.getString("textAlign");
            nativeDialog.setTextAlign(textAlign);
        }

        if (options.containsKey("bgColor")){
            String bgColor = options.getString("bgColor");
            nativeDialog.setBackGround(bgColor);
        }


        if (options.containsKey("showCancel")){
            boolean singer = options.getBoolean("showCancel");
            nativeDialog.setSingleConfirm(singer);
        }

        nativeDialog.showDialog();
        //取消
        nativeDialog.setLeftBtnClickListener(v -> {
            jsCallback.invoke(false);
            nativeDialog.dismissDialog();
        });
        nativeDialog.setRightBtnClickListener(v -> {
            jsCallback.invoke(true);
            nativeDialog.dismissDialog();
        });
    }
}
