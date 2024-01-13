package com.hjq.gson.factory.constructor;

import com.google.gson.Gson;
import com.google.gson.InstanceCreator;
import com.google.gson.ReflectionAccessFilter;
import com.google.gson.ReflectionAccessFilter.FilterResult;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.ReflectionAccessFilterHelper;
import com.google.gson.internal.reflect.ReflectionHelper;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2022/03/30
 *    desc   : 构造函数构造器，参考：{@link com.google.gson.internal.ConstructorConstructor}
 */
public final class MainConstructor {
    private final Map<Type, InstanceCreator<?>> mInstanceCreators;
    private final boolean mUseJdkUnsafe;
    private final List<ReflectionAccessFilter> mReflectionFilters;

    public MainConstructor(Map<Type, InstanceCreator<?>> instanceCreators, boolean useJdkUnsafe, List<ReflectionAccessFilter> reflectionFilters) {
        mInstanceCreators = instanceCreators;
        mUseJdkUnsafe = useJdkUnsafe;
        mReflectionFilters = reflectionFilters;
    }

    /**
     * Check if the class can be instantiated by Unsafe allocator. If the instance has interface or abstract modifiers
     * return an exception message.
     * @param c instance of the class to be checked
     * @return if instantiable {@code null}, else a non-{@code null} exception message
     */
    static String checkInstantiable(Class<?> c) {
        int modifiers = c.getModifiers();
        if (Modifier.isInterface(modifiers)) {
            return "Interfaces can't be instantiated! Register an InstanceCreator "
                + "or a TypeAdapter for this type. Interface name: " + c.getName();
        }
        if (Modifier.isAbstract(modifiers)) {
            return "Abstract classes can't be instantiated! Register an InstanceCreator "
                + "or a TypeAdapter for this type. Class name: " + c.getName();
        }
        return null;
    }

    public <T> ObjectConstructor<T> get(Gson gson, TypeToken<T> typeToken) {
        final Type type = typeToken.getType();
        final Class<? super T> rawType = typeToken.getRawType();

        // first try an instance creator

        @SuppressWarnings("unchecked")
        final InstanceCreator<T> typeCreator = (InstanceCreator<T>) mInstanceCreators.get(type);
        if (typeCreator != null) {
            return new InstanceCreatorConstructor<>(typeCreator, type);
        }

        // Next try raw type match for instance creators
        @SuppressWarnings("unchecked") // types must agree
        final InstanceCreator<T> rawTypeCreator =
            (InstanceCreator<T>) mInstanceCreators.get(rawType);
        if (rawTypeCreator != null) {
            return new InstanceCreatorConstructor<>(rawTypeCreator, type);
        }

        // First consider special constructors before checking for no-args constructors
        // below to avoid matching internal no-args constructors which might be added in
        // future JDK versions
        ObjectConstructor<T> specialConstructor = newSpecialCollectionConstructor(type, rawType);
        if (specialConstructor != null) {
            return specialConstructor;
        }

        FilterResult filterResult = ReflectionAccessFilterHelper.getFilterResult(mReflectionFilters, rawType);
        ObjectConstructor<T> defaultConstructor = newDefaultConstructor(this, gson, rawType, filterResult);
        if (defaultConstructor != null) {
            return defaultConstructor;
        }

        ObjectConstructor<T> defaultImplementation = newDefaultImplementationConstructor(type, rawType);
        if (defaultImplementation != null) {
            return defaultImplementation;
        }

        // Check whether type is instantiable; otherwise ReflectionAccessFilter recommendation
        // of adjusting filter suggested below is irrelevant since it would not solve the problem
        final String exceptionMessage = checkInstantiable(rawType);
        if (exceptionMessage != null) {
            return new ExceptionConstructor<>(exceptionMessage);
        }

        // Consider usage of Unsafe as reflection, so don't use if BLOCK_ALL
        // Additionally, since it is not calling any constructor at all, don't use if BLOCK_INACCESSIBLE
        if (filterResult == FilterResult.ALLOW) {
            // finally try unsafe
            return newUnsafeAllocator(gson, rawType);
        } else {
            final String message = "Unable to create instance of " + rawType + "; ReflectionAccessFilter "
                + "does not permit using reflection or Unsafe. Register an InstanceCreator or a TypeAdapter "
                + "for this type or adjust the access filter to allow using reflection.";
            return new ExceptionConstructor<>(message);
        }
    }

    /**
     * 为没有公共无参数构造函数的特殊 JDK 集合类型创建构造函数
     */
    private static <T> ObjectConstructor<T> newSpecialCollectionConstructor(final Type type, Class<? super T> rawType) {
        if (EnumSet.class.isAssignableFrom(rawType)) {
            return new EnumSetConstructor<>(type);
        }
        // Only support creation of EnumMap, but not of custom subtypes; for them type parameters
        // and constructor parameter might have completely different meaning
        else if (rawType == EnumMap.class) {
            return new EnumMapConstructor<>(type);
        }

        return null;
    }

    private static <T> ObjectConstructor<T> newDefaultConstructor(MainConstructor mainConstructor, Gson gson, Class<? super T> rawType, FilterResult filterResult) {
        // Cannot invoke constructor of abstract class
        if (Modifier.isAbstract(rawType.getModifiers())) {
            return null;
        }

        final Constructor<? super T> constructor;
        try {
            constructor = rawType.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            return null;
        }

        boolean canAccess = filterResult == FilterResult.ALLOW || (ReflectionAccessFilterHelper.canAccess(constructor, null)
            // Be a bit more lenient here for BLOCK_ALL; if constructor is accessible and public then allow calling it
            && (filterResult != FilterResult.BLOCK_ALL || Modifier.isPublic(constructor.getModifiers())));

        if (!canAccess) {
            final String message = "Unable to invoke no-args constructor of " + rawType + "; "
                + "constructor is not accessible and ReflectionAccessFilter does not permit making "
                + "it accessible. Register an InstanceCreator or a TypeAdapter for this type, change "
                + "the visibility of the constructor or adjust the access filter.";
            return new ExceptionConstructor<>(message);
        }

        // Only try to make accessible if allowed; in all other cases checks above should
        // have verified that constructor is accessible
        if (filterResult == FilterResult.ALLOW) {
            final String exceptionMessage = ReflectionHelper.tryMakeAccessible(constructor);
            if (exceptionMessage != null) {
                /*
                 * Create ObjectConstructor which throws exception.
                 * This keeps backward compatibility (compared to returning `null` which
                 * would then choose another way of creating object).
                 * And it supports types which are only serialized but not deserialized
                 * (compared to directly throwing exception here), e.g. when runtime type
                 * of object is inaccessible, but compile-time type is accessible.
                 */

                // New exception is created every time to avoid keeping reference
                // to exception with potentially long stack trace, causing a
                // memory leak
                return new ExceptionConstructor<>(exceptionMessage);
            }
        }

        return new ReflectCreatorConstructor<>(mainConstructor, gson, rawType, constructor);
    }

    /**
     * Constructors for common interface types like Map and List and their
     * subtypes.
     */
    private static <T> ObjectConstructor<T> newDefaultImplementationConstructor(
        final Type type, Class<? super T> rawType) {

        /*
         * IMPORTANT: Must only create instances for classes with public no-args constructor.
         * For classes with special constructors / factory methods (e.g. EnumSet)
         * `newSpecialCollectionConstructor` defined above must be used, to avoid no-args
         * constructor check (which is called before this method) detecting internal no-args
         * constructors which might be added in a future JDK version
         */

        if (Collection.class.isAssignableFrom(rawType)) {
            if (SortedSet.class.isAssignableFrom(rawType)) {
                return SortedSetConstructor.getInstance();
            } else if (Set.class.isAssignableFrom(rawType)) {
                return SetConstructor.getInstance();
            } else if (Queue.class.isAssignableFrom(rawType)) {
                return QueueConstructor.getInstance();
            } else {
                return ListConstructor.getInstance();
            }
        }

        if (Map.class.isAssignableFrom(rawType)) {
            if (ConcurrentNavigableMap.class.isAssignableFrom(rawType)) {
                return ConcurrentSkipListMapConstructor.getInstance();
            } else if (ConcurrentMap.class.isAssignableFrom(rawType)) {
                return ConcurrentMapConstructor.getInstance();
            } else if (SortedMap.class.isAssignableFrom(rawType)) {
                return SortedMapConstructor.getInstance();
            } else if (type instanceof ParameterizedType && !(String.class.isAssignableFrom(
                TypeToken.get(((ParameterizedType) type).getActualTypeArguments()[0]).getRawType()))) {
                return LinkedHashMapConstructor.getInstance();
            } else {
                return LinkedTreeMapConstructor.getInstance();
            }
        }

        return null;
    }

    private <T> ObjectConstructor<T> newUnsafeAllocator(Gson gson, final Class<? super T> rawType) {
        if (mUseJdkUnsafe) {
            return new ReflectSafeCreatorConstructor<>(this, gson, rawType);
        } else {
            final String exceptionMessage = "Unable to create instance of " + rawType + "; usage of JDK Unsafe "
                + "is disabled. Registering an InstanceCreator or a TypeAdapter for this type, adding a no-args "
                + "constructor, or enabling usage of JDK Unsafe may fix this problem.";
            return new ExceptionConstructor<>(exceptionMessage);
        }
    }

    @Override
    public String toString() {
        return mInstanceCreators.toString();
    }
}