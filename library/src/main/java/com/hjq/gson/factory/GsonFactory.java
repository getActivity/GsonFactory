package com.hjq.gson.factory;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.ReflectionAccessFilter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.bind.TypeAdapters;
import com.hjq.gson.factory.constructor.MainConstructor;
import com.hjq.gson.factory.data.BigDecimalTypeAdapter;
import com.hjq.gson.factory.data.BooleanTypeAdapter;
import com.hjq.gson.factory.data.DoubleTypeAdapter;
import com.hjq.gson.factory.data.FloatTypeAdapter;
import com.hjq.gson.factory.data.IntegerTypeAdapter;
import com.hjq.gson.factory.data.JSONArrayTypeAdapter;
import com.hjq.gson.factory.data.JSONObjectTypeAdapter;
import com.hjq.gson.factory.data.LongTypeAdapter;
import com.hjq.gson.factory.data.StringTypeAdapter;
import com.hjq.gson.factory.element.CollectionTypeAdapterFactory;
import com.hjq.gson.factory.element.MapTypeAdapterFactory;
import com.hjq.gson.factory.element.ReflectiveTypeAdapterFactory;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2020/11/10
 *    desc   : Gson 解析容错适配器
 */
@SuppressWarnings("unused")
public final class GsonFactory {

    private static final HashMap<Type, InstanceCreator<?>> INSTANCE_CREATORS = new HashMap<>(0);

    private static final List<TypeAdapterFactory> TYPE_ADAPTER_FACTORIES = new ArrayList<>();

    private static final List<ReflectionAccessFilter> REFLECTION_ACCESS_FILTERS = new ArrayList<>();

    private static JsonCallback sJsonCallback;

    private static volatile Gson sGson;

    private GsonFactory() {}

    /**
     * 获取单例的 Gson 对象
     */
    public static Gson getSingletonGson() {
        // 加入双重校验锁
        if(sGson == null) {
            synchronized (GsonFactory.class) {
                if(sGson == null){
                    sGson = newGsonBuilder().create();
                }
            }
        }
        return sGson;
    }

    /**
     * 设置单例的 Gson 对象
     */
    public static void setSingletonGson(Gson gson) {
        sGson = gson;
    }

    /**
     * 设置 Json 解析出错回调对象
     */
    public static void setJsonCallback(JsonCallback callback) {
        GsonFactory.sJsonCallback = callback;
    }

    /**
     * 获取 Json 解析出错回调对象
     */
    public static JsonCallback getJsonCallback() {
        return sJsonCallback;
    }

    /**
     * 注册类型解析适配器
     */
    public static void registerTypeAdapterFactory(TypeAdapterFactory factory) {
        TYPE_ADAPTER_FACTORIES.add(factory);
    }

    /**
     * 注册构造函数创建器
     *
     * @param type                  对象类型
     * @param creator               实例创建器
     */
    public static void registerInstanceCreator(Type type, InstanceCreator<?> creator) {
        INSTANCE_CREATORS.put(type, creator);
    }

    /**
     * 添加反射访问过滤器，同等于 {@link GsonBuilder#addReflectionAccessFilter(ReflectionAccessFilter)}
     */
    public void addReflectionAccessFilter(ReflectionAccessFilter filter) {
        if (filter == null) {
            return;
        }
        REFLECTION_ACCESS_FILTERS.add(0, filter);
    }

    /**
     * 创建 Gson 构建对象
     */
    public static GsonBuilder newGsonBuilder() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        MainConstructor constructor = new MainConstructor(INSTANCE_CREATORS, true, REFLECTION_ACCESS_FILTERS);
        gsonBuilder.registerTypeAdapterFactory(TypeAdapters.newFactory(String.class, new StringTypeAdapter()))
                .registerTypeAdapterFactory(TypeAdapters.newFactory(boolean.class, Boolean.class, new BooleanTypeAdapter()))
                .registerTypeAdapterFactory(TypeAdapters.newFactory(int.class, Integer.class, new IntegerTypeAdapter()))
                .registerTypeAdapterFactory(TypeAdapters.newFactory(long.class, Long.class, new LongTypeAdapter()))
                .registerTypeAdapterFactory(TypeAdapters.newFactory(float.class, Float.class, new FloatTypeAdapter()))
                .registerTypeAdapterFactory(TypeAdapters.newFactory(double.class, Double.class, new DoubleTypeAdapter()))
                .registerTypeAdapterFactory(TypeAdapters.newFactory(BigDecimal.class, new BigDecimalTypeAdapter()))
                .registerTypeAdapterFactory(new CollectionTypeAdapterFactory(constructor))
                .registerTypeAdapterFactory(new ReflectiveTypeAdapterFactory(constructor, FieldNamingPolicy.IDENTITY, Excluder.DEFAULT))
                .registerTypeAdapterFactory(new MapTypeAdapterFactory(constructor, false))
                .registerTypeAdapterFactory(TypeAdapters.newFactory(JSONObject.class, new JSONObjectTypeAdapter()))
                .registerTypeAdapterFactory(TypeAdapters.newFactory(JSONArray.class, new JSONArrayTypeAdapter()));
        // 添加到自定义的类型解析适配器，因为在 GsonBuilder.create 方法中会对 List 进行反转，所以这里需要放到最后的位置上，这样就会优先解析
        for (TypeAdapterFactory typeAdapterFactory : TYPE_ADAPTER_FACTORIES) {
            gsonBuilder.registerTypeAdapterFactory(typeAdapterFactory);
        }
        return gsonBuilder;
    }
}