package com.hjq.gson.factory.constructor;

import com.google.gson.internal.ObjectConstructor;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2023/08/01
 *    desc   : ConcurrentMap 创建器
 */
public final class ConcurrentMapConstructor implements ObjectConstructor<ConcurrentMap<?, ?>> {

    private static final ConcurrentMapConstructor INSTANCE = new ConcurrentMapConstructor();

    @SuppressWarnings("unchecked")
    public static  <T extends ObjectConstructor<?>> T getInstance() {
        return (T) INSTANCE;
    }

    @Override
    public ConcurrentMap<?, ?> construct() {
        return new ConcurrentHashMap<>();
    }
}