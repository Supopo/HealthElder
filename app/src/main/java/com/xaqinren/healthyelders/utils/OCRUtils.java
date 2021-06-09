package com.xaqinren.healthyelders.utils;

import android.content.Context;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.model.BankCardParams;
import com.baidu.ocr.sdk.model.BankCardResult;

import java.io.File;

/**
 * 百度工具
 */
public class OCRUtils {

    public static void ocrBankCard(Context context, String path, OnResultListener<BankCardResult> bankResult) {
        BankCardParams params = new BankCardParams();
        params.setImageFile(new File(path));
        OCR.getInstance(context).recognizeBankCard(params, bankResult);
    }



}
