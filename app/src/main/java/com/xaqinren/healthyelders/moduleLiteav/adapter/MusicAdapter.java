package com.xaqinren.healthyelders.moduleLiteav.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tencent.bugly.proguard.B;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ItemMusicBlockBinding;
import com.xaqinren.healthyelders.moduleHome.adapter.FragmentPagerAdapter;
import com.xaqinren.healthyelders.moduleLiteav.Constant;
import com.xaqinren.healthyelders.moduleLiteav.activity.MusicListActivity;
import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicBean;
import com.xaqinren.healthyelders.moduleLiteav.fragment.MusicClassFragment;
import com.xaqinren.healthyelders.widget.transformer.RightLeakageTransformer;
import com.xaqinren.healthyelders.widget.transformer.ScaleTransformer;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class MusicAdapter extends BaseQuickAdapter<MMusicBean, MusicAdapter.ViewHolder> {
    public MusicAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NotNull MusicAdapter.ViewHolder baseViewHolder, MMusicBean mMusicBean) {
        ItemMusicBlockBinding binding = DataBindingUtil.bind(baseViewHolder.itemView);
        binding.setViewModel(mMusicBean);
        if (baseViewHolder.adapter == null) {
            //固定3页
            baseViewHolder.fragments.clear();
            baseViewHolder.fragments.add(new MusicClassFragment(mMusicBean.id, 1, mMusicBean.musicList));
            baseViewHolder.fragments.add(new MusicClassFragment(mMusicBean.id, 2, mMusicBean.musicList));
            baseViewHolder.fragments.add(new MusicClassFragment(mMusicBean.id, 3, mMusicBean.musicList));
            baseViewHolder.adapter = new FragmentViewPagerAdapter(((FragmentActivity) getContext()).getSupportFragmentManager(), baseViewHolder.fragments);
            baseViewHolder.viewPager.setOffscreenPageLimit(3);
            baseViewHolder.viewPager.setId(baseViewHolder.getLayoutPosition() + 1);
        }
        baseViewHolder.viewPager.setAdapter(baseViewHolder.adapter);
        baseViewHolder.viewPager.setCurrentItem(baseViewHolder.page);
        Log.e("MusicAdapter", "position = " + baseViewHolder.getAdapterPosition() + "\tpage = " + baseViewHolder.page);
        binding.seeAll.setOnClickListener(view -> {
            //TODO 查看全部
            Bundle bundle = new Bundle();
            bundle.putString(Constant.MUSIC_CLASS_ID, "123");
            getContext().startActivity(new Intent(getContext(), MusicListActivity.class), bundle);
        });
    }

    class ViewHolder extends BaseViewHolder {
        List<Fragment> fragments;
        ViewPager viewPager;
        FragmentViewPagerAdapter adapter;
        int page;
        public ViewHolder(@NotNull View view) {
            super(view);
            fragments = new ArrayList<>();
            viewPager = view.findViewById(R.id.viewpager);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    page = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            int dp32 = (int) getContext().getResources().getDimension(R.dimen.dp_32);
            viewPager.setPageTransformer(false, new RightLeakageTransformer(-dp32));
        }
    }
}
