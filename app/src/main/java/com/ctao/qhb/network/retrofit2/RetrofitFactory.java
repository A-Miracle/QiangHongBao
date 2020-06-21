package com.ctao.qhb.network.retrofit2;


import androidx.collection.ArrayMap;

import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by A Miracle on 2017/5/13.
 * 为甚存在这样一个工厂, baseUrl可能不止一个
 */
public class RetrofitFactory {
    private Map<String, Retrofit> retrofitMap;
    private OkHttpClient okHttpClient;

    static class Single{
        static RetrofitFactory Instance = new RetrofitFactory();
    }

    private RetrofitFactory(){
        retrofitMap = new ArrayMap<>();

        // 对 OkHttpClient 做一些设置
        okHttpClient = new OkHttpClient().newBuilder().build();
    }

    public static RetrofitFactory getInstance(){
        return RetrofitFactory.Single.Instance;
    }

    public Retrofit getRetrofit(String baseUrl) {
        Retrofit retrofit = retrofitMap.get(baseUrl);
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
            retrofitMap.put(baseUrl, retrofit);
        }
        return retrofit;
    }
}
