package com.hjq.gson.factory.element;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.reflect.TypeToken;
import com.hjq.gson.factory.constructor.MainConstructor;
import java.lang.reflect.Type;
import java.util.Map;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2022/03/30
 *    desc   : Map 解析适配器，参考：{@link com.google.gson.internal.bind.MapTypeAdapterFactory}
 */
public class MapTypeAdapterFactory implements TypeAdapterFactory {

   private final MainConstructor mMainConstructor;
   final boolean mComplexMapKeySerialization;

   public MapTypeAdapterFactory(MainConstructor mainConstructor,
                                boolean complexMapKeySerialization) {
      mMainConstructor = mainConstructor;
      mComplexMapKeySerialization = complexMapKeySerialization;
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   @Override
   public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
      Type type = typeToken.getType();

      Class<? super T> rawType = typeToken.getRawType();
      if (!Map.class.isAssignableFrom(rawType)) {
         return null;
      }

      Class<?> rawTypeOfSrc = $Gson$Types.getRawType(type);
      Type[] keyAndValueTypes = $Gson$Types.getMapKeyAndValueTypes(type, rawTypeOfSrc);
      TypeAdapter<?> keyAdapter = getKeyAdapter(gson, keyAndValueTypes[0]);
      TypeAdapter<?> valueAdapter = gson.getAdapter(TypeToken.get(keyAndValueTypes[1]));
      ObjectConstructor<T> constructor = mMainConstructor.get(typeToken);

      // we don't define a type parameter for the key or value types
      MapTypeAdapter result = new MapTypeAdapter(gson, keyAndValueTypes[0], keyAdapter,
              keyAndValueTypes[1], valueAdapter, constructor, mComplexMapKeySerialization);
      result.setReflectiveType(typeToken, null);
      return result;
   }

   /**
    * Returns a type adapter that writes the value as a string.
    */
   private TypeAdapter<?> getKeyAdapter(Gson context, Type keyType) {
      return (keyType == boolean.class || keyType == Boolean.class)
              ? TypeAdapters.BOOLEAN_AS_STRING
              : context.getAdapter(TypeToken.get(keyType));
   }
}