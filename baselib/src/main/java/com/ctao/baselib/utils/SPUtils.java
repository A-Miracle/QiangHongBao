package com.ctao.baselib.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.ctao.baselib.Global;

import java.util.Set;

/**
 * Created by A Miracle on 2016/10/18.
 * SharedPreferences 工具类
 * Builder模式
 */
public class SPUtils {

    private static String PREF_NAME = "admin.pref";

    private static boolean sIsAtLeastGB;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            sIsAtLeastGB = true;
        }
    }

    private static SharedPreferences mPreferences;

    /**
     * Editor.put...
     * @param key
     * @param value Boolean, Int, Float, Long, String, Set<String>
     */
    public static void putObject(String key, Object value) {
        SharedPreferences.Editor edit = getPreferences().edit();
        if (value instanceof Boolean) {
            edit.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            edit.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            edit.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            edit.putLong(key, (Long) value);
        } else if (value instanceof String) {
            edit.putString(key, (String) value);
        } else if (value instanceof Set) {
            edit.putStringSet(key, (Set<String>) value);
        } else if (value == null) {
            edit.putString(key, null);
        }

        if (sIsAtLeastGB) {
            edit.apply();
        } else {
            edit.commit();
        }
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return getPreferences().getBoolean(key, defValue);
    }

    public static int getInt(String key, int defValue) {
        return getPreferences().getInt(key, defValue);
    }

    public static float getFloat(String key, float defValue) {
        return getPreferences().getFloat(key, defValue);
    }

    public static long getLong(String key, long defValue) {
        return getPreferences().getLong(key, defValue);
    }

    public static String getString(String key, String defValue) {
        return getPreferences().getString(key, defValue);
    }

    public static Set<String> getStringSet(String key, Set<String> defValue) {
        return getPreferences().getStringSet(key, defValue);
    }

    public static SharedPreferences getPreferences() {
        if(mPreferences == null){
            mPreferences = context().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
        return mPreferences;
    }

    public static SharedPreferences getPreferences(String prefName) {
        return context().getSharedPreferences(prefName, Context.MODE_PRIVATE);
    }

    public static void releasePreferences(){
        mPreferences = null;
    }

    public static Context context(){
        return Global.getContext();
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private SharedPreferences.Editor edit;

        Builder() {
            edit = getPreferences().edit();
        }

        public Builder putBoolean(String key, boolean value) {
            edit.putBoolean(key, value);
            return this;
        }

        public Builder putInt(String key, int value) {
            edit.putInt(key, value);
            return this;
        }

        public Builder putFloat(String key, float value) {
            edit.putFloat(key, value);
            return this;
        }

        public Builder putLong(String key, long value) {
            edit.putLong(key, value);
            return this;
        }

        public Builder putString(String key, String value) {
            edit.putString(key, value);
            return this;
        }

        public Builder putStringSet(String key, Set<String> value) {
            edit.putStringSet(key, value);
            return this;
        }

        public void commit(){
            if (sIsAtLeastGB) {
                edit.apply();
            } else {
                edit.commit();
            }
            edit = null;
        }
    }
}
