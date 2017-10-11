package com.ctao.qhb.interact.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by A Miracle on 2017/8/29.
 */
public class Update {
    private static final String VERSION_CODE = "versionCode";
    private static final String VERSION_NAME = "versionName";
    private static final String DETAIL = "detail";
    private static final String FILE_NAME = "fileName";
    private static final String URL = "url";

    @SerializedName(VERSION_CODE)
    public int versionCode;
    @SerializedName(VERSION_NAME)
    public String versionName;
    @SerializedName(DETAIL)
    public String detail;
    @SerializedName(FILE_NAME)
    public String fileName;
    @SerializedName(URL)
    public String url;
}
