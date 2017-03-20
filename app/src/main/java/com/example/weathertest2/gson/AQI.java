package com.example.weathertest2.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yf on 2017/3/20.
 */

public class AQI {
    public AQICity city;

    public class AQICity{
       public String pm25;
        public String aqi;
        @SerializedName("qlty")
        public String qulity;
    }

}
