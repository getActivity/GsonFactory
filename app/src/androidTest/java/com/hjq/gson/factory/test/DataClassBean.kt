package com.hjq.gson.factory.test

data class DataClassBean(
    var address: String? = "",
    var age: Int = 20,
    var alias: String,
    var child: DataClassChildBean,
    var company: String?,
    var interest: List<String>,
    var name: String = "轮子哥",
    var stature: Int? = 180,
    var weight: Int
)