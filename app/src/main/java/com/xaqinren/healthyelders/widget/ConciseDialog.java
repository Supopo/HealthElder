package com.xaqinren.healthyelders.widget;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.xaqinren.healthyelders.R;


public class ConciseDialog  {
    private AlertDialog alertDialog;
    private TextView message;
    private TextView leftBtn, rightBtn;
    private Context context;

    public ConciseDialog(Context context) {
        this.context = context;
        init(context);
    }

    public void init(Context context) {
        if (alertDialog == null) {
            View view = View.inflate(context, R.layout.dialog_concise_layout, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(view);
            builder.setCancelable(true);
            alertDialog = builder.create();
            message = view.findViewById(R.id.message);
            leftBtn = view.findViewById(R.id.left_btn);
            rightBtn = view.findViewById(R.id.right_btn);
        }
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
        });
    }
    public void setMessageText(String str) {
        message.setText(str);
    }
    public void setLeftBtnText(String str) {
        leftBtn.setText(str);
    }
    public void setRightBtnText(String str) {
        rightBtn.setText(str);
    }

    public void showDialog() {
        if (alertDialog.isShowing()) {
            return;
        }
        alertDialog.show();
        Window window = alertDialog.getWindow();

    }
}
