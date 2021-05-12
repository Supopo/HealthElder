package com.xaqinren.healthyelders.moduleHome.fragment;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.FragmentHomeBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleHome.adapter.HomeVP2Adapter;
import com.xaqinren.healthyelders.moduleHome.viewModel.HomeViewModel;
import com.xaqinren.healthyelders.utils.TabUtils;
import com.xaqinren.healthyelders.widget.TabLayoutMediator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;

/**
 * Created by Lee. on 2021/5/11.
 */
public class HomeFragment extends BaseFragment<FragmentHomeBinding, HomeViewModel> {
    private static final String TAG = "HomeFragment";
    private Disposable subscribe;
    private String[] titles = {"推荐", "关注", "附近"};

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_home;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }


    @Override
    public void initViewObservable() {
        super.initViewObservable();
        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == CodeTable.EVENT_HOME) {
                    if (event.msgType == CodeTable.RESH_VIEW_CODE) {
                        binding.tabLayout.setVisibility(View.GONE);
                        binding.rlTop.setVisibility(View.VISIBLE);
                        binding.nsv.setScrollingEnabled(true);
                        //滚回到顶部
                        binding.nsv.fling(0);
                        binding.nsv.smoothScrollTo(0, 0);
                    }
                }
            }
        });
        RxSubscriptions.add(subscribe);
    }

    public void initData() {
        super.initData();


        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new HomeTJFragment());
        fragments.add(new XxxFragment());
        fragments.add(new GirlsFragment());
        HomeVP2Adapter vp2Adapter = new HomeVP2Adapter(getActivity(), fragments);
        binding.viewPager2.setAdapter(vp2Adapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager2, true, new TabLayoutMediator.OnConfigureTabCallback() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                //这里需要根据position修改tab的样式和文字等
                tab.setText(titles[position]);
                initTabText(tab, Typeface.BOLD);
            }
        }).attach();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.nsv.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    Log.e(TAG, "onScrollChange: " + scrollX + "---" + scrollY + "----" + oldScrollX + "---" + oldScrollY);
                    if (scrollY >= getResources().getDimension(R.dimen.dp_237)) {
                        //通知首页底部菜单栏变透明
                        RxBus.getDefault().post(new EventBean(CodeTable.EVENT_HOME, CodeTable.SET_MENU_TOUMING));
                        binding.nsv.setScrollingEnabled(false);
                        binding.rlTop.setVisibility(View.GONE);
                        binding.tabLayout.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

    }

    private void initTabText(TabLayout.Tab tabAt, int bold) {
        if (tabAt == null || tabAt.getText() == null) {
            return;
        }
        String trim = tabAt.getText().toString().trim();
        SpannableString spannableString = new SpannableString(trim);
        StyleSpan styleSpan = new StyleSpan(bold);
        spannableString.setSpan(styleSpan, 0, trim.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tabAt.setText(spannableString);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        subscribe.dispose();
    }


}
