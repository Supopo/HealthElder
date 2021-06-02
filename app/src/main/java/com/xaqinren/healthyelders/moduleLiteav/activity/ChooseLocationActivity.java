package com.xaqinren.healthyelders.moduleLiteav.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.databinding.ActivityLiteAvLocationBinding;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleLiteav.adapter.ChooseLocationAdapter;
import com.xaqinren.healthyelders.moduleLiteav.bean.LocationBean;
import com.xaqinren.healthyelders.moduleLiteav.service.LocationService;
import com.xaqinren.healthyelders.moduleLiteav.viewModel.ChooseLocationViewModel;
import com.xaqinren.healthyelders.utils.LogUtils;
import com.xaqinren.healthyelders.widget.pickerView.cityPicker.CityPickerActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.bus.RxBus;
import me.goldze.mvvmhabit.bus.RxSubscriptions;
import me.goldze.mvvmhabit.utils.PermissionUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * 选择地址
 */
public class ChooseLocationActivity extends BaseActivity<ActivityLiteAvLocationBinding, ChooseLocationViewModel>
        implements PoiSearch.OnPoiSearchListener, Inputtips.InputtipsListener, GeocodeSearch.OnGeocodeSearchListener {
    private ChooseLocationAdapter adapter;
    private List<LocationBean> selLocationBeans = new ArrayList<>();
    private List<LocationBean> selLocationTipBeans = new ArrayList<>();
    /**
     * 用户当前定位
     */
    private double lat, lon;
    private String poiName;
    private String cityCode;
    private String cityName;
    private Inputtips inputTips ;
    private Disposable eventDisposable;
    private PoiSearch poiSearch;
    private int locationPageIndex = 1;
    private int locationType = 0;//0 poi 1 str
    private String currentSearch = "";
    private GeocodeSearch geocoderSearch;
    private String clickDesName;
    private int CITY_CODE = 123;
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_lite_av_location;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        setTitle("添加位置");
        rlTitle.setVisibility(View.GONE);
        binding.cancel.setOnClickListener(view -> finish());
        eventDisposable = RxBus.getDefault().toObservable(EventBean.class).subscribe(o -> {
            if (o.msgId == CodeTable.LOCATION_SUCCESS) {
                //定位成功
                LocationBean locationBean = (LocationBean) o.data;
                lat = locationBean.lat;
                lon = locationBean.lon;
                cityCode = locationBean.cityCode;
                cityName = locationBean.cityName;
                poiName = locationBean.desName;
                getAddressList();
                binding.cityName.setText(cityName);
            }
        });
        RxSubscriptions.add(eventDisposable);
        initView();
        checkPermission();
    }

    private void initView() {
        adapter = new ChooseLocationAdapter(R.layout.item_lite_av_location);
        binding.locationList.setLayoutManager(new LinearLayoutManager(this));
        binding.locationList.setAdapter(adapter);
        adapter.setList(selLocationBeans);
        binding.searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    if (locationType == 0){
                        locationType = 1;
                    }
                    if (!editable.equals(currentSearch)) {
                        locationPageIndex = 1;
                    }
                    searchAddress(editable.toString());
                }else{
                    if (locationType == 1){
                        locationType = 0;
                        locationPageIndex = 1;
                    }
                    adapter.setList(selLocationBeans);
                }
            }
        });
        adapter.setOnItemClickListener((adapter1, view, position) -> {
            LocationBean bean = (LocationBean) adapter1.getData().get(position);
            clickDesName = bean.desName;
            getGeocodeSearch(new LatLonPoint(bean.lat, bean.lon));
        });
        adapter.getLoadMoreModule().setEnableLoadMore(true);
        adapter.getLoadMoreModule().setAutoLoadMore(true);
        adapter.getLoadMoreModule().setOnLoadMoreListener(() -> {
            if (binding.searchView.getText().length() > 0) {
                searchAddress(binding.searchView.getText().toString());
            }else {
                getAddressList();
            }
        });
        binding.cityLayout.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra("show_area", 0);
            intent.putExtra("city", cityName);
            intent.setClass(this, CityPickerActivity.class);
            startActivityForResult(intent,CITY_CODE);
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        eventDisposable.dispose();
    }

    @Override
    protected void onDestroy() {
        poiSearch.setOnPoiSearchListener(null);
        poiSearch = null;
        inputTips = null;
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    /**
     * 检查并申请权限
     */
    public void checkPermission() {
        boolean check = PermissionUtils.checkPermission(this,  new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION ,
        });
        if (check) {
            showDialog();
            LocationService.startService(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean check = PermissionUtils.checkPermissionAllGranted(this, permissions);
        if (check) {
            LocationService.startService(this);
        }else{
            ToastUtils.showShort("定位权限缺失");
        }
    }


    /**
     * 定位成功后，获取周边地址列表
     */
    private void getAddressList() {
        PoiSearch.Query query = new PoiSearch.Query(poiName, "", cityCode);
        query.setPageSize(15);// 设置每页最多返回多少条poiitem
        query.setPageNum(locationPageIndex);//设置查询页码
        poiSearch = new PoiSearch(this,query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(lat, lon), 500));//设置周边搜索的中心点以及半径
        poiSearch.searchPOIAsyn();
    }

    private void searchAddress(String keyWord) {
        InputtipsQuery inputquery = new InputtipsQuery(keyWord, cityName);
        if (inputTips == null) {
            inputTips = new Inputtips(this, inputquery);
            inputTips.setInputtipsListener(this);
        }
        else
            inputTips.setQuery(inputquery);
        inputTips.requestInputtipsAsyn();
    }

    private void getGeocodeSearch(LatLonPoint latLonPoint) {
        if (geocoderSearch == null) {
            geocoderSearch = new GeocodeSearch(this);
            geocoderSearch.setOnGeocodeSearchListener(this);
        }
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        LogUtils.e("Location", JSON.toJSONString(poiResult));
        dismissDialog();
        if (locationPageIndex == 1) selLocationBeans.clear();
        for (PoiItem item : poiResult.getPois()) {
            LocationBean bean = new LocationBean();
            bean.address = item.getSnippet();
            bean.desName = item.getTitle();
            bean.distance = item.getDistance() + "";
            bean.lat = item.getLatLonPoint().getLatitude();
            bean.lon = item.getLatLonPoint().getLongitude();
            bean.province = item.getProvinceName();
            bean.city = item.getCityName();
            bean.district = item.getAdName();
            selLocationBeans.add(bean);
        }
        locationPageIndex++;
        adapter.setList(selLocationBeans);
        if (poiResult.getPois().isEmpty()) {
            adapter.getLoadMoreModule().loadMoreEnd();
        }else{
            adapter.getLoadMoreModule().loadMoreComplete();
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void onGetInputtips(List<Tip> list, int i) {
        LogUtils.e("Location", JSON.toJSONString(list));
        LatLng latLng1 = new LatLng(lat,lon);
        if (locationPageIndex == 1) {
            selLocationTipBeans.clear();
        }
        for (Tip item : list) {
            LocationBean bean = new LocationBean();
            bean.desName = item.getName();
            bean.address = item.getAddress();
            if (item.getPoint()!=null) {
                bean.lat = item.getPoint().getLatitude();
                bean.lon = item.getPoint().getLongitude();
            }
            item.getDistrict();
            if (bean.lat != 0 && bean.lon != 0) {
                LatLng latLng2 = new LatLng(bean.lat,bean.lon);
                int distance = (int) AMapUtils.calculateLineDistance(latLng1,latLng2);
                bean.distance = distance+"";
            }
            selLocationTipBeans.add(bean);
        }
        locationPageIndex++;
        adapter.setList(selLocationTipBeans);
        adapter.getLoadMoreModule().loadMoreEnd();
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        //解析result获取地址描述信息
        LocationBean bean = new LocationBean();
        bean.lat = regeocodeResult.getRegeocodeQuery().getPoint().getLatitude();
        bean.lon = regeocodeResult.getRegeocodeQuery().getPoint().getLongitude();
        bean.province = regeocodeResult.getRegeocodeAddress().getProvince();
        bean.city = regeocodeResult.getRegeocodeAddress().getCity();
        bean.district = regeocodeResult.getRegeocodeAddress().getDistrict();
        bean.desName = clickDesName;
        bean.addressInfo = regeocodeResult.getRegeocodeAddress().getFormatAddress();
        //选择返回
        Intent intent = new Intent();
        intent.putExtra("bean", bean);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CITY_CODE) {
                if (data != null) {
                    cityName = data.getStringExtra(CityPickerActivity.KEY_PICKED_CITY);
                    binding.cityName.setText(cityName);
                    binding.searchView.setText(null);
                }
            }
        }
    }
}
