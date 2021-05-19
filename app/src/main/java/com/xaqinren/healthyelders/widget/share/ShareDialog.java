package com.xaqinren.healthyelders.widget.share;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.xaqinren.healthyelders.DataBinderMapperImpl;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.PopShareBinding;

import java.lang.ref.SoftReference;
import java.util.List;

public class ShareDialog{
    private PopupWindow popupWindow;
    private View contentView;
    private SoftReference<Context> context;
    private PopShareBinding binding;
    private ShareFriendAdapter shareFriendAdapter;
    private List<? extends IShareUser> userList;
    public static int VIDEO_TYPE = 0;//短视频
    public static int TP_TYPE = 1;//图文
    private int showType = VIDEO_TYPE;

    public ShareDialog(Context context) {
        this.context = new SoftReference<>(context);
        init();
    }

    public void setShowType(int showType) {
        this.showType = showType;
        showType();
    }

    private void init() {
        contentView = View.inflate(context.get(), R.layout.pop_share, null);
        binding = DataBindingUtil.bind(contentView);
        popupWindow = new PopupWindow(binding.getRoot() , ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setAnimationStyle(R.style.DialogBottomAnimation);
        popupWindow.setOnDismissListener(() -> {

        });
        binding.close.setOnClickListener(view -> popupWindow.dismiss());
        shareFriendAdapter = new ShareFriendAdapter(R.layout.item_share_user);

        binding.atUserList.setLayoutManager(new LinearLayoutManager(context.get(), LinearLayoutManager.HORIZONTAL, false));
        binding.atUserList.setAdapter(shareFriendAdapter);


        binding.shareClsLayout.shareFriend.setOnClickListener(view -> {
            //私信朋友
        });
        binding.shareClsLayout.shareWxFriend.setOnClickListener(view -> {
            //私信微信朋友
        });
        binding.shareClsLayout.shareWxCircle.setOnClickListener(view -> {
            //私信微信朋友圈
        });
        binding.shareOperationLayout.shareSaveNative.setOnClickListener(view -> {
            //保存本地
        });
        binding.shareOperationLayout.shareSaveUrl.setOnClickListener(view -> {
            //复制链接
        });
        binding.shareOperationLayout.shareColl.setOnClickListener(view -> {
            //收藏
        });
        binding.shareOperationLayout.shareReport.setOnClickListener(view -> {
            //举报
        });
        binding.shareOperationLayout.sharePost.setOnClickListener(view -> {
            //生成海报
        });
        binding.rlContainer.setOnClickListener(view -> popupWindow.dismiss());

    }

    private void showType() {
        if (showType == VIDEO_TYPE) {
            binding.shareOperationLayout.sharePost.setVisibility(View.GONE);
        } else if (showType == TP_TYPE) {
            binding.shareOperationLayout.shareSaveNative.setVisibility(View.GONE);
            binding.shareOperationLayout.shareColl.setVisibility(View.GONE);
            binding.shareOperationLayout.shareReport.setVisibility(View.GONE);
            binding.shareOperationLayout.sharePost.setVisibility(View.VISIBLE);
        }
    }

    public void setData(List<? extends IShareUser> data) {
        this.userList = data;
        shareFriendAdapter.setList(userList);
    }

    public void show(View Parent) {
        if (popupWindow == null) {
            init();
        }
        popupWindow.showAsDropDown(Parent, Gravity.BOTTOM, 0, 0);
    }

    public void hide() {
        popupWindow.dismiss();
    }
}
