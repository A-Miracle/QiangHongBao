package com.ctao.qhb.network.service;

import com.ctao.qhb.interact.model.Update;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by A Miracle on 2017/8/29.
 */
public interface UpdateApiService {
    @GET("A-Miracle/QiangHongBao/master/update.json")
    Call<Update> checkUpdate();

    @GET("A-Miracle/QiangHongBao/master/{fileName}")
    Call<ResponseBody> downloadAPk(@Path("fileName") String fileName);
}
