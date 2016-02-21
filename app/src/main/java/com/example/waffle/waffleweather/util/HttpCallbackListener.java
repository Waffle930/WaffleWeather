package com.example.waffle.waffleweather.util;

/**
 * Created by Waffle on 2016/2/21.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
