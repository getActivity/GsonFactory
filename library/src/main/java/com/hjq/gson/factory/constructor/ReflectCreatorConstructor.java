package com.hjq.gson.factory.constructor;

import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.reflect.ReflectionHelper;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2023/08/01
 *    desc   : 反射创建器
 */
public final class ReflectCreatorConstructor<T> implements ObjectConstructor<T> {

    private final KotlinDataClassDefaultValueConstructor<T> mKotlinDataClassDefaultValueConstructor;

    private final Constructor<? super T> mConstructor;

    public ReflectCreatorConstructor(Class<? super T> rawType, Constructor<? super T> constructor) {
        mConstructor = constructor;
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
            instance = (T) mConstructor.newInstance();
            return instance;
        }
        // Note: InstantiationException should be impossible because check at start of method made sure
        //   that class is not abstract
        catch (InstantiationException e) {
            throw new RuntimeException("Failed to invoke constructor '" + ReflectionHelper.constructorToString(
                mConstructor) + "'"
                + " with no args", e);
        } catch (InvocationTargetException e) {
            // don't wrap if cause is unchecked?
            // JsonParseException ?
            throw new RuntimeException("Failed to invoke constructor '" + ReflectionHelper.constructorToString(
                mConstructor) + "'"
                + " with no args", e.getCause());
        } catch (IllegalAccessException e) {
            throw ReflectionHelper.createExceptionForUnexpectedIllegalAccess(e);
        }
    }
}