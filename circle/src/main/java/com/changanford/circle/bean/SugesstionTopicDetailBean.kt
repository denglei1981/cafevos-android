package com.changanford.circle.bean

import com.changanford.common.bean.CircleShareBean

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