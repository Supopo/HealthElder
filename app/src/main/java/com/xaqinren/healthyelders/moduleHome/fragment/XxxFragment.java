package com.xaqinren.healthyelders.moduleHome.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.moduleHome.viewModel.XxxViewModel;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.databinding.ActivityBaseBinding;

//注意ActivityBaseBinding换成自己fragment_layout对应的名字 FragmentXxxBinding
public class XxxFragment extends BaseFragment<ActivityBaseBinding, XxxViewModel> {


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
    }


    //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
    @Override
    public void initViewObservable() {

    }

}
