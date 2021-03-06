package com.xaqinren.healthyelders.modulePicture.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.MainActivity;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.bean.UserInfoMgr;
import com.xaqinren.healthyelders.databinding.ActivityPublishTextPhotoBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.global.Constant;
import com.xaqinren.healthyelders.moduleLiteav.activity.ChooseLocationActivity;
import com.xaqinren.healthyelders.moduleLiteav.adapter.ChooseTopicAdapter;
import com.xaqinren.healthyelders.moduleLiteav.adapter.ChooseUserAdapter;
import com.xaqinren.healthyelders.moduleLiteav.adapter.PublishLocationAdapter;
import com.xaqinren.healthyelders.moduleLiteav.bean.LiteAvUserBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.LocationBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishAtBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishDesBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.PublishSummaryBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.SaveDraftBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.TopicBean;
import com.xaqinren.healthyelders.moduleLiteav.bean.VideoPublishEditBean;
import com.xaqinren.healthyelders.moduleLiteav.service.LocationService;
import com.xaqinren.healthyelders.modulePicture.adapter.PictureAdapter;
import com.xaqinren.healthyelders.modulePicture.bean.LocalPhotoBean;
import com.xaqinren.healthyelders.modulePicture.bean.PublishBean;
import com.xaqinren.healthyelders.modulePicture.viewModel.PublishTextPhotoViewModel;
import com.xaqinren.healthyelders.moduleZhiBo.bean.ListPopMenuBean;
import com.xaqinren.healthyelders.utils.GlideEngine;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.widget.YesOrNoDialog;
import com.xaqinren.healthyelders.widget.ListBottomPopup;
import com.xaqinren.healthyelders.widget.VideoPublishEditTextView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.ImageUtils;
import me.goldze.mvvmhabit.utils.KeyBoardUtils;
import me.goldze.mvvmhabit.utils.PermissionUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * ????????????
 */
public class PublishTextPhotoActivity extends BaseActivity<ActivityPublishTextPhotoBinding, PublishTextPhotoViewModel> implements PoiSearch.OnPoiSearchListener {

    private String TAG = "PublishTextPhotoActivity";

    private PublishLocationAdapter publishLocationAdapter;
    private List<LocationBean> locationBeans = new ArrayList<>();


    //@????????????adapter
    private ChooseUserAdapter userAdapter;
    //@????????????
    private List<LiteAvUserBean> liteAvUserBeans = new ArrayList<>();
    //?????????????????????#?????????
    private ChooseTopicAdapter chooseTopicAdapter;
    //????????????
    private List<TopicBean> listTopicBeans = new ArrayList<>();

    private int location_code = 777;//??????

    //????????????
    private double lat, lon;
    private String poiName;
    private String cityCode;
    private String cityName;
    //????????????
    LocationBean locationBean;
    private Disposable eventDisposable;

    //?????????Id
    private long publishDraftId;


    private int atPage = 1;
    private int atPageSize = 20;

    private Handler mHandler = new Handler();
    //????????????
    private List<LocalPhotoBean> localPhotoBeans = new ArrayList<>();
    private PictureAdapter pictureAdapter;
    private int MAX = 9;
    private int REQUEST_CAMERA = 188;
    private int REQUEST_GALLERY = 189;
    private int AT_CODE = 190;
    private boolean isUploadFile;
    private int upLoadFileCount;
    private boolean isShowKeyBord = false;
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        /*if (intent!=null) {
            ArrayList<String> path = getIntent().getStringArrayListExtra(com.xaqinren.healthyelders.modulePicture.Constant.PHOTO_PATH);
            removeLast();
            addLast(path.get(0));

        }*/
    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_publish_text_photo;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setTitle("??????");
        eventDisposable = RxBus.getDefault().toObservable(EventBean.class).subscribe(o -> {
            if (o.msgId == CodeTable.LOCATION_SUCCESS) {
                //????????????
                LocationBean locationBean = (LocationBean) o.data;
                lat = locationBean.lat;
                lon = locationBean.lon;
                cityCode = locationBean.cityCode;
                cityName = locationBean.cityName;
                poiName = locationBean.desName;
                getAddressList();
            }
        });
        RxSubscriptions.add(eventDisposable);
        checkPermission();
        ArrayList<String> path = getIntent().getStringArrayListExtra(com.xaqinren.healthyelders.modulePicture.Constant.PHOTO_PATH);
        publishDraftId = getIntent().getLongExtra(Constant.DraftId, 0L);
        if (publishDraftId > 0) {
            //?????????????????????
            getDraftContent();
        }
        //??????
        publishLocationAdapter = new PublishLocationAdapter(R.layout.item_publish_location);
        LinearLayoutManager locManager = new LinearLayoutManager(this);
        locManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.includePublish.locationList.setLayoutManager(locManager);
        binding.includePublish.locationList.setAdapter(publishLocationAdapter);
        publishLocationAdapter.setList(locationBeans);

        //??????????????????
        binding.contentInput.setInputMax(500);

        //????????????
        binding.includePublish.locationLayout.setOnClickListener(view -> {
            Intent intent = new Intent(this, ChooseLocationActivity.class);
            startActivityForResult(intent, location_code);
        });

        //??????????????????
        binding.includePublish.saveDraftBtn.setOnClickListener(view -> {
            YesOrNoDialog yesOrNoDialog = new YesOrNoDialog(this);
            yesOrNoDialog.setMessageText("??????????????????????????????");
            yesOrNoDialog.showDialog();
            yesOrNoDialog.setRightBtnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    yesOrNoDialog.dismissDialog();
                    createDraftContent();
                }
            });
        });

        binding.includePublish.publishBtn.setOnClickListener(view -> {
            if (!checkParams())
                return;
            showDialog();
            uploadFile();
        });

        binding.contentInput.setDelEnablePost(false);

        binding.contentInput.setOnTextChangeListener(new VideoPublishEditTextView.OnTextChangeListener() {
            @Override
            public void inputTopic(String str) {
                LogUtils.e(TAG, "inputTopic ?????????????????? -> " + str);
                showTopicView(str);
                if (str.length() == 1) {
                    hideSoftInput();
                }
            }

            @Override
            public void inputNoTopic() {
                LogUtils.e(TAG, "inputNoTopic ?????????????????? -> ");
                binding.includeListTopic.layoutPublishAt.setVisibility(View.GONE);
            }

            @Override
            public void inputAt(String str) {
                LogUtils.e(TAG, "inputTopic ??????@?????? -> " + str);
                //                showAtView(str);
                binding.contentInput.setEnablePost(false);
                Intent intent = new Intent(PublishTextPhotoActivity.this, PublishAtActivity.class);
                startActivityForResult(intent, AT_CODE);
                if (str.length() == 1) {
                    hideSoftInput();
                }
            }

            @Override
            public void inputNoAt() {
                LogUtils.e(TAG, "inputNoTopic ??????@?????? -> ");
                binding.includeListAt.layoutPublishAt.setVisibility(View.GONE);
            }

            @Override
            public void maxInput() {
                LogUtils.e(TAG, "inputNoTopic ????????????????????? -> ");
                ToastUtils.showShort("????????????" + binding.contentInput.getInputMax() + "?????????");
            }
        });

        publishLocationAdapter.setOnItemClickListener((adapter, view, position) -> {
            LocationBean bean = locationBeans.get(position);
            if (bean.isLookMore) {
                Intent intent = new Intent(this, ChooseLocationActivity.class);
                startActivityForResult(intent, location_code);
                return;
            }
            binding.includePublish.myLocation.setText(bean.desName);
            equalsLocation(bean);
        });

        binding.addTopic.setOnClickListener(view -> {
            binding.contentInput.append("#");
        });
        binding.addFriend.setOnClickListener(view -> {
            binding.contentInput.append("@");

        });

        addPhoto(path);

        pictureAdapter = new PictureAdapter();
        pictureAdapter.setList(localPhotoBeans);
        binding.photoList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.photoList.setAdapter(pictureAdapter);
        pictureAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                hideSoftInput();
                if (localPhotoBeans.get(position).type == 0) {
                    //??????

                } else {
                    showListPop();
                }
            }
        });

        pictureAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            hideSoftInput();
            localPhotoBeans.remove(position);
            if (localPhotoBeans.isEmpty()) {
                addAddPhoto();
            } else if (localPhotoBeans.get(localPhotoBeans.size() - 1).type == 0) {
                addAddPhoto();
            }
            pictureAdapter.setList(localPhotoBeans);
        });

        initListView();

        onGlobalLayoutListener = () -> {
            final Rect r = new Rect();
            binding.rlContainer.getWindowVisibleDisplayFrame(r);
            final int screenHeight = binding.rlContainer.getRootView().getHeight();
            final int heightDifference = screenHeight - r.bottom;
            boolean visible = heightDifference > screenHeight / 3;
            if (visible) {
                if (isShowKeyBord) {
                    return;
                }
                isShowKeyBord = true;
                //????????????

            } else {
                if (!isShowKeyBord) {
                    return;
                }
                isShowKeyBord = false;
                //????????????

            }
        };
        binding.rlContainer.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        binding.rlContainer.setOnClickListener(v -> hideSoftInput());
        binding.photoList.setOnClickListener(v -> hideSoftInput());
        binding.photoList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hideSoftInput();
                }
                return false;
            }
        });
    }

    public void hideSoftInput() {
        View currentFocus = getCurrentFocus();
        if (currentFocus == null)
            return;
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private boolean checkParams() {
        if (getUploadFiles().isEmpty()) {
            ToastUtils.showShort("???????????????");
            return false;
        }
        if (TextUtils.isEmpty(binding.desText.getText().toString().trim())) {
            ToastUtils.showShort("???????????????");
            return false;
        }
        if (TextUtils.isEmpty(binding.contentInput.getText().toString().trim())) {
            ToastUtils.showShort("???????????????");
            return false;
        }
        return true;
    }

    private ListBottomPopup listBottomPopup;

    private void showListPop() {
        List<ListPopMenuBean> menus = new ArrayList<>();
        menus.add(new ListPopMenuBean("??????", getResources().getColor(R.color.color_252525), 16));
        menus.add(new ListPopMenuBean("??????", getResources().getColor(R.color.color_252525), 16));
        listBottomPopup = new ListBottomPopup(this, menus, new OnItemClickListener() {

            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (position == 0) {
                    Intent intent = new Intent(PublishTextPhotoActivity.this, TackPictureActivity.class);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else {
                    List<LocalMedia> localMedia = new ArrayList<>();
                    for (LocalPhotoBean localPhotoBean : localPhotoBeans) {
                        if (localPhotoBean.type == 1)
                            continue;
                        LocalMedia media = new LocalMedia();
                        media.setPath(localPhotoBean.getPath());
                        media.setRealPath(localPhotoBean.getPath());
                        localMedia.add(media);
                    }
                    PictureSelector.create(PublishTextPhotoActivity.this)
                            .openGallery(PictureMimeType.ofImage())
                            .loadImageEngine(GlideEngine.createGlideEngine()) // ?????????Demo GlideEngine.java
                            .maxSelectNum(MAX)// ????????????????????????
                            .isCamera(true)// ????????????????????????
                            .previewEggs(true)//??????????????? ????????????????????????????????????(???????????????????????????????????????????????????)
                            .previewImage(true)// ?????????????????????
                            .enableCrop(false)// ???????????? true or false
                            .compress(true)// ?????????????????? ????????????Luban??????
                            .isAndroidQTransform(false)//???????????? ??????????????????????????????????????????
                            .showCropFrame(false)// ?????????????????????????????? ???????????????????????????false   true or false
                            .showCropGrid(false)// ?????????????????????????????? ???????????????????????????false    true or false
                            .selectionData(localMedia)
                            .forResult(REQUEST_GALLERY);//????????????onActivityResult code
                }
                listBottomPopup.dismiss();
            }
        });
        listBottomPopup.showPopupWindow();

    }

    private void addPhoto(List<String> path) {
        if (path != null) {
            for (String s : path) {
                LocalPhotoBean bean = new LocalPhotoBean();
                bean.setPath(s);
                localPhotoBeans.add(bean);
            }
        }
        addAddPhoto();
    }

    private void addPhoto(String path) {
        LocalPhotoBean bean = new LocalPhotoBean();
        bean.setPath(path);
        localPhotoBeans.add(bean);
        addAddPhoto();
    }

    private void addAddPhoto() {
        if (localPhotoBeans.size() < MAX) {
            if (localPhotoBeans.isEmpty() || localPhotoBeans.get(localPhotoBeans.size() - 1).type == 0) {
                LocalPhotoBean bean = new LocalPhotoBean();
                bean.type = 1;
                localPhotoBeans.add(bean);
            }
        }
    }

    private void meagerPhoto(List<LocalMedia> localMedia) {
        localPhotoBeans.clear();
        for (LocalMedia media : localMedia) {
            LocalPhotoBean bean = new LocalPhotoBean();
            bean.setPath(media.getRealPath());
            localPhotoBeans.add(bean);
        }
        addAddPhoto();
        pictureAdapter.setList(localPhotoBeans);
    }

    private String currentTopicStr = "";

    private void showTopicView(String str) {
        //TODO ????????????
        currentTopicStr = str.replace("#", "");
        viewModel.getSearchTopic(str.replace("#", ""));
        binding.includeListTopic.recyclerView.getAdapter().notifyDataSetChanged();
        binding.includeListTopic.layoutPublishAt.setVisibility(View.VISIBLE);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.requestSuccess.observe(this, aBoolean -> dismissDialog());
        viewModel.liteAvUserList.observe(this, liteAvUserBean -> {
            for (LiteAvUserBean avUserBean : liteAvUserBean) {
                avUserBean.readOnly = true;
                if (!singleSearchAt) {
                    //????????????,??????ID,??????USerID,????????????
                    avUserBean.setId(avUserBean.getId());
                }
            }
            atPage++;
            this.liteAvUserBeans.addAll(liteAvUserBean);
            userAdapter.setList(this.liteAvUserBeans);
            if (liteAvUserBean.isEmpty() || liteAvUserBean.size() < atPageSize) {
                userAdapter.getLoadMoreModule().loadMoreEnd(false);
            } else {
                userAdapter.getLoadMoreModule().loadMoreComplete();
            }
        });
        viewModel.topicSearchList.observe(this, topicBeans -> {
            //????????????
            this.listTopicBeans.clear();
            this.listTopicBeans.addAll(topicBeans);
            if (this.listTopicBeans.isEmpty()) {
                this.listTopicBeans.add(new TopicBean(currentTopicStr));
            }
            chooseTopicAdapter.setList(this.listTopicBeans);
        });
        viewModel.uploadFile.observe(this, objects -> {
            uploadFileUrl.addAll(objects);
            LogUtils.e(TAG, "????????????");
            publish();
        });
        viewModel.publishLiveData.observe(this, s -> {
            ToastUtils.showShort(s);
            if (s.equals("????????????")) {
                if (publishDraftId > 0) {
                    //?????????????????????
                    String fileName = UserInfoMgr.getInstance().getUserInfo().getId();
                    viewModel.delDraftsById(getContext(), fileName, publishDraftId);
                }
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constant.PUBLISH_SUCCESS, true);
                startActivity(MainActivity.class, bundle);
                finish();
                overridePendingTransition(R.anim.activity_push_none, R.anim.activity_right_2exit);
            }
        });
    }

    /**
     * ??????
     */
    private void publish() {
        PublishBean bean = new PublishBean();
        bean.bannerImages = uploadFileUrl;
        if (locationBean != null) {
            bean.address = locationBean.desName;
            bean.latitude = locationBean.lat + "";
            bean.longitude = locationBean.lon + "";
            bean.city = locationBean.city;
            bean.province = locationBean.province;
            bean.district = locationBean.district;
        }
        bean.title = binding.desText.getText().toString();

        PublishSummaryBean summaryBean = new PublishSummaryBean();
        PublishDesBean desBean = binding.contentInput.getDesStr();
        summaryBean.content = desBean.content;
        summaryBean.publishFocusItemBeans = desBean.publishFocusItemBeans;
        for (VideoPublishEditBean editBean : binding.contentInput.getAtList()) {
            PublishAtBean atBean = new PublishAtBean();
            atBean.name = editBean.getContent().replace("@", "");
            atBean.uid = editBean.getId() + "";
            summaryBean.atList.add(atBean);
        }
        for (VideoPublishEditBean editBean : binding.contentInput.getTopicList()) {
            summaryBean.topicList.add(editBean.getContent().replace("#", ""));
        }
        bean.summary = summaryBean;

        viewModel.publish(bean);
    }

    private List<String> uploadFileUrl = new ArrayList<>();
    private boolean singleSearchAt = false;
    private String currentAt;

    private void showAtView(String str) {
        atPage = 1;
        this.liteAvUserBeans.clear();
        if (str.equals("@")) {
            singleSearchAt = true;
            viewModel.getMyAtList(atPage, atPageSize);
        } else {
            //??????
            singleSearchAt = false;
            currentAt = str.replace("@", "");
            viewModel.searchUserList(atPage, atPageSize, currentAt);
        }
        binding.includeListAt.layoutPublishAt.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        super.onBackPressed();
    }

    /**
     * ??????@???#????????????
     */
    private void initListView() {
        binding.includeListTopic.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.includeListAt.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userAdapter = new ChooseUserAdapter();
        userAdapter.setList(liteAvUserBeans);
        userAdapter.setOnItemClickListener((adapter, view, position) -> {
            String name = liteAvUserBeans.get(position).getName();
            binding.contentInput.setAtStr("@" + name, liteAvUserBeans.get(position).getId());
        });
        chooseTopicAdapter = new ChooseTopicAdapter(R.layout.item_publish_topic_view_adapter);
        chooseTopicAdapter.setList(listTopicBeans);
        chooseTopicAdapter.setOnItemClickListener((adapter, view, position) -> {
            String title = listTopicBeans.get(position).getName();
            binding.contentInput.setTopicStr("#" + title, 0);//TODO ?????? topic ID
        });
        binding.includeListAt.recyclerView.setAdapter(userAdapter);
        binding.includeListTopic.recyclerView.setAdapter(chooseTopicAdapter);

        userAdapter.getLoadMoreModule().setEnableLoadMore(true);
        userAdapter.getLoadMoreModule().setAutoLoadMore(true);
        userAdapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //???????????????????????????
                if (singleSearchAt) {
                    //????????????,????????????
                    viewModel.getMyAtList(atPage, atPageSize);
                } else {
                    //????????????,????????????
                    viewModel.searchUserList(atPage, atPageSize, currentAt);
                }
            }
        });
    }

    /**
     * ???????????? begin
     */
    private void checkPermission() {
        boolean check = PermissionUtils.checkPermission(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
        });
        if (check) {
            LocationService.startService(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.rlContainer.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
        eventDisposable.dispose();
    }

    /**
     * ??????????????????????????????????????????
     */
    private void getAddressList() {
        PoiSearch.Query query = new PoiSearch.Query(poiName, "", cityCode);
        query.setPageSize(20);// ?????????????????????????????????poiitem
        query.setPageNum(1);//??????????????????
        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(lat, lon), 1000));//??????????????????????????????????????????
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        locationBeans.clear();
        int index = 0;
        for (PoiItem item : poiResult.getPois()) {
            if (index == 6) {
                break;
            }
            LocationBean bean = new LocationBean();
            bean.address = item.getSnippet();
            bean.desName = item.getTitle();
            bean.distance = item.getDistance() + "";
            if (item.getLatLonPoint() != null) {
                bean.lat = item.getLatLonPoint().getLatitude();
                bean.lon = item.getLatLonPoint().getLongitude();
            }
            bean.province = item.getProvinceName();
            bean.city = item.getCityName();
            bean.district = item.getAdName();
            locationBeans.add(bean);
            index++;
        }
        //??????????????????
        LocationBean bean = new LocationBean();
        bean.isLookMore = true;
        bean.desName = "????????????";
        bean.address = "????????????";
        locationBeans.add(bean);
        publishLocationAdapter.setList(locationBeans);
        if (this.locationBean != null)
            equalsLocation(locationBean);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean check = PermissionUtils.checkPermissionAllGranted(this, permissions);
        if (check) {
            LocationService.startService(this);
        } else {
            ToastUtils.showShort("??????????????????");
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == location_code) {
                LocationBean bean = (LocationBean) data.getSerializableExtra("bean");
                this.locationBean = bean;
                binding.includePublish.myLocation.setText(bean.desName);
                equalsLocation(bean);
            } else if (requestCode == REQUEST_GALLERY) {
                List<LocalMedia> result = PictureSelector.obtainMultipleResult(data);
                meagerPhoto(result);
            } else if (requestCode == REQUEST_CAMERA) {
                removeLast();
                /*List<LocalMedia> result = PictureSelector.obtainMultipleResult(data);
                String path = result.get(0).getPath();*/
                ArrayList<String> stringArrayListExtra = data.getStringArrayListExtra(com.xaqinren.healthyelders.modulePicture.Constant.PHOTO_PATH);
                String path = stringArrayListExtra.get(0);
                addLast(path);
            } else if (requestCode == AT_CODE) {
                if (data != null) {
                    LiteAvUserBean bean = (LiteAvUserBean) data.getSerializableExtra(com.xaqinren.healthyelders.modulePicture.Constant.PUBLISH_AT);
                    binding.contentInput.setAtStr("@" + bean.getName(), bean.getId());
                } else {
                    //??????at??????
                    binding.contentInput.delLastChar();
                }
                binding.contentInput.setEnablePost(true);
            }
        } else {
            if (requestCode == AT_CODE) {
                //??????at??????
                binding.contentInput.delLastChar();
                binding.contentInput.setEnablePost(true);
            }
        }
    }

    private void removeLast() {
        if (localPhotoBeans.get(localPhotoBeans.size() - 1).type == 1) {
            localPhotoBeans.remove(localPhotoBeans.get(localPhotoBeans.size() - 1));
        }
    }

    private void addLast(String path) {
        addPhoto(path);
        pictureAdapter.setList(localPhotoBeans);
    }

    /**
     * ????????????location????????????????????????
     *
     * @param bean
     */
    private void equalsLocation(LocationBean bean) {
        if (locationBeans.isEmpty())
            return;
        for (LocationBean locationBean : locationBeans) {
            if (locationBean.desName.equals(bean.desName)) {
                //????????????
                locationBean.isSelLocation = true;
                this.locationBean = locationBean;
            } else {
                locationBean.isSelLocation = false;
            }
        }
        publishLocationAdapter.notifyDataSetChanged();
    }

    /**
     * TODO ????????????????????????
     */
    private void getDraftContent() {
        String fileName = UserInfoMgr.getInstance().getUserInfo().getId();
        SaveDraftBean bean = viewModel.getDraftsById(this, fileName, publishDraftId);
        if (bean == null) {
            return;
        }
        //??????
        PublishDesBean publishDesBean = new PublishDesBean();
        publishDesBean.content = bean.getContent();
        publishDesBean.publishFocusItemBeans = bean.getPublishFocusItemBeans();
        binding.contentInput.initDesStr(publishDesBean);
        binding.desText.setText(bean.getTitle());
        //??????
        lon = bean.getLon();
        lat = bean.getLat();
        locationBean = new LocationBean();
        locationBean.desName = bean.getAddress();
        locationBean.lon = lon;
        locationBean.lat = lat;
        binding.includePublish.myLocation.setText(bean.getAddress());
        //??????
        binding.contentInput.setText(bean.getBodyStr());
        //??????
        addPhoto(bean.getFilePaths());

    }

    /**
     * TODO ?????????????????????
     */
    private void createDraftContent() {
        //??????list ??? ??????  ?????????  ?????????  ???????????????
        String fileName = UserInfoMgr.getInstance().getUserInfo().getId();

        PublishDesBean publishDesBean = binding.contentInput.getDesStr();
        SaveDraftBean saveDraftBean;
        if (publishDraftId > 0) {
            saveDraftBean = viewModel.getDraftsById(this, fileName, publishDraftId);
        } else {
            saveDraftBean = new SaveDraftBean();
            saveDraftBean.setId(System.currentTimeMillis() / 1000);
        }

        saveDraftBean.setTitle(binding.desText.getText().toString().trim());
        saveDraftBean.setContent(publishDesBean.content);
        saveDraftBean.setPublishFocusItemBeans(publishDesBean.publishFocusItemBeans);

        saveDraftBean.setFilePaths(getUploadFiles());
        saveDraftBean.setBodyStr(binding.contentInput.getText().toString());


        if (locationBean != null) {
            saveDraftBean.setAddress(locationBean.desName);
            saveDraftBean.setLon(lon);
            saveDraftBean.setLat(lat);
            saveDraftBean.setProvince(locationBean.province);
            saveDraftBean.setCity(locationBean.city);
            saveDraftBean.setDistrict(locationBean.district);
        }
        List<String> files = getUploadFiles();
        if (!files.isEmpty()) {
            saveDraftBean.setCoverPath(files.get(0));
        }
        saveDraftBean.setComment(false);
        saveDraftBean.setType(1);
        saveDraftBean.setSaveTime(System.currentTimeMillis());

        viewModel.saveDraftsById(this, fileName, saveDraftBean);

        LogUtils.e(TAG, "?????????????????????ID -> " + saveDraftBean.getId());
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constant.PUBLISH_SUCCESS, true);
        startActivity(MainActivity.class, bundle);
        finish();
        overridePendingTransition(R.anim.activity_push_none, R.anim.activity_right_2exit);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void finish() {
        super.finish();
    }

    private List<String> getUploadFiles() {
        List<String> files = new ArrayList<>();
        for (LocalPhotoBean bean : localPhotoBeans) {
            if (bean.type == 0)
                files.add(bean.getPath());
        }
        return files;
    }

    List<String> lubanList = new ArrayList<>();

    private void uploadFile() {

        if (isUploadFile)
            return;
        isUploadFile = true;
        List<String> files = getUploadFiles();
        upLoadFileCount = files.size();
        ImageUtils.compressWithRx(files, new Observer() {
            @Override
            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                lubanList.clear();
            }

            @Override
            public void onNext(@io.reactivex.annotations.NonNull Object o) {
                lubanList.add(o.toString());
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                dismissDialog();
            }

            @Override
            public void onComplete() {
                if (lubanList != null) {
                    LogUtils.e(TAG, "????????????->" + lubanList.size());
                }
                viewModel.uploadFile(lubanList);
            }
        });


    }

}
