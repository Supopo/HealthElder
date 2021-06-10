package com.xaqinren.healthyelders.uniApp.module.nativeDialog;

import com.alibaba.fastjson.JSONObject;

import io.dcloud.feature.uniapp.annotation.UniJSMethod;
import io.dcloud.feature.uniapp.bridge.UniJSCallback;
import io.dcloud.feature.uniapp.common.UniDestroyableModule;

public class NativeDialogModule extends UniDestroyableModule {

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

    @Override
    public void destroy() {

    }
}
