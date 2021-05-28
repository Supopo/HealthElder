package com.xaqinren.healthyelders.widget.pickerView.cityPicker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.xaqinren.healthyelders.R;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AreaGridAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mAreas;

    public AreaGridAdapter(Context context, List<String> list) {
        this.mContext = context;
        mAreas = new ArrayList<>();
        mAreas.addAll(list);
    }

    @Override
    public int getCount() {
        return mAreas == null ? 0 : mAreas.size();
    }

    @Override
    public String getItem(int position) {
        return mAreas == null ? null : mAreas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        HotCityViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_area_select, parent, false);
            holder = new HotCityViewHolder();
            holder.name = (TextView) view.findViewById(R.id.tvCounties);
            view.setTag(holder);
        } else {
            holder = (HotCityViewHolder) view.getTag();
        }
        holder.name.setText(mAreas.get(position));
        return view;
    }

    public static class HotCityViewHolder {
        TextView name;
    }
}
