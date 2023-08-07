package com.hjq.gson.factory.element;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;
import com.hjq.gson.factory.constructor.MainConstructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2020/12/08
 *    desc   : Array 解析适配器，参考：{@link com.google.gson.internal.bind.CollectionTypeAdapterFactory}
 */
public class CollectionTypeAdapterFactory implements TypeAdapterFactory {

    private final MainConstructor mMainConstructor;

    public CollectionTypeAdapterFactory(MainConstructor constructor) {
        mMainConstructor = constructor;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        Type type = typeToken.getType();
        Class<? super T> rawType = typeToken.getRawType();
        // 判断是否包含这种类型
        if (ReflectiveTypeUtils.containsClass(rawType)) {
            return null;
        }
        if (typeToken.getType() instanceof GenericArrayType ||
                typeToken.getType() instanceof Class &&
                ((Class<?>) typeToken.getType()).isArray()) {
            return null;
        }

        if (!Collection.class.isAssignableFrom(rawType)) {
            return null;
        }

        Type elementType = $Gson$Types.getCollectionElementType(type, rawType);
        TypeAdapter<?> elementTypeAdapter = gson.getAdapter(TypeToken.get(elementType));
        ObjectConstructor<T> constructor = mMainConstructor.get(typeToken);

        // create() doesn't define a type parameter
        CollectionTypeAdapter collectionTypeAdapter =
                new CollectionTypeAdapter(gson, elementType, elementTypeAdapter, constructor);
        collectionTypeAdapter.setReflectiveType(typeToken, null);
        return collectionTypeAdapter;
    }
}