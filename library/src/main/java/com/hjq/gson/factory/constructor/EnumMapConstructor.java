package com.hjq.gson.factory.constructor;

import androidx.annotation.NonNull;
import com.google.gson.JsonIOException;
import com.google.gson.internal.ObjectConstructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.EnumMap;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2023/08/01
 *    desc   : EnumMap 创建器
 */
public final class EnumMapConstructor<T> implements ObjectConstructor<T> {

    @NonNull
    private final Type mType;

    public EnumMapConstructor(@NonNull Type type) {
        mType = type;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public T construct() {
        if (mType instanceof ParameterizedType) {
            Type elementType = ((ParameterizedType) mType).getActualTypeArguments()[0];
            if (elementType instanceof Class) {
                @SuppressWarnings({"unchecked", "rawtypes"})
                T map = (T) new EnumMap((Class) elementType);
                return map;
            } else {
                throw new JsonIOException("Invalid EnumMap type: " + mType);
            }
        } else {
            throw new JsonIOException("Invalid EnumMap type: " + mType.toString());
        }
    }
}