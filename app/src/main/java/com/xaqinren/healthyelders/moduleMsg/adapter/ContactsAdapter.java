package com.xaqinren.healthyelders.moduleMsg.adapter;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

import androidx.databinding.DataBindingUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.databinding.ItemContactsBinding;
import com.xaqinren.healthyelders.moduleMsg.bean.ContactsBean;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

import me.goldze.mvvmhabit.utils.StringUtils;

public class ContactsAdapter extends BaseQuickAdapter<ContactsBean, BaseViewHolder> {
    public ContactsAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, ContactsBean contactsBean) {
        ItemContactsBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setViewModel(contactsBean);
        ContentResolver cr = getContext().getContentResolver();
    }
}
