package com.hjq.gson.factory.constructor;

import com.google.gson.JsonIOException;
import com.google.gson.internal.ObjectConstructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.EnumSet;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2023/08/01
 *    desc   : EnumSet 创建器
 */
public final class EnumSetConstructor<T> implements ObjectConstructor<T> {

    private final Type mType;

    public EnumSetConstructor(Type type) {
        mType = type;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T construct() {
        if (mType instanceof ParameterizedType) {
            Type elementType = ((ParameterizedType) mType).getActualTypeArguments()[0];
            if (elementType instanceof Class) {
                @SuppressWarnings({"rawtypes"})
                T set = (T) EnumSet.noneOf((Class)elementType);
                return set;
            } else {
                throw new JsonIOException("Invalid EnumSet type: " + mType);
            }
        } else {
            throw new JsonIOException("Invalid EnumSet type: " + mType.toString());
        }
    }
}