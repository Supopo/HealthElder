package com.xaqinren.healthyelders.moduleHome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.databinding.FragmentXxxBinding;
import com.xaqinren.healthyelders.moduleHome.viewModel.XxxViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.activity.MyGoodsListActivity;

import io.dcloud.feature.sdk.DCUniMPSDK;
import me.goldze.mvvmhabit.base.BaseFragment;

//注意ActivityBaseBinding换成自己fragment_layout对应的名字 FragmentXxxBinding
public class XxxFragment extends BaseFragment<FragmentXxxBinding, XxxViewModel> {


    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_xxx;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    //页面数据初始化方法
    @Override
    public void initData() {
        binding.tvMenu1.setOnClickListener(lis -> {
            try {
                DCUniMPSDK.getInstance().startApp(getActivity(), "__UNI__04E3A11");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        binding.tvMenu2.setOnClickListener(lis -> {
            startActivity(MyGoodsListActivity.class);
        });
    }


    //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
    @Override
    public void initViewObservable() {

    }

}
