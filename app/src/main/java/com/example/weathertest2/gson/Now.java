package com.example.weathertest2.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yf on 2017/3/20.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond")
    public More more;
    public class More{
        @SerializedName("txt")
       public String info;
    }
}
