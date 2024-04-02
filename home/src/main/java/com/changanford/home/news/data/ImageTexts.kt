package com.changanford.home.news.data

data class ImageTexts(
    var artId: Int = 0,
    var createTime: Long = 0L,
    var description: String = "",
    var id: Int = 0,
    var img: String = "",
    var likesCount: Int = 0,
    var sortOrder: Int = 0,
    val infoData: NewsExpandData? = null
)