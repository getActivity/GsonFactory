package com.hjq.gson.factory.constructor;

import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.Primitives;
import java.lang.reflect.Constructor;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2023/08/01
 *    desc   : Kotlin Data Class 创建器，用于处理反射创建 data class 类导致默认值不生效的问题
 */
public final class KotlinDataClassDefaultValueConstructor<T> implements ObjectConstructor<T> {

    private static final String DEFAULT_CONSTRUCTOR_MARKER_CLASS_NAME = "kotlin.jvm.internal.DefaultConstructorMarker";

    private final Class<? super T> mRawType;

    public KotlinDataClassDefaultValueConstructor(Class<? super T> rawType) {
        mRawType = rawType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T construct() {
        T instance = null;
        try {
            Constructor<?>[] constructors = mRawType.getConstructors();
            for (Constructor<?> constructor : constructors) {
                Class<?>[] parameterTypes = constructor.getParameterTypes();

                int parameterLength = parameterTypes.length;

                // 如果参数小于 3 个，证明不是我们要的目标构造函数，就继续下一个循环
                // 如果最后一个参数是 DefaultConstructorMarker 类型，并且最后第二个参数是 int 类型
                if (parameterLength < 3 ||
                    (!DEFAULT_CONSTRUCTOR_MARKER_CLASS_NAME.equals(parameterTypes[parameterTypes.length - 1].getName()) &&
                    !int.class.isAssignableFrom(parameterTypes[parameterTypes.length - 2]))) {
                    continue;
                }

                Object[] parameterValue = new Object[parameterLength];
                // 这个参数是标记位，用于判断有没有设置默认值进去，会进行 & 位运算进行判断
                // 这里设置成 Integer.MAX_VALUE，Integer.MAX_VALUE & 大于 0 的值，都会等于那个值
                // 这样就能每次都能走到设置默认值那里
                // Github 地址：https://github.com/getActivity/GsonFactory/issues/27
                parameterValue[parameterLength - 2] = Integer.MAX_VALUE;
                // 这个参数 DefaultConstructorMarker 类型，用于标记构造函数是 Kotlin 语法自动生成的
                parameterValue[parameterLength - 1] = null;

                for (int i = 0; i < parameterTypes.length - 2; i++) {
                    Class<?> parameterType = parameterTypes[i];
                    parameterValue[i] = getTypeDefaultValue(parameterType);
                }

                instance = (T) constructor.newInstance(parameterValue);
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return instance;
    }

    private Object getTypeDefaultValue(Class<?> clazz) {
        if (Primitives.isWrapperType(clazz)) {
            // 如果是包装类，使用默认值为 null
            return null;
        }

        if (clazz.isPrimitive()) {
            // 如果是基本数据类型，使用对应的包装类的默认值
            if (clazz == byte.class) {
                return (byte) 0;
            } else if (clazz == short.class) {
                return (short) 0;
            } else if (clazz == int.class) {
                return 0;
            } else if (clazz == long.class) {
                return 0L;
            } else if (clazz == float.class) {
                return 0.0f;
            } else if (clazz == double.class) {
                return 0.0;
            } else if (clazz == char.class) {
                return '\u0000';
            } else if (clazz == boolean.class) {
                return false;
            }
        }
        return null;
    }
}