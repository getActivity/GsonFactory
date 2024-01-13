package com.hjq.gson.factory.test

data class DataClassBean(
    var name: String = "轮子哥",
    var alias: String,
    var address: String? = "",
    var company: String?,
    var age: Int = 20,
    var weight: Int,
    var stature: Int? = 180,
    var interest: List<String>,
    var child: DataClassChildBean
)