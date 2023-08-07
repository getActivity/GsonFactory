package com.hjq.gson.factory.constructor;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.internal.ObjectConstructor;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2023/08/01
 *    desc   : LinkedTreeMap 创建器
 */
public final class LinkedTreeMapConstructor implements ObjectConstructor<LinkedTreeMap<?, ?>> {

    private static final LinkedTreeMapConstructor INSTANCE = new LinkedTreeMapConstructor();

    @SuppressWarnings("unchecked")
    public static  <T extends ObjectConstructor<?>> T getInstance() {
        return (T) INSTANCE;
    }

    @Override
    public LinkedTreeMap<?, ?> construct() {
        return new LinkedTreeMap<>();
    }
}