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
            JSONArray dailyForecast = weatherData.getJSONArray("daily_forecast");
            String nowPic = weatherData.getJSONObject("now").getJSONObject("cond").getString("code");
            JSONObject date1 = dailyForecast.optJSONObject(0);
            JSONObject tmp = date1.getJSONObject("tmp");
            String tempMin = tmp.getString("min");
            String tempMax = tmp.getString("max");
            JSONObject cond = date1.getJSONObject("cond");
            String weatherDay = cond.getString("txt_d");
            String weatherNight = cond.getString("txt_n");
            String date1DayPic = cond.getString("code_d");
            String date2DayPic = dailyForecast.optJSONObject(1).getJSONObject("cond").getString("code_d");
            String date3DayPic = dailyForecast.optJSONObject(2).getJSONObject("cond").getString("code_d");
            String date4DayPic = dailyForecast.optJSONObject(3).getJSONObject("cond").getString("code_d");
            String date5DayPic = dailyForecast.optJSONObject(4).getJSONObject("cond").getString("code_d");
            saveWeatherInfo(context,cityName,tempMin,tempMax,weatherDay,weatherNight,publishTime,date1DayPic,date2DayPic,date3DayPic,date4DayPic,date5DayPic,nowPic);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void saveWeatherInfo(Context context , String cityName,String tempMin,String tempMax,String weatherDay,String weatherNight,String publishTime,String date1DayPic,String date2DayPic,String date3DayPic,String date4DayPic,String date5DayPic,String nowPic){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityName);
        editor.putString("temp_min",tempMin);
        editor.putString("temp_max",tempMax);
        editor.putString("weather_day",weatherDay);
        editor.putString("weather_night",weatherNight);
        editor.putString("publish_time",publishTime);
        editor.putString("date1_day_pic",date1DayPic);
        editor.putString("date2_day_pic",date2DayPic);
        editor.putString("date3_day_pic",date3DayPic);
        editor.putString("date4_day_pic",date4DayPic);
        editor.putString("date5_day_pic",date5DayPic);
        editor.putString("now_pic",nowPic);
        editor.commit();
    }
}
