package com.changanford.circle.bean

/**
 * @Author: hpb
 * @Date: 2020/5/18
 * @Des:
 */
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
    val name: String,
    val heat: String,
    val pic: String,
    val postsCount: Int,
    val shareImg: Any,
    val sortOrder: Double,
    val status: Double,
    val topicId: Double,
    val updateTime: Double,
    val shareBeanVO: CircleShareBean?
)

data class CircleShareBean(
    val bizId: String,
    val shareDesc: String,
    val shareImg: String,
    val shareTitle: String,
    val shareUrl: String,
    val type: String,
    val wxminiprogramCode: String
)