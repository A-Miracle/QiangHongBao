package com.ctao.qhb.network;

import com.ctao.baselib.utils.JsonUtils;
import com.ctao.baselib.utils.LogUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by A Miracle on 2016/12/12.
 */
public class Params {
    // int what, String url, Map<String, String> params
    private int what;
    private String baseUrl;
    private String url;
    private Map<String, Object> params = new HashMap<>();

    private int connectTimeout; // 连接超时时间，单位毫秒
    private int readTimeout; // 服务器响应超时时间，单位毫秒

    public Params(String baseUrl, String url){
        this.baseUrl = baseUrl;
        this.url = url;
    }

    public Params addParams(String key, Object value){
        params.put(key,value);
        return this;
    }

    public Params addParams(Map<String, Object> map){
        params.putAll(map);
        return this;
    }

    public Params log(){
        LogUtils.printOut("HTTP-Params: " + JsonUtils.encode(params));
        return this;
    }

    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
}
