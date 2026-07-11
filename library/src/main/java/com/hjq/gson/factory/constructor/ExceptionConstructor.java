package com.hjq.gson.factory.constructor;

import androidx.annotation.NonNull;
import com.google.gson.JsonIOException;
import com.google.gson.internal.ObjectConstructor;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2023/08/01
 *    desc   : 异常的构造器
 */
public final class ExceptionConstructor<T> implements ObjectConstructor<T> {

    @NonNull
    private final String mExceptionMessage;

    public ExceptionConstructor(@NonNull String exceptionMessage) {
        mExceptionMessage = exceptionMessage;
    }

    @NonNull
    @Override
    public T construct() {
        throw new JsonIOException(mExceptionMessage);
    }
}