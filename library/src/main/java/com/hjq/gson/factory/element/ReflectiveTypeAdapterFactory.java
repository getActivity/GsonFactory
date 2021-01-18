package com.hjq.gson.factory.element;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2020/12/08
 *    desc   : JsonObject 解析适配器，参考：{@link com.google.gson.internal.bind.ReflectiveTypeAdapterFactory}
 */
public class ReflectiveTypeAdapterFactory implements TypeAdapterFactory {

    private final ConstructorConstructor constructorConstructor;
    private final FieldNamingStrategy fieldNamingPolicy;
    private final Excluder excluder;

    public ReflectiveTypeAdapterFactory(ConstructorConstructor constructorConstructor,
                                        FieldNamingStrategy fieldNamingPolicy, Excluder excluder) {
        this.constructorConstructor = constructorConstructor;
        this.fieldNamingPolicy = fieldNamingPolicy;
        this.excluder = excluder;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, final TypeToken<T> type) {
        Class<? super T> raw = type.getRawType();

        // 判断是否包含这种类型
        if (ReflectiveTypeTools.containsClass(raw)) {
            return null;
        }
        // 判断是否是数组
        if (type.getType() instanceof GenericArrayType ||
                type.getType() instanceof Class &&
                 ((Class<?>) type.getType()).isArray()) {
            return null;
        }
        // 如果是基本数据类型
        if (!Object.class.isAssignableFrom(raw)) {
            return null;
        }
        // 如果是 List 类型
        if (Collection.class.isAssignableFrom(raw)) {
            return null;
        }
        // 如果是 Map 数组
        if (Map.class.isAssignableFrom(raw)) {
            return null;
        }
        // 判断是否有自定义解析注解
        JsonAdapter annotation = raw.getAnnotation(JsonAdapter.class);
        if (annotation != null) {
            return null;
        }
        // 如果是枚举类型
        if (Enum.class.isAssignableFrom(raw) && raw != Enum.class) {
            return null;
        }
        return new ReflectiveTypeAdapter<>(constructorConstructor.get(type), getBoundFields(gson, type, raw));
    }

    private Map<String, BoundField> getBoundFields(Gson context, TypeToken<?> type, Class<?> raw) {
        Map<String, BoundField> result = new LinkedHashMap<>();
        if (raw.isInterface()) {
            return result;
        }

        Type declaredType = type.getType();
        while (raw != Object.class) {
            Field[] fields = raw.getDeclaredFields();
            for (Field field : fields) {
                boolean serialize = excludeField(field, true);
                boolean deserialize = excludeField(field, false);
                if (!serialize && !deserialize) {
                    continue;
                }
                field.setAccessible(true);
                Type fieldType = $Gson$Types.resolve(type.getType(), raw, field.getGenericType());
                List<String> fieldNames = getFieldNames(field);
                BoundField previous = null;
                for (int i = 0; i < fieldNames.size(); ++i) {
                    String name = fieldNames.get(i);
                    if (i != 0) {
                        // only serialize the default name
                        serialize = false;
                    }
                    BoundField boundField = ReflectiveTypeTools.createBoundField(context, constructorConstructor, field, name,
                            TypeToken.get(fieldType), serialize, deserialize);
                    BoundField replaced = result.put(name, boundField);
                    if (previous == null) {
                        previous = replaced;
                    }
                }
                if (previous != null) {
                    throw new IllegalArgumentException(declaredType
                            + " declares multiple JSON fields named " + previous.getName());
                }
            }
            type = TypeToken.get($Gson$Types.resolve(type.getType(), raw, raw.getGenericSuperclass()));
            raw = type.getRawType();
        }
        return result;
    }

    private boolean excludeField(Field f, boolean serialize) {
        return excludeField(f, serialize, excluder);
    }

    private static boolean excludeField(Field f, boolean serialize, Excluder excluder) {
        return !excluder.excludeClass(f.getType(), serialize) && !excluder.excludeField(f, serialize);
    }

    private List<String> getFieldNames(Field f) {
        return ReflectiveTypeTools.getFieldName(fieldNamingPolicy, f);
    }
}