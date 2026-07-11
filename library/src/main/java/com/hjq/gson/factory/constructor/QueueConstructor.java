package com.hjq.gson.factory.constructor;

import androidx.annotation.NonNull;
import com.google.gson.internal.ObjectConstructor;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2023/08/01
 *    desc   : Queue 创建器
 */
public final class QueueConstructor implements ObjectConstructor<Queue<?>> {

    @NonNull
    private static final QueueConstructor INSTANCE = new QueueConstructor();

    @SuppressWarnings("unchecked")
    @NonNull
    public static  <T extends ObjectConstructor<?>> T getInstance() {
        return (T) INSTANCE;
    }

    @NonNull
    @Override
    public Queue<?> construct() {
        return new ArrayDeque<>();
    }
}