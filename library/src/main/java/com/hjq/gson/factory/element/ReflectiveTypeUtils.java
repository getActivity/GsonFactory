package com.hjq.gson.factory.element;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.hjq.gson.factory.constructor.MainConstructor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2020/12/08
 *    desc   : 反射工具类
 */
public class ReflectiveTypeUtils {

    private final static ArrayList<Class<?>> TYPE_TOKENS = new ArrayList<>();

    static {
        // 添加 Gson 已适配的类型
        TYPE_TOKENS.add(String.class);
        TYPE_TOKENS.add(Integer.class);
        TYPE_TOKENS.add(Boolean.class);
        TYPE_TOKENS.add(Byte.class);
        TYPE_TOKENS.add(Short.class);
        TYPE_TOKENS.add(Long.class);
        TYPE_TOKENS.add(Double.class);
        TYPE_TOKENS.add(Float.class);
        TYPE_TOKENS.add(Number.class);
        TYPE_TOKENS.add(AtomicInteger.class);
        TYPE_TOKENS.add(AtomicBoolean.class);
        TYPE_TOKENS.add(AtomicLong.class);
        TYPE_TOKENS.add(AtomicLongArray.class);
        TYPE_TOKENS.add(AtomicIntegerArray.class);
        TYPE_TOKENS.add(Character.class);
        TYPE_TOKENS.add(StringBuilder.class);
        TYPE_TOKENS.add(StringBuffer.class);
        TYPE_TOKENS.add(BigDecimal.class);
        TYPE_TOKENS.add(BigInteger.class);
        TYPE_TOKENS.add(URL.class);
        TYPE_TOKENS.add(URI.class);
        TYPE_TOKENS.add(UUID.class);
        TYPE_TOKENS.add(Currency.class);
        TYPE_TOKENS.add(Locale.class);
        TYPE_TOKENS.add(InetAddress.class);
        TYPE_TOKENS.add(BitSet.class);
        TYPE_TOKENS.add(Date.class);
        TYPE_TOKENS.add(GregorianCalendar.class);
        TYPE_TOKENS.add(Calendar.class);
        TYPE_TOKENS.add(Time.class);
        TYPE_TOKENS.add(java.sql.Date.class);
        TYPE_TOKENS.add(Timestamp.class);
        TYPE_TOKENS.add(Class.class);
    }

    public static boolean containsClass(Class<?> clazz) {
        return TYPE_TOKENS.contains(clazz);
    }

    public static ReflectiveFieldBound createBoundField(final Gson gson, final MainConstructor constructor, final Field field, final String fieldName,
                                                        final TypeToken<?> fieldType, boolean serialize, boolean deserialize) {

        return new ReflectiveFieldBound(fieldName, serialize, deserialize) {

            final TypeAdapter<?> typeAdapter = getFieldAdapter(gson, constructor, field, fieldType, fieldName);

            @SuppressWarnings({"unchecked", "rawtypes"})
            @Override
            public void write(JsonWriter writer, Object value) throws IOException, IllegalAccessException {
                Object fieldValue = field.get(value);
                TypeAdapter typeWrapper = new TypeAdapterRuntimeTypeWrapper(gson, typeAdapter, fieldType.getType());
                typeWrapper.write(writer, fieldValue);
            }

            @Override
            public void read(JsonReader reader, Object value) throws IOException, IllegalAccessException {
                Object fieldValue = typeAdapter.read(reader);
                if (fieldValue == null) {
                    return;
                }
                // 如果不为空，则直接赋值，之所以这样写的原因是
                // 当后台给某个字段赋值 null，例如 { "age" : null } 这种，也会走到这里来
                // 这样就会导致一个问题，当字段有一个默认值了，后台返回 null 会把默认值给覆盖掉
                // 如果是在 Kotlin 上面使用，问题会更加严重，明明在字段上面定义了默认值，并且声明了字段不为空
                // 如果后台不返回还好，就不会走到这里来，啥事没有，但是如果后台硬是返回了 null
                // Gson 再反射设置进去，这个时候外层的人一旦使用这个字段，就很可能会触发 NullPointerException
                field.set(value, fieldValue);
            }

            @Override
            public boolean writeField(Object value) throws IOException, IllegalAccessException {
                if (!isSerialized()) {
                    return false;
                }

                Object fieldValue = field.get(value);
                return fieldValue != value;
            }
        };
    }

    public static TypeAdapter<?> getFieldAdapter(Gson gson, MainConstructor constructor, Field field, TypeToken<?> fieldType, String fieldName) {
        TypeAdapter<?> adapter = null;
        JsonAdapter annotation = field.getAnnotation(JsonAdapter.class);
        if (annotation != null) {
            adapter = getTypeAdapter(constructor, gson, fieldType, annotation);
        }
        if (adapter == null) {
            adapter = gson.getAdapter(fieldType);
        }
        if (adapter instanceof CollectionTypeAdapter) {
            ((CollectionTypeAdapter<?>) adapter).setReflectiveType(TypeToken.get(field.getDeclaringClass()), fieldName);
        }
        if (adapter instanceof ReflectiveTypeAdapter) {
            ((ReflectiveTypeAdapter<?>) adapter).setReflectiveType(TypeToken.get(field.getDeclaringClass()), fieldName);
        }
        if (adapter instanceof MapTypeAdapter) {
            ((MapTypeAdapter<?, ?>) adapter).setReflectiveType(TypeToken.get(field.getDeclaringClass()), fieldName);
        }
        return adapter;
    }

    public static TypeAdapter<?> getTypeAdapter(MainConstructor constructor,
                                                Gson gson,
                                                TypeToken<?> fieldType,
                                                JsonAdapter annotation) {
        Class<?> value = annotation.value();
        TypeAdapter<?> typeAdapter;

        if (TypeAdapter.class.isAssignableFrom(value)) {
            Class<TypeAdapter<?>> typeAdapterClass = (Class<TypeAdapter<?>>) value;
            typeAdapter = constructor.get(TypeToken.get(typeAdapterClass)).construct();
        } else if (TypeAdapterFactory.class.isAssignableFrom(value)) {
            Class<TypeAdapterFactory> typeAdapterFactory = (Class<TypeAdapterFactory>) value;
            typeAdapter = constructor.get(TypeToken.get(typeAdapterFactory))
                    .construct()
                    .create(gson, fieldType);
        } else {
            throw new IllegalArgumentException(
                    "@JsonAdapter value must be TypeAdapter or TypeAdapterFactory reference.");
        }

        if (typeAdapter != null) {
            typeAdapter = typeAdapter.nullSafe();
        }

        return typeAdapter;
    }

    public static List<String> getFieldNames(FieldNamingStrategy fieldNamingPolicy, Field field) {
        SerializedName annotation = field.getAnnotation(SerializedName.class);
        if (annotation == null) {
            String name = fieldNamingPolicy.translateName(field);
            return Collections.singletonList(name);
        }

        String serializedName = annotation.value();
        String[] alternates = annotation.alternate();
        if (alternates.length == 0) {
            return Collections.singletonList(serializedName);
        }

        List<String> fieldNames = new ArrayList<>(alternates.length + 1);
        fieldNames.add(serializedName);
        Collections.addAll(fieldNames, alternates);
        return fieldNames;
    }
}