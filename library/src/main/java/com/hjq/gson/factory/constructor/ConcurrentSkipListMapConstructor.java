package com.hjq.gson.factory.constructor;

import com.google.gson.internal.ObjectConstructor;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2023/08/01
 *    desc   : ConcurrentSkipListMap 创建器
 */
public final class ConcurrentSkipListMapConstructor implements ObjectConstructor<ConcurrentSkipListMap<?, ?>> {

    private static final ConcurrentSkipListMapConstructor INSTANCE = new ConcurrentSkipListMapConstructor();

    @SuppressWarnings("unchecked")
    public static  <T extends ObjectConstructor<?>> T getInstance() {
        return (T) INSTANCE;
    }

    @Override
    public ConcurrentSkipListMap<?, ?> construct() {
        return new ConcurrentSkipListMap<>();
    }
}