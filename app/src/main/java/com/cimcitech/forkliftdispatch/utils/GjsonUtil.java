package com.cimcitech.forkliftdispatch.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by cimcitech on 2017/3/9.
 */

public class GjsonUtil {

    public static <T> T getPerson(String jsonString, Class<T> cls) {
        T t = null;
        try {
            Gson gson = new Gson();
            t = gson.fromJson(jsonString, cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    public static List<Map<String, Object>> listKeyMaps(String jsonString) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            Gson gson = new Gson();
            list = gson.fromJson(jsonString,
                    new TypeToken<List<Map<String, Object>>>() {
                    }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Object getInstanceByJson(Class<?> cls, String json) {
        Object obj = null;
        Gson gson = new Gson();
        obj = gson.fromJson(json, cls);
        return obj;
    }


    public static <T> T parseJsonWithGson(String jsonData, Class<T> type) {
        Gson gson = new Gson();
        T result = gson.fromJson(jsonData, type);
        return result;
    }

    /**
     * @param json
     * @param cls
     * @return
     * @author
     */
    public static <T> List<T> jsonToList(String json, Class<T[]> cls) {
        Gson gson = new Gson();
        T[] array = gson.fromJson(json, cls);
        return Arrays.asList(array);
    }

    /**
     * @param json
     * @param cls
     * @return
     */
    public static <T> ArrayList<T> jsonToArrayList(String json, Class<T> cls) {
        Type type = new TypeToken<ArrayList<JsonObject>>() {
        }.getType();
        ArrayList<JsonObject> jsonObjects = new Gson().fromJson(json, type);

        ArrayList<T> arrayList = new ArrayList<T>();
        for (JsonObject jsonObject : jsonObjects) {
            arrayList.add(new Gson().fromJson(jsonObject, cls));
        }
        return arrayList;
    }

}
