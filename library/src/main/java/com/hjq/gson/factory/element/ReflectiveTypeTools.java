package com.hjq.gson.factory.element;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Primitives;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

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
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
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
 *    desc   :
 */
public class ReflectiveTypeTools {

    private final static ArrayList<Class> TYPE_TOKENS = new ArrayList<>();

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

    public static boolean containsClass(Class clazz) {
        return TYPE_TOKENS.contains(clazz);
    }

    public static BoundField createBoundField(final Gson context, final ConstructorConstructor constructorConstructor,
                                              final Field field,
                                              final String name,
                                              final TypeToken<?> fieldType,
                                              boolean serialize,
                                              boolean deserialize) {

        final boolean isPrimitive = Primitives.isPrimitive(fieldType.getRawType());

        return new BoundField(name, serialize, deserialize) {

            final TypeAdapter<?> typeAdapter = getFieldAdapter(context, constructorConstructor, field, fieldType);

            @SuppressWarnings({"unchecked", "rawtypes"})
            @Override
            public void write(JsonWriter writer, Object value)
                    throws IOException, IllegalAccessException {
                Object fieldValue = field.get(value);
                TypeAdapter t = new TypeAdapterRuntimeTypeWrapper(context, this.typeAdapter, fieldType.getType());
                t.write(writer, fieldValue);
            }

            @Override
            public void read(JsonReader reader, Object value)
                    throws IOException, IllegalAccessException {
                Object fieldValue = typeAdapter.read(reader);
                if (fieldValue != null || !isPrimitive) {
                    field.set(value, fieldValue);
                }
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

    public static TypeAdapter<?> getFieldAdapter(Gson gson, ConstructorConstructor constructorConstructor, Field field, TypeToken<?> fieldType) {
        JsonAdapter annotation = field.getAnnotation(JsonAdapter.class);
        if (annotation != null) {
            TypeAdapter<?> adapter = getTypeAdapter(constructorConstructor, gson, fieldType, annotation);
            if (adapter != null) {
                return adapter;
            }
        }
        return gson.getAdapter(fieldType);
    }

    public static TypeAdapter<?> getTypeAdapter(ConstructorConstructor constructorConstructor,
                                                Gson gson,
                                                TypeToken<?> fieldType,
                                                JsonAdapter annotation) {
        Class<?> value = annotation.value();
        TypeAdapter<?> typeAdapter;

        if (TypeAdapter.class.isAssignableFrom(value)) {
            Class<TypeAdapter<?>> typeAdapterClass = (Class<TypeAdapter<?>>) value;
            typeAdapter = constructorConstructor.get(TypeToken.get(typeAdapterClass)).construct();
        } else if (TypeAdapterFactory.class.isAssignableFrom(value)) {
            Class<TypeAdapterFactory> typeAdapterFactory = (Class<TypeAdapterFactory>) value;
            typeAdapter = constructorConstructor.get(TypeToken.get(typeAdapterFactory))
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

    public static List<String> getFieldName(FieldNamingStrategy fieldNamingPolicy, Field f) {
        SerializedName serializedName = f.getAnnotation(SerializedName.class);
        List<String> fieldNames = new LinkedList<String>();
        if (serializedName == null) {
            fieldNames.add(fieldNamingPolicy.translateName(f));
        } else {
            fieldNames.add(serializedName.value());
            for (String alternate : serializedName.alternate()) {
                fieldNames.add(alternate);
            }
        }
        return fieldNames;
    }
}