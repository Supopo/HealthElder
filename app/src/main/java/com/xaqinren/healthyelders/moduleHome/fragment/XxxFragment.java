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
import com.xaqinren.healthyelders.moduleMine.activity.SettingPayPassActivity;
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

//??????ActivityBaseBinding????????????fragment_layout??????????????? FragmentXxxBinding
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

    //???????????????????????????
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
        //?????????????????????
        String url = "http://h5.sld.bizyun.top/__UNI__D8BE47E.wgt";
        //????????????????????????
        String fileDir = getActivity().getExternalCacheDir().getAbsolutePath();
        wgtPath = fileDir + "/__UNI__D8BE47E.wgt";

        binding.tvMenu5.setOnClickListener(lis -> {
            //??????
            DownLoadManager.getInstance().load(url, new ProgressCallBack(fileDir, "__UNI__D8BE47E.wgt") {
                @Override
                public void onSuccess(Object o) {
                    Toast.makeText(getActivity(), "????????????", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void progress(long progress, long total) {

                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(getActivity(), "????????????", Toast.LENGTH_SHORT).show();
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
            //            intent.putExtra(UGCKitConstants.PUSHER_NAME, "??????");
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
        binding.tvMenu110.setOnClickListener(lis ->{
            startActivity(SettingPayPassActivity.class);
        });
    }

    /**
     * TODO ?????????????????????????????????,?????????????????????
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
        info.nickname = "??????";
        info.frontcover = "http://1302448977.vod2.myqcloud.com/2734970fvodcq1302448977/004aa94b5285890818150396701/5285890818150396702.jpg";
        info.createTime = "2021-05-10 11:49:40";
        info.review_status = TCVideoInfo.REVIEW_STATUS_NORMAL;
        list.add(info);
        return list;
    }

    private void showEditTextDialog() {
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
        builder.setTitle("???????????????")
                .setPlaceholder("????????????????????????")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("??????", (dialog, index) -> dialog.dismiss())
                .addAction("??????", (dialog, index) -> {
                    CharSequence text = builder.getEditText().getText();
                    if (text != null && text.length() > 0) {
                        Toast.makeText(getActivity(), "???????????????: " + text, Toast.LENGTH_SHORT).show();
                        liveRoomId = text.toString();
                        viewModel.joinLive(liveRoomId);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getActivity(), "??????????????????", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void toXCX() {
        DCUniMPSDK.getInstance().releaseWgtToRunPathFromePath("__UNI__D8BE47E", wgtPath, new ICallBack() {
            @Override
            public Object onCallBack(int code, Object pArgs) {
                if (code == 1) {//??????wgt??????
                    try {
                        DCUniMPSDK.getInstance().startApp(getActivity(), "__UNI__D8BE47E");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {//??????wgt??????
                    Toast.makeText(getActivity(), "??????????????????", Toast.LENGTH_SHORT).show();
                }
                return null;
            }
        });
    }


    //??????????????????????????????????????????ViewModel?????????View??????????????????
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
            //?????????????????????
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
     * ?????????????????????
     */
    public void checkPermission() {
        int targetSdkVersion = 0;
        String[] PermissionString = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION};
        try {
            final PackageInfo info = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;//???????????????Target??????
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Build.VERSION.SDK_INT??????????????????????????? Build.VERSION_CODES.M???6.0??????
            //????????????>=6.0
            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                //??? 1 ???: ??????????????????????????????
                boolean isAllGranted = checkPermissionAllGranted(PermissionString);
                if (isAllGranted) {
                    Log.e("err", "???????????????????????????");
                    return;
                }
                // ????????????????????????, ????????????????????????????????????????????????????????????
                ActivityCompat.requestPermissions(getActivity(), PermissionString, 1);
            }
        }
    }

    /**
     * ???????????????????????????????????????
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                // ????????????????????????????????????, ??????????????? false
                Log.e("err", "??????" + permission + "????????????");
                return false;
            }
        }
        return true;
    }

    /**
     * ??????ActivityManager ????????????????????????IPC??????
     */
    public String getCurrentProcessNameByActivityManager(@NonNull Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<String> aryListTaskID = new ArrayList<String>();
        ArrayList<String> aryListTaskName = new ArrayList<String>();
        //???getRunningTasks()????????????TaskInfo
        List<ActivityManager.RunningTaskInfo> mRunningTasks = am.getRunningTasks(10);
        int intTaskNum = 0;
        for (ActivityManager.RunningTaskInfo amTask : mRunningTasks) {
//            LogUtils.e("WXPayEntryActivity", "amTask ->" + amTask.baseIntent.toString());
//            LogUtils.e("WXPayEntryActivity", "amTask ->" + amTask.isRunning);
            LogUtils.e("WXPayEntryActivity", "amTask ->" + amTask.topActivity.getPackageName());
            LogUtils.e("WXPayEntryActivity", "amTask ->" + amTask.topActivity.getClassName());
            if (amTask.topActivity.getClassName().equals("DCUniMPAloneTaskActivity")) {
                //????????????????????????
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
