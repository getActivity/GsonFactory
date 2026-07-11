package com.hjq.gson.factory.element;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.hjq.gson.factory.GsonFactory;
import com.hjq.gson.factory.ParseExceptionCallback;
import com.hjq.gson.factory.other.ReflectiveFieldBound;
import java.io.IOException;
import java.util.Map;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2020/12/08
 *    desc   : Object 解析适配器，参考：{@link com.google.gson.internal.bind.ReflectiveTypeAdapterFactory.Adapter}
 */
public class ReflectiveTypeAdapter<T> extends TypeAdapter<T> {

    @NonNull
    private final ObjectConstructor<T> mConstructor;
    @NonNull
    private final Map<String, ReflectiveFieldBound> mBoundFields;

    @Nullable
    private TypeToken<?> mTypeToken;
    @Nullable
    private String mFieldName;

    public ReflectiveTypeAdapter(@NonNull ObjectConstructor<T> constructor, @NonNull Map<String, ReflectiveFieldBound> fields) {
        mConstructor = constructor;
        mBoundFields = fields;
    }

    public void setReflectiveType(@NonNull TypeToken<?> typeToken, @Nullable String fieldName) {
        mTypeToken = typeToken;
        mFieldName = fieldName;
    }

    @Nullable
    @Override
    public T read(@NonNull JsonReader in) throws IOException {
        JsonToken jsonToken = in.peek();

        if (jsonToken == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        if (jsonToken != JsonToken.BEGIN_OBJECT) {
            in.skipValue();
            ParseExceptionCallback callback = GsonFactory.getParseExceptionCallback();
            if (callback != null && mTypeToken != null) {
                callback.onParseObjectException(mTypeToken, mFieldName, jsonToken);
            }
            return null;
        }

        T instance = mConstructor.construct();
        in.beginObject();
        while (in.hasNext()) {
            String name = in.nextName();
            ReflectiveFieldBound field = mBoundFields.get(name);
            if (field == null || !field.isDeserialized()) {
                in.skipValue();
                continue;
            }

            JsonToken peek = in.peek();

            try {
                field.read(in, instance);
            } catch (IllegalStateException e) {
                throw new JsonSyntaxException(e);
            } catch (IllegalAccessException e) {
                throw new AssertionError(e);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                ParseExceptionCallback callback = GsonFactory.getParseExceptionCallback();
                if (callback != null) {
                    callback.onParseObjectException(TypeToken.get(instance.getClass()), field.getFieldName(), peek);
                }
            }
        }
        in.endObject();
        return instance;
    }

    @Override
    public void write(@NonNull JsonWriter out, @Nullable T value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        out.beginObject();
        for (ReflectiveFieldBound fieldBound : mBoundFields.values()) {
            try {
                if (fieldBound.writeField(value)) {
                    out.name(fieldBound.getFieldName());
                    fieldBound.write(out, value);
                }
            } catch (IllegalAccessException e) {
                throw new AssertionError(e);
            }
        }
        out.endObject();
    }
}