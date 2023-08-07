package com.hjq.gson.factory.constructor;

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

    private static final QueueConstructor INSTANCE = new QueueConstructor();

    @SuppressWarnings("unchecked")
    public static  <T extends ObjectConstructor<?>> T getInstance() {
        return (T) INSTANCE;
    }

    @Override
    public Queue<?> construct() {
        return new ArrayDeque<>();
    }
}