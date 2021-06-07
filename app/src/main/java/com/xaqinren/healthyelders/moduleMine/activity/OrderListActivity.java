package com.xaqinren.healthyelders.moduleMine.activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivityOrderListBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleLiteav.adapter.FragmentViewPagerAdapter;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleMine.fragment.OrderListFragment;
import com.xaqinren.healthyelders.moduleMine.viewModel.OrderListViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.bus.RxBus;

public class OrderListActivity extends BaseActivity<ActivityOrderListBinding, OrderListViewModel> {
    private List<OrderListFragment> listFragments;
    private Disposable subscribe;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_order_list;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setTitle("我的订单");
        listFragments = new ArrayList<>(5);
        listFragments.add(new OrderListFragment(0));
        listFragments.add(new OrderListFragment(1));
        listFragments.add(new OrderListFragment(2));
        listFragments.add(new OrderListFragment(3));
        listFragments.add(new OrderListFragment(4));
        String[] titles = {"全部","待支付","待发货","待收货","待评价"};
        binding.viewPager.setOffscreenPageLimit(5);
        FragmentViewPagerAdapter adapter = new FragmentViewPagerAdapter(getSupportFragmentManager(),listFragments);
        binding.viewPager.setAdapter(adapter);
        ArrayList<CustomTabEntity> tabEntitys = new ArrayList<>();
        for (String title : titles) {
            tabEntitys.add(new CustomTabEntity() {
                @Override
                public String getTabTitle() {
                    return title;
                }

                @Override
                public int getTabSelectedIcon() {
                    return 0;
                }

                @Override
                public int getTabUnselectedIcon() {
                    return 0;
                }
            });
        }
        binding.tabLayout.setTabData(tabEntitys);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                binding.tabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        binding.tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                binding.viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        viewModel.getBalance();


    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.userBanlance.observe(this, new Observer<UserInfoBean>() {
            @Override
            public void onChanged(UserInfoBean datas) {
                if (datas != null) {
                    double jbyeNum = datas.getPointAccountBalance();
//                    binding.tvTips.setText("余额：" + jbyeNum + "金币");
                    for (OrderListFragment listFragment : listFragments) {
                        listFragment.setBalance(jbyeNum);
                    }
                }
            }
        });
        subscribe = RxBus.getDefault().toObservable(EventBean.class).subscribe(event -> {
            if (event != null) {
                if (event.msgId == CodeTable.WX_PAY_CODE && event.msgType == 1) {
                    //支付成功
                    int currentItem = binding.viewPager.getCurrentItem();
                    listFragments.get(currentItem).onPaySuccess();
                }
            }

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscribe.dispose();
    }
}
