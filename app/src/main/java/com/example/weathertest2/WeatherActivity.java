package com.example.weathertest2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.weathertest2.gson.Forecast;
import com.example.weathertest2.gson.Weather;
import com.example.weathertest2.service.AutoUpdateService;
import com.example.weathertest2.utility.HttpUtil;
import com.example.weathertest2.utility.Util;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    public TextView titleCity;
    public TextView titleUpdate;
    public TextView nowTemperatre;
    public TextView nowInfo;
    public TextView aqi;
    public TextView pm25;
    public TextView comfort;
    public TextView carwash;
    public TextView sport;
    public ScrollView weatherlayout;
    public LinearLayout forecastLayout;
    public ImageView bingPic;
    public SwipeRefreshLayout swipeRefreshLayout;
    public DrawerLayout drawerLayout;
    public Button backButton;
    String mweatherId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view=getWindow().getDecorView();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        titleCity= (TextView) findViewById(R.id.title_city);
        titleUpdate= (TextView) findViewById(R.id.title_update_time);
        nowTemperatre= (TextView) findViewById(R.id.now_temperature);
        nowInfo= (TextView) findViewById(R.id.now_info);
        aqi= (TextView) findViewById(R.id.aqi);
        pm25= (TextView) findViewById(R.id.pm25);
        comfort= (TextView) findViewById(R.id.comfort);
        carwash= (TextView) findViewById(R.id.carwash);
        sport= (TextView) findViewById(R.id.sport);
        weatherlayout= (ScrollView) findViewById(R.id.weatherLayout);
        forecastLayout= (LinearLayout) findViewById(R.id.forecastWeatherlayout);
        bingPic= (ImageView) findViewById(R.id.bing_pic);
        swipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        drawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);
        backButton= (Button) findViewById(R.id.nav);

        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String responseText=prefs.getString("weather",null);
        if(responseText!=null){
            Weather weather= Util.handleWeather(responseText);
            mweatherId=weather.basic.weatherId;
            showWeather(weather);
        }else{
            mweatherId=getIntent().getStringExtra("weather_id");

            requestWeather(mweatherId);

            weatherlayout.setVisibility(View.GONE);
        }
        final String bingPicText=prefs.getString("bing_pic",null);
        if(bingPicText!=null){
            Glide.with(this).load(bingPicText).into(bingPic);
        }else{
            String address="http://guolin.tech/api/bing_pic";
            HttpUtil.sendOkHttp(address, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String bingPicResponseText=response.body().string();
                    SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                    editor.putString("bing_pic",bingPicResponseText);
                    editor.apply();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(WeatherActivity.this).load(bingPicResponseText).into(bingPic);
                        }
                    });

                }
            });
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mweatherId);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


    }

    public void requestWeather(final String weatherId) {
        String address="http://guolin.tech/api/weather?cityid="+weatherId+"&key=19dbb264703e45ff96e4373c0a67268b";
        HttpUtil.sendOkHttp(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(WeatherActivity.this,"连接和风天气服务器失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseText=response.body().string();
                final Weather weather=Util.handleWeather(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        if(weather!=null){
                            Intent intent=new Intent(WeatherActivity.this, AutoUpdateService.class);
                            startActivity(intent);
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            mweatherId=weatherId;
                            showWeather(weather);


                        }else{
                            Toast.makeText(WeatherActivity.this,"连接和风天气服务器,但是获取数据失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    private void showWeather(Weather weather) {
        titleCity.setText(weather.basic.city);

        titleUpdate.setText(weather.basic.update.updateTime);
        nowTemperatre.setText(weather.now.temperature+"℃");
        nowInfo.setText(weather.now.more.info);
        forecastLayout.removeAllViews();
        for (Forecast f:weather.forecastList){
        View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView forecastTime= (TextView) view.findViewById(R.id.forecast_time);
            TextView max= (TextView) view.findViewById(R.id.forecast_max);
            TextView min= (TextView) view.findViewById(R.id.forecast_min);
            TextView forecastInfo= (TextView) view.findViewById(R.id.forecast_info);
            forecastInfo.setText(f.more.info);
            forecastTime.setText(f.date);
            max.setText(f.temperature.max);
            min.setText(f.temperature.min);
             forecastLayout.addView(view);
        }



        if(weather.aqi!=null) {
            aqi.setText(weather.aqi.city.aqi);
            pm25.setText(weather.aqi.city.pm25);
        }
        carwash.setText("洗车指数："+weather.suggestion.carWash.info);
        sport.setText("运动指数"+weather.suggestion.sport.info);
        comfort.setText("舒适指数"+weather.suggestion.comfort.info);
        weatherlayout.setVisibility(View.VISIBLE);

    }
}
