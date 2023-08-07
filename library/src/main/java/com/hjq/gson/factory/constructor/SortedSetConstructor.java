package com.hjq.gson.factory.constructor;

import com.google.gson.internal.ObjectConstructor;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2023/08/01
 *    desc   : SortedSet 创建器
 */
public final class SortedSetConstructor implements ObjectConstructor<SortedSet<?>> {

    private static final SortedSetConstructor INSTANCE = new SortedSetConstructor();

    @SuppressWarnings("unchecked")
    public static  <T extends ObjectConstructor<?>> T getInstance() {
        return (T) INSTANCE;
    }

    @Override
    public SortedSet<?> construct() {
        return new TreeSet<>();
    }
}