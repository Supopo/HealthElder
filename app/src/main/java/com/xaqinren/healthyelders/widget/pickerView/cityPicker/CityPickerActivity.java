package com.xaqinren.healthyelders.widget.pickerView.cityPicker;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.amap.api.location.AMapLocation;
import com.xaqinren.healthyelders.R;
import com.xaqinren.healthyelders.widget.pickerView.cityPicker.adapter.AreaGridAdapter;
import com.xaqinren.healthyelders.widget.pickerView.cityPicker.adapter.CityListAdapter;
import com.xaqinren.healthyelders.widget.pickerView.cityPicker.adapter.ResultListAdapter;
import com.xaqinren.healthyelders.widget.pickerView.cityPicker.db.DBManager;
import com.xaqinren.healthyelders.widget.pickerView.cityPicker.model.City;
import com.xaqinren.healthyelders.widget.pickerView.cityPicker.model.LocateState;
import com.xaqinren.healthyelders.widget.pickerView.cityPicker.view.SideLetterBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 *
 */
public class CityPickerActivity extends AppCompatActivity implements View.OnClickListener, MapLocationHelper.LocationCallBack {
    public static final String KEY_PICKED_CITY = "picked_city";
    public static final String SHOW_AREA = "show_area"; // 0 是隐藏 1 是显示

    private String location;//需要
    private boolean locateState;//需要

    private ListView mListView;
    private ListView mResultListView;
    private SideLetterBar mLetterBar;
    private EditText searchBox;
    private ImageView clearBtn;
    private ImageView backBtn;
    private ViewGroup emptyView;

    private CityListAdapter mCityAdapter;
    private ResultListAdapter mResultAdapter;
    private List<City> mAllCities;
    private DBManager dbManager;

    private int showArea = 1;
    private MapLocationHelper mHelper;


    @Override
    protected void onStart() {
        super.onStart();
        mHelper = new MapLocationHelper(this, this);
        mHelper.startMapLocation();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cp_activity_city_list);

        String area = getIntent().getStringExtra("area");
        String city = getIntent().getStringExtra("city");
        showArea = getIntent().getIntExtra(SHOW_AREA, 1);



        initData();
        initView();

        if (city != null) {
            mCityAdapter.updateLocateState(LocateState.SUCCESS, area);
        } else {
            mCityAdapter.updateLocateState(LocateState.FAILED, area);

        }


    }

    private void initLocation() {
        if (locateState) {
            mCityAdapter.updateLocateState(LocateState.SUCCESS, location);
        } else {
            //定位失败
            mCityAdapter.updateLocateState(LocateState.FAILED, null);
        }
    }


    private void initData() {
        dbManager = new DBManager(this);
        dbManager.copyDBFile();
        mAllCities = dbManager.getAllCities();
        mCityAdapter = new CityListAdapter(this, mAllCities);
        // 城市点击事件
        mCityAdapter.setOnCityClickListener(new CityListAdapter.OnCityClickListener() {
            @Override
            public void onCityClick(String name) {
                if (showArea == 0) {
                    back(name);
                    return;
                }

                if (name.equals("澳门") || name.equals("台湾") || name.equals("香港")) {
                    back(name);
                    return;
                }
            }

            @Override
            public void onLocateClick() {
                //点击定位
                mCityAdapter.updateLocateState(LocateState.LOCATING, null);
                mHelper.startMapLocation();

            }
        });

        mResultAdapter = new ResultListAdapter(this, null);
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.listview_all_city);
        mListView.setAdapter(mCityAdapter);

        TextView overlay = (TextView) findViewById(R.id.tv_letter_overlay);
        mLetterBar = (SideLetterBar) findViewById(R.id.side_letter_bar);
        mLetterBar.setOverlay(overlay);
        mLetterBar.setOnLetterChangedListener(letter -> {
            int position = mCityAdapter.getLetterPosition(letter);
            mListView.setSelection(position);
        });

        searchBox = (EditText) findViewById(R.id.et_search);
        backBtn = findViewById(R.id.iv_left);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString();
                if (TextUtils.isEmpty(keyword)) {
                    clearBtn.setVisibility(View.GONE);
                    emptyView.setVisibility(View.GONE);
                    mResultListView.setVisibility(View.GONE);
                } else {
                    clearBtn.setVisibility(View.VISIBLE);
                    mResultListView.setVisibility(View.VISIBLE);
                    List<City> result = dbManager.searchCity(keyword);
                    if (result == null || result.size() == 0) {
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                        mResultAdapter.changeData(result);
                    }
                }
            }
        });

        emptyView = (ViewGroup) findViewById(R.id.empty_view);
        mResultListView = (ListView) findViewById(R.id.listview_search_result);
        mResultListView.setAdapter(mResultAdapter);
        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                back(mResultAdapter.getItem(position).getName());
            }
        });

        clearBtn = (ImageView) findViewById(R.id.iv_search_clear);
//        backBtn = (ImageView) findViewById(R.id.back);

        clearBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

    }

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

    private void back(String city) {
        Intent data = new Intent();
        data.putExtra(KEY_PICKED_CITY, city);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_search_clear) {
            searchBox.setText("");
            clearBtn.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
            mResultListView.setVisibility(View.GONE);
        }
        if (i == R.id.iv_left) {
            finish();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        mHelper.stopMapLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHelper.destroyMapLocation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCallLocationSuc(AMapLocation loc) {
        if (loc != null) {
            String city = loc.getCity();
            if (city.contains("市")) {
                city = city.replace("市", "");
            }
            locateState = true;
            location = city;
            initLocation();
        }
    }
}
