package com.example.waffle.waffleweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RecoverySystem;
import android.preference.PreferenceManager;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.waffle.waffleweather.R;
import com.example.waffle.waffleweather.util.HttpCallbackListener;
import com.example.waffle.waffleweather.util.HttpUtil;
import com.example.waffle.waffleweather.util.Utility;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Waffle on 2016/2/21.
 */

public class WeatherActivity extends Activity {
    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView tempMinText;
    private TextView tempMaxText;
    private TextView weatherText;
    private TextView publishTimeText;
    private TextView currentDateText;
    private String currentCityId;
    private Button home;
    private Button refresh;
    private ImageView date1DayPic;
    private ImageView date2DayPic;
    private ImageView date3DayPic;
    private ImageView date4DayPic;
    private ImageView date5DayPic;
    private TextView date1;
    private TextView date2;
    private TextView date3;
    private TextView date4;
    private TextView date5;
    private String[] dayOfWeek = {"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};

//    https://api.heweather.com/x3/weather?cityid=城市ID&key=你的认证key
    private final String APIHEAD = "https://api.heweather.com/x3/weather?";
    private final String APIKEY = "8cb79ede42304fffae78596bbf1053e3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        tempMinText = (TextView) findViewById(R.id.temp1);
        tempMaxText = (TextView) findViewById(R.id.temp2);
        weatherText = (TextView) findViewById(R.id.weather_desp);
        publishTimeText = (TextView) findViewById(R.id.publish_text);
        currentDateText = (TextView) findViewById(R.id.current_date);
        date1DayPic = (ImageView) findViewById(R.id.date1_pic);
        date2DayPic = (ImageView) findViewById(R.id.date2_pic);
        date3DayPic = (ImageView) findViewById(R.id.date3_pic);
        date4DayPic = (ImageView) findViewById(R.id.date4_pic);
        date5DayPic = (ImageView) findViewById(R.id.date5_pic);
        date1 = (TextView) findViewById(R.id.date1);
        date2 = (TextView) findViewById(R.id.date2);
        date3 = (TextView) findViewById(R.id.date3);
        date4 = (TextView) findViewById(R.id.date4);
        date5 = (TextView) findViewById(R.id.date5);
        home = (Button) findViewById(R.id.home_button);
        refresh = (Button) findViewById(R.id.refresh_button);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit().clear().commit();
                Intent intent = new Intent(WeatherActivity.this,ChooseAreaActivity.class);
                startActivity(intent);
                finish();
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishTimeText.setText("同步中...");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                currentCityId = prefs.getString("city_id","");
                requestWeatherInfo(currentCityId);
            }
        });
        currentCityId = getIntent().getStringExtra("cityId");
        requestWeatherInfo(currentCityId);
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("city_id",currentCityId).commit();

    }

    private void requestWeatherInfo(final String cityId){
        String address = APIHEAD + "cityid=" + cityId + "&key=" + APIKEY;
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(WeatherActivity.this,response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showWeather();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                publishTimeText.setText("同步失败");
            }
        });
    }

    private void showWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name",""));
        publishTimeText.setText(prefs.getString("publish_time","")+"发布");
        currentDateText.setText(prefs.getString("current_date",""));
        String weatherDay = prefs.getString("weather_day","");
        String weatherNight = prefs.getString("weather_night","");
        if(weatherDay.equals(weatherNight)){
           weatherText.setText(weatherDay);
        }else{
            weatherText.setText(weatherDay + "转" + weatherNight);
        }
        tempMinText.setText(prefs.getString("temp_min","")+"℃");
        tempMaxText.setText(prefs.getString("temp_max","")+"℃");
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK);
        showForecast(date1DayPic,prefs.getString("date1_day_pic","999"));
        date1.setText("今天");
        showForecast(date2DayPic,prefs.getString("date2_day_pic","999"));
        date2.setText(dayOfWeek[today%7]);
        showForecast(date3DayPic,prefs.getString("date3_day_pic","999"));
        date3.setText(dayOfWeek[(today+1)%7]);
        showForecast(date4DayPic,prefs.getString("date4_day_pic","999"));
        date4.setText(dayOfWeek[(today+2)%7]);
        showForecast(date5DayPic,prefs.getString("date5_day_pic","999"));
        date5.setText(dayOfWeek[(today+3)%7]);
    }

//    http://files.heweather.com/cond_icon/100.png
    private void showForecast(ImageView imageView,String picId){
        try {
            InputStream in = this.getAssets().open("weather/"+picId+".png");
            Bitmap weatherPic = BitmapFactory.decodeStream(in);
            imageView.setImageBitmap(weatherPic);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
