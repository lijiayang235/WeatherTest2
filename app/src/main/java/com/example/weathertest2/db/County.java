package com.example.weathertest2.db;

/**
 * Created by yf on 2017/3/19.
 */

public class County {
    int id;
    String countyCode;
    String countyName;
    String weatherId;
    String cityCode;

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public String getCityCode() {
        return cityCode;
    }

    public int getId() {
        return id;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public String getCountyName() {
        return countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }
}
