package com.example.weathertest2.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weathertest2.R;
import com.example.weathertest2.WeatherActivity;
import com.example.weathertest2.db.City;
import com.example.weathertest2.db.County;
import com.example.weathertest2.db.Province;
import com.example.weathertest2.utility.HttpUtil;
import com.example.weathertest2.utility.Util;

import org.litepal.crud.DataSupport;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by yf on 2017/3/20.
 */

public class ChooseArea extends Fragment {
    public static int LEVEL_PROVINCE=0;
    public static int LEVEL_CITY=1;
    public static int LEVEL_COUNTY=2;
    Button backbutton;
    TextView titleText;
    ListView listView;
    ArrayAdapter<String>adapter;
    List<String> dataList=new ArrayList<>();
    int currentLevel;
    List<Province>provincelist;
    List<City>citylist;
    List<County>countylist;
    Province selectProvince;
    City selectCity;
    County selectCounty;
    ProgressDialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=LayoutInflater.from(getContext()).inflate(R.layout.choose_area,container);
        backbutton= (Button) view.findViewById(R.id.back_button);
        titleText= (TextView) view.findViewById(R.id.title_text);
        listView= (ListView) view.findViewById(R.id.list_view);
        adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLevel==LEVEL_CITY){
                    queryProvince();
                }else if(currentLevel==LEVEL_COUNTY){
                    queryCity();
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                if(currentLevel==LEVEL_CITY){
                    selectCity=citylist.get(position);
                    queryCounty();
                }else if(currentLevel==LEVEL_PROVINCE){
                    selectProvince=provincelist.get(position);
                    queryCity();
                }else if(currentLevel==LEVEL_COUNTY){
                    selectCounty=countylist.get(position);
                    Intent intent=new Intent(getContext(),WeatherActivity.class);
                    intent.putExtra("weather_id",selectCounty.getWeatherId());
                    startActivity(intent);
                }

            }
        });
        queryProvince();
        super.onActivityCreated(savedInstanceState);
    }



    private void queryProvince() {
        titleText.setText("中国");
        backbutton.setVisibility(View.GONE);
        provincelist= DataSupport.findAll(Province.class);
        if(provincelist.size()>0){
            dataList.clear();
            for (Province p:provincelist){
                dataList.add(p.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_PROVINCE;

        }else {
            String address="http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }



    }

    private void queryCity() {
        titleText.setText(selectProvince.getProvinceName());
        backbutton.setVisibility(View.VISIBLE);
        citylist=DataSupport.where("provinceCode=?",selectProvince.getProvinceCode()).find(City.class);
        if(citylist.size()>0){
            dataList.clear();
            for(City c:citylist){
                dataList.add(c.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_CITY;

        }else {
            String address="http://guolin.tech/api/china/"+selectProvince.getProvinceCode();
            queryFromServer(address,"city");
        }

    }
    private void queryCounty() {
        titleText.setText(selectCity.getCityName());
        backbutton.setVisibility(View.VISIBLE);
        countylist=DataSupport.where("cityCode=?",selectCity.getCityCode()).find(County.class);
        if(countylist.size()>0){
            dataList.clear();
            for(County county:countylist){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_COUNTY;

        }else {
            String address="http://guolin.tech/api/china/"+selectProvince.getProvinceCode()+"/"+selectCity.getCityCode();
            queryFromServer(address,"county");
        }

    }
    private void queryFromServer(String address, final String type) {
        startProgressDialog();
        
        HttpUtil.sendOkHttp(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cancelDialog();
                        Toast.makeText(getContext(),"连接服务器失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                Boolean result=false;
                if(type.equals("province")){
                    result=Util.handleProvince(responseText);

                }else if(type.equals("city")){
                    result=Util.handleCity(responseText,selectProvince.getProvinceCode());

                }else if(type.equals("county")){
                    result=Util.handleCounty(responseText,selectCity.getCityCode());


                }
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cancelDialog();
                            if(type.equals("province")){
                                queryProvince();
                            }else if(type.equals("city")){
                                queryCity();
                            }else if(type.equals("county")){
                                queryCounty();
                            }
                        }
                    });


                }

            }
        });
    }

    private void cancelDialog() {
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    private void startProgressDialog() {

        if(progressDialog==null){
            progressDialog=new ProgressDialog(getContext());
            progressDialog.setMessage("加载中");
            progressDialog.setCanceledOnTouchOutside(false);

        }
        progressDialog.show();
    }

}
