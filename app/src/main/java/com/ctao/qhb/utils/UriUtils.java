package com.ctao.qhb.utils;

import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import com.ctao.baselib.Global;
import com.ctao.qhb.R;

import java.io.File;

/**
 * Created by A Miracle on 2017/8/29.
 */
public class UriUtils {

    public static Uri fromFile(File file){
        Uri uri;
        if(Build.VERSION.SDK_INT >= 24){
            uri = FileProvider.getUriForFile(Global.getContext(), Global.getContext().getString(R.string.fileprovider), file);
        }else{
            uri = Uri.fromFile(file);
        }
        return uri;
    }
}
