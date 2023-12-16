package com.hjq.gson.factory.constructor

import com.google.gson.internal.ObjectConstructor
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/GsonFactory
 *    time   : 2023/11/25
 *    desc   : Kotlin Data Class 创建器，用于处理反射创建 data class 类导致默认值不生效的问题
 */
class KotlinDataClassDefaultValueConstructor<T : Any>(private val rawType: Class<*>) :  ObjectConstructor<T?> {

    companion object {
        /** 构造函数的字段的默认值 */
        private val ABSENT_VALUE = Any()
    }

    override fun construct(): T? {
        val rawTypeKotlin = rawType.kotlin
        // 寻找 Kotlin 主构造函数，如果找不到就不往下执行
        val constructor = rawTypeKotlin.primaryConstructor ?: return null
        constructor.isAccessible = true

        var fullInitialized = true
        val constructorSize = constructor.parameters.size
        val values = Array<Any?>(constructorSize) { ABSENT_VALUE }

        for (i in 0 until constructorSize) {
            if (values[i] !== ABSENT_VALUE) {
                continue
            }

            when {
                // 判断这个参数是不是可选的
                constructor.parameters[i].isOptional -> fullInitialized = false
                // 判断这个参数是否标记为空的
                constructor.parameters[i].type.isMarkedNullable -> values[i] = null
            }
        }

        val result = if (fullInitialized) {
            constructor.call(*values)
        } else {
            constructor.callBy(IndexedParameterMap(constructor.parameters, values))
        }

        return result as T
    }

    /** 一个简单的 Map，它使用参数索引而不是排序或哈希。 */
    class IndexedParameterMap(
        private val parameterKeys: List<KParameter>,
        private val parameterValues: Array<Any?>,
    ) : AbstractMutableMap<KParameter, Any?>() {

        override fun put(key: KParameter, value: Any?): Any? = null

        override val entries: MutableSet<MutableMap.MutableEntry<KParameter, Any?>>
            get() {
                val allPossibleEntries = parameterKeys.mapIndexed { index, value ->
                    SimpleEntry<KParameter, Any?>(value, parameterValues[index])
                }
                return allPossibleEntries.filterTo(mutableSetOf()) {
                    it.value !== ABSENT_VALUE
                }
            }

        override fun containsKey(key: KParameter) = parameterValues[key.index] !== ABSENT_VALUE

        override fun get(key: KParameter): Any? {
            val value = parameterValues[key.index]
            return if (value !== ABSENT_VALUE) value else null
        }
    }
}