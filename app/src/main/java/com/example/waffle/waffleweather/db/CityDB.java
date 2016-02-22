package com.example.waffle.waffleweather.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Waffle on 2016/2/21.
 */
public class CityDB {
    public static final String DB_NAME = "CityList";

    public static final int VERSION = 1;

    private static CityDB cityDB;

    private SQLiteDatabase db;

    private String[] provinceArray = new String[]{"直辖市","特别行政区","台湾","黑龙江","吉林", "辽宁","内蒙古","河北","河南","山西",
                                                  "山东","江苏","浙江","福建","江西","安徽","湖北","湖南","广东","广西",
                                                  "海南","贵州","云南","四川","西藏","陕西","宁夏","甘肃","青海","新疆"};

    public CityDB(Context context) {
        DataBaseHelper dbHelper = new DataBaseHelper(context);
        try {
            dbHelper.createDataBase();

        } catch (IOException e) {
            e.printStackTrace();
        }
        dbHelper.openDatabase();
        db = dbHelper.getReadableDatabase();
    }

    public synchronized static CityDB getInstance(Context context){
        if(cityDB == null){
            cityDB = new CityDB(context);
        }
        return cityDB;
    }

    public List<String> loadProvinces(){
        List<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(provinceArray));
        return list;
    }

    public Map<String,String> loadCities(String provinceName){
        Map<String,String> map = new HashMap<>();
        Cursor cursor = db.query("City",null,"prov = ?",new String[]{provinceName},null,null,null);
        if(cursor.moveToFirst()){
            do{
                String cityName = cursor.getString(cursor.getColumnIndex("city"));
                String cityID = cursor.getString(cursor.getColumnIndex("city_id"));
                map.put(cityName,cityID);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return map;
    }
}
