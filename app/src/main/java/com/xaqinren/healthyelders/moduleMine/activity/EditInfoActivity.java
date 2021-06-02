package com.xaqinren.healthyelders.moduleMine.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.configure.PickerOptions;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.contrarywind.view.WheelView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.PictureSelectorActivity;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.PictureSelectionConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.style.PictureCropParameterStyle;
import com.luck.picture.lib.style.PictureParameterStyle;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivityEditInfoBinding;
import com.xaqinren.healthyelders.moduleLogin.bean.UserInfoBean;
import com.xaqinren.healthyelders.moduleMine.adapter.EditInfoAdapter;
import com.xaqinren.healthyelders.moduleMine.bean.EditMenuBean;
import com.xaqinren.healthyelders.moduleMine.viewModel.EditInfoViewModel;
import com.xaqinren.healthyelders.moduleMsg.activity.AddFriendActivity;
import com.xaqinren.healthyelders.modulePicture.activity.PublishTextPhotoActivity;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ListPopMenuBean;
import com.xaqinren.healthyelders.utils.GlideEngine;
import com.xaqinren.healthyelders.utils.GlideUtil;
import com.xaqinren.healthyelders.utils.IntentUtils;
import com.xaqinren.healthyelders.utils.MScreenUtil;
import com.xaqinren.healthyelders.utils.UrlUtils;
import com.xaqinren.healthyelders.widget.ListBottomPopup;
import com.xaqinren.healthyelders.widget.SwitchButton;
import com.xaqinren.healthyelders.widget.pickerView.cityPicker.CityPickerActivity;
import com.xaqinren.healthyelders.widget.share.ShareDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.StringUtils;
import me.goldze.mvvmhabit.utils.Utils;

public class EditInfoActivity extends BaseActivity<ActivityEditInfoBinding, EditInfoViewModel> {
    private List<EditMenuBean> editMenuBeans = new ArrayList<>();
    private UserInfoBean userInfoBean;
    private EditInfoAdapter editInfoAdapter;
    private TimePickerView pvTime;
    private int REQUEST_GALLERY = 666;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_edit_info;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setTitle("编辑资料");
        userInfoBean = UserInfoMgr.getInstance().getUserInfo();
        createEditList();
        editInfoAdapter = new EditInfoAdapter();
        editInfoAdapter.setList(editMenuBeans);
        binding.menuList.setLayoutManager(new LinearLayoutManager(this));
        binding.menuList.setNestedScrollingEnabled(false);
        binding.menuList.setAdapter(editInfoAdapter);
        binding.selAvatar.setOnClickListener(view -> {
            //选择头像
            selAvatar();
        });
        editInfoAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (position == 0){
                changeName();
            }else if (position == 1){
            }else if (position == 2){
                changeInfo();
            }else if (position == 3){
                changeSex();
            }else if (position == 4){
                changeBirth();
            }else if (position == 5){
                changeCity();
            }
        });
        int dp94 = (int) getResources().getDimension(R.dimen.dp_94);
        GlideUtil.intoImageView(this, UrlUtils.resetImgUrl(userInfoBean.getAvatarUrl(), dp94, dp94), binding.avatar);
    }
    private void createEditList() {
        editMenuBeans.add(new EditMenuBean("名字", userInfoBean.getNickname(), true, ""));
        editMenuBeans.add(new EditMenuBean("健康号", userInfoBean.getRecommendedCode(), false, ""));
        editMenuBeans.add(new EditMenuBean("简介", getValue(userInfoBean.getIntroduce(),"点击设置") , true, ""));
        editMenuBeans.add(new EditMenuBean("性别", getValue(getSex( userInfoBean.getSex()),"不展示"), true, ""));
        editMenuBeans.add(new EditMenuBean("生日", getValue(null,"不展示"), true, ""));
        editMenuBeans.add(new EditMenuBean("所在地", getValue(userInfoBean.getCityAddress(),"暂不设置"), true, ""));
    }

    private String getSex(String sex) {
        if (sex == null) {
            return null;
        }
        return sex.equals("MALE") ? "男" : "女";
    }

    private String getValue(String value,String defaultValue) {
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        return value;
    }

    private void changeName() {
        startActivityForResult(new Intent(this, EditNameActivity.class), 0x0010);
    }

    private void changeInfo() {
        startActivityForResult(new Intent(this, EditBriefActivity.class), 0x0020);
    }

    private void changeSex() {
        List<ListPopMenuBean> menus = new ArrayList<>();
        menus.add(new ListPopMenuBean("男"));
        menus.add(new ListPopMenuBean("女"));
        menus.add(new ListPopMenuBean("不展示"));
        ListBottomPopup listBottomPopup = new ListBottomPopup(this, menus);
        listBottomPopup.setOnItemClickListener((adapter, view, position) -> {
            //掉接口
            if (position == 0) {

            } else if (position == 1) {

            } else if (position == 2) {

            }
        });
        listBottomPopup.showPopupWindow();
    }

    private void changeBirth() {
        Calendar selData = Calendar.getInstance();
        selData.set(2000, 0, 1);
        Calendar startDate = Calendar.getInstance();
        startDate.set(1900, 0, 1);
        Calendar endDate = Calendar.getInstance();

        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                //                tvTime.setText(getTime(date));
            }
        })
                .setLayoutRes(R.layout.picker_custom_data_1, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        SwitchButton switchButton = v.findViewById(R.id.sb_menu1);
                        switchButton.setOnCheckedChangeListener((view, isChecked) -> {

                        });
                        TextView confirm = v.findViewById(R.id.confirm);
                    }
                })
                .setType(new boolean[]{true, true, true, false, false, false})// 默认全部显示
                .setItemVisibleCount(3)
                .setContentTextSize(20)//滚轮文字大小
                .setTitleText("Title")//标题文字
                .setDecorView(binding.rlContainer)
                .setTextColorCenter(getResources().getColor(R.color.color_252525))
                .setTextColorOut(getResources().getColor(R.color.color_DEDEDE))
                .isAlphaGradient(false)
                .setDividerType(WheelView.DividerType.WRAP)
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .setLineSpacingMultiplier(3)
                .isCyclic(false)//是否循环滚动
                .setDate(selData)
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .setLabel("年", "月", "日", "时", "分", "秒")//默认设置为年月日时分秒
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)//是否显示为对话框样式
                .build();
        pvTime.show();
    }

    private void changeCity() {
        Intent intent = new Intent();
        intent.putExtra("show_area", 0);
        intent.setClass(this, CityPickerActivity.class);
        startActivityForResult(intent,0x0030);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0x0030) {
                if (data != null) {
//                    cityName = data.getStringExtra(CityPickerActivity.KEY_PICKED_CITY);
//                    binding.cityName.setText(cityName);
//                    binding.searchView.setText(null);
                }
            }
            else if (resultCode == REQUEST_GALLERY) {
                List<LocalMedia> result = PictureSelector.obtainMultipleResult(data);
                String path = result.get(0).getPath();
                GlideUtil.intoImageView(this, path, binding.avatar);
            }
        }
    }

    private void selAvatar() {
        List<ListPopMenuBean> menus = new ArrayList<>();
        menus.add(new ListPopMenuBean("拍摄"));
        menus.add(new ListPopMenuBean("从相册中选取"));
        ListBottomPopup listBottomPopup = new ListBottomPopup(this, menus);
        listBottomPopup.setOnItemClickListener((adapter, view, position) -> {
            //掉接口
            if (position == 0) {
                toCamera();
            } else if (position == 1) {
                toPhoto();
            }
            listBottomPopup.dismiss();
        });
        listBottomPopup.showPopupWindow();

    }
    private void toCamera() {
        int width = (int) MScreenUtil.dp2px(this, 150);
        PictureSelector.create(this)
                .openCamera(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
                .maxSelectNum(1)// 最大图片选择数量
                .isEnableCrop(true)
                .isCompress(true)
                .freeStyleCropEnabled(true)
                .cropImageWideHigh(width,width)
                .isAndroidQTransform(false)//开启沙盒 高版本必须选择不然拿不到小图
                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .forResult(REQUEST_GALLERY);//结果回调onActivityResult code
    }
    private void toPhoto() {
        int width = (int) MScreenUtil.dp2px(this, 150);
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
                .maxSelectNum(1)// 最大图片选择数量
                .isCamera(true)// 是否显示拍照按钮
                .isPreviewEggs(true)//预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                .isPreviewImage(true)// 是否可预览图片
                .isEnableCrop(true)// 是否裁剪 true or false
                .freeStyleCropEnabled(true)
                .isCompress(true)// 是否压缩图片 使用的是Luban压缩
                .cropImageWideHigh(width,width)
                .isAndroidQTransform(false)//开启沙盒 高版本必须选择不然拿不到小图
                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .forResult(REQUEST_GALLERY);//结果回调onActivityResult code
    }

}
