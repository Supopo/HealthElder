package com.xaqinren.healthyelders.moduleHome.fragment;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSON;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.tencent.bugly.proguard.B;
import com.tencent.qcloud.ugckit.UGCKitConstants;
import com.tencent.qcloud.ugckit.utils.ScreenUtils;
import com.tencent.qcloud.ugckit.utils.Signature;
import com.tencent.qcloud.xiaoshipin.mainui.TCMainActivity;
import com.tencent.qcloud.xiaoshipin.mainui.list.TCVideoInfo;
import com.tencent.qcloud.xiaoshipin.play.TCVodPlayerActivity;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.apiserver.LiteAvRepository;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.FragmentXxxBinding;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleHome.activity.DraftActivity;
import com.xaqinren.healthyelders.moduleHome.viewModel.XxxViewModel;
import com.xaqinren.healthyelders.moduleLiteav.activity.ChooseUnLookActivity;
import com.xaqinren.healthyelders.moduleLiteav.activity.LiteAvPlay2Activity;
import com.xaqinren.healthyelders.moduleLiteav.activity.LiteAvPlayActivity;
import com.xaqinren.healthyelders.moduleLiteav.activity.VideoPublishActivity;
import com.xaqinren.healthyelders.moduleLiteav.bean.SaveDraftBean;
import com.xaqinren.healthyelders.moduleLogin.activity.SelectLoginActivity;
import com.xaqinren.healthyelders.moduleLogin.bean.LoginTokenBean;
import com.xaqinren.healthyelders.modulePicture.activity.PublishTextPhotoActivity;
import com.xaqinren.healthyelders.modulePicture.activity.TextPhotoDetailActivity;
import com.xaqinren.healthyelders.moduleZhiBo.activity.LiveGuanzhongActivity;
import com.xaqinren.healthyelders.moduleZhiBo.activity.MyGoodsListActivity;
import com.xaqinren.healthyelders.moduleZhiBo.activity.SettingRoomPwdActivity;
import com.xaqinren.healthyelders.moduleZhiBo.activity.StartRenZhengActivity;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.MLVBLiveRoom;
import com.xaqinren.healthyelders.moduleZhiBo.liveRoom.roomutil.commondef.LoginInfo;
import com.xaqinren.healthyelders.moduleZhiBo.popupWindow.ZBGiftListPop;
import com.xaqinren.healthyelders.moduleZhiBo.popupWindow.ZBStartSettingPop;
import com.xaqinren.healthyelders.push.PushNotify;
import com.xaqinren.healthyelders.utils.AnimUtils;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.utils.MScreenUtil;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.dcloud.common.DHInterface.ICallBack;
import io.dcloud.feature.sdk.DCUniMPAloneTaskActivity;
import io.dcloud.feature.sdk.DCUniMPSDK;
import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.http.DownLoadManager;
import me.goldze.mvvmhabit.http.download.ProgressCallBack;
import me.goldze.mvvmhabit.utils.SPUtils;

//注意ActivityBaseBinding换成自己fragment_layout对应的名字 FragmentXxxBinding
public class XxxFragment extends BaseFragment<FragmentXxxBinding, XxxViewModel> {


    private String wgtPath;
    private LoginTokenBean loginTokenBean;
    private String liveRoomId;

    public static final int START_LIVE_PLAY = 100;

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
        checkPermission();

        ViewGroup.LayoutParams layoutParams = binding.container.getLayoutParams();
        layoutParams.height = MScreenUtil.getScreenHeight(getActivity());

        binding.tvMenu1.setOnClickListener(lis -> {
            try {
                DCUniMPSDK.getInstance().startApp(getActivity(), "__UNI__2108B0A");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        binding.tvMenu2.setOnClickListener(lis -> {
            startActivity(MyGoodsListActivity.class);
        });
        binding.tvMenu3.setOnClickListener(lis -> {
            startActivity(SelectLoginActivity.class);
        });
        binding.tvMenu4.setOnClickListener(lis -> {
            toXCX();
        });
        //小程序下载地址
        String url = "http://h5.sld.bizyun.top/__UNI__D8BE47E.wgt";
        //保存到本地的位置
        String fileDir = getActivity().getExternalCacheDir().getAbsolutePath();
        wgtPath = fileDir + "/__UNI__D8BE47E.wgt";

        binding.tvMenu5.setOnClickListener(lis -> {
            //下载
            DownLoadManager.getInstance().load(url, new ProgressCallBack(fileDir, "__UNI__D8BE47E.wgt") {
                @Override
                public void onSuccess(Object o) {
                    Toast.makeText(getActivity(), "下载成功", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void progress(long progress, long total) {

                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(getActivity(), "下载失败", Toast.LENGTH_SHORT).show();
                }
            });
        });
        binding.tvMenu6.setOnClickListener(lis -> {
            startActivity(SettingRoomPwdActivity.class);
        });

        binding.tvMenu7.setOnClickListener(lis -> {
            if (loginTokenBean != null) {
                showDialog();
                viewModel.getUserInfo(loginTokenBean.access_token);
            }
        });

        binding.tvMenu8.setOnClickListener(lis -> {
            startActivity(StartRenZhengActivity.class);
        });

        binding.tvMenu101.setOnClickListener(lis -> {
            startActivity(TCMainActivity.class);
        });

        String jsUserInfo = SPUtils.getInstance().getString(Constant.SP_KEY_TOKEN_INFO);
        loginTokenBean = JSON.parseObject(jsUserInfo, LoginTokenBean.class);


        binding.tvMenu9.setOnClickListener(lis -> {
            LogUtils.v("--", UserInfoMgr.getInstance().getAccessToken());
            showEditTextDialog();
        });
        binding.tvMenu102.setOnClickListener(view -> {
            startActivity(ChooseUnLookActivity.class);
        });
        binding.tvMenu103.setOnClickListener(view -> {
            startActivity(VideoPublishActivity.class);
        });
        binding.tvMenu104.setOnClickListener(view -> {
            String fileName = UserInfoMgr.getInstance().getUserInfo().getId();
            Bundle bundle = new Bundle();
            SaveDraftBean bean = LiteAvRepository.getInstance().getDraftsList(getContext(), fileName).get(0);
            long id = bean.getId();
            int type = bean.getType();
            bundle.putLong(Constant.DraftId, id);
            startActivity(type == 0 ? VideoPublishActivity.class : PublishTextPhotoActivity.class, bundle);
        });
        binding.tvMenu105.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), LiteAvPlay2Activity.class);
            //            intent.putExtra(UGCKitConstants.PLAY_URL, "http://1302448977.vod2.myqcloud.com/2734970fvodcq1302448977/82c4c0ce5285890818148680323/aW5a30SYEaMA.mp4");
            //            intent.putExtra(UGCKitConstants.PUSHER_ID, "123456789");
            //            intent.putExtra(UGCKitConstants.PUSHER_NAME, "老王");
            //            intent.putExtra(UGCKitConstants.PUSHER_AVATAR, "http://1302448977.vod2.myqcloud.com/2734970fvodcq1302448977/82c4c0ce5285890818148680323/5285890818148680324.jpg");
            //            intent.putExtra(UGCKitConstants.COVER_PIC, "http://1302448977.vod2.myqcloud.com/2734970fvodcq1302448977/82c4c0ce5285890818148680323/5285890818148680324.jpg");
            //            intent.putExtra(UGCKitConstants.FILE_ID, "5285890818148680323");
            intent.putExtra(UGCKitConstants.TCLIVE_INFO_LIST, (Serializable) createTestData());
            //            intent.putExtra(UGCKitConstants.TIMESTAMP, "2019-03-26 14:09:28");
            //            intent.putExtra(UGCKitConstants.TCLIVE_INFO_POSITION, 0);
            startActivityForResult(intent, START_LIVE_PLAY);
        });
        binding.tvMenu106.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), TextPhotoDetailActivity.class);
            intent.putExtra(com.xaqinren.healthyelders.moduleLiteav.Constant.VIDEO_ID, "60a8ce59ea3225775c330043");
            startActivity(intent);
        });
        binding.tvMenu107.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), DraftActivity.class);
            startActivity(intent);
        });
        binding.tvMenu108.setOnClickListener(lis ->{
            ZBGiftListPop giftListPop = new ZBGiftListPop(getActivity(), null);
            giftListPop.showPopupWindow();
        });
        binding.tvMenu109.setOnClickListener(lis ->{
            try {
                Class cls = Class.forName("io.dcloud.feature.sdk.DCUniMPAloneTaskActivity");
                Intent intent = new Intent(getContext(), cls);
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        });
    }

    /**
     * TODO 视频播放地址从后台生成,前台生成不安全
     *
     * @return
     */
    private List<TCVideoInfo> createTestData() {
        int time = (int) (System.currentTimeMillis() / 1000 + (24 * 60 * 60 * 10));
        String t = Signature.getTimeExpire(time);
        String playToken = null;
        String playUrl = "http://1302448977.vod2.myqcloud.com/2734970fvodcq1302448977/449bcd025285890818256440011/8bXNLBaIVRIA.mp4";
        try {
            playToken = Signature.singVideo(playUrl, t);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        List<TCVideoInfo> list = new ArrayList<>();
        TCVideoInfo info = new TCVideoInfo();
        info.avatar = "http://1302448977.vod2.myqcloud.com/2734970fvodcq1302448977/004aa94b5285890818150396701/5285890818150396702.jpg";
        info.playurl = playUrl + "?t=" + t + "&sign=" + playToken;
        info.userid = "123456789";
        info.nickname = "老王";
        info.frontcover = "http://1302448977.vod2.myqcloud.com/2734970fvodcq1302448977/004aa94b5285890818150396701/5285890818150396702.jpg";
        info.createTime = "2021-05-10 11:49:40";
        info.review_status = TCVideoInfo.REVIEW_STATUS_NORMAL;
        list.add(info);
        return list;
    }

    private void showEditTextDialog() {
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
        builder.setTitle("进入直播间")
                .setPlaceholder("在此请填入房间号")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("取消", (dialog, index) -> dialog.dismiss())
                .addAction("确定", (dialog, index) -> {
                    CharSequence text = builder.getEditText().getText();
                    if (text != null && text.length() > 0) {
                        Toast.makeText(getActivity(), "您的房间号: " + text, Toast.LENGTH_SHORT).show();
                        liveRoomId = text.toString();
                        viewModel.joinLive(liveRoomId);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getActivity(), "请填入房间号", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void toXCX() {
        DCUniMPSDK.getInstance().releaseWgtToRunPathFromePath("__UNI__D8BE47E", wgtPath, new ICallBack() {
            @Override
            public Object onCallBack(int code, Object pArgs) {
                if (code == 1) {//释放wgt完成
                    try {
                        DCUniMPSDK.getInstance().startApp(getActivity(), "__UNI__D8BE47E");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {//释放wgt失败
                    Toast.makeText(getActivity(), "资源释放失败", Toast.LENGTH_SHORT).show();
                }
                return null;
            }
        });
    }


    //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
    @Override
    public void initViewObservable() {
        viewModel.userInfo.observe(this, userInfo -> {
            dismissDialog();
        });
        viewModel.liveInfo.observe(this, liveInfo -> {
            if (liveInfo != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constant.LiveInitInfo, liveInfo);
                startActivity(LiveGuanzhongActivity.class, bundle);
            }
        });
        viewModel.loginRoomSuccess.observe(this, loginSuccess -> {
            //进入直播间接口
            viewModel.joinLive(liveRoomId);
        });
        viewModel.dismissDialog.observe(this, dismiss -> {
            if (dismiss != null) {
                if (dismiss) {
                    dismissDialog();
                }
            }
        });
    }

    /**
     * 检查并申请权限
     */
    public void checkPermission() {
        int targetSdkVersion = 0;
        String[] PermissionString = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION};
        try {
            final PackageInfo info = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;//获取应用的Target版本
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Build.VERSION.SDK_INT是获取当前手机版本 Build.VERSION_CODES.M为6.0系统
            //如果系统>=6.0
            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                //第 1 步: 检查是否有相应的权限
                boolean isAllGranted = checkPermissionAllGranted(PermissionString);
                if (isAllGranted) {
                    Log.e("err", "所有权限已经授权！");
                    return;
                }
                // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
                ActivityCompat.requestPermissions(getActivity(), PermissionString, 1);
            }
        }
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                Log.e("err", "权限" + permission + "没有授权");
                return false;
            }
        }
        return true;
    }

    /**
     * 通过ActivityManager 获取进程名，需要IPC通信
     */
    public String getCurrentProcessNameByActivityManager(@NonNull Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<String> aryListTaskID = new ArrayList<String>();
        ArrayList<String> aryListTaskName = new ArrayList<String>();
        //以getRunningTasks()取得进程TaskInfo
        List<ActivityManager.RunningTaskInfo> mRunningTasks = am.getRunningTasks(10);
        int intTaskNum = 0;
        for (ActivityManager.RunningTaskInfo amTask : mRunningTasks) {
//            LogUtils.e("WXPayEntryActivity", "amTask ->" + amTask.baseIntent.toString());
//            LogUtils.e("WXPayEntryActivity", "amTask ->" + amTask.isRunning);
            LogUtils.e("WXPayEntryActivity", "amTask ->" + amTask.topActivity.getPackageName());
            LogUtils.e("WXPayEntryActivity", "amTask ->" + amTask.topActivity.getClassName());
            if (amTask.topActivity.getClassName().equals("DCUniMPAloneTaskActivity")) {
                //小程序进程打开过
                try {
                    Class cls = Class.forName(amTask.topActivity.getClassName());
                    Intent intent = new Intent(getContext(),cls);
                    startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }
//            LogUtils.e("WXPayEntryActivity", "amTask ->" + amTask.taskId);
            aryListTaskName.add(amTask.baseActivity.getClassName());
            aryListTaskID.add("" + amTask.id);
            intTaskNum++;
        }
        return null;
    }

}
