package com.example.waffle.waffleweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
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
    public static void handleWeatherResponse(Context context, String response) {
        try {
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
            JSONObject suggest = weatherData.getJSONObject("suggestion");
            String comf = suggest.getJSONObject("comf").getString("brf");
            String drsg = suggest.getJSONObject("drsg").getString("brf");
            String flu = suggest.getJSONObject("flu").getString("brf");
            String sport = suggest.getJSONObject("sport").getString("brf");
            String trav = suggest.getJSONObject("trav").getString("brf");
            String uv = suggest.getJSONObject("uv").getString("brf");
            saveWeatherInfo(context, cityName, tempMin, tempMax, weatherDay, weatherNight, publishTime, date1DayPic, date2DayPic, date3DayPic, date4DayPic, date5DayPic,nowPic,comf,drsg,flu,sport,trav,uv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveWeatherInfo(Context context, String cityName, String tempMin, String tempMax, String weatherDay, String weatherNight, String publishTime,
                                       String date1DayPic, String date2DayPic, String date3DayPic, String date4DayPic, String date5DayPic,String nowPic,
                                       String comf,String drsg,String flu,String sport,String trav,String uv) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("temp_min", tempMin);
        editor.putString("temp_max", tempMax);
        editor.putString("weather_day", weatherDay);
        editor.putString("weather_night", weatherNight);
        editor.putString("publish_time", publishTime);
        editor.putString("date1_day_pic", date1DayPic);
        editor.putString("date2_day_pic", date2DayPic);
        editor.putString("date3_day_pic", date3DayPic);
        editor.putString("date4_day_pic", date4DayPic);
        editor.putString("date5_day_pic", date5DayPic);
        editor.putString("now_pic",nowPic);
        editor.putString("comf_index",comf);
        editor.putString("drsg_index",drsg);
        editor.putString("flu_index",flu);
        editor.putString("sport_index",sport);
        editor.putString("trav_index",trav);
        editor.putString("uv_index",uv);
        editor.commit();
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        float roundPx = w / 2;
        Bitmap output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}
