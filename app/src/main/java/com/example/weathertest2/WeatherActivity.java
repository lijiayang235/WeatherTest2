package com.example.weathertest2;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weathertest2.gson.Forecast;
import com.example.weathertest2.gson.Weather;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String responseText=prefs.getString("weather",null);
        if(!TextUtils.isEmpty(responseText)){
            Weather weather= Util.handleWeather(responseText);
            showWeather(weather);
        }else{
            String weatherId=getIntent().getStringExtra("weather_id");

            requestWeather(weatherId);

            weatherlayout.setVisibility(View.GONE);
        }



    }

    private void requestWeather(String weatherId) {
        String address="http://guolin.tech/api/weather?cityid="+weatherId+"&key=19dbb264703e45ff96e4373c0a67268b";
        HttpUtil.sendOkHttp(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
                        if(weather!=null){

                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
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
        nowTemperatre.setText(weather.now.temperature);
        nowInfo.setText(weather.now.more.info);
        forecastLayout.removeAllViews();
        for (Forecast f:weather.forecastList){
        View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout);
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
