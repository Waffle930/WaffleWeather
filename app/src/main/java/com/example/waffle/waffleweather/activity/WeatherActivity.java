package com.example.waffle.waffleweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RecoverySystem;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.text.TextUtils;
import android.view.MenuItem;
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
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Waffle on 2016/2/21.
 */

public class WeatherActivity extends Activity {

    private DrawerLayout drawerLayout;
    private SwipeRefreshLayout refreshLayout;
    private LinearLayout backgroundLayout;
    private NavigationView navigation;

    private TextView cityNameText;
    private TextView tempMinText;
    private TextView tempMaxText;
    private TextView weatherText;
    private TextView publishTimeText;
    private String currentCityId;

    private Button home;

    private ImageView userPic;
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

    private Boolean isExit = false;

    private String[] dayOfWeek = {"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};

//    https://api.heweather.com/x3/weather?cityid=城市ID&key=你的认证key
    private final String APIHEAD = "https://api.heweather.com/x3/weather?";
    private final String APIKEY = "8cb79ede42304fffae78596bbf1053e3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_layout);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        backgroundLayout = (LinearLayout) findViewById(R.id.background_layout);
        navigation = (NavigationView) findViewById(R.id.navigation);
        cityNameText = (TextView) findViewById(R.id.city_name);
        tempMinText = (TextView) findViewById(R.id.temp1);
        tempMaxText = (TextView) findViewById(R.id.temp2);
        weatherText = (TextView) findViewById(R.id.weather_desp);
        publishTimeText = (TextView) findViewById(R.id.publish_text);
        userPic = (ImageView) navigation.getHeaderView(0).findViewById(R.id.user_pic);
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
        userPic.setImageBitmap(Utility.getRoundedCornerBitmap(BitmapFactory.decodeResource(this.getResources(),R.drawable.user)));
        refreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.YELLOW, Color.GREEN);
        refreshLayout.setBackgroundColor(Color.WHITE);
        refreshLayout.setSize(SwipeRefreshLayout.LARGE);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                publishTimeText.setText("同步中...");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                currentCityId = prefs.getString("city_id","");
                requestWeatherInfo(currentCityId);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.select_city:
                        PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit().clear().commit();
                        Intent intentChoose = new Intent(WeatherActivity.this,ChooseAreaActivity.class);
                        startActivity(intentChoose);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.about:
                        drawerLayout.closeDrawers();
                        Intent intentAbout = new Intent(WeatherActivity.this,AboutActivity.class);
                        startActivity(intentAbout);
                        drawerLayout.closeDrawers();
                        break;
                    default:
                        drawerLayout.closeDrawers();
                        break;
                }
                return true;
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
                        refreshLayout.setRefreshing(false);
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
        String weatherDay = prefs.getString("weather_day","");
        String weatherNight = prefs.getString("weather_night","");
        if(weatherDay.equals(weatherNight)){
           weatherText.setText(weatherDay);
        }else{
            weatherText.setText(weatherDay + "转" + weatherNight);
        }
        tempMinText.setText(prefs.getString("temp_min",""));
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
        try {
            showBackground(prefs.getString("now_pic","999"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showForecast(ImageView imageView,String picId){
        try {
            InputStream in = this.getAssets().open("forcastPic/"+picId+".png");
            Bitmap weatherPic = BitmapFactory.decodeStream(in);
            imageView.setImageBitmap(weatherPic);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showBackground(String nowPic) throws IOException{
        switch (nowPic){
            case "100":
            case "103":
                backgroundLayout.setBackground(Drawable.createFromStream(getAssets().open("currentPic/sun.jpg"),null));
                break;
            case "101":
            case "102":
            case "104":
                backgroundLayout.setBackground(Drawable.createFromStream(getAssets().open("currentPic/cloud.jpg"),null));
                break;
            case "300":
            case "301":
            case "305":
            case "306":
            case "307":
            case "308":
            case "309":
            case "310":
            case "311":
            case "312":
            case "313":
            case "406":
                backgroundLayout.setBackground(Drawable.createFromStream(getAssets().open("currentPic/rain.jpg"),null));
                break;
            case "500":
            case "501":
            case "502":
            case "503":
            case "504":
            case "506":
            case "507":
            case "508":
                backgroundLayout.setBackground(Drawable.createFromStream(getAssets().open("currentPic/sandy.jpg"),null));
                break;
            case "400":
            case "401":
            case "402":
            case "403":
            case "404":
            case "405":
            case "407":
                backgroundLayout.setBackground(Drawable.createFromStream(getAssets().open("currentPic/snow.jpg"),null));
                break;
            case "200":
            case "201":
            case "202":
            case "203":
            case "204":
            case "205":
            case "206":
            case "207":
            case "208":
                backgroundLayout.setBackground(Drawable.createFromStream(getAssets().open("currentPic/wind.jpg"),null));
                break;
            case "209":
            case "210":
            case "211":
            case "212":
            case "213":
                backgroundLayout.setBackground(Drawable.createFromStream(getAssets().open("currentPic/storm.jpg"),null));
                break;
            case "302":
            case "303":
            case "304":
                backgroundLayout.setBackground(Drawable.createFromStream(getAssets().open("currentPic/thunder.jpg"),null));
                break;
            default:
                backgroundLayout.setBackgroundColor(Color.BLUE);
                break;
        }
    }

    private void exitApp(){
        Timer tExit = null;
        if(isExit == false){
            isExit = true;
            Toast.makeText(this,"再按一次退出",Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            },2000);
        }else{
            ActivityCollector.finishAll();
            System.exit(0);
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers();
        } else {
            exitApp();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
