package com.hjq.gson.factory.element;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.google.gson.internal.bind.SerializationDelegatingTypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2020/12/08
 *    desc   : Object 解析适配器，参考：{@link com.google.gson.internal.bind.TypeAdapterRuntimeTypeWrapper}
 */
public class TypeAdapterRuntimeTypeWrapper<T> extends TypeAdapter<T> {

    private final Gson mGson;
    private final TypeAdapter<T> mDelegate;
    private final Type mType;

    public TypeAdapterRuntimeTypeWrapper(Gson gson, TypeAdapter<T> delegate, Type type) {
        mGson = gson;
        mDelegate = delegate;
        mType = type;
    }

    @Override
    public T read(JsonReader in) throws IOException {
        return mDelegate.read(in);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void write(JsonWriter out, T value) throws IOException {
        // Order of preference for choosing type adapters
        // First preference: a type adapter registered for the runtime type
        // Second preference: a type adapter registered for the declared type
        // Third preference: reflective type adapter for the runtime type (if it is a sub class of the declared type)
        // Fourth preference: reflective type adapter for the declared type

        TypeAdapter chosen;
        Type runtimeType = getRuntimeTypeIfMoreSpecific(mType, value);
        if (runtimeType != mType) {
            TypeAdapter runtimeTypeAdapter = mGson.getAdapter(TypeToken.get(runtimeType));
            if (!(runtimeTypeAdapter instanceof ReflectiveTypeAdapterFactory.Adapter)) {
                // The user registered a type adapter for the runtime type, so we will use that
                chosen = runtimeTypeAdapter;
            } else if (!isReflective(mDelegate)) {
                // The user registered a type adapter for Base class, so we prefer it over the
                // reflective type adapter for the runtime type
                chosen = mDelegate;
            } else {
                // Use the type adapter for runtime type
                chosen = runtimeTypeAdapter;
            }
        } else {
            chosen = mDelegate;
        }
        chosen.write(out, value);
    }

    /**
     * Returns whether the type adapter uses reflection.
     *
     * @param typeAdapter the type adapter to check.
     */
    private static boolean isReflective(TypeAdapter<?> typeAdapter) {
        // Run this in loop in case multiple delegating adapters are nested
        while (typeAdapter instanceof SerializationDelegatingTypeAdapter) {
            TypeAdapter<?> delegate = ((SerializationDelegatingTypeAdapter<?>) typeAdapter).getSerializationDelegate();
            // Break if adapter does not delegate serialization
            if (delegate == typeAdapter) {
                break;
            }
            typeAdapter = delegate;
        }

        return typeAdapter instanceof ReflectiveTypeAdapterFactory.Adapter;
    }

    /**
     * Finds a compatible runtime type if it is more specific
     */
    private static Type getRuntimeTypeIfMoreSpecific(Type type, Object value) {
        if (value != null && (type instanceof Class<?> || type instanceof TypeVariable<?>)) {
            type = value.getClass();
        }
        return type;
    }
}