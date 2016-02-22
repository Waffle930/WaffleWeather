package com.example.waffle.waffleweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.waffle.waffleweather.activity.WeatherActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Waffle on 2016/2/21.
 */
public class Utility {
    public static void handleWeatherResponse(Context context , String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherData = (JSONObject) jsonObject.getJSONArray("HeWeather data service 3.0").opt(0);
            JSONObject basic = weatherData.getJSONObject("basic");
            String cityName = basic.getString("city");
            JSONObject update = basic.getJSONObject("update");
            String publishTime = update.getString("loc");
            JSONObject dailyForecast = weatherData.getJSONArray("daily_forecast").optJSONObject(0);
            JSONObject tmp = dailyForecast.getJSONObject("tmp");
            String tempMin = tmp.getString("min");
            String tempMax = tmp.getString("max");
            JSONObject cond = dailyForecast.getJSONObject("cond");
            String weatherDay = cond.getString("txt_d");
            String weatherNight = cond.getString("txt_n");
            saveWeatherInfo(context,cityName,tempMin,tempMax,weatherDay,weatherNight,publishTime);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void saveWeatherInfo(Context context , String cityName,String tempMin,String tempMax,String weatherDay,String weatherNight,String publishTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityName);
        editor.putString("temp_min",tempMin);
        editor.putString("temp_max",tempMax);
        editor.putString("weather_day",weatherDay);
        editor.putString("weather_night",weatherNight);
        editor.putString("publish_time",publishTime);
        editor.putString("current_date",sdf.format(new Date()));
        editor.commit();
    }
}
