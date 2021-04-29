package com.xaqinren.healthyelders.moduleLiteav.activity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.xaqinren.healthyelders.BR;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.databinding.ActivityLiteAvLocationBinding;
import com.xaqinren.healthyelders.moduleLiteav.adapter.ChooseLocationAdapter;
import com.xaqinren.healthyelders.moduleLiteav.bean.SelLocationBean;
import com.xaqinren.healthyelders.moduleLiteav.viewModel.ChooseLocationViewModel;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseActivity;

/**
 * 选择地址
 */
public class ChooseLocationActivity extends BaseActivity<ActivityLiteAvLocationBinding, ChooseLocationViewModel> implements AMapLocationListener {
    private ChooseLocationAdapter adapter;
    private List<SelLocationBean> selLocationBeans = new ArrayList<>();

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
        testData();
        initView();
        initLocation();
    }

    private void initView() {
        adapter = new ChooseLocationAdapter(R.layout.item_lite_av_location);
        binding.locationList.setLayoutManager(new LinearLayoutManager(this));
        binding.locationList.setAdapter(adapter);
        adapter.addData(selLocationBeans);
    }
    private void testData() {
        selLocationBeans.add(new SelLocationBean("西安市","中国陕西省西安市","666m"));
        selLocationBeans.add(new SelLocationBean("西安市","中国陕西省西安市","666m"));
        selLocationBeans.add(new SelLocationBean("西安市","中国陕西省西安市","666m"));
        selLocationBeans.add(new SelLocationBean("西安市","中国陕西省西安市","666m"));
        selLocationBeans.add(new SelLocationBean("西安市","中国陕西省西安市","666m"));
    }
    //声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    private void initLocation() {
        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        mLocationOption.setNeedAddress(true);
        mLocationOption.setGpsFirst(true);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
    }

    private void getAddressList() {

    }

    private void searchAddress() {

    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                String address = amapLocation.getAddress();
                String aoiName = amapLocation.getAoiName();
                String description = amapLocation.getDescription();
                String poiName = amapLocation.getPoiName();
                LogUtils.e("Location",
                        "address ->\t" + address + "\t" +
                                "aoiName ->\t" + aoiName + "\t" +
                                "description ->\t" + description + "\t" +
                                "poiName ->\t" + poiName + "\t"
                );

            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                LogUtils.e("Location","location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
        LogUtils.e("Location",
                "errCode ->\t" + amapLocation.getErrorCode() + "\t" +
                        "errInfo ->\t" + amapLocation.getErrorInfo() + "\t" );
    }
}
