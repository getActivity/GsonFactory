package com.hjq.gson.factory.element;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2020/12/08
 *    desc   : 字段信息存放，参考：{@link com.google.gson.internal.bind.ReflectiveTypeAdapterFactory.BoundField}
 */
public abstract class BoundField {

    /** 字段名称 */
    private final String name;
    /** 序列化标记 */
    private final boolean serialized;
    /** 反序列化标记 */
    private final boolean deserialized;

    public BoundField(String name, boolean serialized, boolean deserialized) {
        this.name = name;
        this.serialized = serialized;
        this.deserialized = deserialized;
    }

    public abstract boolean writeField(Object value) throws IOException, IllegalAccessException;

    public abstract void write(JsonWriter writer, Object value) throws IOException, IllegalAccessException;

    public abstract void read(JsonReader reader, Object value) throws IOException, IllegalAccessException;

    public String getName() {
        return name;
    }

    public boolean isDeserialized() {
        return deserialized;
    }

    public boolean isSerialized() {
        return serialized;
    }
}