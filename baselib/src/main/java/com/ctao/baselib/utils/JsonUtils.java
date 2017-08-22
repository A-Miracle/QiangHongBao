package com.ctao.baselib.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by A Miracle on 2016/11/24.
 */
public final class JsonUtils {
    //===============================JSON解析=====================================
    public static Map<String,Object> decodeObject(String json){
        Map<String,Object> result=new HashMap<>();
        JSONTokener readFrom=new JSONTokener(json);
        try {
            Object object = readFrom.nextValue();
            if(object instanceof JSONObject){
                result=decodingObject((JSONObject)object);
            }
        }catch (JSONException ex){
            LogUtils.e(ex);
        }finally {
            return result;
        }
    }

    public static List<Object> decodeArray(String json){
        List<Object> result=new ArrayList<>();
        JSONTokener readFrom=new JSONTokener(json);
        try {
            Object object = readFrom.nextValue();
            if(object instanceof JSONArray){
                result = decodingArray((JSONArray)object);
            }
        }catch (JSONException ex){
            LogUtils.e(ex);
        }finally {
            return result;
        }
    }

    private static Map<String,Object> decodingObject(JSONObject jsonObject){
        Map<String,Object> map=new HashMap<>();
        Iterator<String> keys=jsonObject.keys();
        try {
            while (keys.hasNext()) {
                String key = keys.next();
                Object item = jsonObject.get(key);
                JsonDecodeType type = getDecodingType(item);
                if (JsonDecodeType.Null == type) {
                }else if (JsonDecodeType.Normal == type) {
                    map.put(key, item);
                }else if (JsonDecodeType.Object == type) {
                    map.put(key,decodingObject((JSONObject) item));
                }else if (JsonDecodeType.Array == type) {
                    map.put(key,decodingArray((JSONArray) item));
                }
            }
        }catch(JSONException ex){
            LogUtils.e(ex);
        }finally {
            return map;
        }
    }

    public static List<Object> decodingArray(JSONArray jsonArray){
        List<Object> list=new ArrayList<>();
        final int length=jsonArray.length();
        try {
            for (int i = 0; i < length; i++) {
                Object item = jsonArray.get(i);
                JsonDecodeType type = getDecodingType(item);
                if (JsonDecodeType.Null == type) {
                }else if (JsonDecodeType.Object == type) {
                    list.add(decodingObject((JSONObject) item));
                } else if (JsonDecodeType.Array == type) {
                    list=decodingArray((JSONArray) item);
                } else if (JsonDecodeType.Normal == type) {
                    list.add(""+item);
                }
            }
        }catch(JSONException ex){
            LogUtils.e(ex);
        }finally{
            return list;
        }
    }

    private enum JsonDecodeType{
        Normal,Object,Array,Null
    }
    //0:object,1:array,2:list,3:map
    private static JsonDecodeType getDecodingType(Object bean){
        JsonDecodeType type = JsonDecodeType.Null;
        if(bean == null){
        }else if(bean instanceof JSONArray){
            type= JsonDecodeType.Array;
        }else if(bean instanceof JSONObject){
            type= JsonDecodeType.Object;
        }else if(decodingNormal(bean)){
            type= JsonDecodeType.Normal;
        }
        return type;
    }

    private static boolean decodingNormal(Object item){
        if(item instanceof String || item instanceof Boolean || item instanceof Integer ||
                item instanceof Long || item instanceof Double || item instanceof Float ||
                item instanceof Number || item instanceof Short || 	item instanceof Character || item instanceof Byte){
            return true;
        }else{
            return false;
        }
    }

    private static JSONObject getJsonObject(String json) throws JSONException {
        return new JSONObject(json);
    }

    private static JSONArray getJsonArray(String json) throws JSONException {
        return new JSONArray(json);
    }

    public static String getString(String json, String key) throws JSONException {
        JSONObject jsonObject=getJsonObject(json);
        return jsonObject.getString(key);
    }

    public static int getInt(String json, String key) throws JSONException {
        JSONObject jsonObject=getJsonObject(json);
        return jsonObject.getInt(key);
    }
    public static long getLong(String json, String key) throws JSONException {
        JSONObject jsonObject=getJsonObject(json);
        return jsonObject.getLong(key);
    }

    public static double getDouble(String json, String key) throws JSONException {
        JSONObject jsonObject=getJsonObject(json);
        return jsonObject.getDouble(key);
    }

    public static boolean getBoolean(String json, String key) throws JSONException {
        JSONObject jsonObject=getJsonObject(json);
        return jsonObject.getBoolean(key);
    }
    //==============================JSON生成=====================================
    //parser the object,map,list,array
    public static String encode(Object bean){
        String result="";
        switch (getEncodingType(bean)){
            case Normal:
                result = bean.toString();//encodingArray(new Object[]{bean}).toString();//single var to list
                break;
            case Object:
                result = encodingObject(bean).toString();
                break;
            case Array:
                result = encodingArray(bean).toString();
                break;
            case List:
                result = encodingList(bean).toString();
                break;
            case Map:
                result =encodingMap(bean).toString();
                break;
            default:
        }
        return result;
    }

    //获取Json对象
    public static JSONObject getJSONObject(Object bean){
        JSONObject result=new JSONObject();
        switch (getEncodingType(bean)){
            case Object:result=encodingObject(bean);
                break;
            case Map:
                result =encodingMap(bean);
                break;
            default:
        }
        return result;
    }

    //获取Json对象
    public static JSONArray getJSONArray(Object bean){
        JSONArray result=new JSONArray();
        switch (getEncodingType(bean)){
            case Array:
                result=encodingArray(bean);
                break;
            case List:result=encodingList(bean);
                break;
            default:
        }
        return result;
    }

    private enum JsonEncodeType{
        Normal,Object,Array,List,Map
    }

    //0:object,1:array,2:list,3:map
    private static JsonEncodeType getEncodingType(Object bean){
        JsonEncodeType type= JsonEncodeType.Object;
        if(bean == null){
            type= JsonEncodeType.Normal;
        }else if(bean instanceof JSONArray){
            type= JsonEncodeType.Array;
        }else if(encodingNormal(bean)){
            type= JsonEncodeType.Normal;
        }else if(bean instanceof List){
            type= JsonEncodeType.List;
        }else if(bean instanceof Map){
            type= JsonEncodeType.Map;
        }else if(bean.getClass().isArray()){
            type= JsonEncodeType.Array;
        }
        return type;
    }

    private static boolean encodingNormal(Object item){
        if(item instanceof String || item instanceof Boolean || item instanceof Integer ||
                item instanceof Long || item instanceof Double || item instanceof Float ||
                item instanceof Number || item instanceof Short || item instanceof Enum ||
                item instanceof Character || item instanceof Byte){
            return true;
        }else {
            return false;
        }
    }
    // create String array data:[{"a":"str0"},{"b":"str1"}]
    // Object such as:int,long,float,double,number,string,boolean,not any other
    private static JSONArray encodingList(Object bean) {
        JSONArray jsonArray = null;
        try {
            List<Object> list = (List<Object>) bean;
            jsonArray = new JSONArray();
            final int length=list.size();
            for (int i = 0; i < length; i++) {
                Object item=list.get(i);
                JsonEncodeType type=getEncodingType(item);
                //LogUtil.e("json", "name=" + i+",item="+item);
                if(JsonEncodeType.Normal == type){
                    jsonArray.put(i,item);
                }else if(JsonEncodeType.List == type){
                    jsonArray.put(i, encodingList(item));
                }else if(JsonEncodeType.Array == type){
                    jsonArray.put(i, encodingArray(item));
                }else if(JsonEncodeType.Map == type){
                    jsonArray.put(i, encodingMap(item));
                }else {
                    jsonArray.put(i, encodingObject(item));
                }
            }
        }catch (JSONException ex){
            LogUtils.e(ex);
        } finally {
            return jsonArray;
        }
    }

    private static JSONArray encodingArray(Object array){
        JSONArray jsonArray=new JSONArray();
        if(!array.getClass().isArray()) return jsonArray;
        final int length = Array.getLength(array);
        for(int i=0;i<length;i++){
            try {
                Object item = Array.get(array,i);
                JsonEncodeType type=getEncodingType(item);
                //LogUtil.e("json","name="+i+",value="+item+",array="+array.toString());
                if(JsonEncodeType.Normal == type){
                    jsonArray.put(i,item);
                }else if(JsonEncodeType.List == type){
                    jsonArray.put(i, encodingList(item));
                }else if(JsonEncodeType.Array == type){
                    jsonArray.put(i, encodingArray(item));
                }else if(JsonEncodeType.Map == type){
                    jsonArray.put(i, encodingMap(item));
                }else {
                    jsonArray.put(i,encodingObject(item));
                }
            }catch (JSONException ex){
                LogUtils.e(ex);
            }
        }
        return jsonArray;
    }

    private static JSONObject encodingMap(Object bean){
        JSONObject jsonObject = new JSONObject();
        Map<String,Object> map = (Map)bean;
        try {
            for (String key : map.keySet()) {
                Object item = map.get(key);
                JsonEncodeType type=getEncodingType(item);
                //LogUtil.e("json","name="+key+",type="+type);
                if(JsonEncodeType.Normal == type) {
                    jsonObject.put(key, item);
                }else if(JsonEncodeType.List == type){
                    jsonObject.put(key, encodingList(item));
                }else if(JsonEncodeType.Array == type){
                    jsonObject.put(key, encodingArray(item));
                }else if(JsonEncodeType.Map == type){
                    jsonObject.put(key, encodingMap(item));
                }else {
                    jsonObject.put(key, encodingObject(item));
                }
            }
        }catch (JSONException e) {
            LogUtils.e(e);
        }
        return jsonObject;
    }

    private static JSONObject encodingObject(Object bean){
        JSONObject jsonObject = new JSONObject();
        if(bean==null) return jsonObject;
        Field[] fs = bean.getClass().getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            Field f = fs[i];
            f.setAccessible(true);
            String name=f.getName();
            //LogUtil.e("json","item="+(i+1)+"name="+name+",type="+f.getType().toString()+",length="+fs.length);
            try {
                Object item=f.get(bean);
                JsonEncodeType type = getEncodingType(item);
                if(encodingNormal(item)){
                    jsonObject.put(name, item);
                }else if(JsonEncodeType.List == type){
                    jsonObject.put(name, encodingList(item));
                }else if(JsonEncodeType.Array == type){
                    jsonObject.put(name, encodingArray(item));
                }else if(JsonEncodeType.Map == type){
                    jsonObject.put(name, encodingMap(item));
                }else{
                    jsonObject.put(name, encodingObject(item));
                }
            }catch (IllegalArgumentException e) {
                LogUtils.e(e);
            }catch (IllegalAccessException e) {
                LogUtils.e(e);
            }catch (JSONException e) {
                LogUtils.e(e);
            }
        }
        return jsonObject;
    }
}
