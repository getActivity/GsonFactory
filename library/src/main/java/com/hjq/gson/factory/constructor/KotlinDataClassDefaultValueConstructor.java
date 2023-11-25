package com.hjq.gson.factory.constructor;

import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.Primitives;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

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
                if (parameterLength < 3) {
                    continue;
                }

                // 如果最后一个参数必须是 DefaultConstructorMarker 类型
                if (!DEFAULT_CONSTRUCTOR_MARKER_CLASS_NAME.equals(parameterTypes[parameterTypes.length - 1].getName())) {
                    continue;
                }

                Field[] fields = mRawType.getDeclaredFields();

                // kotlin data class 构造函数中的参数一定比字段数量要多
                // 因为里面的构造函数中的参数，除了类原有的字段外
                // 还有 N 个 int 标记位和 DefaultConstructorMarker 类型对象
                if (parameterTypes.length <= fields.length) {
                    continue;
                }

                Object[] parameterValue = new Object[parameterLength];
                // 这个参数 DefaultConstructorMarker 类型，用于标记构造函数是 Kotlin 语法自动生成的
                parameterValue[parameterLength - 1] = null;

                for (int i = fields.length; i < parameterTypes.length - 1; i++) {
                    if (!int.class.isAssignableFrom(parameterTypes[i])) {
                        continue;
                    }

                    // 这个参数是标记位，用于判断有没有设置默认值进去，会进行 & 位运算进行判断
                    // 这里设置成 Integer.MAX_VALUE，Integer.MAX_VALUE & 大于 0 的值，都会等于那个值
                    // 这样就能每次都能走到设置默认值那里，需要注意的是，这样的标记位在参数多的时候可能不止一个
                    // 一个 int 标记位最多只能用于 32 个参数，如果超出了的话，就会新增多一个 int 标记位
                    // 1. (i & 1) != 0 ? 0 : i
                    // 2. (i & 2) != 0 ? 0 : i
                    // 3. (i & 4) != 0 ? 0 : i
                    // 4. (i & 8) != 0 ? 0 : i
                    // 5. (i & 16) != 0 ? 0 : i
                    // 6. (i & 32) != 0 ? 0 : i
                    // 7. (i & 64) != 0 ? 0 : i
                    // 8. (i & 128) != 0 ? 0 : i
                    // 9. (i & 256) != 0 ? 0 : i
                    // 10. (i & 512) != 0 ? 0 : i
                    // 11. (i & 1024) != 0 ? 0 : i
                    // 12. (i & 2048) != 0 ? 0 : i
                    // 13. (i & 4096) != 0 ? 0 : i
                    // 14. (i & 8192) != 0 ? 0 : i
                    // 15. (i & 16384) != 0 ? 0 : i
                    // 16. (i & 32768) != 0 ? 0 : i
                    // 17. (i & 65536) != 0 ? 0 : i
                    // 18. (i & 131072) != 0 ? 0 : i
                    // 19. (i & 262144) != 0 ? 0 : i
                    // 20. (i & 524288) != 0 ? 0 : i
                    // 21. (i & 1048576) != 0 ? 0 : i
                    // 22. (i & 2097152) != 0 ? 0 : i
                    // 23. (i & 4194304) != 0 ? 0 : i
                    // 24. (i & 8388608) != 0 ? 0 : i
                    // 25. (i & 16777216) != 0 ? 0 : i
                    // 26. (i & 33554432) != 0 ? 0 : i
                    // 27. (i & 67108864) != 0 ? 0 : i
                    // 28. (i & 134217728) != 0 ? 0 : i
                    // 29. (i & 268435456) != 0 ? 0 : i
                    // 30. (i & 536870912) != 0 ? 0 : i
                    // 31. (i & 1073741824) != 0 ? 0 : i
                    // 32. (i & -2147483648) != 0 ? 0 : i（注意：-2147483648 = Integer.MIN_VALUE）
                    // Github 地址：https://github.com/getActivity/GsonFactory/issues/27
                    parameterValue[i] = Integer.MAX_VALUE;
                }

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