package com.example.weathertest2.utility;

import com.example.weathertest2.db.City;
import com.example.weathertest2.db.County;
import com.example.weathertest2.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yf on 2017/3/20.
 */

public class Util {
    public static Boolean handleProvince(String address){
        try {
            JSONArray jsonArray=new JSONArray(address);

            for (int i=0;i<jsonArray.length();i++){
                Province province=new Province();
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                province.setProvinceCode(jsonObject.getInt("id")+"");
                province.setProvinceName(jsonObject.getString("name"));
                province.save();

            }
            return true;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }
    public static Boolean handleCity(String address,String provinceCode){

        try {
            JSONArray jsonArray=new JSONArray(address);

            for (int i=0;i<jsonArray.length();i++){
                City city=new City();
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                city.setCityCode(jsonObject.getInt("id")+"");
                city.setCityName(jsonObject.getString("name"));
                city.setProvinceCode(provinceCode);
                city.save();

            }
            return true;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static Boolean handleCounty(String address,String cityCode){
        try {
            JSONArray jsonArray=new JSONArray(address);

            for (int i=0;i<jsonArray.length();i++){
                County county=new County();
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                county.setCountyCode(jsonObject.getInt("id")+"");
                county.setCountyName(jsonObject.getString("name"));
                county.setWeatherId(jsonObject.getString("weather_id"));
                county.setCityCode(cityCode);
                county.save();

            }
            return true;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;

    }
}
