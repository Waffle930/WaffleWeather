package com.example.waffle.waffleweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.waffle.waffleweather.R;
import com.example.waffle.waffleweather.util.HttpCallbackListener;
import com.example.waffle.waffleweather.util.HttpUtil;
import com.example.waffle.waffleweather.util.Utility;

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
                PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit().clear().commit();
                requestWeatherInfo(currentCityId);
            }
        });
        currentCityId = getIntent().getStringExtra("cityId");
        requestWeatherInfo(currentCityId);
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
        tempMinText.setText(prefs.getString("temp_min",""));
        tempMaxText.setText(prefs.getString("temp_max",""));
    }
}
