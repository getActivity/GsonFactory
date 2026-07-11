package com.hjq.gson.factory.element;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.hjq.gson.factory.GsonFactory;
import com.hjq.gson.factory.OnParseExceptionCallback;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2020/12/08
 *    desc   : Array 解析适配器，参考：{@link com.google.gson.internal.bind.CollectionTypeAdapterFactory.Adapter}
 */
public class CollectionTypeAdapter<E> extends TypeAdapter<Collection<E>> {

    @NonNull
    private final TypeAdapter<E> mElementTypeAdapter;
    @NonNull
    private final ObjectConstructor<? extends Collection<E>> mObjectConstructor;

    @Nullable
    private TypeToken<?> mTypeToken;
    @Nullable
    private String mFieldName;

    public CollectionTypeAdapter(@NonNull Gson gson, @NonNull Type elementType, @NonNull TypeAdapter<E> elementTypeAdapter,
                                 @NonNull ObjectConstructor<? extends Collection<E>> constructor) {
        mElementTypeAdapter = new TypeAdapterRuntimeTypeWrapper<>(gson, elementTypeAdapter, elementType);
        mObjectConstructor = constructor;
    }

    public void setReflectiveType(@NonNull TypeToken<?> typeToken, @Nullable String fieldName) {
        mTypeToken = typeToken;
        mFieldName = fieldName;
    }

    @Nullable
    @Override
    public Collection<E> read(@NonNull JsonReader in) throws IOException {
        JsonToken jsonToken = in.peek();
        Collection<E> collection = mObjectConstructor.construct();

        if (jsonToken == JsonToken.NULL) {
            in.nextNull();
            return collection;
        }

        if (jsonToken != JsonToken.BEGIN_ARRAY) {
            in.skipValue();
            OnParseExceptionCallback callback = GsonFactory.getOnParseExceptionCallback();
            if (callback != null && mTypeToken != null) {
                callback.onParseObjectException(mTypeToken, mFieldName, jsonToken);
            }
            return collection;
        }

        in.beginArray();
        while (in.hasNext()) {
            JsonToken itemJsonToken = null;
            try {
                // 获取 item 条目的类型
                itemJsonToken = in.peek();
                E instance = mElementTypeAdapter.read(in);
                collection.add(instance);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                OnParseExceptionCallback callback = GsonFactory.getOnParseExceptionCallback();
                if (callback != null && mTypeToken != null) {
                    callback.onParseListItemException(mTypeToken, mFieldName, itemJsonToken);
                }
            }
        }
        in.endArray();

        return collection;
    }

    @Override
    public void write(@NonNull JsonWriter out, @Nullable Collection<E> collection) throws IOException {
        if (collection == null) {
            out.nullValue();
            return;
        }

        out.beginArray();
        for (E element : collection) {
            mElementTypeAdapter.write(out, element);
        }
        out.endArray();
    }
}