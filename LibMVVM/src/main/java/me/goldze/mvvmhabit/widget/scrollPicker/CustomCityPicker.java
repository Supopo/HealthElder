package me.goldze.mvvmhabit.widget.scrollPicker;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.R;

/**
 * 城市选择器
 * <p>
 * mCityPicker = new CustomCityPicker(this, new CustomCityPicker.ResultHandler() {
 * public void handle(String result) {
 * //result为选择的地址
 * }
 * });
 * mCityPicker.initJson();
 * ...
 * mCityPicker.show();
 */

public class CustomCityPicker {

    /**
     * 定义结果回调接口
     */
    public interface ResultHandler {
        void handle(String result);
    }

    private ResultHandler handler;
    private Context context;
    private boolean canAccess = false;

    private Dialog cityPickerDialog;
    private ScrollPickerView province_pv, city_pv, area_pv;

    private TextView tv_cancle, tv_select;
    private String mProvince, mCity, mArea;
    private List<Province> provinceList = new ArrayList<>();
    private List<String> provinces = new ArrayList<>();
    private List<List<String>> citys = new ArrayList<List<String>>();
    private List<List<List<String>>> countys = new ArrayList<List<List<String>>>();


    public CustomCityPicker(Context context, ResultHandler resultHandler) {
        this.context = context;
        this.handler = resultHandler;
        canAccess = true;
        initDialog();
        initView();
    }

    private void initDialog() {
        if (cityPickerDialog == null) {
            cityPickerDialog = new Dialog(context, R.style.date_picker);
            cityPickerDialog.setCancelable(false);
            cityPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            cityPickerDialog.setContentView(R.layout.picker_custom_city);
            Window window = cityPickerDialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(dm);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = dm.widthPixels;
            window.setAttributes(lp);
        }
    }

    private void initView() {
        province_pv = (ScrollPickerView) cityPickerDialog.findViewById(R.id.province_pv);
        city_pv = (ScrollPickerView) cityPickerDialog.findViewById(R.id.city_pv);
        area_pv = (ScrollPickerView) cityPickerDialog.findViewById(R.id.area_pv);

        tv_cancle = (TextView) cityPickerDialog.findViewById(R.id.tv_cancle);
        tv_select = (TextView) cityPickerDialog.findViewById(R.id.tv_select);


        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cityPickerDialog.dismiss();
            }
        });

        tv_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mProvince.equals(mCity)) {
                    handler.handle(mCity + " " + mArea);
                } else {
                    handler.handle(mProvince + " " + mCity + " " + mArea);
                }
                cityPickerDialog.dismiss();
            }
        });
    }

    public void show(String area) {
        if (canAccess) {
            canAccess = true;
            loadComponent();
            addListener();
            setSelectedArea(area);
            cityPickerDialog.show();
        } else {
            canAccess = false;
        }
    }

    public void show() {
        if (canAccess) {
            canAccess = true;
            loadComponent();
            addListener();
            cityPickerDialog.show();
        } else {
            canAccess = false;
        }
    }

    public void initJson() {
        initJson(0);
    }

    //int type :  0 正常模式 ;1 第3级多一个全市区 ;2 只显示陕西省市区
    //提前加载数据，这样不需要花费过长时间
    public void initJson(int type) {
        initArrayList();
        String json = getJson(context, "city.json");
        provinceList.addAll(com.alibaba.fastjson.JSON.parseArray(json, Province.class));
        int provinceSize = provinceList.size();
        //添加省
        for (int x = 0; x < provinceSize; x++) {
            Province pro;
            pro = provinceList.get(x);
            if (type == 0) {
                provinces.add(pro.getAreaName());
                List<City> cities = pro.getCities();
                List<String> xCities = new ArrayList<String>();
                List<List<String>> xCounties = new ArrayList<List<String>>();
                int citySize = cities.size();
                //添加地市
                for (int y = 0; y < citySize; y++) {
                    City cit = cities.get(y);
                    xCities.add(cit.getAreaName());
                    List<County> counties = cit.getCounties();
                    List<String> yCounties = new ArrayList<String>();
                    int countySize = counties.size();
                    //添加区县
                    if (countySize == 0) {
                        yCounties.add(cit.getAreaName());
                    } else {
                        if (type == 2)
                            yCounties.add("全市区");
                        for (int z = 0; z < countySize; z++) {
                            yCounties.add(counties.get(z).getAreaName());
                        }
                    }
                    xCounties.add(yCounties);
                }
                citys.add(xCities);
                countys.add(xCounties);
            }
            if (type == 1) {
                provinces.add(pro.getAreaName());
                List<City> cities = pro.getCities();
                List<String> xCities = new ArrayList<String>();
                List<List<String>> xCounties = new ArrayList<List<String>>();
                int citySize = cities.size();
                //添加地市
                for (int y = 0; y < citySize; y++) {
                    City cit = cities.get(y);
                    xCities.add(cit.getAreaName());
                    List<County> counties = cit.getCounties();
                    List<String> yCounties = new ArrayList<String>();
                    int countySize = counties.size();
                    //添加区县
                    if (countySize == 0) {
                        yCounties.add(cit.getAreaName());
                    } else {
                        yCounties.add("全市区");
                        for (int z = 0; z < countySize; z++) {
                            yCounties.add(counties.get(z).getAreaName());
                        }
                    }
                    xCounties.add(yCounties);
                }
                citys.add(xCities);
                countys.add(xCounties);
            }
            if (type == 2) {
                if (pro.getAreaName().equals("陕西省")) {
                    provinces.add(pro.getAreaName());
                    List<City> cities = pro.getCities();
                    List<String> xCities = new ArrayList<String>();
                    List<List<String>> xCounties = new ArrayList<List<String>>();
                    int citySize = cities.size();
                    //添加地市
                    for (int y = 0; y < citySize; y++) {
                        City cit = cities.get(y);
                        xCities.add(cit.getAreaName());
                        List<County> counties = cit.getCounties();
                        List<String> yCounties = new ArrayList<String>();
                        int countySize = counties.size();
                        //添加区县
                        if (countySize == 0) {
                            yCounties.add(cit.getAreaName());
                        } else {
                            for (int z = 0; z < countySize; z++) {
                                yCounties.add(counties.get(z).getAreaName());
                            }
                        }
                        xCounties.add(yCounties);
                    }
                    citys.add(xCities);
                    countys.add(xCounties);
                }
            }
        }
    }

    /**
     * The type Area.
     */
    public abstract static class Area {
        /**
         * The Area id.
         */
        String areaId;
        /**
         * The Area name.
         */
        String areaName;

        /**
         * Gets area id.
         *
         * @return the area id
         */
        public String getAreaId() {
            return areaId;
        }

        /**
         * Sets area id.
         *
         * @param areaId the area id
         */
        public void setAreaId(String areaId) {
            this.areaId = areaId;
        }

        /**
         * Gets area name.
         *
         * @return the area name
         */
        public String getAreaName() {
            return areaName;
        }

        /**
         * Sets area name.
         *
         * @param areaName the area name
         */
        public void setAreaName(String areaName) {
            this.areaName = areaName;
        }

        @Override
        public String toString() {
            return "areaId=" + areaId + ",areaName=" + areaName;
        }

    }

    /**
     * The type Province.
     */
    public static class Province extends Area {
        /**
         * The Cities.
         */
        List<City> cities = new ArrayList<City>();

        /**
         * Gets cities.
         *
         * @return the cities
         */
        public List<City> getCities() {
            return cities;
        }

        /**
         * Sets cities.
         *
         * @param cities the cities
         */
        public void setCities(List<City> cities) {
            this.cities = cities;
        }

    }

    /**
     * The type City.
     */
    public static class City extends Area {
        private List<County> counties = new ArrayList<County>();

        /**
         * Gets counties.
         *
         * @return the counties
         */
        public List<County> getCounties() {
            return counties;
        }

        /**
         * Sets counties.
         *
         * @param counties the counties
         */
        public void setCounties(ArrayList<County> counties) {
            this.counties = counties;
        }

    }

    /**
     * The type County.
     */
    public static class County extends Area {
    }


    //获取assets文件夹里面的城市JSON
    public static String getJson(Context mContext, String fileName) {

        StringBuilder sb = new StringBuilder();
        AssetManager am = mContext.getAssets();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    am.open(fileName)));
            String next = "";
            while (null != (next = br.readLine())) {
                sb.append(next);
            }
        } catch (IOException e) {
            e.printStackTrace();
            sb.delete(0, sb.length());
        }
        return sb.toString().trim();
    }

    private void initArrayList() {
        provinceList.clear();
        provinces.clear();
        citys.clear();
        countys.clear();

    }

    private void loadComponent() {
        province_pv.setData(provinces);
        city_pv.setData(citys.get(0));
        area_pv.setData(countys.get(0).get(0));
        mProvince = provinces.get(0);
        mCity = citys.get(0).get(0);
        mArea = countys.get(0).get(0).get(0);

        province_pv.setSelected(0);
        city_pv.setSelected(0);
        area_pv.setSelected(0);

        executeScroll();
    }

    private int provinceSelect = 0;
    private int citySelect = 0;

    private void addListener() {
        province_pv.setOnSelectListener(new ScrollPickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                for (int i = 0; i < provinces.size(); i++) {
                    if (provinces.get(i).contains(text)) {
                        provinceSelect = i;
                        break;
                    }
                }
                mProvince = text;
                changeCity(provinceSelect);
            }
        });

        city_pv.setOnSelectListener(new ScrollPickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                for (int i = 0; i < citys.get(provinceSelect).size(); i++) {
                    if (citys.get(provinceSelect).get(i).contains(text)) {
                        citySelect = i;
                        break;
                    }
                }
                mCity = text;
                changeArea(provinceSelect, citySelect);
            }
        });

        area_pv.setOnSelectListener(new ScrollPickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                mArea = text;
            }
        });
    }

    private void changeCity(final int temp) {
        city_pv.setData(citys.get(temp));
        city_pv.setSelected(0);
        mCity = citys.get(temp).get(0);
        executeAnimator(city_pv);
        city_pv.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeArea(temp, 0);
            }
        }, 100);
    }

    private void changeArea(int temp, int temp2) {
        area_pv.setData(countys.get(temp).get(temp2));
        area_pv.setSelected(0);
        mArea = countys.get(temp).get(temp2).get(0);

        executeAnimator(area_pv);
    }

    private void executeAnimator(View view) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 1f, 0f, 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.3f, 1f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.3f, 1f);
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ).setDuration(200).start();
    }

    private void executeScroll() {
        province_pv.setCanScroll(provinces.size() > 1);
        city_pv.setCanScroll(citys.size() > 1);
        area_pv.setCanScroll(countys.size() > 1);
    }

    /**
     * 设置默认选中的地址
     */
    public void setSelectedArea(String area) {
    }
}