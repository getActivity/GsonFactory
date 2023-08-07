package com.hjq.gson.factory.constructor;

import com.google.gson.internal.ObjectConstructor;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2023/08/01
 *    desc   : Set 创建器
 */
public final class SetConstructor implements ObjectConstructor<Set<?>> {

    private static final SetConstructor INSTANCE = new SetConstructor();

    @SuppressWarnings("unchecked")
    public static  <T extends ObjectConstructor<?>> T getInstance() {
        return (T) INSTANCE;
    }

    @Override
    public Set<?> construct() {
        return new LinkedHashSet<>();
    }
}