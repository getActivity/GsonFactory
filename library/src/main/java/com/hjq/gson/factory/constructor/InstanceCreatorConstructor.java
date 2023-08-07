package com.hjq.gson.factory.constructor;

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

    private final InstanceCreator<T> mInstanceCreator;
    private final Type mType;

    public InstanceCreatorConstructor(InstanceCreator<T> instanceCreator, Type type) {
        mInstanceCreator = instanceCreator;
        mType = type;
    }

    @Override
    public T construct() {
        return mInstanceCreator.createInstance(mType);
    }
}