package com.example.waffle.waffleweather.activity;

import android.app.Activity;
import android.os.Bundle;

import com.example.waffle.waffleweather.R;
import com.example.waffle.waffleweather.util.ActivityCollector;

/**
 * Created by Waffle on 2016/2/23.
 */
public class AboutActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
