package com.ctao.baselib.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.ctao.baselib.Global;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by A Miracle on 2017/6/29.
 */
public class FileUtils {

    /** 获取文件后缀名, 小写 */
    public static String getFileExt(String fileName) {
        int pos = fileName.lastIndexOf(".");
        return pos == -1?"":fileName.substring(pos + 1).toLowerCase();
    }

    public static boolean isExists(String path){
        return isExists(new File(path));
    }

    public static boolean isExists(File file){
        return file.exists();
    }

    /** 删除文件 */
    public static boolean deleteFile(File file){
        if(file != null && file.exists() && file.isFile()){
            return file.delete();
        }
        return false;
    }

    /**目录：SD卡/Android/data/包名/type */
    public static File getExternalFilesDir(String type){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 4.4以上, 有外置SD卡优选外置SD卡
            File[] dirs = Global.getContext().getExternalFilesDirs(type);
            if(dirs != null && dirs.length > 1){
                File sdDir = dirs[1];
                if(sdDir != null){
                    return sdDir;
                }
            }
        }
        return Global.getContext().getExternalFilesDir(type);
    }

    /** 文件：SD卡/Android/data/包名/type/fileName */
    public static File createTmpFile(String type, String fileName) {
        return createTmpFile(type, fileName, true);
    }

    /** 文件：SD卡/Android/data/包名/type/fileName */
    public static File createTmpFile(String type, String fileName, boolean deleteExists) {
        File tmpFile = new File(getExternalFilesDir(type), fileName);
        if (tmpFile.exists() && deleteExists) {
            tmpFile.delete();
        }
        try {
            tmpFile.createNewFile();
        } catch (IOException e) {
            LogUtils.e(FileUtils.class.getSimpleName(), e);
        }
        return tmpFile;
    }

    /**
     * 保存bitmap到file
     * @param file
     * @param bitmap
     * @return
     */
    public static Uri saveBitmapToFile(File file, Bitmap bitmap, Bitmap.CompressFormat format) {
        return saveBitmapToFile(file, null, bitmap, format);
    }

    /**
     * 保存bitmap到file
     * @param file
     * @param bitmap
     * @return
     */
    public static Uri saveBitmapToFile(File file, String fileProvider, Bitmap bitmap, Bitmap.CompressFormat format) {
        if (file == null || bitmap == null || file.isDirectory()) {
            return null;
        }
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
            bitmap.compress(format, 100, fOut);
            fOut.flush();

            Uri fromFile;
            if(TextUtils.isEmpty(fileProvider)){
                fromFile = Uri.fromFile(file);
            }else {
                fromFile = FileProvider.getUriForFile(Global.getContext(), fileProvider, file);
            }

            if(false){
                // 其次把文件插入到系统图库
                MediaStore.Images.Media.insertImage(Global.getContext().getContentResolver(), file.getAbsolutePath(), file.getName(), null);

                // 最后通知图库更新
                Global.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fromFile));
            }

            return fromFile;
        } catch (IOException e) {
            LogUtils.e(FileUtils.class.getSimpleName(), e);
        } finally {
            IOUtils.close(fOut);
        }
        return null;
    }
}
