package com.changanford.home.bean

/**
 *  搜索关键词。
 * */
data class SearchKeyBean(
    var createTime: Any,
    var func: Int,
    var hint: Int,
    var hot: Int,
    var id: Int,
    val jumpDataType: Int? = null,
    val jumpDataValue: String,
    var keyword: String,
    var sort: Int,
    var source: Int,
    var status: Int,
    val hotTag:Int
)