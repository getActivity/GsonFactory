package com.hjq.gson.factory;

import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonToken;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2021/02/02
 *    desc   : Json 解析异常监听器
 */
public interface JsonCallback {

    /**
     * 类型解析异常
     *
     * @param typeToken             类型 Token
     * @param fieldName             字段名称
     * @param jsonToken             后台给定的类型
     */
    void onTypeException(TypeToken<?> typeToken, String fieldName, JsonToken jsonToken);
}