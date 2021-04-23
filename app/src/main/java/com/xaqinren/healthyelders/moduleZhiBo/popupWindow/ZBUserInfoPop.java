package com.xaqinren.healthyelders.moduleZhiBo.popupWindow;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ZhiboUserBean;
import com.xaqinren.healthyelders.utils.AnimUtils;

import razerdp.basepopup.BasePopupWindow;

/**
 * Created by Lee. on 2021/4/20.
 * 直播间用户信息弹窗
 */
public class ZBUserInfoPop extends BasePopupWindow {

    private ZhiboUserBean userInfo;
    private Context context;

    public ZBUserInfoPop(Context context, ZhiboUserBean userInfo) {
        super(context);
        this.userInfo = userInfo;
        initView();
    }


    public ZBUserInfoPop(Context context) {
        super(context);
        setShowAnimation(AnimUtils.PopAnimBottom2Enter(context));
        setDismissAnimation(AnimUtils.PopAnimBottom2Exit(context));
        initView();
    }

    private void initView() {
        TextView tvName = findViewById(R.id.tv_name);
        TextView tvDes = findViewById(R.id.tv_des);
        TextView tvAt = findViewById(R.id.tv_at);
        ImageView ivSex = findViewById(R.id.iv_sex);
        tvAt.setText("@TA");
        QMUIRadiusImageView rivPhoto = findViewById(R.id.riv_photo);
        tvName.setText(userInfo.nickname);
        ivSex.setBackground(userInfo.sex == 1 ? context.getResources().getDrawable(R.mipmap.male) : context.getResources().getDrawable(R.mipmap.female));
        Glide.with(context).load(userInfo.headPortrait).into(rivPhoto);
    }


    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.pop_zbj_user_info);
    }
}
