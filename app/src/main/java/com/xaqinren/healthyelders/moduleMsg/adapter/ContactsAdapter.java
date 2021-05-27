package com.xaqinren.healthyelders.moduleMsg.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.xaqinren.healthyelders.moduleMsg.bean.ContactsBean;

import org.jetbrains.annotations.NotNull;

public class ContactsAdapter extends BaseQuickAdapter<ContactsBean, BaseViewHolder> {
    public ContactsAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, ContactsBean contactsBean) {

    }
}
