package com.xaqinren.healthyelders.moduleLiteav.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.nostra13.dcloudimageloader.utils.L;
import com.xaqinren.healthyelders.bean.EventBean;
import com.xaqinren.healthyelders.global.CodeTable;
import com.xaqinren.healthyelders.moduleLiteav.bean.LocationBean;
import com.xaqinren.healthyelders.utils.LogUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import me.goldze.mvvmhabit.bus.RxBus;

public class LocationService extends Service implements AMapLocationListener {
    //声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    public static void startService(Context context) {
        Intent intent = new Intent(context, LocationService.class);
        context.startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //执行一次定位请求
        mlocationClient.startLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initLocation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mlocationClient != null) {
            mlocationClient.onDestroy();
        }
    }

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
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                double lat = amapLocation.getLatitude();//获取纬度
                double lon = amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                String address = amapLocation.getAddress();
                String aoiName = amapLocation.getAoiName();
                String description = amapLocation.getDescription();
                String cityCode = amapLocation.getCityCode();
                String cityName = amapLocation.getCity();
                String poiName = amapLocation.getPoiName();
                LocationBean locationBean = new LocationBean();
                locationBean.lon = lon;
                locationBean.lat = lat;
                locationBean.address = address;
                locationBean.desName = poiName;
                locationBean.cityCode = cityCode;
                locationBean.cityName = cityName;
                //发送定位成功请求
                RxBus.getDefault().post(new EventBean(CodeTable.LOCATION_SUCCESS, locationBean));
            } else {
                RxBus.getDefault().post(new EventBean(CodeTable.LOCATION_ERROR, amapLocation.getErrorCode()));
            }
        }
    }
}
