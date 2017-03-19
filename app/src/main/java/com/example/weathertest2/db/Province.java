package com.example.weathertest2.db;

import org.litepal.crud.DataSupport;

/**
 * Created by yf on 2017/3/19.
 */

public class Province extends DataSupport{
    int id;
    String provinceCode;
    String provinceName;

    public void setId(int id) {
        this.id = id;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getId() {
        return id;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }
}
