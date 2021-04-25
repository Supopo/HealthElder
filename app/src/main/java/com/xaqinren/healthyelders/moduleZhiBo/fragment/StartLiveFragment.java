package com.xaqinren.healthyelders.moduleZhiBo.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.tencent.liteav.demo.beauty.model.BeautyInfo;
import com.tencent.liteav.demo.beauty.model.ItemInfo;
import com.tencent.liteav.demo.beauty.model.TabInfo;
import com.tencent.liteav.demo.beauty.view.BeautyPanel;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.FragmentStartLiveBinding;
import com.xaqinren.healthyelders.moduleZhiBo.activity.SettingRoomPwdActivity;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ListPopMenuBean;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.moduleZhiBo.popupWindow.ZBStartSettingPop;
import com.xaqinren.healthyelders.utils.GlideEngine;
import com.xaqinren.healthyelders.widget.BottomDialog;
import com.xaqinren.healthyelders.widget.ListBottomPopup;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.base.BaseViewModel;

/**
 * Created by Lee. on 2021/4/23.
 * 开启直播间
 */
public class StartLiveFragment extends BaseFragment<FragmentStartLiveBinding, BaseViewModel> {

    private MLVBLiveRoom mLiveRoom;
    private List<LocalMedia> selectList;
    private String photoPath;
    private boolean isAgree = true;
    private boolean isJingXiangRight;//是否镜像翻转
    private boolean isBackCamera;//是否后置摄像头
    private boolean isOpenRoom = true;//是否公开
    private ZBStartSettingPop startSettingPop;
    private BottomDialog mLvJingPop;
    private BottomDialog mMeiYanPop;
    private BeautyPanel mMeiYanControl;
    private BeautyPanel mLvJingControl;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_start_live;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setMoreTextData("我已阅读并同意", "《健康长老视频号直播功能使用条款》", "及", "《健康长老视频号直播行为规范》");
        //直播间控制类
        mLiveRoom = MLVBLiveRoom.sharedInstance(getActivity());
        //打开本地摄像头预览
        mLiveRoom.startLocalPreview(true, binding.videoView);
        initEvent();
    }

    private void initEvent() {
        binding.ivBack.setOnClickListener(lis -> {
            getActivity().finish();
        });
        //选择图片
        binding.rlAddCover.setOnClickListener(lis -> {
            selectImage();
        });
        binding.llLoc.setOnClickListener(lis -> {
            showListPop();
        });

        //选择同意协议
        binding.rlSelect.setOnClickListener(lis -> {
            isAgree = !isAgree;
            if (isAgree) {
                binding.ivSelect.setBackgroundResource(R.mipmap.radbox_sel);
            } else {
                binding.ivSelect.setBackgroundResource(R.mipmap.radbox_nor);
            }
        });
        //镜头翻转
        binding.llJx.setOnClickListener(lis -> {
            isJingXiangRight = !isJingXiangRight;
            mLiveRoom.setMirror(isJingXiangRight);
            if (isJingXiangRight) {
                binding.ivJx.setBackgroundResource(R.mipmap.icon_jingxiang_fz);
            } else {
                binding.ivJx.setBackgroundResource(R.mipmap.icon_jingxiang);
            }
        });
        //摄像头设置
        binding.llFanzhuan.setOnClickListener(lis -> {
            isBackCamera = !isBackCamera;
            mLiveRoom.switchCamera();
        });
        //美颜设置
        binding.llPs.setOnClickListener(lis -> {
            showMYPop();
        });
        //滤镜设置
        binding.llLj.setOnClickListener(lis -> {
            showLJPop();
        });
        //公开设置
        binding.llPwd.setOnClickListener(lis -> {
            startActivity(SettingRoomPwdActivity.class);
        });
        //设置
        binding.llSet.setOnClickListener(lis -> {
            startSettingPop = new ZBStartSettingPop(getActivity());
            startSettingPop.showPopupWindow();
        });
    }

    private void showListPop() {
        List<ListPopMenuBean> menus = new ArrayList<>();
        menus.add(new ListPopMenuBean("显示位置更多人能看到你噢", getResources().getColor(R.color.gray_999), 14));
        menus.add(new ListPopMenuBean("显示位置", 0, 0));
        menus.add(new ListPopMenuBean("隐藏位置", 0, 0));
        ListBottomPopup listBottomPopup = new ListBottomPopup(getActivity(), menus, new OnItemClickListener() {

            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Log.e("--", "点击了：" + menus.get(position).menuName);
            }
        });
        listBottomPopup.showPopupWindow();
    }

    private void showMYPop() {

        if (mMeiYanPop == null) {
            View filterView = View.inflate(getActivity(), R.layout.pop_beauty_control, null);
            mMeiYanControl = filterView.findViewById(R.id.beauty_pannel);
            mMeiYanControl.setPosition(0);
            mMeiYanControl.setBeautyManager(mLiveRoom.getBeautyManager());
            mMeiYanControl.setPopTitle("美颜");
            mMeiYanPop = new BottomDialog(getActivity(), filterView,
                    null);
        }
        mMeiYanPop.show();
        mMeiYanControl.setOnBeautyListener(new BeautyPanel.OnBeautyListener() {
            @Override
            public void onTabChange(TabInfo tabInfo, int position) {
            }

            @Override
            public boolean onClose() {
                return false;
            }

            @Override
            public boolean onClick(TabInfo tabInfo, int tabPosition, ItemInfo itemInfo, int itemPosition) {
                return false;
            }

            @Override
            public boolean onLevelChanged(TabInfo tabInfo, int tabPosition, ItemInfo itemInfo, int itemPosition, int beautyLevel) {
                return false;
            }
        });
        mMeiYanPop.setOnBottomItemClickListener(new BottomDialog.OnBottomItemClickListener() {
            @Override
            public void onBottomItemClick(BottomDialog dialog, View view) {

            }
        });

    }

    //滤镜设置弹窗
    private void showLJPop() {
        if (mLvJingPop == null) {
            View filterView = View.inflate(getActivity(), R.layout.pop_beauty_control, null);
            mLvJingControl = filterView.findViewById(R.id.beauty_pannel);
            mLvJingControl.setPosition(1);
            mLvJingControl.setBeautyManager(mLiveRoom.getBeautyManager());

            mLvJingControl.setPopTitle("滤镜");
            BeautyInfo defaultBeautyInfo = mLvJingControl.getDefaultBeautyInfo();
            mLvJingControl.setBeautyInfo(defaultBeautyInfo);

            mLvJingPop = new BottomDialog(getActivity(), filterView,
                    null);
        }

        mLvJingPop.show();


        mLvJingControl.setOnBeautyListener(new BeautyPanel.OnBeautyListener() {
            @Override
            public void onTabChange(TabInfo tabInfo, int position) {
            }

            @Override
            public boolean onClose() {
                return false;
            }

            @Override
            public boolean onClick(TabInfo tabInfo, int tabPosition, ItemInfo itemInfo, int itemPosition) {
                return false;
            }

            @Override
            public boolean onLevelChanged(TabInfo tabInfo, int tabPosition, ItemInfo itemInfo, int itemPosition, int beautyLevel) {
                return false;
            }
        });
        mLvJingPop.setOnBottomItemClickListener(new BottomDialog.OnBottomItemClickListener() {
            @Override
            public void onBottomItemClick(BottomDialog dialog, View view) {

            }
        });
    }

    //设置一段文字多种点击事件
    public void setMoreTextData(String text1, String text2, String text3, String text4) {
        String all = text1 + text2 + text3 + text4;
        SpannableString spannableString = new SpannableString(all);
        spannableString.setSpan(new NoLineColorSpan() {
            @Override
            public void onClick(@NonNull View widget) {
            }
        }, text1.length(), (text1 + text2).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_DADA)), 0, text1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_DADA)), (text1 + text2).length(), (text1 + text2 + text3).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), text1.length(), (text1 + text2).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new NoLineColorSpan() {
            @Override
            public void onClick(@NonNull View widget) {
            }
        }, (text1 + text2 + text3).length(), all.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), (text1 + text2 + text3).length(), all.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        binding.tvShow.setHighlightColor(Color.TRANSPARENT);
        binding.tvShow.setMovementMethod(LinkMovementMethod.getInstance());
        binding.tvShow.setText(spannableString);
    }

    public abstract class NoLineColorSpan extends ClickableSpan {

        @Override
        public void onClick(@NonNull View widget) {

        }

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            ds.setColor(ds.linkColor);
            ds.setUnderlineText(false);
        }
    }

    //选择照片
    private void selectImage() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .loadImageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
                .maxSelectNum(1)// 最大图片选择数量
                .isCamera(true)// 是否显示拍照按钮
                .previewEggs(true)//预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                .previewImage(true)// 是否可预览图片
                .enableCrop(false)// 是否裁剪 true or false
                .compress(true)// 是否压缩图片 使用的是Luban压缩
                .isAndroidQTransform(false)//开启沙盒 高版本必须选择不然拿不到小图
                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    if (selectList != null && selectList.size() > 0) {
                        LocalMedia localMedia = selectList.get(0);

                        // 例如 LocalMedia 里面返回五种path
                        // 1.media.getPath(); 为原图path
                        // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                        // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                        // 4.media.getOriginalPath()); media.isOriginal());为true时此字段才有值
                        // 5.media.getAndroidQToPath();为Android Q版本特有返回的字段，此字段有值就用来做上传使用
                        // 如果同时开启裁剪和压缩，则取压缩路径为准因为是先裁剪后压缩

                        // 裁剪会出一些问题
                        if (localMedia.isCompressed()) {
                            photoPath = localMedia.getCompressPath();
                        } else if (localMedia.isOriginal()) {
                            photoPath = localMedia.getOriginalPath();
                        } else if (localMedia.isCut()) {
                            photoPath = localMedia.getCutPath();
                        } else {
                            photoPath = localMedia.getRealPath();
                        }
                        // 顺序挺重要
                        if (photoPath == null) {
                            photoPath = localMedia.getAndroidQToPath();
                        }
                        if (photoPath == null) {
                            photoPath = localMedia.getPath();
                        }
                        if (photoPath.contains("content://")) {
                            Uri uri = Uri.parse(photoPath);
                            photoPath = getFilePathByUri(uri, getActivity());
                        }

                        Glide.with(this).load(photoPath).into(binding.ivCover);
                    }
                    break;
            }
        }
    }

    private String getFilePathByUri(Uri uri, Context context) {
        // 以 content:// 开头的，比如  content://media/external/file/960
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            String path = null;
            String[] projection = new String[]{MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null,
                    null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (columnIndex > -1) {
                        path = cursor.getString(columnIndex);
                    }
                }
                cursor.close();
            }
            return path;
        }
        return null;
    }

}