package com.hjq.gson.factory.constructor;

import androidx.annotation.NonNull;
import com.google.gson.InstanceCreator;
import com.google.gson.internal.ObjectConstructor;
import java.lang.reflect.Type;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2023/08/01
 *    desc   : 自定义的创建器
 */
public final class InstanceCreatorConstructor<T> implements ObjectConstructor<T> {

    @NonNull
    private final InstanceCreator<T> mInstanceCreator;
    @NonNull
    private final Type mType;

    public InstanceCreatorConstructor(@NonNull InstanceCreator<T> instanceCreator, @NonNull Type type) {
        mInstanceCreator = instanceCreator;
        mType = type;
    }

    @NonNull
    @Override
    public T construct() {
        return mInstanceCreator.createInstance(mType);
    }
}