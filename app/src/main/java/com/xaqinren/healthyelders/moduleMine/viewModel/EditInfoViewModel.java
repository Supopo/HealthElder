package com.xaqinren.healthyelders.moduleMine.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xaqinren.healthyelders.apiserver.UserRepository;
import com.xaqinren.healthyelders.widget.pickerView.cityPicker.utils.StringUtils;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class EditInfoViewModel extends BaseViewModel {
    public MutableLiveData<Boolean> requestSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> status = new MutableLiveData<>();
    public MutableLiveData<String> fileLiveData = new MutableLiveData<>();

    public EditInfoViewModel(@NonNull Application application) {
        super(application);
    }

    public void uploadFile(String file) {
        showDialog();
        UserRepository.getInstance().updatePhoto(requestSuccess, fileLiveData, file);
    }
    public void updateAvatar(String url) {
        showDialog();
        UserRepository.getInstance().updateUserInfo(requestSuccess,status,url,null,null,null,null,null);
    }
    public void updateNickname(String url) {
        showDialog();
        UserRepository.getInstance().updateUserInfo(requestSuccess,status,null,url,null,null,null,null);
    }
    public void updateIntroduce(String url) {
        showDialog();
        UserRepository.getInstance().updateUserInfo(requestSuccess,status,null,null,url,null,null,null);
    }
    public void updateSex(String url) {
        showDialog();
        UserRepository.getInstance().updateUserInfo(requestSuccess,status,null,null,null,url,null,null);
    }
    public void updateBirthday(String url) {
        showDialog();
        UserRepository.getInstance().updateUserInfo(requestSuccess,status,null,null,null,null,url,null);
    }
    public void updateCity(String url) {
        showDialog();
        UserRepository.getInstance().updateUserInfo(requestSuccess,status,null,null,null,null,null,url);
    }
}
