package com.hjq.gson.factory.other;

import androidx.annotation.NonNull;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2020/12/08
 *    desc   : 反射字段捆绑类，参考：{@link com.google.gson.internal.bind.ReflectiveTypeAdapterFactory.BoundField}
 */
public abstract class ReflectiveFieldBound {

    /** 字段名称 */
    @NonNull
    private final String mFieldName;
    /** 序列化标记 */
    private final boolean mSerialized;
    /** 反序列化标记 */
    private final boolean mDeserialized;

    public ReflectiveFieldBound(@NonNull String name, boolean serialized, boolean deserialized) {
        mFieldName = name;
        mSerialized = serialized;
        mDeserialized = deserialized;
    }

    @NonNull
    public String getFieldName() {
        return mFieldName;
    }

    public boolean isDeserialized() {
        return mDeserialized;
    }

    public boolean isSerialized() {
        return mSerialized;
    }

    public abstract void write(@NonNull JsonWriter writer, @NonNull Object value) throws IOException, IllegalAccessException;

    public abstract void read(@NonNull JsonReader reader, @NonNull Object value) throws IOException, IllegalAccessException;

    public abstract boolean writeField(@NonNull Object value) throws IOException, IllegalAccessException;
}