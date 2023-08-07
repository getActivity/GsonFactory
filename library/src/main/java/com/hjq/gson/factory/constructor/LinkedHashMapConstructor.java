package com.hjq.gson.factory.constructor;

import com.google.gson.internal.ObjectConstructor;
import java.util.LinkedHashMap;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2023/08/01
 *    desc   : LinkedHashMap 创建器
 */
public final class LinkedHashMapConstructor implements ObjectConstructor<LinkedHashMap<?, ?>> {

    private static final LinkedHashMapConstructor INSTANCE = new LinkedHashMapConstructor();

    @SuppressWarnings("unchecked")
    public static  <T extends ObjectConstructor<?>> T getInstance() {
        return (T) INSTANCE;
    }

    @Override
    public LinkedHashMap<?, ?> construct() {
        return new LinkedHashMap<>();
    }
}