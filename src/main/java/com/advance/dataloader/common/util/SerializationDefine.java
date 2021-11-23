package com.advance.dataloader.common.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.assertj.core.util.Preconditions;
import org.assertj.core.util.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Advance
 * @date 2021年10月14日 14:47
 * @since V1.0.0
 */
public class SerializationDefine {
    public static String segSymbols = "##segSymbols##";//分割符号

    public static String Object2String(Object obj) {
        Preconditions.checkArgument(obj != null, "序列化对象为null");
        return JSONObject.toJSONString(obj);
    }

    public static <T> T String2Object(String str, Class<T> tClass) {
        return JSONObject.parseObject(str, tClass);
    }

    public static <T> String[] List2StringArray(List<T> objects) {
        if (objects == null || objects.size() == 0) {
            return new String[]{};
        }

        String[] strArray = new String[objects.size()];
        for (int i = 0; i < objects.size(); i++) {
            strArray[i] = Object2String(objects.get(i));
        }

        return strArray;
    }

    public static <T> List<T> StringArray2List(String[] strs, Class<T> tClass) {
        if (strs == null || strs.length == 0) {
            return new ArrayList<T>();
        }

        List<T> objList = new ArrayList<T>();
        for (int i = 0; i < strs.length; i++) {
            objList.add(String2Object(strs[i], tClass));
        }
        return objList;
    }

    public static <T> String List2Str(List<T> objects) {
        String[] strings = List2StringArray(objects);
        if (strings == null || strings.length == 0) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < strings.length; i++) {
            if (i == 0) {
                stringBuffer.append(strings[i]);
            } else {
                stringBuffer.append(segSymbols + strings[i]);
            }
        }
        return JSONArray.toJSONString(JSONArray.parse(stringBuffer.toString()));
    }

    public static String StrArr2Str(String[] strarr){
        StringBuilder sb = new StringBuilder();
        if (strarr != null && strarr.length > 0) {
            for (int i = 0; i < strarr.length; i++) {
                if (i < strarr.length - 1) {
                    sb.append(strarr[i] + ",");
                } else {
                    sb.append(strarr[i]);
                }
            }
        }
        return sb.toString();
    }

    public static <T> List<T> Str2List(String str, Class<T> tClass) {
        if (Strings.isNullOrEmpty(str)) {
            return new ArrayList<T>();
        }

        String[] strs = str.split(segSymbols);
        return StringArray2List(strs, tClass);
    }
}
