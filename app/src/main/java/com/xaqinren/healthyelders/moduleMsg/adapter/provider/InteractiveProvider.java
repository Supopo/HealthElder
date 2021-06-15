package com.xaqinren.healthyelders.moduleMsg.adapter.provider;

import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.igexin.sdk.PushManager;
import com.tencent.qcloud.tim.uikit.component.face.FaceManager;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemProviderInteractiveBinding;
import com.xaqinren.healthyelders.moduleMsg.Constant;
import com.xaqinren.healthyelders.moduleMsg.adapter.MsgSmallUserAdapter;
import com.xaqinren.healthyelders.moduleMsg.bean.InteractiveBean;
import com.xaqinren.healthyelders.moduleMsg.bean.MessageDetailBean;
import com.xaqinren.healthyelders.push.PayLoadBean;
import com.xaqinren.healthyelders.push.PushNotify;
import com.xaqinren.healthyelders.utils.DateUtils;
import com.xaqinren.healthyelders.utils.GlideUtil;
import com.xaqinren.healthyelders.utils.UrlUtils;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import retrofit2.http.Url;

public class InteractiveProvider<T extends MessageDetailBean> extends BaseItemProvider<T>{
    float dp66;
    float dp4;
    SimpleDateFormat simpleDateFormat;
    public InteractiveProvider() {
//        2021-05-27 19:58
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

    @Override
    public int getItemViewType() {
        return InteractiveBean.TYPE_TOP;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_provider_interactive;
    }

    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, T t) {
        dp66 = getContext().getResources().getDimension(R.dimen.dp_66);
        dp4 = getContext().getResources().getDimension(R.dimen.dp_4);
        InteractiveBean bean = (InteractiveBean) t;
        ItemProviderInteractiveBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setData(bean);
//        MsgSmallUserAdapter adapter = new MsgSmallUserAdapter();
//        binding.userList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
//        binding.userList.setAdapter(adapter);
//        adapter.addData(new Object());
//        adapter.addData(new Object());
//        adapter.addData(new Object());
        binding.userList.setVisibility(View.GONE);



        GlideUtil.intoImageView(getContext(), bean.getSendUser().getAvatarUrl(), binding.avatar);
        GlideUtil.intoCirImageView(getContext(),
                UrlUtils.resetImgUrl(bean.getContent().getLogoUrl(), (int) dp66, (int) dp66),
                binding.cover, (int) dp4);

        binding.time.setText(getInteractiveMessageBody(bean));
        if (bean.getMessageType().equals(Constant.FAVORITE)) {
            binding.statusIcon.setImageResource(R.mipmap.icon_yongh_dianzan);
        } else if (bean.getMessageType().equals(Constant.AT)) {
            binding.statusIcon.setImageResource(R.mipmap.icon_yongh_at);
        } else if (bean.getMessageType().equals(Constant.COMMENT)) {
            binding.statusIcon.setImageResource(R.mipmap.icon_yongh_pinl);
        } else if (bean.getMessageType().equals(Constant.REPLY)) {
            binding.statusIcon.setImageResource(R.mipmap.icon_yongh_pinl);
        }

        if (bean.getIdentity() == null ){
            //陌生人
            binding.friendTag.setVisibility(View.GONE);
        } else if (bean.getIdentity().equals(FriendProvider.STRANGER)) {
            //陌生人
            binding.friendTag.setVisibility(View.GONE);
        } else if (bean.getIdentity().equals(FriendProvider.FANS)) {
            //粉丝
            binding.friendTag.setVisibility(View.VISIBLE);
            binding.friendTag.setText("粉丝");
        } else if (bean.getIdentity().equals(FriendProvider.ATTENTION)) {
            //关注的人
            binding.friendTag.setVisibility(View.VISIBLE);
            binding.friendTag.setText("关注");
        } else if (bean.getIdentity().equals(FriendProvider.FRIEND)) {
            //朋友
            binding.friendTag.setVisibility(View.VISIBLE);
            binding.friendTag.setText("朋友");
        } else if (bean.getIdentity().equals(FriendProvider.FOLLOW)) {
            //关注的人
            binding.friendTag.setVisibility(View.VISIBLE);
            binding.friendTag.setText("关注");
        }

        //设置处理加载聊天表情文字
        FaceManager.handlerEmojiText(binding.describe, bean.getContent().getBody(), false);
    }

    public String getInteractiveMessageBody(InteractiveBean url){
        String date = DateUtils.getRelativeTime(url.getCreatedAt());
        if (url.getMessageType().equals(com.xaqinren.healthyelders.moduleMsg.Constant.FAVORITE)) {
            return date;
        } else if (url.getMessageType().equals(com.xaqinren.healthyelders.moduleMsg.Constant.AT)) {
            return url.getContent().getTitle() + " " + date;
        } else if (url.getMessageType().equals(com.xaqinren.healthyelders.moduleMsg.Constant.COMMENT)) {
            return url.getContent().getTitle() + " " + date;
        } else if (url.getMessageType().equals(com.xaqinren.healthyelders.moduleMsg.Constant.REPLY)) {
            return url.getContent().getTitle() + " " + date;
        } else {
            return date;
        }
    }

}
