package com.example.weathertest2.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yf on 2017/3/20.
 */

public class Forecast {
    @SerializedName("tmp")
    public Temperature temperature;
    public class Temperature{
        public String max;
        public String min;

    }
    public String date;
    @SerializedName("cond")
    public More more;
    public class More{
        @SerializedName("txt_d")
        public String info;

    }
}
