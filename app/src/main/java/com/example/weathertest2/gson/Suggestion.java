package com.example.weathertest2.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yf on 2017/3/20.
 */

public class Suggestion {
    @SerializedName("cw")
    public CarWash carWash;
    public class CarWash{
        @SerializedName("txt")
       public String info;
    }
    @SerializedName("comf")
    public Comfort comfort;
    public class Comfort{
        @SerializedName("txt")
        public String info;
    }
    public Sport sport;
    public class Sport{
        @SerializedName("txt")
        public String info;
    }
}
