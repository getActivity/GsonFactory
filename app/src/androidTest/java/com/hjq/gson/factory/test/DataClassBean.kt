package com.hjq.gson.factory.test

data class DataClassBean(
    val name: String?,
    val age: Int = 18,
    val address: String?,
    val birthday: Long = System.currentTimeMillis()
)