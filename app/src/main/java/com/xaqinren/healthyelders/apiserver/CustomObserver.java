package com.xaqinren.healthyelders.apiserver;


import android.net.ParseException;
import android.util.Log;

import com.google.gson.JsonParseException;
import com.xaqinren.healthyelders.BuildConfig;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.global.AppApplication;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.bean.VideoEvent;
import com.xaqinren.healthyelders.utils.LogUtils;

import org.json.JSONException;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.http.NetworkUtil;
import me.goldze.mvvmhabit.utils.ToastUtils;
import retrofit2.HttpException;


public abstract class CustomObserver<T extends MBaseResponse> implements Observer<T> {
    private String msg;

    @Override
    public void onSubscribe(Disposable d) {
        if (!NetworkUtil.isNetworkAvailable(AppApplication.getInstance())) {
            d.dispose();
            ToastUtils.showShort("请检查网络");
        }
    }

    @Override
    public void onNext(T t) {
        if (t.isOk()) {
            onSuccess(t);
        } else {
            String code = t.getCode();
            boolean showToast = false;
            if (!code.startsWith("0") && !code.startsWith("1")) {
                showToast = true;
            } else if (Constant.DEBUG) {
                showToast = true;
            }
            if (showToast)
                ToastUtils.showShort(t.getMessage());
            //判断token过期
            if (t.getCode().equals(CodeTable.TOKEN_ERR_CODE) || t.getCode().equals(CodeTable.TOKEN_NO_CODE)) {
                onTokenErr();
                return;
            } else if (t.getCode().equals(CodeTable.NO_CARD_ID)) {
                RxBus.getDefault().post(new EventBean(CodeTable.NO_CARD, null));
                return;
            }
            onFail(t.getCode(), t);
        }
    }


    //在当前方法中操作关闭dialog等可以同步到网络请求完各个状态方法中
    protected abstract void dismissDialog();

    @Override
    public void onComplete() {
        dismissDialog();
    }

    protected abstract void onSuccess(T data);

    public void onFail(String code, T data) {

    }

    public void onTokenErr() {
        RxBus.getDefault().post(new VideoEvent(1, "全部停止播放"));
        RxBus.getDefault().post(new EventBean(CodeTable.TOKEN_ERR, null));
    }

    @Override
    public void onError(Throwable e) {
        dismissDialog();
        Log.e("CustomObserver", e.getMessage());
        if (e instanceof HttpException) {     //   HTTP错误
            onException(ExceptionReason.BAD_NETWORK);
        } else if (e instanceof ConnectException
                || e instanceof UnknownHostException) {   //   连接错误
            onException(ExceptionReason.CONNECT_ERROR);
        } else if (e instanceof InterruptedIOException) {   //  连接超时
            onException(ExceptionReason.CONNECT_TIMEOUT);
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {   //  解析错误
            onException(ExceptionReason.PARSE_ERROR);
        } else if (e instanceof ApiException) {
            ApiException exception = (ApiException) e;
            msg = exception.msg;
            onException(ExceptionReason.DATA_ERROR);
        } else {
            onException(ExceptionReason.UNKNOWN_ERROR);
        }
    }


    private void onException(ExceptionReason reason) {
        dismissDialog();

        switch (reason) {
            case CONNECT_ERROR:
                if (Constant.DEBUG) {
                    ToastUtils.showShort("连接异常");
                }
                break;
            case CONNECT_TIMEOUT:
                ToastUtils.showShort("连接超时");
                break;
            case BAD_NETWORK:
                if (Constant.DEBUG) {
                    ToastUtils.showShort("网络异常");
                }
                break;
            case PARSE_ERROR:
                if (Constant.DEBUG) {
                    ToastUtils.showShort("解析异常");
                }
                break;
            case UNKNOWN_ERROR:
                if (Constant.DEBUG) {
                    ToastUtils.showShort("未知异常");
                }
                break;
            case DATA_ERROR:
                if (Constant.DEBUG) {
                    ToastUtils.showShort(msg);
                }
                break;
            default:
                break;
        }
    }

    public enum ExceptionReason {
        //解析数据失败
        PARSE_ERROR,
        //网络问题
        BAD_NETWORK,
        //连接错误
        CONNECT_ERROR,
        //连接超时
        CONNECT_TIMEOUT,
        //未知错误
        UNKNOWN_ERROR,
        //数据错误
        DATA_ERROR,
    }
}


