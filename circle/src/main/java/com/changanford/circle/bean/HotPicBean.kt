package com.changanford.circle.bean

/**
 *Author lcw
 *Time on 2021/9/28
 *Purpose
 */
data class HotPicBean(
    val dataList: ArrayList<HotPicItemBean> = arrayListOf(),
    val extend: Extend = Extend(),
    val pageNo: Int = 0,
    val pageSize: Int = 0,
    val total: Int = 0,
    val totalPage: Int = 0
)

data class HotPicItemBean(
    val description: String = "",
    val likesCount: Int = 0,
    val name: String = "",
    val pic: String = "",
    val postsCount: Int = 0,
    val topicId: Int = 0,
    val userCount: Int = 0
)