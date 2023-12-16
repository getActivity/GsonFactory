package com.hjq.gson.factory.constructor;

import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.UnsafeAllocator;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2023/08/01
 *    desc   : 反射（安全）创建器
 */
public final class ReflectSafeCreatorConstructor<T> implements ObjectConstructor<T> {

    private final ObjectConstructor<T> mKotlinDataClassDefaultValueConstructor;

    private final Class<? super T> mRawType;

    public ReflectSafeCreatorConstructor(Class<? super T> rawType) {
        mRawType = rawType;
        mKotlinDataClassDefaultValueConstructor = new KotlinDataClassDefaultValueConstructor<>(rawType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T construct() {
        T instance = mKotlinDataClassDefaultValueConstructor.construct();

        if (instance != null) {
            return instance;
        }

        try {
            instance = (T) UnsafeAllocator.INSTANCE.newInstance(mRawType);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException(("Unable to create instance of " + mRawType + ". "
                + "Registering an InstanceCreator or a TypeAdapter for this type, or adding a no-args "
                + "constructor may fix this problem."), e);
        }
    }
}