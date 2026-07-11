package com.hjq.gson.factory.constructor;

import androidx.annotation.NonNull;
import com.google.gson.internal.ObjectConstructor;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2023/08/01
 *    desc   : SortedMap 创建器
 */
public final class SortedMapConstructor implements ObjectConstructor<SortedMap<?, ?>> {

    @NonNull
    private static final SortedMapConstructor INSTANCE = new SortedMapConstructor();

    @SuppressWarnings("unchecked")
    @NonNull
    public static  <T extends ObjectConstructor<?>> T getInstance() {
        return (T) INSTANCE;
    }

    @NonNull
    @Override
    public SortedMap<?, ?> construct() {
        return new TreeMap<>();
    }
}