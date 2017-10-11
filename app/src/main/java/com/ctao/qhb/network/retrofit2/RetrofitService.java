package com.ctao.qhb.network.retrofit2;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by A Miracle on 2017/5/12.
 * 基于 retrofit2 的网络请求
 */
public interface RetrofitService {

    /**
     * 使用post body方式提交数据, 需添加 ScalarsConverterFactory or GsonConverterFactory 转换器
     * @param url 请求url
     * @param param 参数
     * @return String
     */
    @POST()
    <T> Call<T> postBody(@Url String url, @Body T param);

    /**
     * 使用post方式提交数据, 需添加 GsonConverterFactory 转换器
     * @param url 请求url
     * @param param 参数
     * @return String
     */
    @FormUrlEncoded
    @POST()
    <T> Call<T> post(@Url String url, @FieldMap Map<String, Object> param);

    /**
     * 使用get方式提交数据, 需添加 GsonConverterFactory 转换器
     * @param url 请求url
     * @param param 参数
     * @return String
     */
    @GET()
    <T> Call<T> get(@Url String url, @QueryMap Map<String, Object> param);

    /**
     * 使用get方式提交数据, 需添加 ScalarsConverterFactory or GsonConverterFactory 转换器
     * @param url 请求url
     * @param param 参数
     * @return String
     */
    @GET()
    <T> Call<T> getBody(@Url String url, @Body T param);
}
