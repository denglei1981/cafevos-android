package com.changanford.common.bean

class ListMainBean<T>(
    var dataList: ArrayList<T>?,
    val extend: HomeExtendBean,
    val pageNo: Int,
    val pageSize: Int,
    val total: Int,
    val totalPage: Int
)