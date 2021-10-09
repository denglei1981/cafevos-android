package com.changanford.home.news.data

data class Tag(
    var createTime: Long,
    var id: Int,
    var status: Int,
    var tagName: String,
    var tagNameLike: Any,
    var type: Int
)