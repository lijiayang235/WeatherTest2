package com.example.weathertest2.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by yf on 2017/3/20.
 */

public class Weather {
    public AQI aqi;
   public Basic basic;
   public Now now;
   public   Suggestion suggestion;
    @SerializedName("daily_forecast")
    public List<Forecast>forecastList;
    public String status;
}
