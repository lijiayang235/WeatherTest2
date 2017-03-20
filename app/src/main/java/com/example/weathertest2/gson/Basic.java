package com.example.weathertest2.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yf on 2017/3/20.
 */

public class Basic {
    @SerializedName("id")
    public String weatherId;
    public String city;
    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
