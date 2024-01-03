package com.changanford.circle.bean

import com.changanford.common.bean.CircleShareBean

data class SugesstionTopicDetailBean(
    val commentCount: Double,
    val createTime: Double,
    val createrId: Any,
    val description: String,
    val isGrounding: Double,
    val isHot: Double,
    val isRecommend: Double,
    val lastPostsTime: Any,
    val likesCount: Int,
    val isBuyCarDiary: Int,
    val name: String,
    val heat: String,
    val pic: String,
    val postsCount: Int,
    val viewsCount: Int = 0,
    val shareImg: Any,
    val sortOrder: Double,
    val status: Double,
    val topicId: Double,
    val updateTime: Double,
    val shareBeanVO: CircleShareBean?
)