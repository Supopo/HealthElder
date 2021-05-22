package com.xaqinren.healthyelders.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.xaqinren.healthyelders.R;


/**
 * Created by Boyce on 2020/12/22.
 */
public class DownLoadProgressDialog extends Dialog {
    private RingProgressBar progressbar_dialog;
    private ICancelDownLoad iCancelDownLoad;

    public void setICancelDownLoad(ICancelDownLoad iCancelDownLoad) {
        this.iCancelDownLoad = iCancelDownLoad;
    }

    public DownLoadProgressDialog(Context context) {
        super(context, R.style.Theme_dialog_loading);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_progress_dialog);
        setCancelable(false);
        progressbar_dialog = findViewById(R.id.progressbar_dialog);
        findViewById(R.id.iv_close).setOnClickListener(v -> {
            iCancelDownLoad.cancelDownLoad();
            dismiss();
        });
    }

    public void setProgress(int progress){
        progressbar_dialog.setProgress(progress);
    }

    public interface ICancelDownLoad{
        void cancelDownLoad();
    }
}
