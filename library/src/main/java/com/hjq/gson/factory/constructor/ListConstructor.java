package com.hjq.gson.factory.constructor;

import com.google.gson.internal.ObjectConstructor;
import java.util.ArrayList;
import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2023/08/01
 *    desc   : List 创建器
 */
public final class ListConstructor implements ObjectConstructor<List<?>> {

    private static final ListConstructor INSTANCE = new ListConstructor();

    @SuppressWarnings("unchecked")
    public static  <T extends ObjectConstructor<?>> T getInstance() {
        return (T) INSTANCE;
    }

    @Override
    public List<?> construct() {
        return new ArrayList<>();
    }
}