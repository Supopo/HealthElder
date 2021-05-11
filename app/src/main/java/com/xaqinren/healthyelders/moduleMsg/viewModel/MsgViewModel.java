package com.xaqinren.healthyelders.moduleMsg.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.moduleMsg.bean.MsgBean;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * Created by Lee. on 2021/5/11.
 */
public class MsgViewModel extends BaseViewModel {
    public MsgViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<MsgBean>> msgList = new MutableLiveData<>();

    public void getMsgList() {
        List<MsgBean> msgs = new ArrayList();
        for (int i = 0; i < 30; i++) {
            MsgBean msgBean = new MsgBean();
            msgBean.name = "用户" + (i + 1);
            msgs.add(msgBean);
        }
        msgList.setValue(msgs);
    }
}
