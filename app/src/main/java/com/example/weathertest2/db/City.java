package com.example.weathertest2.db;

import org.litepal.crud.DataSupport;

/**
 * Created by yf on 2017/3/19.
 */

public class City extends DataSupport {
    int id;
    String cityCode;
    String cityName;
    String provinceCode;

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public int getId() {
        return id;
    }

    public String getCityCode() {
        return cityCode;
    }

    public String getCityName() {
        return cityName;
    }
}
