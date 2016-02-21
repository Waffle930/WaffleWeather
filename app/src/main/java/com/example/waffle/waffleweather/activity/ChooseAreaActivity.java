package com.example.waffle.waffleweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.waffle.waffleweather.R;
import com.example.waffle.waffleweather.db.CityDB;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Waffle on 2016/2/21.
 */
public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private CityDB cityDB;
    private List<String> dataList = new ArrayList<String>();
    private String selectedProvince;
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        cityDB = CityDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = dataList.get(position);
                        queryCities();
                }
            }
        });
        queryProvinces();
    }

    private void queryProvinces(){
        dataList.clear();
        dataList.addAll(cityDB.loadProvinces());
        adapter.notifyDataSetChanged();
        listView.setSelection(0);
        titleText.setText("中国");
        currentLevel = LEVEL_PROVINCE;
    }

    private void queryCities(){
        dataList.clear();
        dataList.addAll(cityDB.loadCities(selectedProvince).keySet());
        adapter.notifyDataSetChanged();
        listView.setSelection(0);
        titleText.setText(selectedProvince);
        currentLevel = LEVEL_CITY;
    }

    @Override
    public void onBackPressed() {
        if(currentLevel == LEVEL_CITY){
            queryProvinces();
        }else if(currentLevel == LEVEL_PROVINCE){
            finish();
        }
    }
}
